package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.OrderShopEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName OrderShopMapper
 * @Description TODO
 * @date 2022/10/29 10:33
 * @Version 1.0
 */

@Mapper
public interface OrderShopMapper extends BaseMapper<OrderShopEntity> {
    Boolean insertOrder(OrderShopEntity orderShopEntity);

    List<OrderShopEntity> myOrderList(@Param("stuId") Integer stuId);

    // 查询是否已购买该课程
    int selectThisCourse(@Param("stuId") Integer stuId, @Param("couId") Long couId);
}
