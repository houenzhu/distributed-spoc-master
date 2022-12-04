package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.TimeUtil;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.mapper.ChapterMapper;
import com.zhe.spoc.distributed.mapper.ProgressMapper;
import com.zhe.spoc.distributed.service.ChapterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HouEnZhu
 * @ClassName ChapterServiceImpl
 * @Description TODO
 * @date 2022/10/23 14:28
 * @Version 1.0
 */

@Service
@Slf4j
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, ChapterEntity> implements ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private ProgressMapper progressMapper;

    @Autowired
    private ServerCoursClient serverCoursClient;

    @Autowired
    private ServerStuClient serverStuClient;

    /**
     * 添加章节
     *
     * @param chapterEntity
     * @return
     */
    @Override
    public Boolean insertChapter(ChapterEntity chapterEntity) {
        List<ChapterEntity> allChapter = chapterMapper.getLastChapter(chapterEntity.getCourseId());
        if (allChapter.size() == 0) {
            chapterEntity.setChaIndex(1);
            return chapterMapper.insertChapter(chapterEntity);
        }
        ChapterEntity chapter = allChapter.get(allChapter.size() - 1);
        Integer chaIndex = chapter.getChaIndex();
        chaIndex += 1;
        chapterEntity.setChaIndex(chaIndex);
        return chapterMapper.insertChapter(chapterEntity);
    }

    /**
     * 删除章节后排序
     * @param chaId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean RemoveChapter(Long chaId) {
        ChapterEntity chapterEntity = query().eq("cha_id", chaId).one();
        Long courseId = chapterEntity.getCourseId();
        Integer chaIndex = chapterEntity.getChaIndex();
        List<ChapterEntity> chapterEntityList = query()
                .eq("course_id", courseId)
                .eq("is_deleted", 0)
                .last("ORDER BY cha_index").list();
        List<Integer> allIndex = chapterEntityList.stream()
                .map(ChapterEntity::getChaIndex)
                .collect(Collectors.toList());
        Integer lastIndex = allIndex.get(allIndex.size() - 1);
        chapterMapper.reorderClassify(chaIndex + 1, lastIndex, -1, courseId);
        return chapterMapper.RemoveChapter(chaId);
    }

    @Override
    public List<ChapterEntity> chapterList() {
        return chapterMapper.chapterList();
    }

    @Override
    public Boolean UpdateChapter(ChapterEntity chapterEntity) {
        return chapterMapper.UpdateChapter(chapterEntity);
    }

    @Override
    public Boolean insertChapterComment(CourseChapterAppraiseEntity chapterAppraise) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        chapterAppraise.setStuId(user.getId());
        return chapterMapper.insertChapterComment(chapterAppraise);
    }

//    @Override
//    public Double StudentPercent(Integer stuTime, Integer TotalDuration) {
//        if (TotalDuration == null) {
//            return null;
//        }
//        return chapterMapper.StudentPercent(stuTime, TotalDuration);
//    }

    // 教师获取本班全部学生的进度(章节的进度)
    @Override
    public Map<String, Object> AllStudentPercent(LearningProgressEntity progressEntity) {
        Map<String, Object> map = new HashMap<>();
        List<StudentEntity> studentEntities = chapterMapper.selectAllStudent(progressEntity.getClassId()); // 拿到该班级所有学生id
        if (CollectionUtils.isEmpty(studentEntities)) {
            return null;
        }
        for (StudentEntity studentEntity : studentEntities) {
            Integer stuId1 = studentEntity.getStuId(); // 获得该班级每个学生的id
            progressEntity.setStuId(stuId1);
            List<LearningProgressEntity> learningProgressEntities = studentProgess(progressEntity);
            if (CollectionUtils.isEmpty(learningProgressEntities)) {
                studentEntity.setStuPercent(0);
            }
            for (LearningProgressEntity learningProgressEntity : learningProgressEntities) {
                Integer TotalDuration = chapterMapper.chaTime(learningProgressEntity.getChaId()); // 获得该章节的总时长
                int time = learningProgressEntity.getTime();
                if (time == 0) {
                    studentEntity.setStuPercent(0);
                    break;
                }
                Double percent = chapterMapper.StudentPercent(time, TotalDuration);// 计算百分比
                studentEntity.setStuPercent(percent); // 把每个学生的学习进度赋值
            }
        }
        map.put("students", studentEntities);
        return map;
    }

    @Override
    public List<LearningProgressEntity> studentProgess(LearningProgressEntity progressEntity) {
        return chapterMapper.studentProgess(progressEntity);
    }

    // 调整章节顺序
    @Override
    public Boolean updateChapterOrder(List<ChapterEntity> chapterEntities) {
        return chapterMapper.updateChapterOrder(chapterEntities);
    }

    @Override
    public CommonResult<List<ChapterEntity>> CourseChapter(Long courseId) {
        List<ChapterEntity> chapterEntities = chapterMapper.CourseChapter(courseId);
        if (CollectionUtils.isEmpty(chapterEntities)) {
            return CommonResult.failed(ResultCode.FAILED);
        }
        return CommonResult.success(chapterEntities);
    }

    /**
     * 通过名称查询章节
     *
     * @param chapterEntity
     * @return
     */
    @Override
    public CommonResult<List<ChapterEntity>> ChapterByName(ChapterEntity chapterEntity) {
        List<ChapterEntity> chapterEntities = chapterMapper.ChapterByName(chapterEntity);
        if (CollectionUtils.isEmpty(chapterEntities)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无章节信息");
        }
        return CommonResult.success(chapterEntities);
    }

    /**
     * 正式版调序
     * @param params
     * @param courseId
     */
    @Override
    public void reorder(Map<String, Object> params, Long courseId) {
        Integer newIndex = Integer.valueOf((String) params.get("newIndex"));
        Integer oldIndex = Integer.valueOf((String) params.get("oldIndex"));
        if (newIndex != null && oldIndex != null) {
            int dragFromOrder =  oldIndex;
            int dragToOrder =  newIndex;
            int from = dragFromOrder > dragToOrder ? dragToOrder : dragFromOrder;
            int to = dragFromOrder < dragToOrder ? dragToOrder : dragFromOrder;
            ChapterEntity chapter = chapterMapper.getChapterByChaIndex(dragFromOrder, courseId);
            chapter.setChaIndex(-1);
            chapterMapper.updateById(chapter);
            if (dragFromOrder < dragToOrder) {
                chapterMapper.reorderClassify(from + 1, to, -1, courseId);
            } else {
                chapterMapper.reorderClassify(from, to - 1, 1, courseId);
            }
            ChapterEntity chapter2 = chapterMapper.getChapterByChaIndex(-1, courseId);
            chapter2.setChaIndex(dragToOrder);
            chapterMapper.updateById(chapter2);
        }
    }

    /**
     * 获取学生所有课程进度
     * @param stuId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> StudentCoursePercent(Integer stuId) {
        // 1. 先获取该学生的全部课程
        List<StuCourseEntity> stuCourseEntities = chapterMapper.AllCourse(stuId);
        List<Long> courseIds = stuCourseEntities.stream()
                .distinct()
                .map(StuCourseEntity::getCouId)
                .collect(Collectors.toList());
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for (Long courseId: courseIds) {
            CourseDTO courseDto = serverCoursClient.getCourseDto(courseId);
            // 先判断该课程是否存在
            if (courseDto == null) {
                continue;
            }
            // 2. 获取该课程的全部章节的时间
            Integer ALLTime = chapterMapper.AllChapterTime(courseId);
            if (ALLTime == null || ALLTime == 0) {
                courseDto.setStuProgress(0);
                courseDTOS.add(courseDto);
                continue;
            }
            // 获取学生观看的时长
            Integer StuTime = chapterMapper.AllStuChapterTime(courseId, stuId);
            if (StuTime == null) {
                courseDto.setStuProgress(0);
                courseDTOS.add(courseDto);
                continue;
            }
            // 3. 总数相除
            Double percent = chapterMapper.StudentPercent(StuTime, ALLTime);
            if (percent > 100) {
                courseDto.setStuProgress(100);
                courseDTOS.add(courseDto);
            }else {
                courseDto.setStuProgress(percent);
                courseDTOS.add(courseDto);
            }
        }

        // 5. 返回
        return CommonResult.success(courseDTOS);
    }

    /**
     * 快速排序
     * @param arr
     * @param low
     * @param high
     */
    void quickSort(int [] arr, int low, int high) {
        int i,j,temp,t;
        if(low>high){
            return;
        }
        i=low;
        j=high;
        //temp就是基准位
        temp = arr[low];

        while (i<j) {
            //先看右边，依次往左递减
            while (temp<=arr[j]&&i<j) {
                j--;
            }
            //再看左边，依次往右递增
            while (temp>=arr[i]&&i<j) {
                i++;
            }
            //如果满足条件则交换
            if (i<j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
            }

        }
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];
        arr[i] = temp;
        //递归调用左半数组
        quickSort(arr, low, j-1);
        //递归调用右半数组
        quickSort(arr, j+1, high);
    }

}
