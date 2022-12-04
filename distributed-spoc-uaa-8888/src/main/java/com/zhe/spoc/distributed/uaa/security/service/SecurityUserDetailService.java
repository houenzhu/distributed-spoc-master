package com.zhe.spoc.distributed.uaa.security.service;


import com.alibaba.fastjson.JSON;
import com.zhe.common.entity.PermissionEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.uaa.service.PermissionService;
import com.zhe.spoc.distributed.uaa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: houenzhu
 */
@Service
@Slf4j
public class SecurityUserDetailService implements UserDetailsService {


    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) {

        UserEntity user = userService.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        //获取权限
//        List<com.zhe.spoc.distributed.uaa.entity.PermissionEntry> permis = permissionService.getPermissionsByUserId(user.getId());
        List<PermissionEntity> permissions = permissionService.getPermissionsByUserId(user.getId());
        List<String> codes = permissions.stream().map(PermissionEntity::getCode).collect(Collectors.toList());
        String[] authorities = null;
        if (CollectionUtils.isNotEmpty(codes)) {
            authorities = new String[codes.size()];
            codes.toArray(authorities);
        }
        redisTemplate.opsForValue().set("codes", codes);
        //身份令牌
        String principal = JSON.toJSONString(user);
        return User.withUsername(principal).password(user.getPassword()).authorities(authorities).build();
    }
}
