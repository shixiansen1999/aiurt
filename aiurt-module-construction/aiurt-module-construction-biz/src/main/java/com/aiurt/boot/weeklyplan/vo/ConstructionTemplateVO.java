package com.aiurt.boot.weeklyplan.vo;

import com.aiurt.boot.weeklyplan.entity.ConstructionTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 施工供电模板VO对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ConstructionTemplateVO对象", description = "ConstructionTemplateVO对象")
public class ConstructionTemplateVO extends ConstructionTemplate {
    /**
     * 用户名称
     */
    @Excel(name = "用户名称", width = 15)
    @ApiModelProperty(value = "用户名称")
    private String userName;
}
