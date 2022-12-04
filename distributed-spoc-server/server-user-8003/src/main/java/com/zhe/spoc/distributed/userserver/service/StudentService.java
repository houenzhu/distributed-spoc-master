package com.zhe.spoc.distributed.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudentService extends IService<UserEntity> {

    // 该班级的学生名单
    List<UserEntity> studentList(Long classId);

    StudentEntity StudentInfo(Integer stuId);

    Map<String, Object> StudentInfo();

    CommonResult<Boolean> sign();

    CommonResult<Integer> signCount();

    UserEntity getMyUserInfo(Integer stuId);

    CommonResult<?> getAllStu();

    CommonResult<?> AllCount();
}
