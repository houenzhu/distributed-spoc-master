package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.ShopCarEntity;
import com.zhe.spoc.distributed.service.ShopCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName ShopCarController
 * @Description TODO
 * @date 2022/10/26 10:33
 * @Version 1.0
 */

@RestController
public class ShopCarController {

    @Autowired
    private ShopCarService shopCarService;

    @PostMapping("/addShopCar")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> addShopCar(@RequestBody ShopCarEntity shopCarEntity) {
        Boolean aBoolean = shopCarService.addShopCar(shopCarEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "添加失败");
        }
        return CommonResult.success(true, "加入成功!");
    }

    @GetMapping("/myShopCar")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<?> myShopCar() {
        return shopCarService.myShopCar_1();
    }

    /**
     * 购买单个课程
     * @param shopCarEntity
     * @return
     */
    @PostMapping("/BuyOneCourse")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> BuyOneCourse(@RequestBody ShopCarEntity shopCarEntity) {
        Boolean aBoolean = shopCarService.buyOneCourse(shopCarEntity);
        if (!aBoolean) {
            return CommonResult.failed(ResultCode.FAILED, "您的积分不足噢");
        }
        return CommonResult.success(true, "购买成功!");
    }

    /**
     * 批量购买课程
     * @param shopCarEntityList
     * @return
     */
    @PostMapping("/BuyMoreCourse")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> BuyMoreCourse(@RequestBody List<ShopCarEntity> shopCarEntityList) throws InterruptedException {
        return shopCarService.BuyMoreCourse(shopCarEntityList);
    }

    @PostMapping("/DeleteShopCar")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Boolean> DeleteShopCar(@RequestBody List<ShopCarEntity> shopCarEntityList) {
        return shopCarService.deleteShopCar(shopCarEntityList);
    }
}
