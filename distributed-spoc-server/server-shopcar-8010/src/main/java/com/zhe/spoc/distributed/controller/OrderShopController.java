package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.spoc.distributed.service.OrderShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName OrderShopController
 * @Description TODO
 * @date 2022/10/29 13:34
 * @Version 1.0
 */

@RestController
public class OrderShopController {
    @Autowired
    private OrderShopService orderShopService;

    @GetMapping("/myOrder")
    @PreAuthorize("hasAnyAuthority('teacher', 'student')")
    public CommonResult<Map<String, Object>> myOrder() {
        Map<String, Object> map = orderShopService.myOrder();
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无订单信息噢!");
        }
        return CommonResult.success(map);
    }
}
