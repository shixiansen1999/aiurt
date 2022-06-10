package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/18 15:16
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDTO extends StockLevel2Check{
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private  String  startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private  String  endTime;

    @ApiModelProperty(value="id集合")
    private List<Integer> stationIds;


}
