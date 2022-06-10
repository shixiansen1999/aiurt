package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/17 10:59
 * @Version 1.0
 */
@Data
public class AddApplyDTO {
    /**申领仓库 备件库*/
    @ApiModelProperty(value = "申领仓库 备件库")
    private  String  warehouseCode;

    /**出库仓库 二级库*/
    @ApiModelProperty(value = "出库仓库 二级库")
    private  String  outWarehouseCode;


    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("申领物资的编号与数量")
    private List<MaterialVO> materialVOList;
}
