package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.dto.CourseAppraiseDTO;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.CourseAppraiseEntity;
import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.CourseCommentMapper;
import com.zhe.spoc.distributed.service.CourseCommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HouEnZhu
 * @ClassName CourseCommentServiceImpl
 * @Description TODO
 * @date 2022/10/26 19:47
 * @Version 1.0
 */
@Service
@Slf4j
public class CourseCommentServiceImpl extends ServiceImpl<CourseCommentMapper, CourseAppraiseEntity>
        implements CourseCommentService {

    @Autowired
    private CourseCommentMapper courseCommentMapper;

    @Autowired
    private ServerUserClient serverUserClient;
    // 课程评论
    @Override
    public Boolean CommentCourse(CourseAppraiseEntity courseAppraiseEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        courseAppraiseEntity.setStuId(user.getId());
        if (courseAppraiseEntity.getCouAppraise() == null || Objects.equals(courseAppraiseEntity.getCouAppraise(), "")){
            return false;
        } else if (courseAppraiseEntity.getCouId() == null) {
            return false;
        }
        return courseCommentMapper.CommentCourse(courseAppraiseEntity);
    }

    @Override
    public CommonResult<?> theCourseComment(Long courseId) {
        if (courseId == null) {
            return CommonResult.failed("课程id不能为空!");
        }
        List<CourseAppraiseDTO> courseAppraiseDTOS = courseCommentMapper.theCourseComment(courseId);
        if (CollectionUtils.isEmpty(courseAppraiseDTOS)) {
            return CommonResult.success("暂无评论!");
        }
        for (CourseAppraiseDTO courseAppraiseDTO : courseAppraiseDTOS) {
            Integer stuId = courseAppraiseDTO.getStuId();
            UserEntity stuName = serverUserClient.getTeacherInfo(stuId);
            courseAppraiseDTO.setUserDto(BeanUtil.copyProperties(stuName, UserDto.class));
        }
        return CommonResult.success(courseAppraiseDTOS);
    }
}
