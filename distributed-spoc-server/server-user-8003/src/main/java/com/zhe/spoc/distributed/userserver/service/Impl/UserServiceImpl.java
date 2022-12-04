package com.zhe.spoc.distributed.userserver.service.Impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.dto.UserDto;
import com.zhe.common.entity.TeacherEntity;
import com.zhe.common.entity.UserEntity;
import com.zhe.spoc.distributed.userserver.dao.StudentDao;
import com.zhe.spoc.distributed.userserver.dao.UserDao;
import com.zhe.spoc.distributed.userserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhe.common.utils.SystemConstants.TRUE_NAME_PREFIX;
import static com.zhe.common.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * @author HouEnZhu
 * @ClassName UserServiceImpl
 * @Description TODO
 * @date 2022/10/15 11:33
 * @Version 1.0
 */

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Integer> getTeacherId() {
        return userDao.getTeacherId();
    }

    // 获取教师信息
    @Override
    public UserEntity findById(Integer teaId) {
        return userDao.findById(teaId);
    }

    //根据id查找学生
    @Override
    public UserEntity findByStuId(Integer stu_id) {
        return userDao.findByStuId(stu_id);
    }

    //根据姓名查找学生
    @Override
    public List<UserEntity> findByStuName(String stu_name) {
        List<UserEntity> byStuName = userDao.findByStuName(stu_name);
        if (CollectionUtils.isEmpty(byStuName)){
            return null;
        }
        return byStuName;
    }

    @Override
    public UserEntity userId(String fullname) {
        UserEntity userEntity = userDao.userId(fullname);
        if (userEntity == null){
            return null;
        }
        return userEntity;
    }

    @Override
    public Boolean InsertMoreUser(List<UserEntity> user) {
        for (UserEntity userEntity : user) {
            String passwordEncode = passwordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(passwordEncode);
        }
        return userDao.InsertMoreUser(user);
    }

    /**
     * 获取真实姓名
     * @param userId
     * @return
     */
    @Override
    public UserDto getTrueName(Integer userId) {
        return userDao.getTrueName(userId);
    }

    /**
     * 更换头像
     * @param photo
     * @return
     */
    @Override
    public CommonResult<Boolean> uploadPhoto(String photo) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = user.getId();
        Boolean success = userDao.updatePhoto(photo, userId);
        if (BooleanUtil.isFalse(success)) {
            return CommonResult.failed(ResultCode.FAILED,"出错啦!");
        }
        return CommonResult.success(true, "更换头像成功!");
    }


    @Override
    public CommonResult<?> getAllTeacher() {
        List<TeacherEntity> teacherEntities = userDao.allTea();
        if (CollectionUtils.isEmpty(teacherEntities)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, null);
        }
        return CommonResult.success(teacherEntities);
    }

    /**
     * 随机批量添加学生
     * @param num
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> AddMorePersonAuto(Integer num) {
        String password = "123";
        String username;
        String fullname;
        String phone;
        List<UserEntity> userEntityList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            UserEntity user = new UserEntity();
            // 加密
            password = passwordEncoder.encode(password);
            username = USER_NICK_NAME_PREFIX + RandomUtil.randomString(15);
            fullname = TRUE_NAME_PREFIX + RandomUtil.randomNumbers(5);
            phone = RandomUtil.randomNumbers(11);
            user.setPassword(password)
                    .setUsername(username)
                    .setFullname(fullname)
                    .setPhone(phone);
            userEntityList.add(user);
        }
        userDao.InsertMoreUser(userEntityList);
        List<Integer> allStuId = userDao.getAllStuId();
        // 拿到全部用户id
        List<Integer> userIds = allStuId.stream()
                .distinct()
                .collect(Collectors.toList());
        // 拿到没有身份的用户id
        List<Integer> allUserId = userDao.getAllUserId(userIds);
        userDao.insertStu(allUserId);
        List<UserEntity> allUserInfo = userDao.getAllUserInfo(userIds);
        boolean success = studentDao.insertStudentSedule(allUserInfo);
        return CommonResult.success(success, "插入成功!");
    }

}
