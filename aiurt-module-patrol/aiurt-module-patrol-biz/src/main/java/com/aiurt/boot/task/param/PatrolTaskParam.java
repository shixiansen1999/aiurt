package com.aiurt.boot.task.param;

import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author cgkj0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatrolTaskParam extends PatrolTask {
    /**
     * 接口请求标识，1标识巡检任务池请求，2标识巡视任务列表
     */
    @Excel(name = "接口请求标识，1标识巡检任务池请求，2标识巡视任务列表", width = 15)
    @ApiModelProperty(value = "接口请求标识，1标识巡检任务池请求，2标识巡视任务列表")
    private Integer identify;
    /**
     * 任务状态数组
     */
    @Excel(name = "任务状态数组", width = 15)
    @ApiModelProperty(value = "任务状态数组")
    private Integer[] statusArray;
    /**
     * 处置用户名称
     */
    @Excel(name = "处置用户名称", width = 15)
    @ApiModelProperty(value = "处置用户名称")
    private String disposeUserName;
    /**
     * 任务结束用户名称
     */
    @Excel(name = "任务结束用户名称", width = 15)
    @ApiModelProperty(value = "任务结束用户名称")
    private String endUsername;
    /**
     * 审核用户名称
     */
    @Excel(name = "审核用户名称", width = 15)
    @ApiModelProperty(value = "审核用户名称")
    private String auditUsername;
    /**
     * 退回用户名称
     */
    @Excel(name = "退回用户名称", width = 15)
    @ApiModelProperty(value = "退回用户名称")
    private String backUsername;
    /**
     * 组织机构编号
     */
    @Excel(name = "组织机构编号", width = 15)
    @ApiModelProperty(value = "组织机构编号")
    private String organizationCode;
    /**
     * 树形层级，1线路，2站点
     */
    @Excel(name = "树形层级，1线路，2站点", width = 15)
    @ApiModelProperty(value = "树形层级，1线路，2站点")
    private Integer level;
    /**
     * 站点编号
     */
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 组织机构信息
     */
    @Excel(name = "组织机构信息", width = 15)
    @ApiModelProperty(value = "组织机构信息")
    private List<PatrolTaskOrganizationDTO> departInfo;
    /**
     * 站点信息
     */
    @Excel(name = "站点信息", width = 15)
    @ApiModelProperty(value = "站点信息")
    private List<PatrolTaskStationDTO> stationInfo;

    /**
     * 巡检人员信息
     */
    @Excel(name = "巡检人员信息", width = 15)
    @ApiModelProperty(value = "巡检人员信息")
    private List<PatrolTaskUser> userInfo;
    /**
     * 任务计划执行日期范围开始日期
     */
    @Excel(name = "任务计划执行日期范围开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务计划执行日期范围开始日期,格式yyyy-MM-dd")
    private Date dateHead;
    /**
     * 任务计划执行日期范围结束日期
     */
    @Excel(name = "任务计划执行日期范围结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务计划执行日期范围结束日期,格式yyyy-MM-dd")
    private Date dateEnd;
    /**
     * 任务提交日期范围开始日期
     */
    @Excel(name = "任务提交日期范围开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务提交日期范围开始日期,格式yyyy-MM-dd")
    private Date submitDateHead;
    /**
     * 任务提交日期范围结束日期
     */
    @Excel(name = "任务提交日期范围结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "任务提交日期范围结束日期,格式yyyy-MM-dd")
    private Date submitDateEnd;
    /**
     * 专业信息
     */
    @Excel(name = "专业信息", width = 15)
    @ApiModelProperty(value = "专业信息")
    private List<String> majorInfo;
    /**
     * 子系统信息
     */
    @Excel(name = "子系统信息", width = 15)
    @ApiModelProperty(value = "子系统信息")
    private List<String> subsystemInfo;
}
