package com.aiurt.boot.plan.param;

import com.aiurt.boot.plan.entity.PatrolPlanOrganization;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatrolPlanOrganizationParam extends PatrolPlanOrganization {

    /**
     * 组织机构名称
     */
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private String departName;

}
