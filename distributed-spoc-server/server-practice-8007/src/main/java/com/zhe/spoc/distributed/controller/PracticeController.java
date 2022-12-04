package com.zhe.spoc.distributed.controller;

import com.netflix.client.http.HttpRequest;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.PracticeEntity;
import com.zhe.spoc.distributed.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName PracticeController
 * @Description TODO
 * @date 2022/10/23 16:36
 * @Version 1.0
 */

@RestController
public class PracticeController {

    @Autowired
    private PracticeService practiceService;


    // 创建线下实践
    @PostMapping("/insertPractice")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> insertPractice(@RequestBody PracticeEntity practiceEntity) {
        return practiceService.insertPractice(practiceEntity);
    }

    // 取消线下实践
    @PostMapping("/removePractice")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> removePractice(@RequestParam Long praId) {
        Boolean aBoolean = practiceService.removePractice(praId);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "取消失败");
        }
        return CommonResult.success(true, "取消成功");
    }

    // 我发布的实践
    @GetMapping("/myPractice")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> myPractice(@RequestParam Integer begin,
                                                         @RequestParam Integer count) {
        return practiceService.myPractice(begin, count);
    }

    // 模糊查询我发布的实践
    @GetMapping("/myPracticeByName")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> myPracticeByName(@RequestParam String praName, @RequestParam Integer pageNum,
                                            @RequestParam Integer pageSize) {
        return practiceService.practiceEntityListByName(praName, pageNum, pageSize);

    }

    /**
     * 模糊查询我发布实践的数量
     * @param praName
     * @return
     */
    @GetMapping("/myPracticeByNameNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> myPracticeByNameNum(@RequestParam String praName) {
        return practiceService.myPracticeByNameNum(praName);
    }

    /**
     * 根据id查询我发布的实践数量
     * @param praId
     * @return
     */
    @GetMapping("/myPracticeByIdNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> myPracticeByNameNum(@RequestParam Long praId) {
        return practiceService.myPracticeByIdNum(praId);
    }


    // 根据id查询我发布的实践
    @GetMapping("/myPracticeById")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> myPracticeById(@RequestParam Long praId) {
        return practiceService.practiceEntityById(praId);

    }


    // 模糊查询发布的实践
    @GetMapping("/PracticeByName")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> PracticeByName(@RequestParam String praName, @RequestParam Integer pageNum,
                                            @RequestParam Integer pageSize) {
        return practiceService.PracticeByName(praName, pageNum, pageSize);

    }

    /**
     * 模糊查询发布实践的数量
     * @param praName
     * @return
     */
    @GetMapping("/PracticeByNameNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> PracticeByNameNum(@RequestParam String praName) {
        return practiceService.PracticeByNameNum(praName);
    }

    /**
     * 根据id查询发布的实践数量
     * @param praId
     * @return
     */
    @GetMapping("/PracticeByIdNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> PracticeByIdNum(@RequestParam Long praId) {
        return practiceService.PracticeByIdNum(praId);
    }


    // 根据id查询发布的实践
    @GetMapping("/PracticeById")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> PracticeById(@RequestParam Long praId) {
        return practiceService.PracticeById(praId);

    }


    // 教师查看全部线下实践信息(分页)
    @GetMapping("/AllPracticeInPage")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> AllPracticeInPage(@RequestParam Integer begin,
                                                               @RequestParam Integer count) {
        return practiceService.SelectAllPracticeMapInPage(begin, count);

    }

    @GetMapping("/AllPracticeNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Integer> AllPracticeNum() {
        Integer integer = practiceService.MyPracticeCount();
        if (integer == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无实践课程!");
        }
        return CommonResult.success(integer);
    }

    @GetMapping("/PracticeNum")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> PracticeNum() {
        return practiceService.PracticeNum();
    }

    @PostMapping("/updatePractice")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> updatePractice(@RequestBody PracticeEntity practiceEntity) {
        return practiceService.updatePractice(practiceEntity);
    }

}
