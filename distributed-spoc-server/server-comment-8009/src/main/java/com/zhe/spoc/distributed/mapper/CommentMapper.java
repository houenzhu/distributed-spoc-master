package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper extends BaseMapper<CommentEntity> {
    Boolean InsertComment(CommentEntity commentEntity);

    List<CommentEntity> MyComment(@Param("uuid") Integer uuid);
}
