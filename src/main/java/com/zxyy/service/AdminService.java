package com.zxyy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxyy.pojo.dto.AdminLoginDTO;
import com.zxyy.pojo.entity.Admin;

public interface AdminService extends IService<Admin> {
    Admin login(AdminLoginDTO employeeLoginDTO);
}
