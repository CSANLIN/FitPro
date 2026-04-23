package com.fitness.module.user.service;

import com.fitness.common.PageResult;
import com.fitness.module.user.dto.PasswordUpdateDTO;
import com.fitness.module.user.dto.UserQueryDTO;
import com.fitness.module.user.dto.UserUpdateDTO;
import com.fitness.module.user.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表（管理端）
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<UserVO> pageList(UserQueryDTO query);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getDetail(Long id);

    /**
     * 更新用户资料
     *
     * @param id 用户ID
     * @param dto 更新参数
     */
    void updateProfile(Long id, UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param dto 密码修改参数
     */
    void updatePassword(Long id, PasswordUpdateDTO dto);

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param id 用户ID
     */
    void toggleStatus(Long id);
}