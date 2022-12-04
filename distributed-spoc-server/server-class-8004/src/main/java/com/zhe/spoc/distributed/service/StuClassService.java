package com.zhe.spoc.distributed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.entity.ClassEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuClassService
 * @Description TODO
 * @date 2022/10/24 19:28
 * @Version 1.0
 */

public interface StuClassService extends IService<ClassEntity> {

    ClassEntity StuClassEntity(Integer stuId);

    Map<String, Object> StuClassAll();

}
