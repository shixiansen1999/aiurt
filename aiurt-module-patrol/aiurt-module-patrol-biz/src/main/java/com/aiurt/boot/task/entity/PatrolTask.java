package com.aiurt.boot.task.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "patrol_task对象", description = "patrol_task")
public class PatrolTask extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**
     * 任务编号
     */
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务编号，导出使用参数")
    private List<String> codeList;
    /**
     * 计划编号
     */
    @Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String planCode;
    /**
     * 任务名称
     */
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
    /**
     * 作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3
     */
    @Excel(name = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3", width = 15)
    @ApiModelProperty(value = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3")
    private java.lang.Integer type;
    /**
     * 是否委外：0否、1是
     */
    @Excel(name = "是否委外：0否、1是", width = 15)
    @ApiModelProperty(value = "是否委外：0否、1是")
    private java.lang.Integer outsource;
    /**
     * 计划令编号
     */
    @Excel(name = "计划令编号", width = 15)
    @ApiModelProperty(value = "计划令编号")
    private java.lang.String planOrderCode;
    /**
     * 计划令图片
     */
    @Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private java.lang.String planOrderCodeUrl;

    /**
     * 巡检频次：1 一天1次、2 一周1次、3 一周2次
     */
    @Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次", width = 15)
    @ApiModelProperty(value = "巡检频次：1 一天1次、2 一周1次、3 一周2次")
    @Dict(dicCode = "patrol_plan_period")
    private java.lang.Integer period;
    /**
     * 巡检的日期(yyyy-MM-dd)
     */
    @Excel(name = "巡检的日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "巡检的日期(yyyy-MM-dd)")
    private java.util.Date patrolDate;
    /**
     * 巡检开始时间(HH:mm)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检开始时间(HH:mm)")
    private java.util.Date startTime;
    /**
     * 巡检结束时间(HH:mm)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "巡检结束时间(HH:mm)")
    private java.util.Date endTime;
    /**
     * 手工下发巡检的开始日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的开始日期(yyyy-MM-dd)")
    private java.util.Date startDate;
    /**
     * 手工下发巡检的结束日期(yyyy-MM-dd)
     */
    @Excel(name = "手工下发巡检的结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "手工下发巡检的结束日期(yyyy-MM-dd)")
    private java.util.Date endDate;
    /**
     * 任务获取方式：1 个人领取、2常规指派、3 手工下发
     */
    @Excel(name = "任务获取方式：1 个人领取、2常规指派、3 手工下发", width = 15)
    @ApiModelProperty(value = "任务获取方式：1 个人领取、2常规指派、3 手工下发")
    @Dict(dicCode = "patrol_task_access")
    private java.lang.Integer source;
    /**
     * 开始点击执行巡视任务的时间(yyyy-MM-dd HH:mm:ss)
     */
    @Excel(name = "开始点击执行巡视任务的时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始点击执行巡视任务的时间,格式yyyy-MM-dd HH:mm:ss")
    private java.util.Date beginTime;
    /**
     * 巡检结果提交时间(yyyy-MM-dd HH:mm:ss)
     */
    @Excel(name = "巡检结果提交时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "巡检结果提交时间,格式yyyy-MM-dd HH:mm:ss")
    private java.util.Date submitTime;
    /**
     * 任务结束用户ID
     */
    @Excel(name = "任务结束用户ID", width = 15)
    @ApiModelProperty(value = "任务结束用户ID")
    private java.lang.String endUserId;
    /**
     * 任务提交的用户签名图片
     */
    @Excel(name = "任务提交的用户签名图片", width = 15)
    @ApiModelProperty(value = "任务提交的用户签名图片")
    private java.lang.String signUrl;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @Excel(name = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成", width = 15)
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    @Dict(dicCode = "patrol_task_status")
    private java.lang.Integer status;
    /**
     * 驳回原因
     */
    @Excel(name = "驳回原因", width = 15)
    @ApiModelProperty(value = "驳回原因")
    private java.lang.String rejectReason;

    /**
     * 是否需要审核：0否、1是
     */
    @Excel(name = "是否需要审核：0否、1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
    /**
     * 审核用户ID
     */
    @Excel(name = "审核用户ID", width = 15)
    @ApiModelProperty(value = "审核用户ID")
    private java.lang.String auditorId;
    /**
     * 审核时间
     */
    @Excel(name = "审核时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间(yyyy-MM-dd HH:mm:ss)")
    private java.util.Date auditorTime;
    /**
     * 审核备注信息
     */
    @Excel(name = "审核备注信息", width = 15)
    @ApiModelProperty(value = "审核备注信息")
    private java.lang.String auditorRemark;
    /**
     * 异常状态：0异常、1正常
     */
    @Excel(name = "异常状态：0异常、1正常", width = 15)
    @ApiModelProperty(value = "异常状态：0异常、1正常")
    private java.lang.Integer abnormalState;
    /**
     * 处置状态：0未处置、1已处置
     */
    @Excel(name = "处置状态：0未处置、1已处置", width = 15)
    @ApiModelProperty(value = "处置状态：0未处置、1已处置")
    @Dict(dicCode = "patrol_dispose_status")
    private java.lang.Integer disposeStatus;
    /**
     * 处置时间(yyyy-MM-dd)
     */
    @Excel(name = "处置时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "处置时间(yyyy-MM-dd)")
    private java.util.Date disposeTime;
    /**
     * 处置用户ID
     */
    @Excel(name = "处置用户ID", width = 15)
    @ApiModelProperty(value = "处置用户ID")
    private java.lang.String disposeId;
    /**
     * 漏检状态:0未漏检，1已漏检
     */
    @Excel(name = "漏检状态:0未漏检，1已漏检", width = 15)
    @ApiModelProperty(value = "漏检状态:0未漏检，1已漏检")
    @Dict(dicCode = "patrol_omit_status")
    private java.lang.Integer omitStatus;
    /**
     * mac匹配；0异常；1正常
     */
    @Excel(name = "mac匹配；0异常；1正常", width = 15)
    @ApiModelProperty(value = "mac匹配；0异常；1正常")
    private java.lang.Integer macStatus;
    /**
     * 漏检说明
     */
    @Excel(name = "漏检说明", width = 15)
    @ApiModelProperty(value = "漏检说明")
    private java.lang.String omitExplain;
    /**
     * 退回人用户ID
     */
    @Excel(name = "退回人用户ID", width = 15)
    @ApiModelProperty(value = "退回人用户ID")
    private java.lang.String backId;
    /**
     * 退回理由
     */
    @Excel(name = "退回理由", width = 15)
    @ApiModelProperty(value = "退回理由")
    private java.lang.String backReason;
    /**
     * 指派人用户ID
     */
    @Excel(name = "指派人用户ID", width = 15)
    @ApiModelProperty(value = "指派人用户ID")
    private java.lang.String assignId;

    /**
     * 作废状态：0未作废、1已作废
     */
    @Excel(name = "作废状态：0未作废、1已作废", width = 15)
    @ApiModelProperty(value = "作废状态：0未作废、1已作废")
    @Dict(dicCode = "patrol_discard_status")
    private java.lang.Integer discardStatus;
    /**
     * 作废理由
     */
    @Excel(name = "作废理由", width = 15)
    @ApiModelProperty(value = "作废理由")
    private java.lang.String discardReason;
    /**
     * 是否已重新生成：0否、1是
     */
    @Excel(name = "是否已重新生成：0否、1是", width = 15)
    @ApiModelProperty(value = "是否已重新生成：0否、1是")
    @Dict(dicCode = "patrol_task_rebuild")
    private java.lang.Integer rebuild;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 删除状态： 0未删除 1已删除
     */
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**
     * 巡视抽查状态：0未确认，1已确认
     */
    @Dict(dicCode = "spot_check_status")
    @ApiModelProperty(value = "巡视抽查状态：0未确认，1已确认")
    private Integer spotCheckStatus;
    /**
     * 巡视抽查时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "巡视抽查时间")
    private Date spotCheckTime;
    /**
     * 巡视抽查人id
     */
    @Dict(dictTable = "sys_user",dicCode = "id", dicText = "realname")
    @ApiModelProperty(value = "巡视抽查人id")
    private String spotCheckUserId;
    /**
     * 巡视抽查备注
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "巡视抽查备注")
    private String spotCheckRemark;
    /**
     * 标准工时
     */
    @ApiModelProperty(value = "标准工时：单位秒")
    private Integer standardDuration;

    /**
     * 巡视工时：单位秒
     */
    @ApiModelProperty(value = "巡视工时：单位秒")
    private Integer duration;

    /**
     * 执行任务时的wifi连接时间，也就是计算巡视时长开始时间
     */
    @ApiModelProperty(value = "执行任务时的wifi连接时间，也就是计算巡视时长开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wifiConnectTime;
}
