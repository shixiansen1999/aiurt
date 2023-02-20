package com.aiurt.modules.signmeeting.vo;

import java.util.List;

import com.aiurt.modules.signmeeting.entity.Conferee;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 会议签到
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Data
@ApiModel(value="sign_meetingPage对象", description="会议签到")
public class SignMeetingPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**会议时间*/
	@Excel(name = "会议时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "会议时间")
    private java.util.Date meetingTime;
	/**会议地点*/
	@Excel(name = "会议地点", width = 15)
	@ApiModelProperty(value = "会议地点")
    private java.lang.String place;
	/**会议内容*/
	@Excel(name = "会议内容", width = 15)
	@ApiModelProperty(value = "会议内容")
    private java.lang.String content;
	/**实际参加人数*/
	@Excel(name = "实际参加人数", width = 15)
	@ApiModelProperty(value = "实际参加人数")
    private java.lang.Integer attendance;
	/**附件*/
	@Excel(name = "附件", width = 15)
	@ApiModelProperty(value = "附件")
    private java.lang.String attachmentString;

	@ExcelCollection(name="参会人员")
	@ApiModelProperty(value = "参会人员")
	private List<Conferee> confereeList;

}
