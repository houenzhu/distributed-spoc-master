package com.zhe.spoc.distributed.userserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.entity.RoleEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleService extends IService<RoleEntity> {
    List<RoleEntity> roleLists(Integer user_id);

    Map<String, Object> getPerson();

    boolean register(UserEntity user);

    RoleEntity getRole(Integer userId);

}
