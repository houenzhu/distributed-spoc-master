package com.zhe.spoc.distributed.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.mapper.CommentMapper;
import com.zhe.spoc.distributed.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName CommentServiceImpl
 * @Description TODO
 * @date 2022/10/24 14:20
 * @Version 1.0
 */

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Boolean InsertComment(CommentEntity commentEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Boolean role = null;
        for (GrantedAuthority grantedAuthority : authorities) {
            String Role = grantedAuthority.getAuthority();
            if ("teacher".equals(Role)){
                role = true;
            }else {
                role = false;
            }
        }
        commentEntity.setRole(role);
        commentEntity.setUuId(userId);
        return commentMapper.InsertComment(commentEntity);
    }

    @Override
    public List<CommentEntity> MyComment(Integer uuid) {
        return commentMapper.MyComment(uuid);
    }

    @Override
    public Map<String, Object> MyCommentMap() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CommentEntity> commentEntities = MyComment(user.getId());
        if (CollectionUtils.isEmpty(commentEntities)) {
            return null;
        }
        map.put("myComment", commentEntities);
        return map;
    }
}
