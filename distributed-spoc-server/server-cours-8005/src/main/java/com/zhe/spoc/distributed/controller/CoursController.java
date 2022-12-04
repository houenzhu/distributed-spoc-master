package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.CoursTypeDTO;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.service.CoursService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName CoursController
 * @Description TODO
 * @date 2022/10/18 16:45
 * @Version 1.0
 */

@RestController
@RequestMapping("/teacher")
public class CoursController {

    @Autowired
    private CoursService coursService;

    // 获取该班级所有课程(用于远程调用)
    @GetMapping("/getAllCours/{classId}")
    @PreAuthorize("hasAuthority('teacher')")
    public List<CoursEntity> getAllCours(@PathVariable("classId") Long classId){
        return coursService.coursList(classId);
    }

    // 获取该班级所有课程 + 教师个人信息
    @GetMapping("/getAllCoursMap/{classId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> getAllCoursMap(@PathVariable("classId") Long classId, @RequestParam Integer pageSize,
                                          @RequestParam Integer pageNum){
        return coursService.CoursMap(classId, pageNum, pageSize);
    }

    // 获取该班级课程数量
    @GetMapping("/getAllCoursMapNum/{classId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> getAllCoursMapNum(@PathVariable("classId") Long classId) {
        return coursService.getAllCoursMapNum(classId);
    }

    // 新增班级课程
    @PostMapping("/InsertCoursClass/{couId}/{classId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> InsertCoursClass(@PathVariable("couId") Long couId, @PathVariable("classId") Long classId) {
        Boolean aBoolean = coursService.insertCoursClass(couId, classId);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "您已经添加过该课程了!");
        }
        return CommonResult.success(true, "添加成功");
    }

    /**
     * 批量添加班级课程
     * @param coursEntities
     * @return
     */
    @PostMapping("/InsertMoreCoursClass")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> InsertMoreCoursClass(@RequestBody List<CouClassEntity> coursEntities) {
        return coursService.InsertMoreCoursClass(coursEntities);

    }

    // 新增课程信息
    @PostMapping("/InsertCours")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> InsertCours(@RequestBody CoursEntity coursEntity) {
        Boolean aBoolean = coursService.InsertCours(coursEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "新增失败");
        }
        return CommonResult.success(true);
    }

    // 修改课程信息
    @PostMapping("/UpdateCours")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> UpdateCours(@RequestBody CoursEntity coursEntity) {
        Boolean aBoolean = coursService.updateCours(coursEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "课程id不能为空");
        }
        return CommonResult.success(true, "修改成功!");
    }

    // 获取所有课程
    @GetMapping("/GetAllCours")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<List<CoursEntity>> GetAllCours(){
        List<CoursEntity> coursEntities = coursService.GetAllCours();
        if (coursEntities == null){
            return CommonResult.failed(ResultCode.FAILED, "暂无课程信息");
        }
        return CommonResult.success(coursEntities);
    }

    // 获取所有课程数量
    @GetMapping("/GetAllCoursNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> GetAllCoursNum(){
        return coursService.GetAllCoursNum();
    }

    // 获取该教师所有课程的数量
    @GetMapping("/MyCourseCount")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> MyCourseCount(){
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int num = coursService.MyCourseCount(user.getId());
        if (num == 0){
            return CommonResult.failed(ResultCode.FAILED, "暂无课程信息");
        }
        return CommonResult.success(num);
    }

    // 获取所有课程(分页)
    @GetMapping("/GetAllCoursInPage")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Map<String, Object>> GetAllCoursInPage(@RequestParam Integer begin,
                                                               @RequestParam Integer count){
        Map<String, Object> allCourseInPage = coursService.getAllCourseInPage(begin, count);
        if (allCourseInPage == null){
            return CommonResult.failed(ResultCode.FAILED, "暂无课程信息");
        }
        return CommonResult.success(allCourseInPage);
    }

    // 获取所有课程(远程调用)
    @GetMapping("/GetAllCoursFeign")
    @PreAuthorize("hasAuthority('teacher')")
    public List<CoursEntity> GetAllCoursFeign(){
        List<CoursEntity> coursEntities = coursService.GetAllCours();
        if (coursEntities == null){
            return null;
        }
        return coursEntities;
    }

    // 查看我创建的所有课程
    @GetMapping("/GetMyCours")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Map<String, Object>> GetMyCours(@RequestParam("pageSize") Integer pageSize,
                                        @RequestParam("pageNum") Integer pageNum){
        Map<String, Object> map = coursService.myCoursInPage(pageNum, pageSize);
        if (map == null) {
            return CommonResult.failed("暂无课程数据");
        }
        return CommonResult.success(map);
    }

    // 通过名称获取我的课程
    @GetMapping("/GetMyCoursName/{couName}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<List<CoursEntity>> GetMyCours(@PathVariable("couName") String couName,
                                                      @RequestParam("pageNum") Integer pageNum,
                                                      @RequestParam("pageSize") Integer pageSize) {
        List<CoursEntity> coursEntities = coursService.MyCoursByName(couName,pageNum, pageSize);
        if (coursEntities == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无此课程!");
        }
        return CommonResult.success(coursEntities, "查询成功!");
    }

    // 通过课程名称查找课程的数量(我的课程)
    @GetMapping("/GetMyCoursNameNum/{courName}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> GetMyCoursNameNum(@PathVariable("courName") String courName){
        int num = coursService.MyCourseCountByNameNum(courName);
        if (num == 0){
            return CommonResult.failed(ResultCode.FAILED, "暂无此课程，请检查课程名称是否正确");
        }
        return CommonResult.success(num);
    }

    // 通过课程ID获取我的课程
    @GetMapping("/GetMyCoursById/{couId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<List<CoursEntity>> GetMyCoursById(@PathVariable("couId") Long couId) {
        List<CoursEntity> myCourseById = coursService.getMyCourseById(couId);
        if (myCourseById == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无此课程!");
        }
        return CommonResult.success(myCourseById, "查询成功!");
    }

    // 通过课程ID获取我的课程的数量
    @GetMapping("/GetMyCoursByIdNum/{couId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> GetMyCoursByIdNum(@PathVariable("couId") Long couId) {
        return coursService.GetMyCoursByIdNum(couId);
    }

    // 通过课程id查找课程
    @GetMapping("/GetAllCoursById/{courId}")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<List<CoursEntity>> GetAllCoursById(@PathVariable("courId") Long courId){
        List<CoursEntity> coursEntities = coursService.coursById(courId);
        if (coursEntities == null){
            return CommonResult.failed(ResultCode.FAILED, "暂无此课程，请检查此ID是否正确");
        }
        return CommonResult.success(coursEntities);
    }

    // 通过课程id查找课程的数量
    @GetMapping("/GetAllCoursByIdNum/{courId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> GetAllCoursByIdNum(@PathVariable("courId") Long courId) {
        return coursService.GetAllCoursByIdNum(courId);
    }

    // 通过课程名称查找课程
    @GetMapping("/GetAllCoursByName/{courName}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<List<CoursEntity>> GetAllCoursByName(@PathVariable("courName") String courName,
                                                             @RequestParam("pageNum") Integer pageNum,
                                                             @RequestParam("pageSize") Integer pageSize){
        List<CoursEntity> coursEntities = coursService.coursByName(courName, pageNum, pageSize);
        if (coursEntities == null){
            return CommonResult.failed(ResultCode.FAILED, "暂无此课程，请检查课程名称是否正确");
        }
        return CommonResult.success(coursEntities);
    }

    // 通过课程名称查找课程的数量(全部课程)
    @GetMapping("/GetAllCoursByNameNum/{courName}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> GetAllCoursByNameNum(@PathVariable("courName") String courName){
        int num = coursService.CoursByNameNum(courName);
        if (num == 0){
            return CommonResult.failed(ResultCode.FAILED, "暂无此课程，请检查课程名称是否正确");
        }
        return CommonResult.success(num);
    }

    @PostMapping("/DeleteCourse/{couId}")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> DeleteCourse(@PathVariable("couId") Long couId) {
        Boolean aBoolean = coursService.DeleteCours(couId);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "删除失败");
        }
        return CommonResult.success(true, "删除成功!");
    }

    // 用于查看价格或者其他的
    @GetMapping("/BuyCourseId/{couId}")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    public CoursEntity BuyCourseId(@PathVariable("couId") Long couId){
        return coursService.coursByIdBuy(couId);
    }

    // 通过大类搜索小类
    @GetMapping("/childType/{couParentType}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<CouTypeSonEntity>> childType(@PathVariable("couParentType") Integer couParentType) {
        List<CouTypeSonEntity> coursEntities = coursService.childType(couParentType);
        if (coursEntities == null){
            return CommonResult.failed(ResultCode.FAILED, "该类暂无课程!");
        }
        return CommonResult.success(coursEntities);
    }

    // 遍历大类元素
    @GetMapping("/parentType")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    public CommonResult<List<CoursTypeDTO>> parentType(){
        List<CoursTypeDTO> coursTypeEntities = coursService.parentType();
        return CommonResult.success(coursTypeEntities);
    }

    /**
     * 获取全部课程(教师给班级选课)
     * @return
     */
    @GetMapping("/getAllCourse")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getAllCourse(@RequestParam("classId") Long classId) {
        return coursService.getAllCourse(classId);
    }

    /**
     * 远程调用CourseDto
     * @param couId
     * @return
     */
    @GetMapping("/getCourseDto/{couId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CourseDTO getCourseDto(@PathVariable("couId") Long couId) {
        return coursService.getCourseDto(couId);
    }

    /**
     * 获取这个同学全部选择的课程
     * @return
     */
    @GetMapping("/getTheStudentCourse")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CourseDTO getTheStudentCourse() {
        return coursService.getTheStudentCourse();
    }

    /**
     * 实践添加用
     * @return
     */
    @GetMapping("/getAllCourseByPractice")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getAllCourseByPractice() {
        return coursService.getAllCourseByPractice();
    }
}
