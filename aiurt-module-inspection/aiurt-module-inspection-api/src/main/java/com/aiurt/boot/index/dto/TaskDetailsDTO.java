package com.aiurt.boot.index.dto;

import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.io.Serializable;

/**
 * @author wgp
 * @Title:
 * @Description: 检修任务列表DTO
 * @date 2022/9/516:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskDetailsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("站点")
    private String stationName;
    @ApiModelProperty("线路")
    private String lineName;
    @ApiModelProperty("所属班组")
    private String teamName;
    @ApiModelProperty("检修人员")
    private String realName;
    @ApiModelProperty("任务状态")
    private String statusName;
    @ApiModelProperty("提交时间")
    private String submitTime;
    @ApiModelProperty("详情")
    private Page<RepairPoolDetailsDTO> detailList;
}
