package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouEnZhu
 * @ClassName ChapterEntity
 * @Description TODO
 * @date 2022/10/22 15:22
 * @Version 1.0
 */

@TableName("chapter")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChapterEntity implements Serializable {

    /**
     * 章节id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("cha_id")
    private Long chaId;

    /**
     * 章节顺序
     */
    @TableField("cha_index")
    private Integer chaIndex;

    /**
     * 章节标题
     */
    @TableField("cha_title")
    private String chaTitle;

    /**
     * 课程id
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 章节地址
     */
    @TableField("cha_url")
    private String chaUrl;

    /**
     * 章节时长
     */
    @TableField("cha_time")
    private int chaTime;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
}
