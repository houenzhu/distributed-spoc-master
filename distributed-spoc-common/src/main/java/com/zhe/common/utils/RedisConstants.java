package com.zhe.common.utils;

/**
 * @author HouEnZhu
 * @ClassName RedisConstants常量
 * @Description TODO
 * @date 2022/11/4 14:14
 * @Version 1.0
 */

public class RedisConstants {

    public static final String COURSE_TYPE_KEY = "course:type:"; // 父类大类的分类
    public static final String COURSE_CHILD_TYPE_KEY = "course:child:type:"; // 父类大类的分类
    public static final Long COURSE_TYPE_TTL = 30L; // 设置父类过期时间
    public static final String COURSE_ID_KEY = "course:id:"; // 根据id查询课程的信息的保存
    public static final String PRACTICE_ID_KEY = "practice:id:"; // 根据id查询线下实践的信息的保存
    public static final String COURSE_PAGE_KEY = "course:page:"; // 课程分页数据保存
    public static final String COURSE_TEA_PAGE_KEY = "course:tea:page:"; // 课程分页数据保存
    public static final String ALL_COURS_NUM_KEY = "course:num"; // 全部课程数量
    public static final String MY_COURSE_PAGE_NUM_KEY = "course:page:num:"; // 我的课程分页数量
    public static final String MY_COURSE_PAGE_BY_NAME_KEY = "my:course:name:page:"; // 我的课程(名称查询)分页数据保存
    public static final String DELETE_COURSE_PAGE_KEY = "course:page:"; // 删除课程分页数据保存
    public static final String BUY_COURSE_BY_KEY = "shop:user"; // 学生购买课程分布式锁保存
    public static final String MY_COURSE_PROGRESS_KEY = "progress:stu:"; // 学生课程进度
    public static final Long COURSE_PAGE_TTL = 15L; //设置课程分页数据信息的过期时间
    public static final Long COURSE_ID_TTL = 15L; // 设置根据id查询课程的信息的过期时间
    public static final Long ALL_COURS_NUM_TTL = 15L; // 设置全部课程数量信息的过期时间
    public static final Long PRACTICE_ID_TTL = 15L; // 设置根据id查询课程的信息的过期时间
    public static final Long COURSE_ID_NULL = 15L; // 设置根据id查询课程的信息空值的过期时间
    public static final Long PRACTICE_ID_NULL = 15L; // 设置根据id查询线下实践的信息空值的过期时间
    public static final Long COURSE_PAGE_NULL = 15L; // 设置课程分页数据信息空值的过期时间
    public static final Long BUY_COURSE_BY_KEY_TTL = 5L; // 学生购买课程分布式锁过期时间
    public static final Long MY_COURSE_PROGRESS_NULL_KEY_TTL = 5L; // 学生课程进度空值过期时间
    public static final Long MY_COURSE_PROGRESS_KEY_TTL = 15L; // 学生课程进度过期时间

    public static final String USER_SIGN_KEY = "sign:"; // 签到
    public static final String BLOG_LIKED_KEY = "blog:liked:"; // 点赞资讯
    public static final String INFO_COMMENT_LIKED_KEY = "infoComment:liked:"; // 点赞资讯的评论


}
