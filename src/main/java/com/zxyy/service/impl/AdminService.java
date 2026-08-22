package com.zxyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.constant.MessageConstant;
import com.zxyy.constant.StatusConstant;
import com.zxyy.exception.AccountLocked;
import com.zxyy.exception.AccountNotFound;
import com.zxyy.exception.PasswordError;
import com.zxyy.mapper.AdminMapper;
import com.zxyy.pojo.dto.AdminLoginDTO;
import com.zxyy.pojo.entity.Admin;
import com.zxyy.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {
    /**
     * 登录接口
     * @param adminLoginDTO
     * @return
     */
    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String plainPassword = adminLoginDTO.getPassword();
        //1.查询数据库
        Admin admin = query().eq("username", username).one();
        //2.判断是否存在 不存在直接抛出异常
        if(admin == null){
            throw new AccountNotFound(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //3.存在判断密码是否正确
        if(!PasswordUtil.matches(plainPassword,admin.getPassword())){
            throw new PasswordError(MessageConstant.PASSWORD_ERROR);
        }
        //4.判断账号是否被锁定
        if(admin.getStatus() == StatusConstant.DISABLE){
            //账号被锁定
            throw new AccountLocked(MessageConstant.ACCOUNT_LOCKED);
        }
        //5.密码正确返回实体 发放token
        return admin;
    }
}
