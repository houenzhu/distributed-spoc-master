package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.LearningProgressEntity;
import com.zhe.common.entity.StuCouProgress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author HouEnZhu
 * @ClassName StuCouProgressMapper
 * @Description TODO
 * @date 2022/11/7 19:52
 * @Version 1.0
 */

@Mapper
public interface StuCouProgressMapper extends BaseMapper<StuCouProgress> {
    int HaveProgress(StuCouProgress stuCouProgress);
    int ProgressId(StuCouProgress stuCouProgress);


}
