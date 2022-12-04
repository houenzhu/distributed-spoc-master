package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.entity.OrderShopEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderShopService extends IService<OrderShopEntity> {

    Map<String, Object> myOrder();

}
