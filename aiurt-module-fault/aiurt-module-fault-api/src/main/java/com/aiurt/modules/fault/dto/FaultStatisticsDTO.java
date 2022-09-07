package com.aiurt.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zwl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 未完成
     */
    @ApiModelProperty(value = "未完成次数")
    @TableField(exist = false)
    private Long notComplete;


    /**
     * 已完成
     */
    @ApiModelProperty(value = "已完成次数")
    @TableField(exist = false)
    private Long alreadyComplete;


    /**
     * 报修故障
     */
    @ApiModelProperty(value = "报修故障次数")
    @TableField(exist = false)
    private Long repairFault;



    /**
     * 自检故障
     */
    @ApiModelProperty(value = "自检故障次数")
    @TableField(exist = false)
    private Long ownFault;


    /**
     * 故障发生次数列表
     */
    @ApiModelProperty(value = "故障发生次数列表")
    @TableField(exist = false)
    private List<FaultFrequencyDTO> faultFrequencyDTOList;
}
