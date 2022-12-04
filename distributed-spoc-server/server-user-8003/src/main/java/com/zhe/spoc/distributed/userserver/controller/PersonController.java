package com.zhe.spoc.distributed.userserver.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.RoleEntity;
import com.zhe.common.entity.TeacherEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.userserver.service.RoleService;
import com.zhe.spoc.distributed.userserver.service.StudentService;
import com.zhe.spoc.distributed.userserver.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName PersonController
 * @Description TODO
 * @date 2022/10/12 20:42
 * @Version 1.0
 */

@RestController
public class PersonController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;


    @GetMapping("/getPerson")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    public CommonResult<Map<String, Object>> getRole(){
        Map<String, Object> persons = roleService.getPerson();
        return CommonResult.success(persons);
    }

    @GetMapping("/getRole/{userId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public RoleEntity getRole(@PathVariable("userId") Integer useId){
        return roleService.getRole(useId);
    }

    @PostMapping("/AddPerson")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> register(@RequestBody UserEntity user){
        boolean register = roleService.register(user);
        return CommonResult.success(register);
    }

    // 批量注册
    @PostMapping("/AddMorePerson")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> AddMorePerson(@RequestBody List<UserEntity> user){
        Boolean aBoolean = userService.InsertMoreUser(user);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED,"注册失败");
        }
        return CommonResult.success(true, "注册成功!");
    }

    /**
     * 自动注册多个学生
     * @return
     */
    @PostMapping("/AddMorePersonAuto")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> AddMorePersonAuto(@RequestParam Integer num){
        return userService.AddMorePersonAuto(num);
    }

    @GetMapping("/getTeacherInfo/{teaId}")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public UserEntity getTeacherInfo(@PathVariable("teaId") Integer teaId){
        return userService.findById(teaId);
    }


    @GetMapping("/getStudentInfoId/{stu_id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<UserEntity> getStudentInfo(@PathVariable("stu_id") Integer stu_id){
        UserEntity stuIdInfo = userService.findByStuId(stu_id);
        if (stuIdInfo == null){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "无此学生信息,请查看输入学生id是否正确!");
        }
        return CommonResult.success(stuIdInfo);
    }

    @GetMapping("/getStudentInfoName/{stu_name}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<UserEntity>> getStudentInfoName(@PathVariable("stu_name") String stu_name){
        List<UserEntity> stuIdInfo = userService.findByStuName(stu_name);
        if (stuIdInfo == null){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "无此学生信息,请查看输入学生姓名是否正确!");
        }
        return CommonResult.success(stuIdInfo);
    }

    @GetMapping("/getUserIdByName/{fullname}")
    @PreAuthorize("hasAuthority('teacher')")
    public UserEntity getUserIdByName(@PathVariable("fullname") String fullname) {
        return userService.userId(fullname);
    }

    @GetMapping("/getTrueName/{userId}")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    public UserDto getTrueName(@PathVariable("userId") Integer userId) {
        return userService.getTrueName(userId);
    }

    @PostMapping("/uploadPhoto")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    public CommonResult<Boolean> uploadPhoto(@RequestParam String photo) {
        return userService.uploadPhoto(photo);
    }

    @GetMapping("/getAllTeacher")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> getAllTeacher(){
        return userService.getAllTeacher();
    }
}
