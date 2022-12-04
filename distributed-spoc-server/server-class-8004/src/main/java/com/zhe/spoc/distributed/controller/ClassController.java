package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.ClassEntity;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.StuClassEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.service.ClassService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName ClassController
 * @Description TODO
 * @date 2022/10/14 19:26
 * @Version 1.0
 */

@RestController
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 一个班级的信息
     * @param id
     * @return
     */
    @GetMapping("/GetOneClass/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<ClassEntity> GetOneClass(@PathVariable("id") Integer id){
        ClassEntity oneClass = classService.getOneClass(id);
        return CommonResult.success(oneClass);
    }

    /**
     * 所有班级的信息
     * @return
     */
    @GetMapping("/GetAllClass")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<ClassEntity>> GetAllClass(){
        List<ClassEntity> allClass = classService.getAllClass();
        return CommonResult.success(allClass);
    }

    /**
     * 所有班级的信息(分页管理)map
     * @return
     */
    @GetMapping("/GetAllClassPage")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Map<String, Object>> GetAllClassPage(@RequestParam Integer pageNum,
                                                             @RequestParam Integer pageSize){
        Map<String, Object> allClassPage = classService.getAllClassPage(pageNum, pageSize);
        if (allClassPage == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "该班级暂无课程信息");
        }
        return CommonResult.success(allClassPage);
    }

    // 创建班级
    @PostMapping("/InsertClass")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> InsertClass(@RequestBody ClassEntity classEntity){
        Boolean insertClass = classService.insertClass(classEntity);
        return CommonResult.success(insertClass);
    }


    // 查看班级学生名单(ID查询)
    @GetMapping("/check_stu_class/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<UserEntity>> student_list(@PathVariable("classId") Long classId){
        List<UserEntity> userEntities = classService.stuLists(classId);
        if (CollectionUtils.isEmpty(userEntities)){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "没有该班级或该班级暂无学生信息!");
        }
        return CommonResult.success(userEntities);
    }

    // 查看班级学生名单(班级名称(模糊查询))
    @GetMapping("/check_stu_class_1/{className}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<UserEntity>> student_list_1(@PathVariable("className") String className){
        List<UserEntity> userEntities = classService.stuLists_1(className);
        if (CollectionUtils.isEmpty(userEntities)){
            return CommonResult.failed(ResultCode.VALIDATE_FAILED,"没有该班级或该班级暂无学生信息!");
        }
        return CommonResult.success(userEntities);
    }

    // 查看自己教学的班级
    @GetMapping("/classByMyId/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public List<ClassEntity> classByMyId(@PathVariable("id") Integer id){
        return classService.classList(id);
    }


    @GetMapping("/getMyClass")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Map<String, Object>> getMyClass(){
        Map<String, Object> myClass = classService.getMyClass();
        if (MapUtils.isEmpty(myClass)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED,"暂无班级信息");
        }
        return CommonResult.success(myClass);
    }

    //添加班级学生
    @PostMapping("/insertOneStu")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> insertOneStu(@RequestBody StuClassEntity stuClassEntity){
        Boolean aBoolean = classService.InsertOneStu(stuClassEntity);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "添加失败");
        }
        return CommonResult.success(true, "添加成功!");
    }

    //批量班级学生
    @PostMapping("/insertMoreStu")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> insertMoreStu(@RequestBody List<StuClassEntity> stuClassEntityList){
        Boolean aBoolean = classService.InsertMoreStu(stuClassEntityList);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "添加失败");
        }
        return CommonResult.success(true, "添加成功!");
    }

    //逻辑删除一个学生
    @PostMapping("/RemoveOneStu")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> RemoveOneStu(@RequestParam Integer stuId, @RequestParam Long classId){
        Boolean aBoolean = classService.RemoveOneStu(stuId, classId);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "删除失败");
        }
        return CommonResult.success(true, "删除成功!");
    }

    //逻辑删除该班级的一个课程
    @PostMapping("/RemoveOneCours")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> RemoveOneCours(@RequestParam Long classId, @RequestParam Long couId){
        return classService.RemoveOneCours(classId, couId);
    }

    @PostMapping("/deletedClass/{classId}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> deletedClass(@PathVariable("classId") Long classId) {
        Boolean aBoolean = classService.deleteClass(classId);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "删除失败");
        }
        return CommonResult.success(true, "删除成功!");
    }

    @GetMapping("/TeaClassCourse")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Map<String, Object>> TeaClassCourse(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        Map<String, Object> map = classService.TeaClassCourse(pageNum, pageSize);
        if (map == null) {
            return CommonResult.failed(ResultCode.FAILED, "暂无班级信息");
        }
        return CommonResult.success(map);
    }

    // 修改班级信息
    @PostMapping("/updateClass")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<Boolean> updateClass(@RequestBody ClassEntity classEntity) {
        Boolean aBoolean = classService.updateClass(classEntity);
        if (!aBoolean){
            return CommonResult.failed(ResultCode.FAILED, "修改失败");
        }
        return CommonResult.success(true, "修改成功!");
    }

    @GetMapping("/getTheAllStudent")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getTheAllStudent(@RequestParam("classId") Long classId,
                                            @RequestParam Integer pageNum,
                                            @RequestParam Integer pageSize) {
        return classService.getTheAllStudent(classId, pageNum, pageSize);
    }

    @GetMapping("/getTheAllStudentNum")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getTheAllStudentNum(@RequestParam Long classId) {
        return classService.getTheAllStudentNum(classId);
    }

    @GetMapping("/getClassName")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getClassName(@RequestParam String className,
                                        @RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
        return classService.getClassName(className, pageNum, pageSize);
    }

    @GetMapping("/getClassNameNum")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> getClassNameNum(@RequestParam String className) {
        return classService.getClassNameNum(className);
    }
}
