package com.zhe.spoc.distributed.userserver.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.dto.UserDto;
import com.zhe.common.dto.UserDto_1;
import com.zhe.common.entity.RoleEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.userserver.dao.RoleDao;
import com.zhe.spoc.distributed.userserver.dao.StudentDao;
import com.zhe.spoc.distributed.userserver.service.RoleService;
import com.zhe.spoc.distributed.userserver.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName RoleServiceImpl
 * @Description TODO
 * @date 2022/10/12 20:41
 * @Version 1.0
 */

@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleDao, RoleEntity> implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<RoleEntity> roleLists(Integer user_id) {
        return roleDao.roleLists(user_id);
    }

    /**
     * 登陆后获取用户信息
     * @return
     */
    public Map<String, Object> getPerson(){
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = user.getId();
        UserEntity teaInfo = studentDao.getStuUserInfo(id);
        RoleEntity role = getRole(id);
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        map.put("userInfo", BeanUtil.copyProperties(teaInfo, UserDto_1.class));
        map.put("role", role);
        map.put("authorities", authorities);
        return map;
    }

    @Override
    public boolean register(UserEntity user) {
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return roleDao.register(user);
    }

    //获取当前用户的角色
    @Override
    public RoleEntity getRole(Integer userId) {
        return roleDao.getRole(userId);
    }


}
