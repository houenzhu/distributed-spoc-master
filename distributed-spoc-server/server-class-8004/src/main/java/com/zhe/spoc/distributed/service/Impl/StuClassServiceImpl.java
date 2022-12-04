package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.ClassEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.StuClassMapper;
import com.zhe.spoc.distributed.service.StuClassService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuClassServiceImpl
 * @Description TODO
 * @date 2022/10/24 19:29
 * @Version 1.0
 */

@Service
@Slf4j
public class StuClassServiceImpl extends ServiceImpl<StuClassMapper, ClassEntity> implements StuClassService {

    @Autowired
    private StuClassMapper stuClassMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Override
    public ClassEntity StuClassEntity(Integer stuId) {
        return stuClassMapper.StuClassEntity(stuId);
    }

    @Override
    public Map<String, Object> StuClassAll() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClassEntity classEntity = StuClassEntity(user.getId());
        if (classEntity == null) {
            return null;
        }
        Integer teaId = classEntity.getTeaId();
        UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
        map.put("classInfo", classEntity);
        map.put("teacherInfo", teacherInfo);
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        return map;
    }

}
