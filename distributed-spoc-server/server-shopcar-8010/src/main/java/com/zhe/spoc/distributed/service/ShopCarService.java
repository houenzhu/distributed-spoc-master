package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.api.CommonResult;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.ShopCarEntity;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface ShopCarService extends IService<ShopCarEntity> {

    Boolean addShopCar(ShopCarEntity shopCarEntity);

    List<CoursEntity> myShopCar(Integer stuId);


    Boolean buyOneCourse(ShopCarEntity shopCarEntity);


    Boolean updateScore(Double stuScore, Integer stuId);

    Boolean deleteByCouId(ShopCarEntity shopCarEntity);

    CommonResult<Boolean> BuyMoreCourse(List<ShopCarEntity> shopCarEntityList) throws InterruptedException;

    CommonResult<Boolean> buyCourseInOrder(List<ShopCarEntity> shopCarEntityList, UserEntity user, double total);

    CommonResult<Boolean> deleteShopCar(List<ShopCarEntity> shopCarEntityList);

    CommonResult<?> myShopCar_1();

}
