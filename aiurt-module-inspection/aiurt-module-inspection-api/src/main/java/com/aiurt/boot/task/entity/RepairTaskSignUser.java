package com.aiurt.boot.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * repair_task_sign_user表对象
 *
 * @author 华宜威
 * @date 2023-06-16 17:57:07
 */
@Data
@TableName("repair_task_sign_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "repair_task_sign_user对象", description = "repair_task_sign_user")
public class RepairTaskSignUser {
    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**检修任务id*/
    @ApiModelProperty(value = "检修任务id")
    private String repairTaskId;
    /**签名的用户id*/
    @ApiModelProperty(value = "签名的用户id")
    private String userId;
    /**签名的用户姓名*/
    @ApiModelProperty(value = "签名的用户姓名")
    private String realname;
    /**用户的签名url地址*/
    @ApiModelProperty(value = "用户的签名url地址")
    private String signUrl;
    /**是检修人还是同行人，0检修人，1同行人*/
    @ApiModelProperty(value = "是检修人还是同行人，0检修人，1同行人")
    private Integer isPeer;
    /**删除状态： 0未删除 1已删除*/
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
