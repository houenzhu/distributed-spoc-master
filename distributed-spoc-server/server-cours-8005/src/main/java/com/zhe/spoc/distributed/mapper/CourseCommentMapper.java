package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.dto.CourseAppraiseDTO;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.CourseAppraiseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName CourseCommentMapper
 * @Description TODO
 * @date 2022/10/26 19:43
 * @Version 1.0
 */

@Mapper
@Repository
public interface CourseCommentMapper extends BaseMapper<CourseAppraiseEntity> {
    Boolean CommentCourse(CourseAppraiseEntity courseAppraiseEntity);

    List<CourseAppraiseDTO> theCourseComment(@Param("courseId") Long courseId);
}
