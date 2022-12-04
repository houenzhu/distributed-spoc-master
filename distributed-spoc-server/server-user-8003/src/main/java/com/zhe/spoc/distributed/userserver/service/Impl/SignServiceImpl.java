package com.zhe.spoc.distributed.userserver.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.SignEntity;
import com.zhe.spoc.distributed.userserver.dao.SignMapper;
import com.zhe.spoc.distributed.userserver.service.SignService;
import org.springframework.stereotype.Service;

/**
 * @author HouEnZhu
 * @ClassName SignServiceImpl
 * @Description TODO
 * @date 2022/11/26 12:39
 * @Version 1.0
 */

@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, SignEntity> implements SignService {
}
