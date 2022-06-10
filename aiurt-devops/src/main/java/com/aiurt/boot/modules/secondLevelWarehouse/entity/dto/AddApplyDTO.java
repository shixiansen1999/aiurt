package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author km
 * @Date 2021/9/17 10:59
 * @Version 1.0
 */
@Data
public class AddApplyDTO {
    /**出库仓库 二级库*/
    @ApiModelProperty(value = "出库仓库 二级库")
    @NotEmpty(message = "出库仓库不能为空")
    private  String  outWarehouseCode;


    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("申领物资的编号与数量")
    @NotNull(message = "申领物资不能为空")
    private List<MaterialVO> materialVOList;
}
