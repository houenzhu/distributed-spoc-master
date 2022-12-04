package com.zhe.spoc.distributed.uaa.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.PermissionEntity;
import com.zhe.spoc.distributed.uaa.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: houenzhu
 */
@Service
public class PermissionService extends ServiceImpl<PermissionMapper, PermissionEntity> {



    public List<PermissionEntity> getPermissionsByUserId(Integer userId){
        return baseMapper.selectPermissionsByUserId(userId);
    }

}
