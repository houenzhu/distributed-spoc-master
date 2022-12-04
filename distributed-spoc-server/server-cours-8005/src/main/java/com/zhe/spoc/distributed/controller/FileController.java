package com.zhe.spoc.distributed.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhe.common.entity.FileEntity;
import com.zhe.spoc.distributed.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName FileController
 * @Description TODO
 * @date 2022/11/8 22:12
 * @Version 1.0
 */

@RestController
@RequestMapping("/file")
public class FileController {

    private static final String FileNameManual = "template_images";
    @Autowired
    private FileMapper fileMapper;
    /**
     * 文件上传接口
     * @param file 前端传递过来的文件
     * @return
     * @throws IOException
     */
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();//获取文件原始名字
        String type = FileNameUtil.extName(originalFilename);//获取文件类型
        long size = file.getSize();//获取文件大小
        //先存储到磁盘
//        File uploadParentFile = new File(fileUploadPath);

        //定义一个文件唯一的标识
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUid =  uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUUid);
        File parentFile = uploadFile.getParentFile();
        //判断配置文件目录是否存在，不存在就创建一个文件目录
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }

        String md5;
        String url;
        //上传文件到磁盘
        file.transferTo(uploadFile);
        //获取文件md5
        md5 = SecureUtil.md5(uploadFile);
        //从数据库查询是否存在相同的记录
        FileEntity dbFiles = getFileMd5(md5);
        //获取文件的URL
        if(dbFiles != null){
            url = dbFiles.getUrl();
            //由于文件已存在，所以删除刚刚上传的重复文件
            uploadFile.delete();
        }else {
            //若数据库不存在重复文件，则不删除刚刚上传的文件
            url = "http://localhost:10010/api/cours/file/" + fileUUid;
        }

        //然后存储到数据库
        FileEntity saveFile = new FileEntity();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);
        return url;
    }

    /**
     * 文件下载接口   http://localhost:10010/api/cours/file/{fileUUID}
     * @param fileUUid
     * @param response
     * @throws IOException
     */
    @GetMapping("/{fileUUid}")
    public void download(@PathVariable String fileUUid, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUid);

        //设置文件的输出格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUid, "UTF-8"));
        response.setContentType("application/octet-stream");

        //读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }


    /***
     * 通过文件的md5查询文件
     * @param md5
     * @return
     */
    private FileEntity getFileMd5(String md5){
        //查找文件的唯一md5是否存在
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<FileEntity> filesList = fileMapper.selectList(queryWrapper);//可能有多条记录，但是名字不一样
        return filesList.size() == 0 ? null : filesList.get(0);
    }
}
