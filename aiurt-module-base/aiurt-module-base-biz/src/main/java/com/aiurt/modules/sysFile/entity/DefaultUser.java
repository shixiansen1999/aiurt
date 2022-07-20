package com.aiurt.modules.sysFile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @description: DefaultUser
 * @author: Mr.zhao
 * @date: 2021/11/22 14:50
 */

@ApiModel(value="默认人员常用表")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "default_user")
public class DefaultUser {

    /**
     * 人员名称
     */
    @TableField(exist = false)
    private String userName;


    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键id")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private String userId;

    /**
     * 常用用户id
     */
    @TableField(value = "default_id")
    @ApiModelProperty(value="常用用户id")
    private String defaultId;

    /**
     * 删除状态：0.未删除 1已删除
     */
    @TableField(value = "del_flag")
    @ApiModelProperty(value="删除状态：0.未删除 1已删除")
    private int delFlag;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    @ApiModelProperty(value="创建人")
    private String createBy;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    @ApiModelProperty(value="修改人")
    private String updateBy;

    /**
     * 创建时间，CURRENT_TIMESTAMP
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间，CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 修改时间，根据当前时间戳更新
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间，根据当前时间戳更新")
    private Date updateTime;

}
