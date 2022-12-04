package com.zhe.spoc.distributed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhe.common.dto.CommentDTO;
import com.zhe.common.dto.InformationDTO;
import com.zhe.common.entity.CommentEntity;
import com.zhe.common.entity.CommentLikeEntity;
import com.zhe.common.entity.InformationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InformationMapper extends BaseMapper<InformationEntity> {

    Boolean InsertInformation(InformationEntity informationEntity);
    Boolean DeleteInformation(@Param("infoId") Long infoId);
    Boolean UpdateInformation(InformationEntity informationEntity);

    InformationEntity SelectInfoById(@Param("teaId") Integer teaId, @Param("infoId") Long infoId);

    List<InformationEntity> SelectInfoByName(@Param("teaId") Integer teaId, @Param("infoTitle") String infoTitle);
    List<InformationEntity> allInformation();

    /**
     * 查看该资讯的所有评论
     * @param infoId
     * @return
     */
    List<CommentDTO> allComment(@Param("infoId") Long infoId);

    int getCommentNum(@Param("infoId") Long infoId);

    InformationDTO InfoDetail(@Param("infoId") Long infoId);

    List<InformationEntity> InformationByAuthName(@Param("authName") String authName);

    /**
     * 给该评论点赞（资讯）
     * @param commentId
     * @return
     */
    boolean likeCommentInInformation(@Param("commentId") Long commentId);

    boolean dislikeCommentInInformation(@Param("commentId") Long commentId);

    /**
     * 该资讯的全部评论点赞数
     * @param infoId
     * @return
     */
    Integer commentLikes(@Param("infoId") Long infoId);

    /**
     * 添加到用户点赞表
     * @param userId
     * @param commentId
     * @param isLike
     * @return
     */
    Boolean insertCommentLike(@Param("userId") Integer userId, @Param("commentId") Long commentId,
                              @Param("isLike") Boolean isLike);

    /**
     * 查看是否有记录
     * @param userId
     * @param commentId
     * @return
     */
    Integer selectCommentLike(@Param("userId") Integer userId, @Param("commentId") Long commentId);

    /**
     * 更新用户点赞表
     * @param userId
     * @param commentId
     * @param isLike
     * @return
     */
    Boolean updateCommentLike(@Param("userId") Integer userId, @Param("commentId") Long commentId,
                              @Param("isLike") Boolean isLike);

    /**
     * 是否已经点赞
     * @param userId
     * @param commentId
     * @return
     */
    Integer selectCommentIsLike(@Param("userId") Integer userId, @Param("commentId") Long commentId);

    Integer getCommentLikeDetails(@Param("commentId") Long commentId, @Param("userId") Integer userId);

    /**
     * 查看该评论点赞数
     * @param commentId
     * @return
     */
    int getCommentLike(@Param("commentId") Long commentId);
}
