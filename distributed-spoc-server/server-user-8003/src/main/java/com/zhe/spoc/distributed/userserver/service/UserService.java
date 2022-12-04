package com.zhe.spoc.distributed.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.TeacherEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService extends IService<UserEntity> {
    List<Integer> getTeacherId();

    UserEntity findById(Integer teaId);

    UserEntity findByStuId(Integer stu_id);

    List<UserEntity> findByStuName(String stu_name);

    UserEntity userId(String fullname);

    Boolean InsertMoreUser(List<UserEntity> user);

    UserDto getTrueName(Integer userId);

    CommonResult<Boolean> uploadPhoto(String photo);

    CommonResult<?> getAllTeacher();

    CommonResult<Boolean> AddMorePersonAuto(Integer num);
}
