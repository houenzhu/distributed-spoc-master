package com.zhe.spoc.distributed.client;

import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.RoleEntity;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient("user-server")
public interface ServerUserClient {

    @GetMapping("/getTeacherInfo/{teaId}")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    UserEntity getTeacherInfo(@PathVariable("teaId") Integer teaId);

    @GetMapping("/getClassStu/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    List<UserEntity> getClassStu(@PathVariable("classId") Long classId);

    @GetMapping("/getUserIdByName/{fullname}")
    @PreAuthorize("hasAuthority('teacher')")
    UserEntity getUserIdByName(@PathVariable("fullname") String fullname);

    @GetMapping("/getRole/{userId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    RoleEntity getRole(@PathVariable("userId") Integer useId);

    @GetMapping("/getTrueName/{userId}")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    UserDto getTrueName(@PathVariable("userId") Integer userId);


    /**
     * 获取学生用户信息
     * @param stuId
     * @return
     */
    @GetMapping("/getMyUserInfo/{stuId}")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    UserEntity getMyUserInfo(@PathVariable("stuId") Integer stuId);


    @GetMapping("/getMyInfoFeign/{stuId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    StudentEntity getMyInfoFeign(@PathVariable("stuId") Integer stuId);
}
