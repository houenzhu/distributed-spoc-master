package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.ChapterEntity;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.CourseChapterAppraiseEntity;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.spoc.distributed.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName ChapterController
 * @Description TODO
 * @date 2022/10/23 14:31
 * @Version 1.0
 */

@RestController
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    // 添加章节
    @PostMapping("/insertChapter")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> insertChapter(@RequestBody ChapterEntity chapterEntity) {
        Boolean aBoolean = chapterService.insertChapter(chapterEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "添加失败");
        }
        return CommonResult.success(true, "添加成功!");
    }

    // 删除一个章节
    @PostMapping("/RemoveChapter")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> RemoveChapter(@RequestParam Long chaId) {
        Boolean aBoolean = chapterService.RemoveChapter(chaId);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "该章节已经删除过了");
        }
        return CommonResult.success(true, "删除成功!");
    }

    // 获取全部章节信息
    @GetMapping("/chapterList")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<List<ChapterEntity>> chapterList() {
        List<ChapterEntity> chapterEntities = chapterService.chapterList();
        if (chapterEntities == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无信息");
        }
        return CommonResult.success(chapterEntities);
    }

    // 更新章节的名称和地址
    @PostMapping("/updateChapter")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> updateChapter(@RequestBody ChapterEntity chapterEntity) {
        Boolean aBoolean = chapterService.UpdateChapter(chapterEntity);
        return CommonResult.success(aBoolean, "更新成功!");
    }

    // 章节评论
    @PostMapping("/insertChapterComment")
    @PreAuthorize("hasAuthority('student')")
    public CommonResult<Boolean> insertChapterComment(@RequestBody CourseChapterAppraiseEntity chapterAppraise) {
        Boolean aBoolean = chapterService.insertChapterComment(chapterAppraise);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "评论失败");
        }
        return CommonResult.success(true, "评论成功!");
    }

//    @GetMapping("/StudentPercent/{stuTime}/{TotalDuration}")
//    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
//    public CommonResult<String> StudentPercent(@PathVariable("TotalDuration") Integer TotalDuration,
//                                              @PathVariable("stuTime") Integer  stuTime) {
//        String s = chapterService.StudentPercent(stuTime, TotalDuration);
//        if (s == null) {
//            return CommonResult.failed(ResultCode.FAILED, "章节总时长不能为0!");
//        }
//        return CommonResult.success(s);
//    }

    @GetMapping("/AllStudentPercent")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Map<String, Object>> StudentPercent(@RequestBody LearningProgressEntity progressEntity) {
        Map<String, Object> map = chapterService.AllStudentPercent(progressEntity);
        if (map == null) {
            return CommonResult.failed(ResultCode.FAILED, "暂无班级信息");
        }
        return CommonResult.success(map);
    }

    @GetMapping("/StudentCoursePercent")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> StudentCoursePercent(@RequestParam Integer stuId) {
        return chapterService.StudentCoursePercent(stuId);
    }

    @PostMapping("/updateChapterOrder")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> updateChapterOrder(@RequestBody List<ChapterEntity> chapterEntity) {
        Boolean aBoolean = chapterService.updateChapterOrder(chapterEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "调整失败!");
        }
        return CommonResult.success(true, "调整成功!");
    }

    @GetMapping("/CourseChapter")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<List<ChapterEntity>> CourseChapter(@RequestParam Long courseId) {
        return chapterService.CourseChapter(courseId);
    }

    /**
     * 通过名称查询章节
     * @param chapterEntity
     * @return
     */
    @PostMapping("/ChapterByName")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<ChapterEntity>> ChapterByName(@RequestBody ChapterEntity chapterEntity) {
        return chapterService.ChapterByName(chapterEntity);
    }

    /**
     * 拖拽后的排序
     */
    @PostMapping("/drag/{courseId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Void> drag(@RequestParam Map<String, Object> params, @PathVariable("courseId") Long courseId) {
        chapterService.reorder(params, courseId);
        return CommonResult.success(null,"ok");
    }

    /**
     * 获取该章节的视频地址
     * @param chaId
     * @return
     */
    @GetMapping("/getVideo")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> getVideo(@RequestParam Long chaId) {
        return chapterService.getVideo(chaId);
    }

}
