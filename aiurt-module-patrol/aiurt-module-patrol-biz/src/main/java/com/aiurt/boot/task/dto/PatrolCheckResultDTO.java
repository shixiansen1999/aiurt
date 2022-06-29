package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolCheckResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatrolCheckResultDTO extends PatrolCheckResult {
    /**
     * 子节点
     */
    @ApiModelProperty(value = "子节点")
    List<PatrolCheckResultDTO> children = new ArrayList<>();
}
