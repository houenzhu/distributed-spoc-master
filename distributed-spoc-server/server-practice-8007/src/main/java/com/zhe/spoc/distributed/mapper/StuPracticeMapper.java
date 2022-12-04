package com.zhe.spoc.distributed.mapper;

import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName StuPracticeMapper
 * @Description TODO
 * @date 2022/10/28 13:05
 * @Version 1.0
 */

@Mapper
public interface StuPracticeMapper {
    Boolean deleteMyPractice(StuPracticeEntity stuPracticeEntity);

    List<PracticeEntity> myPractice(@Param("stuId") Integer stuId);
}
