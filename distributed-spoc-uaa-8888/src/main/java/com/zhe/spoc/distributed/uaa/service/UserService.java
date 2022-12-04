package com.zhe.spoc.distributed.uaa.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.uaa.mapper.UserMapper;
import org.springframework.stereotype.Service;


/**
 * @Description:
 * @Author: houenzhu
 */
@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    public UserEntity getUserByUsername(String username) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }

}
