package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "培训经历", description = "培训经历")
public class ExperienceResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 培训起始时间
     */
    @ApiModelProperty(value = "培训起始时间")
    private String starDate;

    /**
     * 培训结束时间
     */
    @ApiModelProperty(value = "培训结束时间")
    private String endDate;

    /**
     * 培训描述
     */
    @ApiModelProperty(value = "培训描述")
    private String description;

}
