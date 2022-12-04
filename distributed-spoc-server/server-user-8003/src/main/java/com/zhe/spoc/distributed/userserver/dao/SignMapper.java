package com.zhe.spoc.distributed.userserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.SignEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignMapper extends BaseMapper<SignEntity> {
    boolean studentSign(SignEntity signEntity);

    int AllCount(@Param("userId") Integer userId);
}
