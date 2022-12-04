package com.zhe.spoc.distributed.service;

import com.zhe.common.entity.PracticeEntity;
import com.zhe.common.entity.StuPracticeEntity;

import java.util.List;
import java.util.Map;

public interface StuPracticeService {
    Boolean deleteMyPractice(StuPracticeEntity stuPracticeEntity);

    Map<String, Object> MyPractice();
}
