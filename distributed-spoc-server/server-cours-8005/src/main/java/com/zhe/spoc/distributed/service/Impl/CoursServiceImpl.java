package com.zhe.spoc.distributed.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.CoursTypeDTO;
import com.zhe.common.dto.CoursTypeListDTO;
import com.zhe.common.dto.CourseDTO;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.utils.CacheClient;
import com.zhe.spoc.distributed.mapper.CoursMapper;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.service.CoursService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zhe.common.utils.RedisConstants.*;

/**
 * @author HouEnZhu
 * @ClassName CoursServiceImpl
 * @Description TODO
 * @date 2022/10/18 16:44
 * @Version 1.0
 */

@Service
@Slf4j
public class CoursServiceImpl extends ServiceImpl<CoursMapper,CoursEntity> implements CoursService {

    @Autowired
    private CoursMapper coursMapper;

    @Autowired
    private ServerUserClient serverUserClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheClient cacheClient;

    //查看本班级的所有课程
    @Override
    public Map<String, Object> coursList(Long classId, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        pageNum = (pageNum - 1) * pageSize;
        List<CoursEntity> courseClass = coursMapper.myCourseClass(classId, pageNum, pageSize);
        if (CollectionUtils.isEmpty(courseClass)) {
            return null;
        }
        for (CoursEntity coursEntity: courseClass) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);
            // 获取父类
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取子类
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        map.put("courseClass", courseClass);
        return map;
    }

    // 远程调用的
    @Override
    public List<CoursEntity> coursList(Long classId) {
        return coursMapper.coursList_1(classId);
    }

    @Override
    public CommonResult<?> CoursMap(Long classId, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        Map<String, Object> map = new HashMap<>();
        List<CoursEntity> coursEntities = coursMapper.coursList(classId, pageNum, pageSize);
        if (CollectionUtils.isEmpty(coursEntities))  {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无课程");
        }
        for (CoursEntity coursEntity: coursEntities) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);
        }
        map.put("coursEntities", coursEntities);
        return CommonResult.success(map);
    }

    @Override
    public CommonResult<?> getAllCoursMapNum(Long classId) {
        if (classId == null) {
            return CommonResult.failed("请传入班级编号!");
        }
        int allCoursMapNum = coursMapper.getAllCoursMapNum(classId);
        return CommonResult.success(allCoursMapNum);
    }

    @Override
    public CourseDTO getCourseDto(Long couId) {
        return coursMapper.getCourseDTO(couId);
    }

    /**
     * 获取这个同学选择的全部课程
     * @return
     */
    @Override
    public CourseDTO getTheStudentCourse() {
        return null;
    }

    /**
     * 实践添加课程
     * @return
     */
    @Override
    public CommonResult<?> getAllCourseByPractice() {
        List<CourseDTO> allCourseByPractice = coursMapper.getAllCourseByPractice();
        if (CollectionUtils.isEmpty(allCourseByPractice)){
            return CommonResult.failed("暂无课程!");
        }
        for (CourseDTO courseDTO : allCourseByPractice) {
            Integer teaId = courseDTO.getTeaId();
            UserDto trueName = serverUserClient.getTrueName(teaId);
            courseDTO.setUserDto(trueName);
        }
        return CommonResult.success(allCourseByPractice);
    }

    // 通过id查询全部课程
    @Override
    public List<CoursEntity> coursById(Long couId) {
        String key = COURSE_ID_KEY + couId;
        // 先从redis中查询
        String courseJson = redisTemplate.opsForValue().get(key);

        // 判断是否为空值
        if (StrUtil.isNotBlank(courseJson)) {
            // 不为空 直接返回
            return JSONUtil.toList(courseJson, CoursEntity.class);
        }
        // 为空字符串
        if (courseJson != null) {
            return null;
        }

        // 为空
        // 先查数据库
        List<CoursEntity> coursEntities = coursMapper.coursById(couId);
        if (CollectionUtils.isEmpty(coursEntities)) {
            redisTemplate.opsForValue().set(key, "", COURSE_ID_NULL, TimeUnit.SECONDS);
            return null;
        }
        // 拼接数据
        for (CoursEntity coursEntity: coursEntities){
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);
            // 获取父类
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取子类
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        cacheClient.set(key, JSONUtil.toJsonStr(coursEntities), COURSE_ID_TTL, TimeUnit.MINUTES);
        return coursEntities;
    }

    @Override
    public List<CoursEntity> coursByName(String couName, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        List<CoursEntity> coursEntities = coursMapper.coursByName(couName, pageNum, pageSize);
        if (CollectionUtils.isEmpty(coursEntities)){
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);

            // 获取父类
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取子类
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        return coursEntities;
    }

    @Override
    public int CoursByNameNum(String couName) {
        return coursMapper.CoursByNameNum(couName);
    }

    @Override
    public List<CoursEntity> GetAllCours() {
        List<CoursEntity> coursEntities = coursMapper.GetAllCours();
        if (CollectionUtils.isEmpty(coursEntities)) {
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(coursEntity.getTeaId());
            coursEntity.setUser(teacherInfo);
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(coursEntity.getCouType());
            coursEntity.setCoursTypeEntity(coursTypeEntity);
        }
        return coursEntities;
    }

    // 新增课程
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> InsertCours(CoursEntity coursEntity) {
        if (coursEntity == null) {
            return CommonResult.failed("课程信息不能为空!");
        }
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer teaId = user.getId();
        coursEntity.setTeaId(teaId);
        // 设置章节数为0
        coursEntity.setCouCataNum(0);
        // 先操作数据库
        Boolean aBoolean = coursMapper.InsertCours(coursEntity);
        // 删除redis中的分页信息
        Set<String> keys = redisTemplate.keys(DELETE_COURSE_PAGE_KEY + "*");
        Set<String> keys1 = redisTemplate.keys(COURSE_TEA_PAGE_KEY + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete(keys1);
        redisTemplate.delete(ALL_COURS_NUM_KEY);
        redisTemplate.delete(MY_COURSE_PAGE_NUM_KEY);
        return CommonResult.success("新增成功!");
    }

    // 修改课程信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCours(CoursEntity coursEntity) {
        Long couId = coursEntity.getCouId();
        if (couId == null) {
            return null;
        }
        // 先更新数据库
        Boolean aBoolean = coursMapper.updateCours(coursEntity);

        // 在删除缓存,删除redis中的分页信息
        Set<String> keys = redisTemplate.keys(DELETE_COURSE_PAGE_KEY + "*");
        Set<String> keys1 = redisTemplate.keys(COURSE_TEA_PAGE_KEY + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete(keys1);
        redisTemplate.delete(COURSE_ID_KEY + couId);
        return aBoolean;
    }

    @Override
    public Boolean DeleteCours(Long couId) {
        // 先更新数据库
        Boolean aBoolean = coursMapper.DeleteCours(couId);
        // 删除redis中的分页信息
        Set<String> keys = redisTemplate.keys(DELETE_COURSE_PAGE_KEY + "*");
        Set<String> keys1 = redisTemplate.keys(COURSE_TEA_PAGE_KEY + "*");
        redisTemplate.delete(keys);
        redisTemplate.delete(keys1);
        redisTemplate.delete(ALL_COURS_NUM_KEY);
        redisTemplate.delete(MY_COURSE_PAGE_NUM_KEY);
        redisTemplate.delete(COURSE_ID_KEY + couId);
        return aBoolean;
    }

    @Override
    public CoursEntity coursByIdBuy(Long couId) {
        return coursMapper.coursByIdBuy(couId);
    }

    @Override
    public List<CoursTypeDTO> parentType() {
        String type = COURSE_TYPE_KEY + "courseType";
        // 从redis查出来
        String courseType = redisTemplate.opsForValue().get(type);
        // 判断是否为空
        if (StrUtil.isNotBlank(courseType)) {
            // 不为空
            return JSONUtil.toList(courseType, CoursTypeDTO.class);
        }
        // 为空 从数据库查出来
        List<CoursTypeDTO> parentType = coursMapper.parentType();
        // 若数据库查出为空
        if (CollectionUtils.isEmpty(parentType)) {
            return null;
        }
        // 存入redis
        redisTemplate.opsForValue().set(type, JSONUtil.toJsonStr(parentType), COURSE_TYPE_TTL, TimeUnit.MINUTES);
        // 返回
        return parentType;
    }

    @Override
    public List<CouTypeSonEntity> childType(Integer couParentType) {
        String key = COURSE_CHILD_TYPE_KEY + couParentType;
        String childTypeJson = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(childTypeJson)) {
            return JSONUtil.toList(childTypeJson, CouTypeSonEntity.class);
        }
        if (childTypeJson != null) {
            return null;
        }
        List<CouTypeSonEntity> couTypeSonEntities = coursMapper.childType(couParentType);
        if (CollectionUtils.isEmpty(couTypeSonEntities)) {
            cacheClient.set(key, "", COURSE_ID_NULL, TimeUnit.SECONDS);
            return null;
        }
        // 存入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(couTypeSonEntities), COURSE_TYPE_TTL, TimeUnit.MINUTES);
        return couTypeSonEntities;
    }

    @Override
    public List<CoursEntity> TeaCourse(Integer begin, Integer count) {
        begin = (begin - 1) * count;
        List<CoursEntity> coursEntities = coursMapper.TeaCourse(begin, count);
        coursEntities
                .forEach(coursEntity -> {
                    Integer teaId = coursEntity.getTeaId();
                    UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
                    coursEntity.setUser(teacherInfo);
                });
        return coursEntities;
    }

    // 获取所有课程(分页)
    @Override
    public Map<String, Object> getAllCourseInPage(Integer begin, Integer count) {
        Map<String, Object> map = new HashMap<>();
        String key = COURSE_PAGE_KEY + begin + ":" + count;
        // 先查redis
        String courseJson = redisTemplate.opsForValue().get(key);
        // 判断是否为空
        if (StrUtil.isNotBlank(courseJson)) {
            // 不为空，直接返回
            List<CoursEntity> coursEntities = JSONUtil.toList(courseJson, CoursEntity.class);
            map.put("courseInfo", coursEntities);
            return map;
        }
        if (courseJson != null) {
            return null;
        }
        // 查数据库
        List<CoursEntity> coursEntities = TeaCourse(begin, count);
        if (CollectionUtils.isEmpty(coursEntities)) {
            // 设置缓存空值
            cacheClient.set(key, "", COURSE_PAGE_NULL, TimeUnit.SECONDS);
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            // 获取课程大类编号
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取课程小类编号
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);

            // 转成集合
            Long couId = coursEntity.getCouId();
            List<CoursTypeListDTO> coursTypeListDTO = coursMapper.CourseTypeToList(couId);
            coursEntity.setCouTypeToList(coursTypeListDTO);

            // 获取该课程章节数量
            int chapterNum = coursMapper.GetAllChapterNum(couId);
            // 更新到课程数量中
            coursMapper.updateCouCataNum(couId, chapterNum);
            coursEntity.setCouCataNum(chapterNum);
        }


        // 存进redis
        cacheClient.set(key, JSONUtil.toJsonStr(coursEntities), COURSE_PAGE_TTL, TimeUnit.MINUTES);

        map.put("courseInfo", coursEntities);
        // 返回
        return map;
    }

    /**
     * 我的课程分页（教师）
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> myCoursInPage(Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = COURSE_TEA_PAGE_KEY + user.getId() + ":" + pageNum + ":" + pageSize;
        pageNum = (pageNum - 1) * pageSize;
        String json = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            // 不为空，直接返回
            List<CoursEntity> coursEntities = JSONUtil.toList(json, CoursEntity.class);
            map.put("MyCourseInfo", coursEntities);
            return map;
        }
        if (json != null) {
            return null;
        }

        List<CoursEntity> coursEntities = coursMapper.myCours(user.getId(), pageNum, pageSize);
        // 查数据库
        if (CollectionUtils.isEmpty(coursEntities)) {
            // 设置缓存空值
            cacheClient.set(key, "", COURSE_PAGE_NULL, TimeUnit.SECONDS);
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            // 获取父类
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取子类
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);

            // 转成集合
            Long couId = coursEntity.getCouId();
            List<CoursTypeListDTO> coursTypeListDTO = coursMapper.CourseTypeToList(couId);
            coursEntity.setCouTypeToList(coursTypeListDTO);

            // 获取该课程章节数量
            int chapterNum = coursMapper.GetAllChapterNum(couId);
            // 更新到课程数量中
            coursMapper.updateCouCataNum(couId, chapterNum);
            coursEntity.setCouCataNum(chapterNum);
        }
        // 写入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(coursEntities), COURSE_PAGE_TTL, TimeUnit.MINUTES);
        map.put("MyCourseInfo", coursEntities);
        return map;
    }

    /**
     * 计算数量
     * @param teaId
     * @return
     */
    @Override
    public int MyCourseCount(Integer teaId) {
        String key = MY_COURSE_PAGE_NUM_KEY + teaId;
        String json = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            // 不为空
            return Integer.valueOf(json);
        }
        // 查询数据库
        int num = coursMapper.MyCourseCount(teaId);
        redisTemplate.opsForValue().set(key, StrUtil.toString(num), ALL_COURS_NUM_TTL, TimeUnit.MINUTES);
        return num;
    }

    @Override
    public int CoursByIdNum(Long couId) {
        return coursMapper.CoursByIdNum(couId);
    }

    /**
     * 该教师名称搜索课程总数
     * @param couName
     * @return
     */
    @Override
    public int MyCourseCountByNameNum(String couName) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return coursMapper.MyCourseCountByNameNum(user.getId(), couName);
    }

    //  通过课程ID获取我的课程
    @Override
    public List<CoursEntity> getMyCourseById(Long couId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CoursEntity> coursEntities = coursMapper.CoursById(user.getId(), couId);
        if (CollectionUtils.isEmpty(coursEntities)) {
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            Integer teaId = coursEntity.getTeaId();
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId);
            coursEntity.setUser(teacherInfo);

            // 获取父类
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            // 获取子类
            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        return coursEntities;
    }

    /**
     * 根据id查询的课程数量（用于分页）
     * @param courId
     * @return
     */
    @Override
    public CommonResult<Integer> GetAllCoursByIdNum(Long courId) {
        int Num = coursMapper.GetAllCoursByIdNum(courId);
        if (Num == 0) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据!");
        }
        return CommonResult.success(Num, "查询成功!");
    }

    /**
     * 根据id查询的我的课程数量（用于分页）
     * @param couId
     * @return
     */
    @Override
    public CommonResult<Integer> GetMyCoursByIdNum(Long couId) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int Num = coursMapper.GetMyCoursByIdNum(couId, user.getId());
        if (Num == 0) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无数据!");
        }
        return CommonResult.success(Num, "查询成功!");
    }

    /**
     * 全部课程的数量
     * @return
     */
    @Override
    public CommonResult<Integer> GetAllCoursNum() {
        // 从redis获取
        String Num = redisTemplate.opsForValue().get(ALL_COURS_NUM_KEY);
        // 有则直接返回
        if (StrUtil.isNotBlank(Num)) {
            Integer num = Integer.valueOf(Num);
            return CommonResult.success(num, "拿取成功!");
        }
        // 判断命中的是否空值 ""
        if (Num != null) {
            // 返回错误信息
            return null;
        }
        // 没有 查询数据库
        int num = coursMapper.GetAllCoursNum();
        if (num == 0) {
            // 防止缓存穿透，使用空值缓存方法
            redisTemplate.opsForValue().set(ALL_COURS_NUM_KEY, "", COURSE_ID_NULL, TimeUnit.SECONDS);
            return CommonResult.failed(ResultCode.VALIDATE_FAILED,"暂无数据!");
        }
        // 添加redis
        redisTemplate.opsForValue().set(ALL_COURS_NUM_KEY, StrUtil.toString(num), ALL_COURS_NUM_TTL, TimeUnit.MINUTES);
        // 返回数据
        return CommonResult.success(num, "查询成功!");
    }

    /**
     * 获取全部课程
     * @return
     */
    @Override
    public CommonResult<?> getAllCourse(Long classId) {
        List<Long> classAllCourse = coursMapper.getClassAllCourse(classId);
        List<Long> classCourses = classAllCourse.stream()
                .distinct().collect(Collectors.toList());
        List<CourseDTO> allCourse = coursMapper.getAllCourse(classCourses);
        if (CollectionUtils.isEmpty(allCourse)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无课程信息");
        }
        allCourse
                .forEach(courseDTO -> {
                    UserDto trueName = serverUserClient.getTrueName(courseDTO.getTeaId());
                    courseDTO.setUserDto(trueName);
                });
        return CommonResult.success(allCourse);
    }

    /**
     * 查看分类课程
     * @param couType
     * @return
     */
    @Override
    public CommonResult<?> getAllCourseByType(Integer couType) {
        List<CoursEntity> allCourseByType = coursMapper.getAllCourseByType(couType);
        if (CollectionUtils.isEmpty(allCourseByType)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无课程信息");
        }
        return CommonResult.success(allCourseByType);
    }

    /**
     * 批量新增班级课程
     * @param coursEntities
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> InsertMoreCoursClass(List<CouClassEntity> coursEntities) {
        try {

            boolean success = coursMapper.InsertMoreCoursClass(coursEntities);
            return CommonResult.success(success);
        }catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException();
        }
    }


    // 新增本班级课程
    @Override
    @Transactional
    public Boolean insertCoursClass(Long couId, Long classId) {
        CouClassEntity couClassEntity = sameCours(couId, classId);
        if (couClassEntity != null){
            return false;
        }
        return coursMapper.insertCoursClass(couId, classId);
    }


    // 根据名字查询我创建的课程 分页
    @Override
    public List<CoursEntity> MyCoursByName(String couName, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum - 1) * pageSize;
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CoursEntity> coursEntities = coursMapper.likeCours(user.getId(), couName, pageNum, pageSize);
        if (CollectionUtils.isEmpty(coursEntities)) {
            return null;
        }
        for (CoursEntity coursEntity: coursEntities) {
            Integer teaId1 = coursEntity.getTeaId();
            UserEntity teacherInfo = serverUserClient.getTeacherInfo(teaId1);
            coursEntity.setUser(teacherInfo);

            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        return coursEntities;
    }

    @Override
    public CouClassEntity sameCours(Long couId, Long classId) {
        return coursMapper.sameCours(couId, classId);
    }

    /**
     * 通过课程id查询我创建的课程
     * @param id
     * @param couId
     * @return
     */
    @Override
    public List<CoursEntity> CoursById(Integer id, Long couId) {
        List<CoursEntity> entityList = coursMapper.CoursById(id, couId);
        if (CollectionUtils.isEmpty(entityList)) {
            return null;
        }
        for (CoursEntity coursEntity: entityList) {
            Integer couType = coursEntity.getCouType();
            CoursTypeEntity coursTypeEntity = coursMapper.coursType(couType);
            coursEntity.setCoursTypeEntity(coursTypeEntity);

            Integer couChileType = coursEntity.getCouChileType();
            CouTypeSonEntity couTypeSonEntity = coursMapper.couTypeSon(couChileType);
            coursEntity.setCouTypeSonEntity(couTypeSonEntity);
        }
        return entityList;
    }



}
