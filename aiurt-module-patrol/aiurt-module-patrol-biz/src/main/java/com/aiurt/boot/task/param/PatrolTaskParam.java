package com.aiurt.boot.task.param;

import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatrolTaskParam extends PatrolTask {
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
     * 任务计划执行日期范围
     */
    @Excel(name = "任务计划执行日期范围", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围")
    private String dateScope;
    /**
     * 任务计划执行日期范围开始日期
     */
    @Excel(name = "任务计划执行日期范围开始日期", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围开始日期")
    private Date dateHead;
    /**
     * 任务计划执行日期范围结束日期
     */
    @Excel(name = "任务计划执行日期范围结束日期", width = 15)
    @ApiModelProperty(value = "任务计划执行日期范围结束日期")
    private Date dateEnd;
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
