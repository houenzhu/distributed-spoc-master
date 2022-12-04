package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.CourseAppraiseEntity;

/**
 * @author HouEnZhu
 * @ClassName CourseCommentService
 * @Description TODO
 * @date 2022/10/26 19:46
 * @Version 1.0
 */

public interface CourseCommentService extends IService<CourseAppraiseEntity> {
    Boolean CommentCourse(CourseAppraiseEntity courseAppraiseEntity);

    CommonResult<?> theCourseComment(Long courseId);
}
