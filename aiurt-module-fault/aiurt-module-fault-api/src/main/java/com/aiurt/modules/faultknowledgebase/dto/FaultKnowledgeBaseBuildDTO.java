package com.aiurt.modules.faultknowledgebase.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 支持前端单元格合并的知识库DTO
 */
@ApiModel(value = "支持前端单元格合并的知识库DTO", description = "支持前端单元格合并的知识库DTO")
@Data
public class FaultKnowledgeBaseBuildDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String virId;
    /**
     * 知识库ID
     */
    @ApiModelProperty(value = "知识库ID")
    private String id;

    /**
     * 故障现象编号
     */
    @Excel(name = "故障现象编号", width = 15)
    @ApiModelProperty(value = "故障现象编号")
    private String faultPhenomenonCode;
    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;
    /**
     * 排查方法
     */
    @Excel(name = "排查方法", width = 15)
    @ApiModelProperty(value = "排查方法")
    private String method;
    /**
     * 专业编码
     */
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;
    /**
     * 专业子系统
     */
    @Excel(name = "专业子系统", width = 15)
    @ApiModelProperty(value = "专业子系统")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private String systemCode;
    /**
     * 设备类型
     */
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @Dict(dictTable = "device_Type", dicText = "name", dicCode = "code")
    private String deviceTypeCode;
    /**
     * 设备组件
     */
    @Excel(name = "设备组件", width = 15)
    @ApiModelProperty(value = "设备组件")
    @Dict(dictTable = "device_compose", dicText = "material_name", dicCode = "material_code")
    private String materialCode;
    /**
     * 故障知识库ID
     */
    @ApiModelProperty(value = "故障知识库ID")
    private String knowledgeBaseId;
    /**
     * 故障原因
     */
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultCause;
    /**
     * 解决方案
     */
    @Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    private String solution;
    /**
     * 维修视频url
     */
    @org.jeecgframework.poi.excel.annotation.Excel(name = "维修视频url", width = 15)
    @ApiModelProperty(value = "维修视频url")
    private String videoUrl;
    /**
     * 原因出现率百分比
     */
    @Excel(name = "原因出现率百分比", width = 15)
    @ApiModelProperty(value = "原因出现率百分比")
    private String happenRate;
    /**
     * 状态
     */
    @Excel(name = "状态(0:待审批,1:已审批,2:已驳回)", width = 15)
    @ApiModelProperty(value = "状态(0:待审批,1:已审批,2:已驳回)")
    @Dict(dicCode = "fault_knowledge")
    private Integer status;

    /**
     * 同一知识库的首条记录
     */
    @ApiModelProperty(value = "同一知识库的首条记录")
    private Boolean first;
    /**
     * 组别(相同的故障现象编号为一组)
     */
    @ApiModelProperty(value = "组别(相同的故障现象编号为一组)")
    private Integer group;
    /**
     * 方案数量，没有则为1
     */
    @ApiModelProperty(value = "方案数量，没有则为1")
    private Integer size;
    @ApiModelProperty(value = "判断是否有审核按钮")
    @TableField(exist = false)
    private Boolean haveButton;

    @ApiModelProperty(value = "判断登录人是否是创建人")
    @TableField(exist = false)
    private Boolean isCreateUser;
    /**
     * 排序方式
     */
    @ApiModelProperty(value = "流程发起人(0:工班长,1:技术员)")
    @TableField(exist = false)
    private Integer processInitiator;

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @TableField(exist = false)
    private String processInstanceId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;
    /**
     * 模板key，流程标识
     */
    @ApiModelProperty(value = "模板key，流程标识")
    @TableField(exist = false)
    private String modelKey;
}
