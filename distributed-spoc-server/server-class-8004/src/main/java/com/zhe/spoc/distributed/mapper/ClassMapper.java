package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ClassMapper extends BaseMapper<ClassEntity> {

    @Select("SELECT * FROM class where tea_id = #{id}")
    ClassEntity getOneClass(@Param("id") Integer id);

    @Select("SELECT * FROM class where deleted = 0")
    List<ClassEntity> getAllClass();

    @Select("SELECT * FROM class, t_user where tea_id = id LIMIT #{begin}, #{count} ")
    List<ClassEntity> getAllClassInPage(@Param("begin") Integer begin, @Param("count") Integer count);

    List<ClassEntity> getAllClassInPage_1(@Param("begin") Integer begin, @Param("count") Integer count);

    Boolean insertClass(ClassEntity classEntity);

    @Select("SELECT * FROM t_user where id IN" +
            "(SELECT stu_id from student WHERE stu_id IN" +
            "(SELECT stu_id from stu_class where class_id = #{classId} AND deleted = 0))")
    List<UserEntity> stuLists(@Param("classId") Long classId);

    @Select("SELECT * FROM t_user where id IN" +
            "(SELECT stu_id from student WHERE stu_id IN" +
            "(SELECT stu_id from stu_class where class_id in" +
            "(SELECT class_id FROM class where class_name LIKE '%${className}%') AND deleted = 0))")
    List<UserEntity> stuLists_1(@Param("className") String className);

    @Select("SELECT * FROM class where tea_id in (SELECT id from t_user where id = #{id})")
    List<ClassEntity> classList(@Param("id") Integer id);

//    @Insert("INSERT INTO stu_class (stu_id, class_id) VALUES (#{stuId}, #{classId})")
    Boolean InsertOneStu(StuClassEntity stuClassEntity);

    Boolean InsertMoreStu(List<StuClassEntity> stuClassEntityList);
    @Update("UPDATE stu_class set deleted = 1 where stu_id = #{stuId} AND class_id = #{classId}")
    Boolean RemoveOneStu(@Param("stuId")Integer stuId, @Param("classId") Long classId);

    Boolean RemoveOneCourse(@Param("classId") Long classId, @Param("couId") Long couId);

//    @Update("UPDATE class set class_student_num = #{classStudentNum} where class_id = #{classId} ")
    Boolean StuNum(@Param("classStudentNum") int classStudentNum, @Param("classId") Long classId);

    Boolean updateCouRecoNum(CoursEntity coursEntity);

    Boolean deleteClass(@Param("classId") Long classId);

    List<ClassEntity> TeaClass(@Param("teaId") Integer teaId, @Param("begin") Integer begin, @Param("count") Integer count);

    Boolean updateClass(ClassEntity classEntity);

    /**
     * 获取该班所有学生
     * @param classId
     * @return
     */
    List<StudentEntity> TheClassAllStudent(@Param("classId") Long classId,
                                           @Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize);

    List<ClassEntity> getClassName(@Param("className") String className,
                                   @Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize);

    int getClassNameNum(@Param("className") String className);

    int getTheAllStudentNum(@Param("classId") Long classId);
}
