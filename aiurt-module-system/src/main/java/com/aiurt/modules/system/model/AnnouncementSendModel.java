package com.aiurt.modules.system.model;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Data
public class AnnouncementSendModel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**通告id*/
	@ApiModelProperty("通告id")
	private java.lang.String anntId;
	/**用户id*/
	@ApiModelProperty("用户id")
	private java.lang.String userId;
	/**标题*/
	@ApiModelProperty("标题")
	private java.lang.String titile;
	/**内容*/
	@ApiModelProperty("内容")
	private java.lang.String msgContent;
	/**发布人*/
	@ApiModelProperty("发布人")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
	private java.lang.String sender;
	/**优先级（L低，M中，H高）*/
	@ApiModelProperty("优先级（L低，M中，H高）")
	@Dict(dicCode = "priority")
	private java.lang.String priority;
	/**阅读状态*/
	@ApiModelProperty("阅读状态0未读1已读")
	@Dict(dicCode ="read_flag")
	private java.lang.String readFlag;
	/**发布时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("发布时间")
	private java.util.Date sendTime;
	/**页数*/
	private java.lang.Integer pageNo;
	/**大小*/
	private java.lang.Integer pageSize;
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
     * 消息类型1:通知公告2:系统消息
     */
	@ApiModelProperty("消息类型1:通知公告2:系统消息3特情消息")
	@Dict(dicCode = "msg_category")
    private java.lang.String msgCategory;
	/**
	 * 业务id
	 */
	@ApiModelProperty("业务id")
	private java.lang.String busId;
	/**
	 * 业务类型
	 */
	@ApiModelProperty("业务类型")
	private java.lang.String busType;
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
	 * 业务类型查询（0.非bpm业务）
	 */
	@ApiModelProperty("业务类型查询（0.非bpm业务）")
	private java.lang.String bizSource;

	/**
	 * 通告对象类型（USER:指定用户，ALL:全体用户）
	 */
	@Dict(dicCode = "msg_type")
	@ApiModelProperty("通告对象类型")
	private java.lang.String msgType;

	/**
	 * 指定用户
	 **/
	@ApiModelProperty("指定用户")
	private java.lang.String userIds;

	/**
	 * 指定接收范围
	 **/
	@TableField(exist = false)
	@ApiModelProperty("指定接收范围")
	private java.lang.String userNames;

	/**
	 * 摘要
	 */
	@ApiModelProperty("摘要")
	private java.lang.String msgAbstract;

	private List<String> msgCategoryList;
}
