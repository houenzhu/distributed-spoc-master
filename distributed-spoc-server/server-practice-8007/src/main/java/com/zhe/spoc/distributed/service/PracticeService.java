package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName PracticeService
 * @Description TODO
 * @date 2022/10/23 16:35
 * @Version 1.0
 */

public interface PracticeService extends IService<PracticeEntity> {
    CommonResult<?> insertPractice(PracticeEntity practiceEntity);

    Boolean removePractice(Long praId);

    List<PracticeEntity> practiceList(Integer teaId,Integer begin, Integer count);

    CommonResult<?> myPractice(Integer begin, Integer count);


    CommonResult<?> practiceEntityListByName(String praName, Integer pageNum, Integer pageSize);

    CommonResult<?> insertStuPractice(StuPracticeEntity stuPracticeEntity);

    Map<String, Object> SelectPractice();

    List<PracticeEntity> SelectAllPracticeInPage(Integer begin, Integer count);

    CommonResult<?> SelectAllPracticeMapInPage(Integer begin, Integer count);

    Integer MyPracticeCount();

    CommonResult<?> practiceEntityById(Long praId);

    CommonResult<?> PracticeNum();

    CommonResult<?> myPracticeByNameNum(String praName);

    CommonResult<?> myPracticeByIdNum(Long praId);

    CommonResult<?> PracticeByName(String praName, Integer pageNum, Integer pageSize);

    CommonResult<?> PracticeByNameNum(String praName);

    CommonResult<?> PracticeByIdNum(Long praId);

    CommonResult<?> PracticeById(Long praId);

    CommonResult<?> updatePractice(PracticeEntity practiceEntity);
}
