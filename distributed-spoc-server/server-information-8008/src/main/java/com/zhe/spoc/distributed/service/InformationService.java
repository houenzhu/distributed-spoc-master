package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.CommentDTO;
import com.zhe.common.dto.InformationDTO;
import com.zhe.common.entity.InformationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InformationService extends IService<InformationEntity> {

    CommonResult<?> InsertInformation(InformationEntity informationEntity);

    Boolean DeleteInformation(Long infoId);

    Boolean UpdateInformation(InformationEntity informationEntity);

    InformationEntity SelectInfoById(Integer teaId, Long infoId);

    InformationEntity informationEntityById(Long infoId);
    List<InformationEntity> SelectInfoByName(Integer teaId, String infoTitle);

    List<InformationEntity> informationEntityByName(String infoTitle);

    List<InformationEntity> allInformation();

    CommonResult<?> AllComment(Long infoId);

    CommonResult<Boolean> likeInformation(Long infoId);

    CommonResult<?> getCommentNum(Long infoId);

    CommonResult<?> InfoDetail(Long infoId);

    CommonResult<?> InformationByAuthName(String authName);

    CommonResult<?> likeCommentInInformation(Long commentId);


}
