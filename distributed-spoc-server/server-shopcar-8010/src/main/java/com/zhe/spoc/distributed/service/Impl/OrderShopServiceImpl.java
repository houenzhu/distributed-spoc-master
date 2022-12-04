package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.OrderShopEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.mapper.OrderShopMapper;
import com.zhe.spoc.distributed.service.OrderShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName OrderShopServiceImpl
 * @Description TODO
 * @date 2022/10/29 10:34
 * @Version 1.0
 */

@Service
@Slf4j
public class OrderShopServiceImpl extends ServiceImpl<OrderShopMapper, OrderShopEntity> implements OrderShopService {

    @Autowired
    private OrderShopMapper orderShopMapper;

    @Autowired
    private ServerCoursClient serverCoursClient;
    @Override
    public Map<String, Object> myOrder() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderShopEntity> orderShopEntities = orderShopMapper.myOrderList(user.getId());
        if (CollectionUtils.isEmpty(orderShopEntities)) {
            return null;
        }
        for (OrderShopEntity orderShopEntity: orderShopEntities) {
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(orderShopEntity.getCouId());
            orderShopEntity.setCoursEntity(coursEntity);
        }
        map.put("orderDetails", orderShopEntities);
        return map;
    }
}
