package com.zhe.spoc.distributed.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.CoursTypeDTO;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.entity.CouClassEntity;
import com.zhe.common.entity.CouTypeSonEntity;
import com.zhe.common.entity.CoursEntity;

import java.util.List;
import java.util.Map;

public interface CoursService extends IService<CoursEntity> {
    Map<String, Object> coursList(Long classId, Integer pageNum, Integer pageSize);
    List<CoursEntity> coursList(Long classId);

    Boolean insertCoursClass(Long couId, Long classId);

//    List<CoursEntity> likeCours(Integer teaId, String couName, Integer pageNum, Integer pageSize);
    List<CoursEntity> MyCoursByName(String couName, Integer pageNum, Integer pageSize);
    CouClassEntity sameCours(Long couId, Long classId);

    List<CoursEntity> CoursById(Integer id, Long couId);


    List<CoursEntity> coursById(Long couId);

    List<CoursEntity> coursByName(String couName, Integer pageNum, Integer pageSize);
    // 用于计算名称搜索(全部课程)数量
    int CoursByNameNum(String couName);
    List<CoursEntity> GetAllCours();

    CommonResult<?> InsertCours(CoursEntity coursEntity);

    Boolean updateCours(CoursEntity coursEntity);

    Boolean DeleteCours(Long couId);

    CoursEntity coursByIdBuy(Long couId);

    // 遍历大类的元素
    List<CoursTypeDTO> parentType();

    // 通过大类搜索小类
    List<CouTypeSonEntity> childType(Integer couParentType);

    List<CoursEntity> TeaCourse(Integer begin, Integer count);

    Map<String, Object> getAllCourseInPage(Integer begin, Integer count);

    Map<String, Object> myCoursInPage(Integer pageNum, Integer pageSize);
//    Map<String, Object> SeachCourseById(Long couId);

    // 计算本教师的课程数量
    int MyCourseCount(Integer teaId);

    int CoursByIdNum(Long couId);

    int MyCourseCountByNameNum(String couName);

    List<CoursEntity> getMyCourseById(Long couId);

    CommonResult<Integer> GetAllCoursByIdNum(Long courId);

    CommonResult<Integer> GetMyCoursByIdNum(Long couId);

    CommonResult<Integer> GetAllCoursNum();

    CommonResult<?> getAllCourse(Long classId);

    CommonResult<?> getAllCourseByType(Integer couType);

    CommonResult<?> InsertMoreCoursClass(List<CouClassEntity> coursEntities);

    CommonResult<?> CoursMap(Long classId, Integer pageNum, Integer pageSize);

    CommonResult<?> getAllCoursMapNum(Long classId);

    CourseDTO getCourseDto(Long couId);

    CourseDTO getTheStudentCourse();

    CommonResult<?> getAllCourseByPractice();
}
