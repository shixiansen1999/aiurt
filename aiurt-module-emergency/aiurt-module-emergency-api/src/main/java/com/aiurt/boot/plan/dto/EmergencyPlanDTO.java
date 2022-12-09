package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.entity.EmergencyPlanAtt;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanDTO extends EmergencyPlan {
    /**应急队伍*/
    @ApiModelProperty(value = "应急队伍")
    @NotBlank(message = "应急队伍不能为空")
    List<String> emergencyTeamId;

    /**
     * 处置程序
     */
    @ApiModelProperty(value = "处置程序")
    private List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedure;

    /**
     * 应急物资
     */
    @ApiModelProperty(value = "应急物资")
    private List<EmergencyPlanMaterials> emergencyPlanMaterials;

    /**
     * 应急预案附件
     */
    @ApiModelProperty(value = "应急预案附件")
    private List<EmergencyPlanAtt> emergencyPlanAtt;


}
