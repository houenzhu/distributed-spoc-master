package com.zhe.spoc.distributed.userserver.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.userserver.service.StudentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StudentController
 * @Description TODO
 * @date 2022/10/16 15:24
 * @Version 1.0
 */

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;


    @GetMapping("/getClassStu/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public List<UserEntity> getClassStu(@PathVariable("classId") Long classId) {
        List<UserEntity> userEntities = studentService.studentList(classId);
        if (CollectionUtils.isEmpty(userEntities)){
            return null;
        }
        return userEntities;
    }

    // 获取全部学生名单
    @GetMapping("/getAllStu")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getAllStu(){
       return studentService.getAllStu();
    }

    // 学生获取自己信息
    @GetMapping("/getMyInfo")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> getMyInfo() {
        Map<String, Object> map = studentService.StudentInfo();
        return CommonResult.success(map);
    }

    // 学生获取自己信息
    @GetMapping("/getMyInfoFeign/{stuId}")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    public StudentEntity getMyInfoFeign(@PathVariable("stuId") Integer stuId) {
        return studentService.StudentInfo(stuId);
    }

    /**
     * 获取学生用户信息
     * @param stuId
     * @return
     */
    @GetMapping("/getMyUserInfo/{stuId}")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    public UserEntity getMyUserInfo(@PathVariable("stuId") Integer stuId){
        return studentService.getMyUserInfo(stuId);
    }

    // 签到功能
    @PostMapping("/sign")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> Sign() {
        return studentService.sign();
    }

    // 累计签到功能
    @GetMapping("/sign/count")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Integer> SignCount() {
        return studentService.signCount();
    }

    /**
     * 累计签到
     * @return
     */
    @GetMapping("/sign/AllCount")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> AllCount() {
        return studentService.AllCount();
    }
}
