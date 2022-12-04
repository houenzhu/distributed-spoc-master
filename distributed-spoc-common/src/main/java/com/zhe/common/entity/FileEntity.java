package com.zhe.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouEnZhu
 * @ClassName FIleEntity
 * @Description 图片链接上传表
 * @date 2022/11/8 22:00
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("file")
public class FileEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件上传地址
     */
    private String url;

    /**
     * 文件是否删除
     */
    private boolean is_delete;

    /**
     * 是否禁用
     */
    private boolean enable;

    /**
     * md5
     */
    private String md5;

    /**
     * 上传日期
     */
    private LocalDateTime upload_time;
}
