package com.aiurt.modules.system.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Data
@TableName("sys_announcement")
@ApiModel(value="sys_announcement对象", description="系统通告表")
public class SysAnnouncement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private java.lang.String id;
    /**
     * 标题
     */
    @Excel(name = "标题", width = 15)
    @ApiModelProperty("标题")
    private java.lang.String titile;
    /**
     * 内容
     */
    @Excel(name = "内容", width = 30)
    @ApiModelProperty("内容")
    private java.lang.String msgContent;
    /**
     * 开始时间
     */
    @Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private java.util.Date startTime;
    /**
     * 结束时间
     */
    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private java.util.Date endTime;
    /**
     * 发布人
     */
    @Excel(name = "发布人", width = 15)
    @ApiModelProperty("发布人")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private java.lang.String sender;
    /**
     * 优先级（L低，M中，H高）
     */
    @Excel(name = "优先级（L低，M中，H高,）", width = 15, dicCode = "priority")
    @ApiModelProperty("优先级（L低，M中，H高,）")
    @Dict(dicCode = "priority")
    private java.lang.String priority;

    /**
     * 消息类型1:通知公告2:系统消息
     */
    @Excel(name = "消息类型", width = 15, dicCode = "msg_category")
    @Dict(dicCode = "msg_category")
    @ApiModelProperty("消息类型")
    private java.lang.String msgCategory;
    /**
     * 通告对象类型（USER:指定用户，ALL:全体用户）
     */
    @Excel(name = "通告对象类型", width = 15, dicCode = "msg_type")
    @Dict(dicCode = "msg_type")
    @ApiModelProperty("通告对象类型")
    private java.lang.String msgType;
    /**
     * 发布状态（0未发布，1已发布，2已撤销）
     */
    @Excel(name = "发布状态", width = 15, dicCode = "send_status")
    @Dict(dicCode = "send_status")
    @ApiModelProperty("发布状态")
    private java.lang.String sendStatus;
    /**
     * 发布时间
     */
    @Excel(name = "发布时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发布时间")
    private java.util.Date sendTime;
    /**
     * 撤销时间
     */
    @Excel(name = "撤销时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("撤销时间")
    private java.util.Date cancelTime;
    /**
     * 删除状态（0，正常，1已删除）
     */
    private java.lang.String delFlag;
    /**
     * 创建人
     */
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * 指定用户
     **/
    @ApiModelProperty("指定用户")
    private java.lang.String userIds;

    /**
     * 指定范围（组织机构）
     **/
    @ApiModelProperty("指定范围（组织机构）")
    private java.lang.String orgIds;

    /**
     * 业务类型(email:邮件 bpm:流程)
     */
    @ApiModelProperty("业务类型")
    private java.lang.String busType;
    /**
     * 业务id
     */
    @ApiModelProperty("业务id")
    private java.lang.String busId;
    /**
     * 打开方式 组件：component 路由：url
     */
    @ApiModelProperty("打开方式 组件：component 路由：url")
    private java.lang.String openType;
    /**
     * 组件/路由 地址
     */
    @ApiModelProperty("组件/路由 地址")
    private java.lang.String openPage;
    /**
     * 摘要
     */
    @ApiModelProperty("摘要")
    private java.lang.String msgAbstract;
    /**
     * 钉钉task_id，用于撤回消息
     */
    private java.lang.String dtTaskId;

    /**
     * 指定接收范围
     **/
    @TableField(exist = false)
    private java.lang.String userNames;

    /**
     * 查询发布时间的起始时间
     */
    @Excel(name = "查询发布时间的起始时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private String sTime;
    /**
     * 查询发布时间的结束时间
     */
    @Excel(name = "查询发布时间的结束时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private String eTime;


    /**
     * 特情等级
     */
    @Excel(name = "特情等级", width = 15, dicCode = "level")
    @Dict(dicCode = "level")
    @ApiModelProperty("特情等级")
    private java.lang.String level;
}
