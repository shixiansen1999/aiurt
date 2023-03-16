package com.aiurt.common.result;

import com.baomidou.mybatisplus.annotation.IdType;
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
import java.util.List;

@Data
@TableName("t_work_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="t_work_log对象", description="工作日志")
public class WorkLogDetailResult {


    /**主键id*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
    private  Long  id;

    /**线路*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路")
    private  String  lineName;

    /**班组*/
    @Excel(name = "班组", width = 25)
    @ApiModelProperty(value = "班组")
    private  String  OrgName;

    /**提交人*/
    @Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private  String  submitName;

    /**
     * 巡视内容
     */
    @Excel(name = "巡视内容", width = 15)
    @ApiModelProperty(value = "巡视内容")
    private  String  patrolContent;

    /**检修内容*/
    @Excel(name = "检修内容", width = 15)
    @ApiModelProperty(value = "检修内容")
    private  String  repairContent;

    /**故障内容*/
    @Excel(name = "故障报修", width = 15)
    @ApiModelProperty(value = "故障报修")
    private  String  faultContent;

    /**交接班内容*/
    @Excel(name = "交接班内容", width = 35)
    @ApiModelProperty(value = "交接班内容")
    private  Object  content;

    /**接班人名称*/
    @Excel(name = "接班人名称", width = 15)
    @ApiModelProperty(value = "接班人名称")
    private  String  succeedName;

    /**日期*/
    @ApiModelProperty(value = "日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private  Date  logTime;

    /**接班人id*/
    @ApiModelProperty(value = "接班人id")
    private  String  succeedId;

    @ApiModelProperty(value = "签名")
    public List<String> signature;


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

    /**交班人姓名*/
    @ApiModelProperty(value = "交班人姓名")
    private  String  handoverName;

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

    /**防疫相关工作*/
    @ApiModelProperty(value = "防疫相关工作")
    private String antiepidemicWork;

    /**工作安排*/
    @ApiModelProperty(value = "工作安排")
    private String schedule;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private  Date  submitTime;

    /**未完成事项*/
    @ApiModelProperty(value = "未完成事项")
    private  String  unfinishedMatters;
}
