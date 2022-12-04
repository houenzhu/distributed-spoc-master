package com.zhe.spoc.distributed.userserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.RoleEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleDao extends BaseMapper<RoleEntity> {
    @Select("SELECT * FROM t_role where id in (SELECT role_id FROM t_user_role WHERE user_id = #{user_id})")
    List<RoleEntity> roleLists(@Param("user_id") Integer user_id);

    @Insert("INSERT INTO t_user (username, password, fullname, phone) VALUES(#{username}, #{password}, #{fullname},#{phone})")
    boolean register(UserEntity user);

    @Select("SELECT * FROM t_role where id IN (SELECT role_id from t_user_role where user_id = #{userId})")
    RoleEntity getRole(@Param("userId") Integer userId);
}
