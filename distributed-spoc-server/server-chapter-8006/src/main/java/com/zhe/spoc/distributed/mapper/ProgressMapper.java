package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.common.entity.StuCouProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProgressMapper extends BaseMapper<LearningProgressEntity> {
    Integer checkStudent(LearningProgressEntity learningProgress);

    Boolean UpdateStudentProgress(LearningProgressEntity learningProgress);

    Boolean InsertStudentProgress(LearningProgressEntity learningProgress);

    Boolean addScoreStuTime(@Param("stuScore") Double stuScore, @Param("stuStudyTime") Integer stuStudyTime, @Param("stuId") Integer stuId);

    /**
     * 秒转时间
     * @param SecToTime
     * @return
     */
    String sec_to_time(@Param("SecToTime") int SecToTime);

    /**
     * 获取该章节的上次进度
     * @param stuCouProgress
     * @return
     */
    String progressTime(StuCouProgress stuCouProgress);

    /**
     * 获取该班级该课程该学生的全部章节时长
     * @param learningProgress
     * @return
     */
    Integer StudentCoursePercent(LearningProgressEntity learningProgress);

    int ChapterTime(@Param("chaId") Long chaId);

}
