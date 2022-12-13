package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChapterMapper extends BaseMapper<ChapterEntity> {
    Boolean insertChapter(ChapterEntity chapterEntity);

    Boolean RemoveChapter(@Param("chaId") Long chaId);

    List<ChapterEntity> chapterList();

    Boolean UpdateChapter(ChapterEntity chapterEntity);

    Boolean insertChapterComment(CourseChapterAppraiseEntity chapterAppraise);

    Double StudentPercent(@Param("stuTime") Integer stuTime, @Param("TotalDuration")Integer TotalDuration);

    List<LearningProgressEntity> studentProgess(LearningProgressEntity progressEntity);

    Integer chaTime(@Param("chaId") Long chaId);

    List<StuClassEntity> selectAllByClassId(LearningProgressEntity learningProgressEntity);

    List<StudentEntity> selectAllStudent(@Param("classId") Long classId);

    Boolean updateChapterOrder(List<ChapterEntity> chapterEntities);
    List<ChapterEntity> CourseChapter(@Param("course_id") Long courseId);

    List<ChapterEntity> ChapterByName(ChapterEntity chapterEntity);

    void reorderClassify(@Param("from") int from, @Param("to") int to, @Param("value") int value, @Param("courseId") Long courseId);

    void reorderClassify_1(@Param("from") int from, @Param("to") int to, @Param("value") int value);

    List<ChapterEntity> getLastChapter(@Param("course_id") Long courseId);

    /**
     * 旧排序的章节序号
     * @param chaIndex
     * @param courseId
     * @return
     */
    ChapterEntity getChapterByChaIndex(@Param("chaIndex") Integer chaIndex, @Param("courseId") Long courseId);

    /**
     * 获取该课程所有章节的总时长
     * @param courseId
     * @return
     */
    Integer totalTime(@Param("courseId") Long courseId);

    /**
     * 获取该课程章节序号的数组
     * @param courseId
     * @return
     */
    int[] chaIndexArr(@Param("courseId") Long courseId);

    /**
     * 获取该学生选的全部课程
     * @param stuId
     * @return
     */
    List<StuCourseEntity> AllCourse(@Param("stuId") Integer stuId);

    /**
     * 获取该课程的全部章节时长
     * @param courseId
     * @return
     */
    Integer AllChapterTime(@Param("courseId") Long courseId);

    Integer AllStuChapterTime(@Param("courseId") Long courseId, @Param("stuId") Integer stuId);

    String getVideo(@Param("chaId") Long chaId);
}
