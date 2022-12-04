package com.zhe.spoc.distributed.uaa.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Author: houenzhu
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {


    @Select("SELECT" +
            " id,code,description,url " +
            " FROM" +
            " t_permission " +
            " WHERE" +
            " id IN (" +
            " SELECT permission_id FROM t_role_permission WHERE role_id IN (" +
            " SELECT role_id FROM t_user_role WHERE user_id = #{userId} ) )")
    List<PermissionEntity> selectPermissionsByUserId(@Param("userId") Integer userId);


}
