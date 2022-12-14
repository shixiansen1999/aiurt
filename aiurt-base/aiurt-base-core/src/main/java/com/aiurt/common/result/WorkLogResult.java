package com.aiurt.common.result;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("t_work_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="t_work_log对象", description="工作日志")
public class WorkLogResult {

    /**主键id*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
    private  String  id;

    /**线路*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路")
    private  String  lineName;

    /**提交人班组*/
    @Excel(name = "班组", width = 15)
    @ApiModelProperty(value = "班组")
    private  String  submitOrgName;
    /**提交人班组*/
    @Excel(name = "班组", width = 15)
    @ApiModelProperty(value = "班组")
    @TableField(exist = false)
    private  String  submitOrgId;

    /**日志编号*/
    @Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "日志编号")
    private  String  code;

    /**提交人*/
    @Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private  String  submitName;

    /**巡检内容*/
    @Excel(name = "巡检内容", width = 15)
    @ApiModelProperty(value = "巡检内容")
    private  String  patrolContent;

    /**检修内容*/
    @Excel(name = "检修内容", width = 15)
    @ApiModelProperty(value = "检修内容")
    private  String  repairContent;

    /**故障内容*/
    @Excel(name = "故障报修", width = 15)
    @ApiModelProperty(value = "故障报修")
    private  String  faultContent;

    /**工作内容*/
    @Excel(name = "工作内容", width = 15)
    @ApiModelProperty(value = "工作内容")
    private  Object  workContent;

    /**交接班内容*/
    @Excel(name = "交接班内容", width = 15)
    @ApiModelProperty(value = "交接班内容")
    private  Object  content;

    /**提交时间*/
    @Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private  Date  submitTime;

    /**交班人姓名*/
    @Excel(name = "交班人", width = 15)
    @ApiModelProperty(value = "交班人姓名")
    private  String  handoverName;

    /**接班人*/
    @Excel(name = "接班人", width = 15)
    @ApiModelProperty(value = "接班人")
    private  String  succeedName;

    /**巡检编号*/
    @ApiModelProperty(value = "巡检编号")
    private  String  patrolIds;

    /**检修编号*/
    @ApiModelProperty(value = "检修编号")
    private  String  repairCode;

    /**故障编号*/
    @ApiModelProperty(value = "故障编号")
    private  String  faultCode;

    /**提交状态:0-未提交 1-已提交*/
    @ApiModelProperty(value = "提交状态:0-未提交 1-已提交")
    @Dict(dicCode = "work_log_submit_status")
    private  Integer  status;

    /**审核状态:0-未审核 1-已审核*/
    @ApiModelProperty(value = "审核状态:0-未审核 1-已审核")
    @Dict(dicCode = "work_log_audit_status")
    private  Integer  checkStatus;

    /**用户头像*/
    @ApiModelProperty(value = "用户头像")
    private  String  avatar;

    /**确认状态:0-未确认 1-已确认*/
    @ApiModelProperty(value = "确认状态:0-未确认 1-已确认")
    @Dict(dicCode = "work_log_confirm_status")
    private  Integer  confirmStatus;
    /**提交状态描述:0-未提交 1-已提交*/
    @Excel(name = "提交状态", width = 15)
    @ApiModelProperty(value = "提交状态")
    private  String  statusDesc;

    /**确认状态描述*/
    @Excel(name = "交接班确认", width = 15)
    @ApiModelProperty(value = "交接班确认状态")
    private  String  confirmStatusDesc;

    /**审核状态描述*/
    @Excel(name = "审核状态", width = 15)
    @ApiModelProperty(value = "审核状态")
    private  String  checkStatusDesc;

    /**提交人id*/
    @ApiModelProperty(value = "提交人id")
    private  String  submitId;

    /**日期*/
    @ApiModelProperty(value = "日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private  Date  logTime;

    /**接班人id*/
    @ApiModelProperty(value = "接班人id")
    private  String  succeedId;

    /**配合施工时间*/
    @ApiModelProperty(value = "配合施工时间")
    private  String  assortTime;

    /**配合施工地点*/
    @ApiModelProperty(value = "配合施工地点")
    private  String  assortLocation;

    /**配合施工地点*/
    @ApiModelProperty(value = "配合施工地点")
    private  String  assortLocationName;

    /**配合施工单位*/
    @ApiModelProperty(value = "配合施工单位")
    private  String  assortUnit;

    /**配合施工参与人*/
    @ApiModelProperty(value = "配合施工参与人")
    private  String  assortIds;

    /**配合施工参与人姓名*/
    @ApiModelProperty(value = "配合施工参与人姓名")
    private  String  assortNames;

    /**配合施工人次*/
    @ApiModelProperty(value = "配合施工人次")
    private  Integer  assortNum;

    /**配合施工内容*/
    @ApiModelProperty(value = "配合施工内容")
    private  Object  assortContent;
    @ApiModelProperty(value = "配合施工内容")
    private String urlList;

    @ApiModelProperty(value = "签名")
    private String signature;

    public String[] assortTimes;

    /**创建时间,CURRENT_TIMESTAMP*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
    private  Date  createTime;

    /**是否已落实工区消毒(0:否，1：是)*/
    @ApiModelProperty(value = "是否已落实工区消毒(0:否，1：是)")
    private  Integer isDisinfect;

    /**是否已清洁工区卫生(0:否，1：是)*/
    @ApiModelProperty(value = "是否已清洁工区卫生(0:否，1：是)")
    private  Integer isClean;

    /**班组上岗人员体温情况(0:异常，1：正常)*/
    @ApiModelProperty(value = "班组上岗人员体温情况(0:异常，1：正常)")
    private  Integer  isAbnormal;

    /**是否有应急处置情况(0:否，1：是)*/
    @ApiModelProperty(value = "是否有应急处置情况(0:否，1：是)")
    private  Integer  isEmergencyDisposal;

    /**是否进行文件宣贯(0:否，1：是)*/
    @ApiModelProperty(value = "是否进行文件宣贯(0:否，1：是)")
    private  Integer  isDocumentPublicity;

    /**应急情况说明*/
    @ApiModelProperty(value = "应急情况说明")
    private  Object  emergencyDisposalContent;

    /**文件宣贯*/
    @ApiModelProperty(value = "文件宣贯")
    private  Object documentPublicityContent;

    /**其他工作内容*/
    @ApiModelProperty(value = "其他工作内容")
    private  Object  otherWorkContent;

    /**注意事项*/
    @ApiModelProperty(value = "注意事项")
    private  Object  note;

    /**交班人id*/
    @ApiModelProperty(value = "交班人id")
    private  String  handoverId;

    /**防疫相关工作*/
    @ApiModelProperty(value = "防疫相关工作")
    private String antiepidemicWork;

    /** 时间 年月日 星期几*/
    @ApiModelProperty(value = "时间 年月日 星期几")
    private String time;

    /**参加人员*/
    @ApiModelProperty(value = "参加人员")
    private String userList;

    /**负责人*/
    @ApiModelProperty(value = "负责人")
    private String foreman;

    /**班会名称*/
    @ApiModelProperty(value = "班会名称")
    private String className;

    /**班会时间*/
    @ApiModelProperty(value = "班会时间")
    private String classTime;

    /**工作安排*/
    @ApiModelProperty(value = "工作安排")
    private String schedule;

    /**班组*/
    @ApiModelProperty(value = "班组")
    private  String  OrgName;

    @TableField(exist = false)
    private String secert;

    @TableField(exist = false)
    private String secertduration;

    @TableField(exist = false)
    private String duration;

}
