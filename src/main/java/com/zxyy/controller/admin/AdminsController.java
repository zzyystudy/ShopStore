package com.zxyy.controller.admin;


import com.zxyy.constant.JwtClaimsConstant;
import com.zxyy.pojo.dto.AdminLoginDTO;
import com.zxyy.pojo.entity.Admin;
import com.zxyy.pojo.vo.AdminLoginVo;
import com.zxyy.properties.JwtProperties;
import com.zxyy.result.Result;
import com.zxyy.service.AdminService;
import com.zxyy.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/admins")
@Tag(name = "管理端用户接口")
@Slf4j
public class AdminsController {

    @Autowired
    private AdminService adminService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(description = "管理员登录")
    public Result<AdminLoginVo> login(@RequestBody AdminLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Admin employee = adminService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        AdminLoginVo adminLoginVo = AdminLoginVo.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(adminLoginVo);
    }


    @GetMapping("/hello")
    @Operation(description = "hello")
    public Result hello(){
        return Result.success("hello");
    }



}
