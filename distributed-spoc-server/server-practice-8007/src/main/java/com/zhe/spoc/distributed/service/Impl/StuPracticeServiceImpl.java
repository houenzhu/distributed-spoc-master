package com.zhe.spoc.distributed.service.Impl;

import com.zhe.common.entity.CoursEntity;
import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.mapper.StuPracticeMapper;
import com.zhe.spoc.distributed.service.StuPracticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuPracticeServiceImpl
 * @Description TODO
 * @date 2022/10/28 13:07
 * @Version 1.0
 */

@Service
@Slf4j
public class StuPracticeServiceImpl implements StuPracticeService {

    @Autowired
    private StuPracticeMapper stuPracticeMapper;

    @Autowired
    private ServerCoursClient serverCoursClient;

    @Override
    public Boolean deleteMyPractice(StuPracticeEntity stuPracticeEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        stuPracticeEntity.setStuId(user.getId());
        return stuPracticeMapper.deleteMyPractice(stuPracticeEntity);
    }

    // 查看我报名的实践
    @Override
    public Map<String, Object> MyPractice() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PracticeEntity> practiceEntities = stuPracticeMapper.myPractice(user.getId());
        if (CollectionUtils.isEmpty(practiceEntities)) {
            return null;
        }
        for (PracticeEntity practiceEntity: practiceEntities) {
            CoursEntity coursEntity = serverCoursClient.BuyCourseId(practiceEntity.getCouId());
            practiceEntity.setCoursEntity(coursEntity);
        }
        map.put("MyPracticeInfo", practiceEntities);
        return map;
    }

}
