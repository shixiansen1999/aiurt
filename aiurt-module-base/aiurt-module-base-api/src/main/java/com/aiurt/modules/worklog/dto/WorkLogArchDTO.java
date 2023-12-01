package com.aiurt.modules.worklog.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author sbx
 * @since 2023/11/24
 */
@Data
public class WorkLogArchDTO {
    /**主键id*/
    @ApiModelProperty(value = "主键id，自动递增")
    private  String  id;

    /**线路*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路")
    private  String  lineName;

    /**提交人班组*/
    @ApiModelProperty(value = "班组")
    private  String  submitOrgName;
    /**提交人班组*/
    @ApiModelProperty(value = "班组")
    private  String  submitOrgId;

    /**日志编号*/
    @ApiModelProperty(value = "日志编号")
    private  String  code;

    /**提交人*/
    @ApiModelProperty(value = "提交人")
    private  String  submitName;

    /**巡检内容*/
    @ApiModelProperty(value = "巡检内容")
    private  String  patrolContent;

    /**检修内容*/
    @ApiModelProperty(value = "检修内容")
    private  String  repairContent;

    /**未完成任务内容*/
    @ApiModelProperty(value = "未完成任务内容")
    private  String  unfinishContent;

    /**故障内容*/
    @ApiModelProperty(value = "故障报修")
    private  String  faultContent;

    @ApiModelProperty(value = "巡检修内容")
    private String patrolRepairContent;

    /**工作内容*/
    @ApiModelProperty(value = "工作内容")
    private  String  workContent;

    /**交接班内容*/
    @ApiModelProperty(value = "交接班内容")
    private  String  content;

    /**提交时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    /**交班人姓名*/
    @ApiModelProperty(value = "交班人姓名")
    private  String  handoverName;

    /**接班人*/
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
    @ApiModelProperty(value = "提交状态")
    private  String  statusDesc;

    /**确认状态描述*/
    @ApiModelProperty(value = "交接班确认状态")
    private  String  confirmStatusDesc;

    /**审核状态描述*/
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

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接班人确认时间")
    private String succeedTime;

    @ApiModelProperty(value = "施工时间")
    private String constructTime;

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
    private  String  assortContent;
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
    private  String  emergencyDisposalContent;

    /**文件宣贯*/
    @ApiModelProperty(value = "文件宣贯")
    private  String documentPublicityContent;

    /**其他工作内容*/
    @ApiModelProperty(value = "其他工作内容")
    private  String  otherWorkContent;

    /**注意事项*/
    @ApiModelProperty(value = "注意事项")
    private  String  note;

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

    @ApiModelProperty(value = "附件链接")
    private String url;

    @ApiModelProperty(value = "归档状态(0:未归档，1:已归档)")
    private Integer ecmStatus;

    @ApiModelProperty("密级")
    private String secert;

    @ApiModelProperty(value = "保管期限")
    private String secertDuration;

    private String duration;
    /**未完成事项*/
    @ApiModelProperty(value = "未完成事项")
    private  String  unfinishedMatters;
    /**所在班组*/
    @ApiModelProperty(value = "所在班组")
    private  String  orgId;
    /**是否能编辑*/
    private Boolean editFlag;
    /**是否能补录*/
    private Boolean additionalRecordingFlag;

    @ApiModelProperty(value = "站点编码，逗号隔开")
    private String stationCode;

    @ApiModelProperty(value = "位置编码，逗号隔开")
    private String positionCode;

    /**是否补录：0否，1是*/
    @ApiModelProperty(value = "是否补录：0否，1是")
    @Dict(dicCode = "work_log_isAdditionalRecording")
    private  Integer  isAdditionalRecording;

    @ApiModelProperty(value = "档案类型id")
    private String archtypeId;

    @ApiModelProperty(value = "档案类型的文件夹id")
    private String refileFolderId;

    @ApiModelProperty(value = "所属全宗id")
    private String sectId;

    @ApiModelProperty(value = "归档后的文件名，不包含文件格式")
    private String fileName;
}
