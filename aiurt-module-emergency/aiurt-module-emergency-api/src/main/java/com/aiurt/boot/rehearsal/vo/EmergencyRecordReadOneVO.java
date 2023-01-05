package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.dto.EmergencyDeptDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordQuestion;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordStep;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author
 * @date 2022/12/2 10:59
 * @description: 查询单条记录是的VO对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "查询单条记录是的VO对象", description = "查询单条记录是的VO对象")
public class EmergencyRecordReadOneVO extends EmergencyImplementationRecord {
    /**
     * 关联的月计划信息
     */
    @ApiModelProperty(value = "关联的月计划信息")
    private EmergencyRecordMonthVO rehearsalMonth;
    /**
     * 参与部门信息
     */
    @ApiModelProperty(value = "参与部门信息")
    private List<EmergencyDeptDTO> depts;
    /**
     * 参与部门信息名称,英文分号分隔
     */
    @ApiModelProperty(value = "参与部门信息名称,英文分号分隔")
    private String deptNames;
    /**
     * 演练步骤信息
     */
    @ApiModelProperty(value = "演练步骤信息")
    private List<EmergencyRecordStep> steps;
    /**
     * 登记问题信息
     */
    @ApiModelProperty(value = "登记问题信息")
    private List<EmergencyRecordQuestion> questions;
    /**
     * 演练地点名称，格式：线路/站点/位置
     */
    @ApiModelProperty(value = "演练地点名称，格式：线路/站点/位置")
    private String stationName;
    /**
     * 观察岗位/点位名称，格式：线路/站点/位置
     */
    @ApiModelProperty(value = "观察岗位/点位名称，格式：线路/站点/位置")
    private String positionName;
}
