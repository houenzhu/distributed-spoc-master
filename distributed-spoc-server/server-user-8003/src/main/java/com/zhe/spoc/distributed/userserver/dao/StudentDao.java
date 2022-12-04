package com.zhe.spoc.distributed.userserver.dao;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.StuClassEntity;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentDao extends BaseMapper<UserEntity> {
    @Select("SELECT * FROM t_user where id in " +
            "(SELECT stu_id from stu_class where stu_id in " +
            "(SELECT stu_id from stu_class where class_id = #{classId}) AND deleted = 0)")
    List<UserEntity> studentList(@Param("classId") Long classId);

    List<Integer> StudentClass();
    List<Integer> getAllStudent(@Param("stuId") List<Integer> stuId);
    StudentEntity StudentInfo(@Param("stuId") Integer stuId);

    Boolean updateScore(@Param("score") Double Score,@Param("stuId") Integer stuId);

    UserEntity getStuUserInfo(@Param("stuId") Integer stuId);

    boolean insertStudentSedule(@Param("studentList") List<UserEntity> studentList);
}
