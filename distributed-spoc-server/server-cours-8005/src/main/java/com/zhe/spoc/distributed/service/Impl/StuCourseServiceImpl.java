package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseInteractionEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.CoursMapper;
import com.zhe.spoc.distributed.mapper.StuCourseMapper;
import com.zhe.spoc.distributed.service.StuCourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuCourseServiceImpl
 * @Description TODO
 * @date 2022/10/25 10:34
 * @Version 1.0
 */

@Service
@Slf4j
public class StuCourseServiceImpl extends ServiceImpl<StuCourseMapper, CoursEntity> implements StuCourseService {

    @Autowired
    private StuCourseMapper stuCourseMapper;

    @Autowired
    private CoursMapper coursMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Override
    public List<CoursEntity> courseList(Integer stuId) {
        return stuCourseMapper.couseList(stuId);
    }

    @Override
    public Map<String, Object> MyCourseList() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CoursEntity> coursEntities = courseList(user.getId());
        if (CollectionUtils.isEmpty(coursEntities)) {
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);
        }
        map.put("courseEntity", coursEntities);
        return map;
    }

    @Override
    public List<CoursEntity> couseList_1(Long classId) {
        return stuCourseMapper.couseList_1(classId);
    }

    // 查看班级所需学课程
    @Override
    public Map<String, Object> MycouseList_1(Long classId) {
        Map<String, Object> map = new HashMap<>();
        List<CoursEntity> coursEntities = couseList_1(classId);
        if (CollectionUtils.isEmpty(coursEntities)) {
            return null;
        }
        for (CoursEntity coursEntity : coursEntities) {
            Integer teaId = coursEntity.getTeaId();
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
            coursEntity.setUser(teacherInfo);
        }
        map.put("courseEntities", coursEntities);
        return map;
    }

    // 浏览可选课程
    @Override
    public List<CoursEntity> OptionalCourses() {
        List<CoursEntity> courseEntities = stuCourseMapper.OptionalCourses();
        if (CollectionUtils.isEmpty(courseEntities)) {
            return null;
        }
        for (CoursEntity coursEntity: courseEntities) {
            List<CourseInteractionEntity> recommend = Recommend(coursEntity.getCouId());
            List<CourseInteractionEntity> collect = Collect(coursEntity.getCouId());
            long count = recommend.stream()
                    .map(CourseInteractionEntity::getRecommend)
                    .count(); // 点赞数
            long count1 = collect.stream()
                    .map(CourseInteractionEntity::getCollect)
                    .count(); // 购买数
            coursEntity.setCouId(coursEntity.getCouId());
            coursEntity.setCouRecoNum((int) count);
            coursEntity.setCouCollNum((int) count1);
            coursMapper.updateCouRecoNum(coursEntity);
        }
        return courseEntities;
    }

    // 多条件模糊搜索
    @Override
    public List<CoursEntity> findByCourse(CoursEntity coursEntity) {
        List<CoursEntity> byCourse = stuCourseMapper.findByCourse(coursEntity);
        if (CollectionUtils.isEmpty(byCourse)) {
            return null;
        }
        return byCourse;
    }

    // 查看课程细节
    @Override
    public CoursEntity findByOneCourse(Long couId) {
        return stuCourseMapper.findByOneCourse(couId);
    }

    // 获取点赞数
    @Override
    public List<CourseInteractionEntity> Recommend(Long couId) {
        return stuCourseMapper.Recommend(couId);
    }

    @Override
    public List<CourseInteractionEntity> Collect(Long couId) {
        return stuCourseMapper.Collect(couId);
    }


}
