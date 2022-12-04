package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.UserDto;
import com.zhe.common.dto.UserDto_1;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.client.ServerCoursClient;
import com.zhe.spoc.distributed.client.ServerStuClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.mapper.ClassMapper;
import com.zhe.spoc.distributed.service.ClassService;
import org.apache.catalina.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author HouEnZhu
 * @ClassName ClassServiceImpl
 * @Description TODO
 * @date 2022/10/14 19:24
 * @Version 1.0
 */

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassEntity> implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Autowired
    private ServerCoursClient serverCoursClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //查看一个班级的信息
    @Override
    public ClassEntity getOneClass(Integer id) {
        ClassEntity oneClass = classMapper.getOneClass(id);
        UserEntity teacherInfo = serverUserClient.getTeacherInfo(oneClass.getTeaId());
        oneClass.setUser(teacherInfo);
        return oneClass;
    }

    //查看所有班级的信息
    @Override
    public List<ClassEntity> getAllClass() {
        //        for (ClassEntity classEntity: allClass){
//            UserEntity teacherInfo = serverUserClient.getTeacherInfo(classEntity.getTeaId());
//            classEntity.setUser(teacherInfo);
//        }
        return classMapper.getAllClass();
    }

    //查看所有班级的信息(但是分页)
    @Override
    public List<ClassEntity> getAllClassInPage(Integer begin, Integer count) {
        begin = (begin - 1) * count;
        List<ClassEntity> allClassInPage = classMapper.getAllClassInPage(begin, count);
        allClassInPage
                .forEach(classEntity -> {
                    UserEntity teacherInfo = serverUserClient.getTeacherInfo(classEntity.getTeaId());
                    classEntity.setUser(teacherInfo);
                });
        return allClassInPage;
    }

    // 全部班级信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getAllClassPage(Integer begin, Integer count) {
        begin = (begin - 1) * count;
        Map<String, Object> map = new HashMap<>();
        List<ClassEntity> allClassInPage_1 = classMapper.getAllClassInPage_1(begin, count);
        for (ClassEntity classEntity: allClassInPage_1) {
            Long classId = classEntity.getClassId();
            List<CoursEntity> allCours = serverCoursClient.getAllCours(classId);
            if (CollectionUtils.isEmpty(allCours)) {
                break;
            }
            // 实时更新点赞和拥有的数量
            for (CoursEntity coursEntity: allCours) { // 获取该班级课程
                List<CourseInteractionEntity> recommend = serverCoursClient.Recommend(coursEntity.getCouId());
                List<CourseInteractionEntity> collect = serverCoursClient.Collect(coursEntity.getCouId());
                long count1 = recommend.stream()
                        .map(CourseInteractionEntity::getRecommend)
                        .count(); // 点赞数
                long count2 = collect.stream()
                        .map(CourseInteractionEntity::getCollect)
                        .count(); // 拥有数
                coursEntity.setCouRecoNum((int) count1);
                coursEntity.setCouCollNum((int) count2);
                classMapper.updateCouRecoNum(coursEntity);
            }
            if (CollectionUtils.isEmpty(allClassInPage_1)){
                classEntity.setCoursEntities(null);
            }
            classEntity.setCoursEntities(allCours);
        }

        for (ClassEntity classEntity: allClassInPage_1) { // 获取班级学生列表
            Long classId = classEntity.getClassId();
            List<UserEntity> classStu = serverUserClient.getClassStu(classId);
            classEntity.setUserEntityList(classStu);
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(classEntity.getTeaId());// 获取班级创建者信息
            classEntity.setUser(teacherInfo);
            stuLists(classEntity.getClassId());
        }
        map.put("classInfo", allClassInPage_1);
        return map;
    }


    //添加班级班级(默认添加)
    @Override
    public Boolean insertClass(ClassEntity classEntity) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = user.getId();
        classEntity.setTeaId(id); // 默认教师为创建课程者
        return classMapper.insertClass(classEntity);
    }

    // 根据班级id查询班级学生
    @Override
    public List<UserEntity> stuLists(Long classId) {
        List<UserEntity> userEntities = classMapper.stuLists(classId);
        int StuNumber = 0;
        long count = userEntities.stream()
                .map(UserEntity::getId)
                .count();
        StuNumber = (int) count;
        classMapper.StuNum(StuNumber, classId);
        return userEntities;
    }



    // 根据班级名字查询班级学生
    @Override
    public List<UserEntity> stuLists_1(String className) {
        return classMapper.stuLists_1(className);
    }

    //查看教师个人教学的班级
    @Override
    public List<ClassEntity> classList(Integer id) {
        return classMapper.classList(id);
    }


    /**
     * 查看教师教学的班级信息及该班级的学生表
     * @return
     */
    @Override
    public Map<String, Object> getMyClass() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = user.getId();
        List<ClassEntity> classEntities = classList(id);
        if (CollectionUtils.isEmpty(classEntities)) {
            return null;
        }
        for (ClassEntity classEntity: classEntities) {
            Long classId = classEntity.getClassId();
            List<UserEntity> classStu = serverUserClient.getClassStu(classId);
            classEntity.setUserEntityList(classStu);
            List<CoursEntity> allCours = serverCoursClient.getAllCours(classId);
            classEntity.setCoursEntities(allCours);

        }
        map.put("class", classEntities);
        return map;
    }

    //新增该一个学生
    @Override
    public Boolean InsertOneStu(StuClassEntity stuClassEntity) {
        return classMapper.InsertOneStu(stuClassEntity);
    }

    @Override
    public Boolean InsertMoreStu(List<StuClassEntity> stuClassEntityList) {
        return classMapper.InsertMoreStu(stuClassEntityList);
    }

    //逻辑删除一个学生
    @Override
    public Boolean RemoveOneStu(Integer stuId, Long classId) {
        return classMapper.RemoveOneStu(stuId,classId);
    }

    //逻辑删除该班级的一个课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> RemoveOneCours(Long classId, Long couId) {
        if (classId == null || couId == null) {
            return CommonResult.failed(ResultCode.FAILED,"班级id或者课程id不能为空");
        }
        Boolean success = classMapper.RemoveOneCourse(classId, couId);
        return CommonResult.success(success);
    }

    @Override
    public Boolean StuNum(int classStudentNum, Long classId) {
        List<UserEntity> userEntities = stuLists(classId);
        long count = userEntities.stream()
                .map(userEntity -> userEntity.getId())
                .collect(Collectors.toList())
                .stream().count();
        return null;
    }

    // 删除班级
    @Override
    public Boolean deleteClass(Long classId) {
        return classMapper.deleteClass(classId);
    }

    // 全部课程
    @Override
    public List<ClassEntity> getAllClassInPage_1(Integer begin, Integer count) {
        begin = (begin - 1) * count;
        return classMapper.getAllClassInPage_1(begin, count);
    }

    // 教师本人的班级情况
    @Override
    public List<ClassEntity> TeaClass(Integer teaId, Integer begin, Integer count) {
        begin = (begin - 1) * count;
        return classMapper.TeaClass(teaId, begin, count);
    }

    @Override
    public Map<String, Object> TeaClassCourse(Integer begin, Integer count) {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ClassEntity> classEntities = TeaClass(user.getId(), begin, count);
        if (CollectionUtils.isEmpty(classEntities)) {
            return null;
        }
        for (ClassEntity classEntity: classEntities) {
            Long classId = classEntity.getClassId();
            List<UserEntity> classStu = serverUserClient.getClassStu(classId);
            List<CoursEntity> allCours = serverCoursClient.getAllCours(classEntity.getClassId());
            classEntity.setUserEntityList(classStu);
            classEntity.setCoursEntities(allCours);

        }
        map.put("class", classEntities);
        return map;
    }

    // 修改班级信息
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateClass(ClassEntity classEntity) {
        Boolean a = false;
        try {
            a = classMapper.updateClass(classEntity);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.getStackTrace();
            throw new RuntimeException();
        }
        return a;
    }

    /**
     * 获取该班级的所有学生
     * @param classId
     * @return
     */
    @Override
    public CommonResult<?> getTheAllStudent(Long classId, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        List<StudentEntity> studentEntities = classMapper.TheClassAllStudent(classId, pageNum, pageSize);
        for (StudentEntity studentEntity : studentEntities) {
            Integer ids = studentEntity.getStuId();
            StudentEntity myInfoFeign = serverUserClient.getMyInfoFeign(ids);
            UserEntity myUserInfo = serverUserClient.getMyUserInfo(ids);
            UserDto_1 userDto_1 = new UserDto_1();
            userDto_1.setStuScore(myInfoFeign.getStuScore());
            studentEntity.setUserDto_1(BeanUtil.copyProperties(myUserInfo, UserDto_1.class));
        }
        return CommonResult.success(studentEntities, "查询成功!");
    }

    @Override
    public CommonResult<?> getClassName(String className, Integer pageNum, Integer pageSize) {
        if (className == null) {
            return CommonResult.failed(ResultCode.FAILED, "输入不能为空!");
        }
        Map<String, Object> map = new HashMap<>();
        pageNum = (pageNum - 1) * pageSize;
        List<ClassEntity> allClassInPage_1 = classMapper.getClassName(className, pageNum, pageSize);
        if (CollectionUtils.isEmpty(allClassInPage_1)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无该班级");
        }
        for (ClassEntity classEntity: allClassInPage_1) {
            Long classId = classEntity.getClassId();
            List<CoursEntity> allCours = serverCoursClient.getAllCours(classId);
            if (CollectionUtils.isEmpty(allCours)) {
                break;
            }
            if (CollectionUtils.isEmpty(allClassInPage_1)){
                classEntity.setCoursEntities(null);
            }
            classEntity.setCoursEntities(allCours);
        }

        for (ClassEntity classEntity: allClassInPage_1) { // 获取班级学生列表
            Long classId = classEntity.getClassId();
            List<UserEntity> classStu = serverUserClient.getClassStu(classId);
            classEntity.setUserEntityList(classStu);
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(classEntity.getTeaId());// 获取班级创建者信息
            classEntity.setUser(teacherInfo);
            stuLists(classEntity.getClassId());
        }
        map.put("classInfo", allClassInPage_1);
        return CommonResult.success(map);
    }

    /**
     * 根据名称的数量
     * @param className
     * @return
     */
    @Override
    public CommonResult<?> getClassNameNum(String className) {
        if (className == null || "".equals(className)) {
            return CommonResult.success(0);
        }
        int classNameNum = classMapper.getClassNameNum(className);
        return CommonResult.success(classNameNum);
    }

    /**
     * 获取该班级学生的数量
     * @return
     */
    @Override
    public CommonResult<?> getTheAllStudentNum(Long classId) {
        int theAllStudentNum = classMapper.getTheAllStudentNum(classId);
        return CommonResult.success(theAllStudentNum);
    }

}
