package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.dto.CoursTypeDTO;
import com.zhe.common.dto.CoursTypeListDTO;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CoursMapper extends BaseMapper<CoursEntity> {

    List<CoursEntity> coursList(@Param("classId") Long classId, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    int getAllCoursMapNum(@Param("classId") Long classId);

    List<CoursEntity> coursList_1(@Param("classId") Long classId);
    @Insert("INSERT INTO cou_class (cou_id, class_id) VALUES(#{couId}, #{classId})")
    Boolean insertCoursClass(@Param("couId") Long couId, @Param("classId") Long classId);

    List<CoursEntity> myCours(@Param("id") Integer id, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    List<CoursEntity> myCourseClass(@Param("classId") Long classId, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    List<CoursEntity> likeCours(@Param("teaId")Integer teaId, @Param("couName") String couName,
                                @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    @Select("SELECT * from cou_class where cou_id = #{couId} AND class_id = #{classId}")
    CouClassEntity sameCours(@Param("couId") Long couId, @Param("classId") Long classId);

    @Select("SELECT * FROM cours where tea_id IN (SELECT id FROM t_user where id = #{teaId}) AND cou_id = #{couId} AND deleted = 0")
    List<CoursEntity> CoursById(@Param("teaId")Integer teaId, @Param("couId") Long couId);


    @Select("SELECT * FROM cours where deleted = 0")
    List<CoursEntity> GetAllCours();
    @Select("SELECT * FROM cours where cou_id = #{couId} AND deleted = 0")
    List<CoursEntity> coursById(@Param("couId") Long couId);

    int CoursByIdNum(@Param("couId") Long couId);
    // 单个课程信息
    @Select("SELECT * FROM cours where cou_id = #{couId} AND deleted = 0")
    CoursEntity coursByIdBuy(@Param("couId") Long couId);

    List<CoursEntity> coursByName(@Param("couName") String couName,
                                  @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    int CoursByNameNum(@Param("couName") String couName);
    Boolean InsertCours(CoursEntity coursEntity);

    Boolean updateCours(CoursEntity coursEntity);

    Boolean DeleteCours(@Param("couId") Long couId);

    Boolean updateCouRecoNum(CoursEntity coursEntity);

    // 遍历大类的元素
    List<CoursTypeDTO> parentType();


    // 通过大类搜索小类
    List<CouTypeSonEntity> childType(@Param("couParentType") Integer couParentType);

    CoursTypeEntity coursType(@Param("couParentType") Integer couParentType);

    CouTypeSonEntity couTypeSon(@Param("couChileType") Integer couChileType);
    List<CoursEntity> TeaCourse(@Param("begin") Integer begin, @Param("count") Integer count);

    int MyCourseCount(@Param("teaId") Integer teaId);
    int MyCourseCountByNameNum(@Param("teaId") Integer teaId, @Param("couName") String couName);
    int GetAllCoursByIdNum(@Param("couId") Long couId);
    int GetMyCoursByIdNum(@Param("couId") Long couId, @Param("teaId") Integer teaId);
    int GetAllCoursNum();
    List<CoursTypeListDTO> CourseTypeToList(@Param("couId") Long couId);
    int GetAllChapterNum(@Param("couId") Long couId);
    boolean updateCouCataNum(@Param("couId") Long couId, @Param("couCataNum") int couCataNum);

    List<CourseDTO> getAllCourse(@Param("couId") List<Long> couId);

    List<CoursEntity> getAllCourseByType(@Param("couType") Integer couType);

    /**
     * 批量新增课程到该班级
     * @param coursEntities
     * @return
     */
    boolean InsertMoreCoursClass(List<CouClassEntity> coursEntities);

    CourseDTO getCourseDTO(@Param("couId") Long couId);

    List<Long> getClassAllCourse(@Param("classId") Long classId);

    List<CourseDTO> getAllCourseByPractice();
}
