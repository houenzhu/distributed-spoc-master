package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.CommentDTO;
import com.zhe.common.dto.InformationDTO;
import com.zhe.common.dto.UserDto;
import com.zhe.common.dto.UserDto_1;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.CommentLikeEntity;
import com.zhe.common.entity.InformationEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.InformationMapper;
import com.zhe.spoc.distributed.service.InformationService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.zhe.common.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.zhe.common.utils.RedisConstants.INFO_COMMENT_LIKED_KEY;

/**
 * @author HouEnZhu
 * @ClassName InformationServiceImpl
 * @Description TODO
 * @date 2022/10/24 12:05
 * @Version 1.0
 */

@Service
@Slf4j
public class InformationServiceImpl extends ServiceImpl<InformationMapper, InformationEntity> implements InformationService {

    @Autowired
    private InformationMapper informationMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Autowired
    private StringRedisTemplate redisTemplate;
    // 教师创建资讯
    @Override
    public CommonResult<?> InsertInformation(InformationEntity informationEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        informationEntity.setTeaId(user.getId());
        if ("<p><br></p>".equals(informationEntity.getInfoMain()) || informationEntity.getInfoTitle() == null) {
            return CommonResult.failed("内容或者标题不能为空!");
        }
        Boolean isSuccess = informationMapper.InsertInformation(informationEntity);
        return CommonResult.success(isSuccess, "发布成功!");
    }

    @Override
    public Boolean DeleteInformation(Long infoId) {
        return informationMapper.DeleteInformation(infoId);
    }

    // 修改资讯内容或标题
    @Override
    public Boolean UpdateInformation(InformationEntity informationEntity) {
        return informationMapper.UpdateInformation(informationEntity);
    }


    @Override
    public InformationEntity SelectInfoById(Integer teaId, Long infoId) {
        return informationMapper.SelectInfoById(teaId, infoId);
    }

    // 根据id查询
    @Override
    public InformationEntity informationEntityById(Long infoId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InformationEntity informationEntity = SelectInfoById(user.getId(), infoId);
        if (informationEntity == null) {
            return null;
        }
        return informationEntity;
    }


    @Override
    public List<InformationEntity> SelectInfoByName(Integer teaId, String infoTitle) {
        return informationMapper.SelectInfoByName(teaId, infoTitle);
    }

    // 根据标题查询
    @Override
    public List<InformationEntity> informationEntityByName(String infoTitle) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<InformationEntity> informationEntities = SelectInfoByName(user.getId(), infoTitle);
        if (CollectionUtils.isEmpty(informationEntities)) {
            return null;
        }
        return informationEntities;
    }

    // 浏览资讯（学生和教师）
    @Override
    public List<InformationEntity> allInformation() {
        List<InformationEntity> informationEntities = informationMapper.allInformation();
        if (CollectionUtils.isEmpty(informationEntities)) {
            return null;
        }
        for (InformationEntity informationEntity : informationEntities) {
            Long infoId = informationEntity.getInfoId();
            int infoLike = informationEntity.getInfoLike();
            Integer commentLikes = informationMapper.commentLikes(infoId);
            if (commentLikes == null) {
                commentLikes = 0;
            }
            int likes = infoLike + commentLikes;
            informationEntity.setLikes(likes);
        }
        return informationEntities;
    }

    @Override
    public CommonResult<?> AllComment(Long infoId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        if (infoId == null || infoId == 0) {
            return CommonResult.failed(ResultCode.FAILED, "出错啦!id为空啦");
        }
        List<CommentDTO> commentDTOS = informationMapper.allComment(infoId);
        if (CollectionUtils.isEmpty(commentDTOS)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无评论噢!");
        }
        for (CommentDTO commentDTO : commentDTOS) {
            Integer integer = commentDTO.getUId();
            UserDto name = serverUserClient.getTrueName(integer);
            commentDTO.setUserDto(name);
            Long commentId = commentDTO.getCId();
            Integer commentLikeDetails = informationMapper.getCommentLikeDetails(commentId, userId);
            if (commentLikeDetails == null) {
                commentDTO.setIsLike(false);
            }else {
                commentDTO.setIsLike(commentLikeDetails == 0 ? false : true);
            }

        }
        return CommonResult.success(commentDTOS);
    }

    // 点赞资讯
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> likeInformation(Long infoId) {
        // 1. 先获取当前用户id
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        // 2. 判断当前登录用户是否已经点赞
        String key = BLOG_LIKED_KEY + infoId;
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (BooleanUtil.isFalse(isMember)) {
            // 3. 未点赞，可以点赞
            // 3.1 数据库点赞 + 1
            boolean isSuccess = update().setSql("info_like = info_like + 1").eq("info_id", infoId).update();
            if (isSuccess) {
                redisTemplate.opsForSet().add(key, userId.toString());
            }
        }else {
            // 4. 已点赞，取消点赞
            // 4.1 数据库点赞数 - 1
            boolean isSuccess = update().setSql("info_like = info_like - 1").eq("info_id", infoId).update();

            // 4.2 把用户到redis的set集合中移除
            if (isSuccess) {
                redisTemplate.opsForSet().remove(key, userId.toString());
            }
        }
        return CommonResult.success(true, "操作成功!");
    }

    @Override
    public CommonResult<?> getCommentNum(Long infoId) {
        int commentNum = informationMapper.getCommentNum(infoId);
        return CommonResult.success(commentNum);
    }

    @Override
    public CommonResult<?> InfoDetail(Long infoId) {
        if (infoId == null) {
            return CommonResult.failed(ResultCode.FAILED, "资讯id不能为空!");
        }
        InformationDTO informationDTO = informationMapper.InfoDetail(infoId);
        if (informationDTO == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "该资讯不存在或者已经删除了!");
        }
        Integer teaId = informationDTO.getTeaId();
        UserEntity teacherInfo = serverUserClient.getMyUserInfo(teaId);
        informationDTO.setUserDto_1(BeanUtil.copyProperties(teacherInfo, UserDto_1.class));
        return CommonResult.success(informationDTO);
    }

    /**
     * 根据作者姓名查询
     * @param authName
     * @return
     */
    @Override
    public CommonResult<?> InformationByAuthName(String authName) {
        List<InformationEntity> informationEntities = informationMapper.InformationByAuthName(authName);
        if (CollectionUtils.isEmpty(informationEntities)) {
            return CommonResult.failed("暂无资讯!");
        }
        return CommonResult.success(informationEntities);
    }

    /**
     * 给该评论点赞
     * @param commentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> likeCommentInInformation(Long commentId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 获取当前用户id
        Integer userId = user.getId();
        // 拼接key
        String key = INFO_COMMENT_LIKED_KEY + commentId;
        // 判断用户是否点赞
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());

        // 先查询有无记录
        Integer Like = informationMapper.selectCommentLike(userId, commentId);
        // 查询有记录且已点赞
        Integer isLike = informationMapper.selectCommentIsLike(userId, commentId);
        if (Like == null || Like == 0) {
            // 没有记录
            // 插入，给他点赞
            informationMapper.insertCommentLike(userId, commentId, true);
        }else {
            // 有记录
            // 查看是否已经点赞
            if (isLike == null || isLike == 0) {
                // 没有点赞但是有记录 点赞
                informationMapper.updateCommentLike(userId, commentId, true);
            }else {
                // 已点赞也有记录 取消点赞
                informationMapper.updateCommentLike(userId, commentId, false);
            }
        }

        // 未点赞可以点赞
        if (BooleanUtil.isFalse(isMember)) {
            // 点赞，数据库 + 1
            boolean isSuccess = informationMapper.likeCommentInInformation(commentId);
            if (isSuccess) {
                // 把用户到redis的set集合中添加
                redisTemplate.opsForSet().add(key, userId.toString());
            }
        }else {
            // 已点赞，取消点赞
            boolean isSuccess = informationMapper.dislikeCommentInInformation(commentId);
            if (isSuccess) {
                // 把用户到redis的set集合中移除
                redisTemplate.opsForSet().remove(key, userId.toString());
            }
        }
        return CommonResult.success(true);
    }


}
