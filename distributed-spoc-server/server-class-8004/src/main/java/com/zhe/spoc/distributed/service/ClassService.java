package com.zhe.spoc.distributed.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.ClassEntity;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.StuClassEntity;
import com.zhe.common.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ClassService extends IService<ClassEntity> {

    ClassEntity getOneClass(Integer id);

    List<ClassEntity> getAllClass();

    List<ClassEntity> getAllClassInPage(Integer begin, Integer count);

    Boolean insertClass(ClassEntity classEntity);

    List<UserEntity> stuLists(Long classId);

    Map<String, Object> getAllClassPage(Integer begin, Integer count);
    List<UserEntity> stuLists_1(String className);

    List<ClassEntity> classList(Integer id);

    Map<String, Object> getMyClass();

    Boolean InsertOneStu(StuClassEntity stuClassEntity);

    /**
     * 批量添加
     * @param stuClassEntityList
     * @return
     */
    Boolean InsertMoreStu(List<StuClassEntity> stuClassEntityList);

    Boolean RemoveOneStu(Integer stuId, Long classId);

    CommonResult<?> RemoveOneCours(Long classId, Long couId);

    Boolean StuNum(int classStudentNum, Long classId);

    Boolean deleteClass(Long classId);

    List<ClassEntity> getAllClassInPage_1(Integer begin, Integer count);

    List<ClassEntity> TeaClass(Integer teaId, Integer begin, Integer count);

    Map<String, Object> TeaClassCourse(Integer begin, Integer count);

    Boolean updateClass(ClassEntity classEntity);

    CommonResult<?> getTheAllStudent(Long classId, Integer pageNum, Integer pageSize);

    CommonResult<?> getClassName(String className, Integer pageNum, Integer pageSize);

    CommonResult<?> getClassNameNum(String className);

    CommonResult<?> getTheAllStudentNum(Long classId);

}
