package com.zhe.spoc.distributed.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: houenzhu
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
