package com.fitness.module.auth.service;

import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return 令牌信息
     */
    TokenVO register(RegisterDTO dto);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 令牌信息
     */
    TokenVO login(String username, String password);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    TokenVO refresh(String refreshToken);

    /**
     * 用户登出
     *
     * @param refreshToken 刷新令牌
     */
    void logout(String refreshToken);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getCurrentUserInfo();
}