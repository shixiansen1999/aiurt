package com.aiurt.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SparePartReplaceDTO implements Serializable {
    private static final long serialVersionUID = 3972774102472342350L;

    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**仓库编号*/


    /**故障/检修编号*/
    @ApiModelProperty(value = "故障/检修编号")
    private String code;

    /**维修记录id*/
    @ApiModelProperty(value = "维修记录id")
    private String repairRecordId;

    /**设组id*/
    @ApiModelProperty(value = "设组编码")
    private String deviceCode;

    @ApiModelProperty(value = "设组名称")
    @TableField(exist = false)
    private String deviceName;

    /**原组件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "原组件名称")
    @TableField(exist = false)
    private String oldSparePartName;

    /**新组件编号*/
    @ApiModelProperty(value = "新组件编号", required = true)
    private String newSparePartCode;

    @ApiModelProperty(value = "新组件名称", required = true)
    @TableField(exist = false)
    private String newSparePartName;

    /**新组件数量*/
    @ApiModelProperty(value = "新组件数量")
    private Integer newSparePartNum;


    @ApiModelProperty(value = "新组件拼接后的编码, 前端使用展示")
    private String newSparePartSplitCode;


}
