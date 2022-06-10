package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author km
 * @Date 2021/9/18 13:45
 * @Version 1.0
 */
@Data
public class StockInOrderLevel2DTO {
    /**仓库编号*/
    @ApiModelProperty(value = "仓库编号")
    @NotNull(message = "请选择入库仓库")
    private  String  warehouseCode;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private  String  note;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    private  java.util.Date  stockInTime;

    @ApiModelProperty("入库物资的编号与数量")
    private List<MaterialVO> materialVOList;
}
