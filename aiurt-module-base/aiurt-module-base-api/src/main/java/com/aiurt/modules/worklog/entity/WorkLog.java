package com.aiurt.modules.worklog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
@TableName("t_work_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="t_work_log对象", description="工作日志")
public class WorkLog {

    /**主键id,自动递增*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id,自动递增")
    private  Long  id;

    /**日志编号*/
    @Excel(name = "日志编号", width = 15)
    @ApiModelProperty(value = "日志编号")
    private  String  code;

    /**巡检编号*/
    @Excel(name = "巡检编号", width = 15)
    @ApiModelProperty(value = "巡检编号")
    private  String  patrolIds;

    /**巡检内容*/
    @Excel(name = "巡检内容", width = 15)
    @ApiModelProperty(value = "巡检内容")
    private  String  patrolContent;

    /**检修编号*/
    @Excel(name = "检修编号", width = 15)
    @ApiModelProperty(value = "检修编号")
    private  String  repairCode;

    /**检修内容*/
    @Excel(name = "检修内容", width = 15)
    @ApiModelProperty(value = "检修内容")
    private  String  repairContent;

    /**故障编号*/
    @Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
    private  String  faultCode;

    /**故障内容*/
    @Excel(name = "故障内容", width = 15)
    @ApiModelProperty(value = "故障内容")
    private  String  faultContent;

    /**提交状态:0-未提交 1-已提交*/
    @Excel(name = "提交状态:0-未提交 1-已提交", width = 15)
    @ApiModelProperty(value = "提交状态:0-未提交 1-已提交")
    private  Integer  status;

    /**审核状态:0-未审核 1-已审核*/
    @Excel(name = "审核状态:0-未审核 1-已审核", width = 15)
    @ApiModelProperty(value = "审核状态:0-未审核 1-已审核")
    private  Integer  checkStatus;

    /**确认状态:0-未确认 1-已确认*/
    @Excel(name = "确认状态:0-未确认 1-已确认", width = 15)
    @ApiModelProperty(value = "确认状态:0-未确认 1-已确认")
    private  Integer  confirmStatus;

    /**提交人id*/
    @Excel(name = "提交人id", width = 15)
    @ApiModelProperty(value = "提交人id")
    private  String  submitId;

    /**提交时间*/
    @Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    /**日期*/
    @Excel(name = "日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    @NotNull(message = "日期不能为空")
    private  Date  logTime;

    /**工作内容*/
    @Excel(name = "工作内容", width = 15)
    @ApiModelProperty(value = "工作内容")
    @NotNull(message = "工作内容不能为空")
    private  Object  workContent;

    /**交接班内容*/
    @Excel(name = "交接班内容", width = 15)
    @ApiModelProperty(value = "交接班内容")
    private  Object  content;

    /**附件链接*/
    @Excel(name = "附件链接", width = 15)
    @ApiModelProperty(value = "附件链接")
    private  String  url;

    /**接班人id*/
    @Excel(name = "接班人id", width = 15)
    @ApiModelProperty(value = "接班人id")
    private  String  succeedId;

    /**接班人确认时间*/
    @Excel(name = "接班人确认时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接班人确认时间")
    private  Date  succeedTime;

    /**审批人*/
    @Excel(name = "审批人", width = 15)
    @ApiModelProperty(value = "审批人")
    private  String  approverId;

    /**审批时间*/
    @Excel(name = "审批时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
    private  Date  approvalTime;

    /**删除状态:0.未删除 1已删除*/
    @Excel(name = "删除状态:0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态:0.未删除 1已删除")
    @TableLogic
    private  Integer  delFlag;

    /**所在班组*/
    @Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
    private  String  orgId;

    /**创建时间,CURRENT_TIMESTAMP*/
    @Excel(name = "创建时间,CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
    private  Date  createTime;

    /**修改时间,根据当前时间戳更新*/
    @Excel(name = "修改时间,根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间,根据当前时间戳更新")
    private  Date  updateTime;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    /**配合施工时间*/
    @Excel(name = "配合施工时间", width = 15)
    @ApiModelProperty(value = "配合施工时间")
    private  String  assortTime;

    /**配合施工地点*/
    @Excel(name = "配合施工地点", width = 15)
    @ApiModelProperty(value = "配合施工地点")
    private  String  assortLocation;

    /**配合施工单位*/
    @Excel(name = "配合施工单位", width = 15)
    @ApiModelProperty(value = "配合施工单位")
    private  String  assortUnit;

    /**配合施工参与人*/
    @Excel(name = "配合施工参与人", width = 15)
    @ApiModelProperty(value = "配合施工参与人")
    private  String  assortIds;

    /**配合施工人次*/
    @Excel(name = "配合施工人次", width = 15)
    @ApiModelProperty(value = "配合施工人次")
    private  Integer  assortNum;

    /**配合施工内容*/
    @Excel(name = "配合施工内容", width = 15)
    @ApiModelProperty(value = "配合施工内容")
    private  Object  assortContent;




    public static final String ID = "id";
    private static final String PATROL_CODE = "patrol_code";
    private static final String REPAIR_CODE = "repair_code";
    private static final String FAULT_CODE = "fault_code";
    private static final String STATUS = "status";
    private static final String SUBMIT_ID = "submit_id";
    private static final String SUBMIT_TIME = "submit_time";
    private static final String WORK_CONTENT = "work_content";
    private static final String CONTENT = "content";
    private static final String URL = "url";
    private static final String SUCCEED_ID = "succeed_id";
    private static final String SUCCEED_TIME = "succeed_time";
    private static final String APPROVER_ID = "approver_id";
    private static final String APPROVAL_TIME = "approval_time";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";


}

