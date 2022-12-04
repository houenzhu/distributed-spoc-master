package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.CommentDTO;
import com.zhe.common.dto.InformationDTO;
import com.zhe.common.entity.InformationEntity;
import com.zhe.spoc.distributed.service.InformationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName InformationController
 * @Description TODO
 * @date 2022/10/24 12:06
 * @Version 1.0
 */

@RestController
public class InformationController {

    @Autowired
    private InformationService informationService;

    @PostMapping("/insertInformation")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<?> insertInformation(@RequestBody InformationEntity informationEntity) {
        return informationService.InsertInformation(informationEntity);

    }

    // 删除资讯
    @PostMapping("/removeInformation")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> removeInformation(@RequestParam Long infoId) {
        Boolean aBoolean = informationService.DeleteInformation(infoId);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED,"失败失败!");
        }
        return CommonResult.success(true, "删除成功!");
    }

    // 修改资讯
    @PostMapping("/updateInformation")
    @PreAuthorize("hasAuthority('teacher')")
    public CommonResult<Boolean> updateInformation(@RequestBody InformationEntity informationEntity) {
        Boolean aBoolean = informationService.UpdateInformation(informationEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED,"修改失败!");
        }
        return CommonResult.success(true, "修改成功!");
    }

    @GetMapping("/allInformation")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<List<InformationEntity>> allInformation() {
        List<InformationEntity> informationEntities = informationService.allInformation();
        if (informationEntities == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无咨询信息!");
        }
        return CommonResult.success(informationEntities);
    }

    // 教师根据id查询已发布的资讯
    @GetMapping("/InformationById")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<InformationEntity> InformationById(@RequestParam Long infoId) {
        InformationEntity informationEntity = informationService.informationEntityById(infoId);
        if (informationEntity == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无该资讯!");
        }
        return CommonResult.success(informationEntity);
    }

    /**
     * 根据作者姓名查询
     * @param authName
     * @return
     */
    @GetMapping("/InformationByAuthName")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<?> InformationByAuthName(@RequestParam String authName){
        return informationService.InformationByAuthName(authName);
    }

    // 教师根据标题查询已发布的资讯
    @GetMapping("/InformationByName")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public CommonResult<List<InformationEntity>> InformationByName(@RequestParam String infoTitle) {
        List<InformationEntity> informationEntities = informationService.informationEntityByName(infoTitle);
        if (informationEntities == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无该资讯!");
        }
        return CommonResult.success(informationEntities);
    }

    @GetMapping("/AllComment")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> AllComment(@RequestParam Long infoId) {
        return informationService.AllComment(infoId);
    }

    /**
     * 点赞资讯
     * @param infoId
     * @return
     */
    @PostMapping("/likeInformation")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public CommonResult<Boolean> likeInformation(@RequestParam Long infoId) {
        return informationService.likeInformation(infoId);
    }

    @GetMapping("/getCommentNum")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public CommonResult<?> getCommentNum(@RequestParam Long infoId) {
        return informationService.getCommentNum(infoId);
    }

    @GetMapping("/InfoDetail")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    public CommonResult<?> InfoDetail(@RequestParam Long infoId) {
        return informationService.InfoDetail(infoId);
    }

    /**
     * 给该评论点赞
     * @param commentId
     * @return
     */
    @PostMapping("/likeCommentInInformation")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<?> likeCommentInInformation(@RequestParam Long commentId) {
        return informationService.likeCommentInInformation(commentId);
    }




}
