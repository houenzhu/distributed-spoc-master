package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ShopCarMapper extends BaseMapper<ShopCarEntity> {
    Boolean addShopCar(ShopCarEntity shopCarEntity);

    List<CoursEntity> myShopCar(@Param("stuId") Integer stuId);

    Boolean buyOneCourse(ShopCarEntity shopCarEntity);

    Boolean updateScore(@Param("stuScore") Double stuScore, @Param("stuId") Integer stuId);

    Boolean deleteByCouId(ShopCarEntity shopCarEntity);

    // 批量购买
    Boolean BuyMoreCourse(List<OrderShopEntity> orderShopEntity);

    Boolean deleteShopCar(List<ShopCarEntity> shopCarEntityList);

    Boolean insertMoreMyCourse(@Param("stuCourseEntities") List<StuCourseEntity> stuCourseEntities);
}
