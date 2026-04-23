package com.fitness.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.user.dto.PasswordUpdateDTO;
import com.fitness.module.user.dto.UserQueryDTO;
import com.fitness.module.user.dto.UserUpdateDTO;
import com.fitness.module.user.entity.UserEntity;
import com.fitness.module.user.mapper.UserMapper;
import com.fitness.module.user.service.UserService;
import com.fitness.module.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> pageList(UserQueryDTO query) {
        // 构建查询条件
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getKeyword()), UserEntity::getUsername, query.getKeyword())
                .or(StringUtils.hasText(query.getKeyword()))
                .like(StringUtils.hasText(query.getKeyword()), UserEntity::getNickname, query.getKeyword())
                .or(StringUtils.hasText(query.getKeyword()))
                .like(StringUtils.hasText(query.getKeyword()), UserEntity::getPhone, query.getKeyword());
        wrapper.eq(Objects.nonNull(query.getRole()), UserEntity::getRole, query.getRole());
        wrapper.eq(Objects.nonNull(query.getStatus()), UserEntity::getStatus, query.getStatus());
        wrapper.orderByDesc(UserEntity::getCreatedAt);

        // 分页查询
        IPage<UserEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<UserEntity> result = this.page(page, wrapper);

        // 转换为 VO
        IPage<UserVO> voPage = result.convert(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setEmail(user.getEmail());
            vo.setPhone(user.getPhone());
            vo.setGender(user.getGender());
            vo.setBirthday(user.getBirthday());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            vo.setCreatedAt(user.getCreatedAt());
            vo.setUpdatedAt(user.getUpdatedAt());
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public UserVO getDetail(Long id) {
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException(1101, "用户不存在");
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long id, UserUpdateDTO dto) {
        // 验证用户是否存在
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException(1101, "用户不存在");
        }

        // 检查手机号是否已被其他用户使用
        if (StringUtils.hasText(dto.getPhone())) {
            Long phoneCount = this.count(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getPhone, dto.getPhone())
                    .ne(UserEntity::getId, id));
            if (phoneCount > 0) {
                throw new BusinessException(1102, "手机号已被其他用户使用");
            }
        }

        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(dto.getEmail())) {
            Long emailCount = this.count(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getEmail, dto.getEmail())
                    .ne(UserEntity::getId, id));
            if (emailCount > 0) {
                throw new BusinessException(1103, "邮箱已被其他用户使用");
            }
        }

        // 更新字段
        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }

        this.updateById(user);
        log.info("用户资料更新成功: userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, PasswordUpdateDTO dto) {
        // 验证用户是否存在
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException(1101, "用户不存在");
        }

        // 验证原密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(1104, "原密码错误");
        }

        // 验证新密码和确认密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(1105, "两次输入的新密码不一致");
        }

        // 更新密码
        String encryptedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encryptedPassword);
        this.updateById(user);
        log.info("用户密码修改成功: userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id) {
        // 验证用户是否存在
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException(1101, "用户不存在");
        }

        // 切换状态（0正常 ↔ 1禁用）
        Integer newStatus = user.getStatus() == 0 ? 1 : 0;
        user.setStatus(newStatus);
        this.updateById(user);
        log.info("用户状态切换成功: userId={}, newStatus={}", id, newStatus);
    }
}