package com.zhe.spoc.distributed.userserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.TeacherEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.common.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDao extends BaseMapper<UserEntity> {
    @Select("SELECT id from t_user, class where t_user.id = tea_id")
    List<Integer> getTeacherId();

    UserEntity findById(@Param("teaId") Integer teaId);

    @Select("SELECT * FROM t_user where id in (SELECT stu_id FROM student where stu_id = #{stu_id})")
    UserEntity findByStuId(@Param("stu_id") Integer stu_id);

    @Select("SELECT * FROM t_user where id in (SELECT stu_id FROM student where stu_name LIKE '%${stu_name}%')")
    List<UserEntity> findByStuName(@Param("stu_name") String stu_name);

    @Select("SELECT id from t_user where fullname = #{fullname}")
    UserEntity userId(@Param("fullname") String fullname);

    Boolean InsertMoreUser(List<UserEntity> user);

    UserDto getTrueName(@Param("userId") Integer userId);
    Boolean updatePhoto(@Param("photo") String photo, @Param("id") Integer id);

    List<TeacherEntity> allTea();

    List<Integer> getAllStuId();

    List<Integer> getAllUserId(@Param("userId") List<Integer> userId);

    List<UserEntity> getAllUserInfo(@Param("userId") List<Integer> userId);
    /**
     * 批量插入学生身份
     * @param userRoleEntities
     * @return
     */
    boolean insertStu(@Param("userRoleEntities") List<Integer> userRoleEntities);
}
