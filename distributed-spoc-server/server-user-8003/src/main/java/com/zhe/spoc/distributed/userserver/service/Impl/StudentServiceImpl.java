package com.zhe.spoc.distributed.userserver.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.UserDto_1;
import com.zhe.common.entity.*;
import com.zhe.spoc.distributed.client.ServerClassClient;
import com.zhe.spoc.distributed.client.ServerUserClient;
import com.zhe.spoc.distributed.userserver.dao.RoleDao;
import com.zhe.spoc.distributed.userserver.dao.SignMapper;
import com.zhe.spoc.distributed.userserver.dao.StudentDao;
import com.zhe.spoc.distributed.userserver.dao.UserDao;
import com.zhe.spoc.distributed.userserver.service.SignService;
import com.zhe.spoc.distributed.userserver.service.StudentService;
import com.zhe.spoc.distributed.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zhe.common.utils.RedisConstants.USER_SIGN_KEY;

/**
 * @author HouEnZhu
 * @ClassName StudentServiceImpl
 * @Description TODO
 * @date 2022/10/11 19:11
 * @Version 1.0
 */

@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentDao, UserEntity> implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private RoleDao roleDao;


    @Autowired
    private ServerClassClient serverClassClient;

    @Autowired
    private SignService signService;

    @Autowired
    private SignMapper signMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 该班级的学生名单
    @Override
    public List<UserEntity> studentList(Long classId) {
        return studentDao.studentList(classId);
    }


    @Override
    public StudentEntity StudentInfo(Integer stuId) {
        return studentDao.StudentInfo(stuId);
    }

    // 学生查看个人信息
    @Override
    public Map<String, Object> StudentInfo() {
        Map<String, Object> map = new HashMap<>();
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        UserEntity userEntity = studentDao.getStuUserInfo(userId);
        RoleEntity role = roleDao.getRole(userId);
        StudentEntity studentEntity = StudentInfo(userId);
        studentEntity.setUserDto_1(BeanUtil.copyProperties(userEntity, UserDto_1.class));
        map.put("role", role);
        map.put("StuInfo", studentEntity);
        return map;
    }

    /**
     * 学生签到功能
     * @return true
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> sign() {
        // 1. 先获取个人信息
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        // 2. 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 3. 拼接key
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5. 写入redis  SETBIT KEY offset 1
        redisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);

        // 获取现在的月份
        int monthValue = now.getMonthValue();

        // 获取现在的年份
        int year = now.getYear();

        // 持久化到数据库
        SignEntity signEntity = new SignEntity();
        signEntity.setUserId(userId).setMonth(monthValue).setYear(year);
        signService.save(signEntity);
        // 5. 获取本月截止今天为止的所有的签到记录  返回的是一个十进制的数字  BITFIELD sign:9:202211 GET u16 0
        List<Long> result = redisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return CommonResult.success(true, "本月还没有签到记录");
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return CommonResult.success(true);
        }
        // 6. 循环遍历
        int count = 0;
        double score = 0;
        while (true) {
            // 7. 让这个数字与1做与运算，得到数字的最后一个bit位 // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0 说明未签到 结束
                break;
            }else {
                // 如果为1 说明已签到 计数器 + 1
                count++;
            }
            // 把数字右移一位，抛弃最后一位 继续下一个bit位
            num >>>= 1;
        }
        if (count == 1) {
            score = 10;
            studentDao.updateScore(score, userId);
        }else {
            score = 15;
            studentDao.updateScore(score, userId);
        }
        return CommonResult.success(true, "签到成功!连续签到增加" + score + "积分");
    }

    /**
     * 获取连续签到的数据
     * @return num
     */
    @Override
    public CommonResult<Integer> signCount() {
        // 1. 先获取个人信息
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        // 2. 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 3. 拼接key
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();

        // 5. 获取本月截止今天为止的所有的签到记录  返回的是一个十进制的数字  BITFIELD sign:9:202211 GET u16 0
        List<Long> result = redisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return CommonResult.success(0, "本月还没有签到记录");
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return CommonResult.success(0);
        }
        // 6. 循环遍历
        int count = 0;
        while (true) {
            // 7. 让这个数字与1做与运算，得到数字的最后一个bit位 // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0 说明未签到 结束
                break;
            }else {
                // 如果为1 说明已签到 计数器 + 1
                count++;
            }
            // 把数字右移一位，抛弃最后一位 继续下一个bit位
            num >>>= 1;
        }
        return CommonResult.success(count,"连续签到了" + count + "天");
    }

    @Override
    public UserEntity getMyUserInfo(Integer stuId) {
        return studentDao.getStuUserInfo(stuId);
    }

    @Override
    public CommonResult<?> getAllStu() {
        // 拿到有班级的学生
        List<StudentEntity> studentEntities = new ArrayList<>();
        List<Integer> AllIds = studentDao.StudentClass();
        List<Integer> ids = AllIds.stream()
                .distinct()
                .collect(Collectors.toList());
        List<Integer> allStudent = studentDao.getAllStudent(ids);
        if (allStudent == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂无学生信息!");
        }
        for (Integer stuId: allStudent) {
            StudentEntity studentEntity = StudentInfo(stuId);
            studentEntities.add(studentEntity);
        }
        return CommonResult.success(studentEntities);
    }

    /**
     * 累计签到
     * @return
     */
    @Override
    public CommonResult<?> AllCount() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        int num = signMapper.AllCount(userId);
        return CommonResult.success(num);
    }

}
