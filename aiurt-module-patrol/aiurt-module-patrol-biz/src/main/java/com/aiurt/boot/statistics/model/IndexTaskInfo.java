package com.aiurt.boot.statistics.model;

import com.aiurt.boot.statistics.dto.IndexOrgDTO;
import com.aiurt.boot.statistics.dto.IndexStationDTO;
import com.aiurt.boot.statistics.dto.IndexUserDTO;
import com.aiurt.boot.task.dto.PatrolBillDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @author JB
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexTaskInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private java.lang.String id;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private java.lang.String name;
    /**
     * 异常状态：0异常、1正常
     */
    @ApiModelProperty(value = "巡视结果：0异常、1正常")
    private java.lang.Integer abnormalState;
    /**
     * 异常状态字典名称
     */
    @ApiModelProperty(value = "巡视结果字典名称")
    private java.lang.String abnormalDictName;
    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    private java.lang.String code;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private java.lang.Integer status;
    /**
     * 任务状态字典名称
     */
    @ApiModelProperty(value = "任务状态字典名称")
    private java.lang.String statusDictName;
    /**
     * 任务的巡视人员
     */
    @ApiModelProperty(value = "任务的巡视人员")
    private List<IndexUserDTO> userInfo;
    /**
     * 任务的组织机构
     */
    @ApiModelProperty(value = "任务的组织机构")
    private List<IndexOrgDTO> orgInfo;
    /**
     * 巡检结果提交时间(yyyy-MM-dd HH:mm:ss)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "巡检结果提交时间,格式yyyy-MM-dd HH:mm:ss")
    private java.util.Date submitTime;

    /**
     * mac匹配；0异常；1正常
     */
    @Excel(name = "mac匹配；0异常；1正常", width = 15)
    @ApiModelProperty(value = "mac匹配；0异常；1正常")
    private java.lang.Integer macStatus;

    /**
     * mac地址匹配结果
     */
    @ApiModelProperty(value = "mac地址匹配结果")
    private String macMatchResult;
    /**
     * 任务的站点
     */
    @ApiModelProperty(value = "任务的站点")
    private List<IndexStationDTO> stationInfo;

    @ApiModelProperty(value = "巡视单内容")
    private List<PatrolBillDTO> children;
}
