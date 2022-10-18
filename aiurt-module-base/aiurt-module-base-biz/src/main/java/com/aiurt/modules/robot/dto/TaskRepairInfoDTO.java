package com.aiurt.modules.robot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author JB
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskRepairInfoDTO {
    @ApiModelProperty(value = "巡检任务ID")
    @NotBlank(message = "巡检任务ID为空")
    private String taskId;
    @ApiModelProperty(value = "报修编码")
    @NotNull(message = "报修编码为空")
    private List<String> repairCodes;
}
