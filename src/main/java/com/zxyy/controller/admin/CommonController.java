package com.zxyy.controller.admin;

import com.zxyy.constant.MessageConstant;
import com.zxyy.result.Result;
import com.zxyy.util.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common/upload")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {
    //将对象存储的工具类诸如进来
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 这个MultipartFile要上传的文件大了 传不上 前端应该进行一次压缩
     * @param file
     * @return
     */
    @PostMapping
    @Operation(summary = "上传文件到oss")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);

        try {
            //这里将文件名称用uuid代替
            //获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString()+suffix;
            String url = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.ALIOSS_ERROR);
    }
}
