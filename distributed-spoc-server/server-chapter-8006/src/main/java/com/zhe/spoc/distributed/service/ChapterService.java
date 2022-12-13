package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.ChapterEntity;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseChapterAppraiseEntity;
import com.zhe.common.entity.LearningProgressEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName ChapterService
 * @Description TODO
 * @date 2022/10/23 14:27
 * @Version 1.0
 */

public interface ChapterService extends IService<ChapterEntity> {
    Boolean insertChapter(ChapterEntity chapterEntity);

    Boolean RemoveChapter(Long chaId);

    List<ChapterEntity> chapterList();

    Boolean UpdateChapter(ChapterEntity chapterEntity);

    Boolean insertChapterComment(CourseChapterAppraiseEntity chapterAppraise); // 评论章节

//    String StudentPercent(Integer stuTime, Integer TotalDuration); // 学习时长

    Map<String, Object> AllStudentPercent(LearningProgressEntity progressEntity);

    List<LearningProgressEntity> studentProgess(LearningProgressEntity progressEntity);

    Boolean updateChapterOrder(List<ChapterEntity> chapterEntities);

    CommonResult<List<ChapterEntity>> CourseChapter(Long courseId);

    CommonResult<List<ChapterEntity>> ChapterByName(ChapterEntity chapterEntity);

    void reorder(Map<String, Object> params,Long courseId);

    CommonResult<?> StudentCoursePercent(Integer stuId);

    CommonResult<?> getVideo(Long chaId);
}
