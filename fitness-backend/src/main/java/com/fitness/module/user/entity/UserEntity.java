package com.fitness.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private String role;
    private Integer status;
}
