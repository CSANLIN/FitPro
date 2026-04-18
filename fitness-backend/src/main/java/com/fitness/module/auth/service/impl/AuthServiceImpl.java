package com.fitness.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.service.AuthService;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;
import com.fitness.module.user.entity.UserEntity;
import com.fitness.module.user.mapper.UserMapper;
import com.fitness.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    /**
     * Refresh Token 在 Redis 中的 key 前缀
     */
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenVO register(RegisterDTO dto) {
        // 1. 校验用户名是否已存在
        Long usernameCount = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, dto.getUsername()));
        if (usernameCount > 0) {
            throw new BusinessException(1001, "用户名已存在");
        }

        // 2. 校验手机号是否已存在
        Long phoneCount = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, dto.getPhone()));
        if (phoneCount > 0) {
            throw new BusinessException(1002, "手机号已存在");
        }

        // 3. 校验密码和确认密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(1003, "两次输入的密码不一致");
        }

        // 4. 密码加密
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        // 5. 构建用户实体
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(encryptedPassword);
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setRole("MEMBER"); // 注册用户默认角色为 MEMBER
        user.setStatus(0); // 状态正常

        // 6. 保存用户
        userMapper.insert(user);

        // 7. 生成双 Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 8. 存储 Refresh Token 到 Redis
        String refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken,
                jwtTokenProvider.getRefreshTokenExpire(), TimeUnit.SECONDS);

        // 9. 返回 TokenVO
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken(accessToken);
        tokenVO.setRefreshToken(refreshToken);
        tokenVO.setTokenType("Bearer");
        tokenVO.setExpiresIn((int) jwtTokenProvider.getAccessTokenExpire());
        tokenVO.setRefreshExpiresIn((int) jwtTokenProvider.getRefreshTokenExpire());

        log.info("用户注册成功: username={}, userId={}", dto.getUsername(), user.getId());
        return tokenVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenVO login(String username, String password) {
        // 1. 根据用户名查找用户
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username));
        if (user == null) {
            throw new BusinessException(1004, "用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(1004, "用户名或密码错误");
        }

        // 3. 检查用户状态（0=正常，1=禁用）
        if (user.getStatus() != 0) {
            throw new BusinessException(1005, "用户账户已被禁用，请联系管理员");
        }

        // 4. 生成双 Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 5. 存储 Refresh Token 到 Redis
        String refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken,
                jwtTokenProvider.getRefreshTokenExpire(), TimeUnit.SECONDS);

        // 6. 返回 TokenVO
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken(accessToken);
        tokenVO.setRefreshToken(refreshToken);
        tokenVO.setTokenType("Bearer");
        tokenVO.setExpiresIn((int) jwtTokenProvider.getAccessTokenExpire());
        tokenVO.setRefreshExpiresIn((int) jwtTokenProvider.getRefreshTokenExpire());

        log.info("用户登录成功: username={}, userId={}", username, user.getId());
        return tokenVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenVO refresh(String refreshToken) {
        // 1. 验证 Refresh Token 格式和类型
        if (!jwtTokenProvider.validateToken(refreshToken, "refresh")) {
            throw new BusinessException(1006, "无效的刷新令牌");
        }

        // 2. 从 Token 中提取用户 ID
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 从 Redis 中获取存储的 Refresh Token
        String refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX + userId;
        String storedRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);

        // 4. 验证 Refresh Token 是否与存储的一致
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(1007, "刷新令牌已失效，请重新登录");
        }

        // 5. 查询用户信息
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(1008, "用户不存在");
        }

        // 6. 检查用户状态
        if (user.getStatus() != 0) {
            throw new BusinessException(1005, "用户账户已被禁用，请联系管理员");
        }

        // 7. 生成新的 Access Token
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());

        // 8. 返回新的 TokenVO（Refresh Token 保持不变）
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken(newAccessToken);
        tokenVO.setRefreshToken(refreshToken);
        tokenVO.setTokenType("Bearer");
        tokenVO.setExpiresIn((int) jwtTokenProvider.getAccessTokenExpire());
        tokenVO.setRefreshExpiresIn((int) jwtTokenProvider.getRefreshTokenExpire());

        log.info("令牌刷新成功: userId={}", userId);
        return tokenVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String refreshToken) {
        // 1. 验证 Refresh Token 格式和类型
        if (!jwtTokenProvider.validateToken(refreshToken, "refresh")) {
            throw new BusinessException(1006, "无效的刷新令牌");
        }

        // 2. 从 Token 中提取用户 ID
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 从 Redis 中删除 Refresh Token
        String refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX + userId;
        Boolean deleted = redisTemplate.delete(refreshTokenKey);

        if (Boolean.TRUE.equals(deleted)) {
            log.info("用户登出成功: userId={}", userId);
        } else {
            log.warn("用户登出时未找到 Refresh Token: userId={}", userId);
        }
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 1. 从 SecurityContext 中获取当前认证的用户 ID
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new BusinessException(1009, "用户未认证");
        }

        Long userId;
        try {
            userId = Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            throw new BusinessException(1009, "用户未认证");
        }

        // 2. 查询用户信息
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(1008, "用户不存在");
        }

        // 3. 检查用户状态
        if (user.getStatus() != 0) {
            throw new BusinessException(1005, "用户账户已被禁用，请联系管理员");
        }

        // 4. 转换为 UserInfoVO
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setGender(user.getGender());
        userInfoVO.setBirthday(user.getBirthday());
        userInfoVO.setRole(user.getRole());
        userInfoVO.setStatus(user.getStatus());
        userInfoVO.setCreatedAt(user.getCreatedAt());

        return userInfoVO;
    }
}