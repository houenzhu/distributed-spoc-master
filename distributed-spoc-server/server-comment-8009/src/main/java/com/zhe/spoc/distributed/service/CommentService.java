package com.zhe.spoc.distributed.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhe.common.entity.CommentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<CommentEntity> {

    Boolean InsertComment(CommentEntity commentEntity);

    List<CommentEntity> MyComment(Integer uuid);

    Map<String, Object> MyCommentMap();
}
