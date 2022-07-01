package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolAccessory;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

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
    /**
     * 附件信息
     */
    @ApiModelProperty(value = "附件信息")
    private List<PatrolAccessory> accessoryInfo;
}
