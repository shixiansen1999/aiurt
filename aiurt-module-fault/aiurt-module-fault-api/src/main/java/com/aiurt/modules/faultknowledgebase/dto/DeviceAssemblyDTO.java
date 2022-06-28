package com.aiurt.modules.faultknowledgebase.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@ApiModel("设备组件")
public class DeviceAssemblyDTO {

    /**主键id*/
    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private  Long  id;

    /**物资基础数据编号-物资编号*/
    @Excel(name = "物资基础数据编号-物资编号", width = 15)
    @ApiModelProperty(value = "物资基础数据编号-物资编号")
    private  String  materialCode;

    /**组件名称*/
    @Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
    private  String  materialName;

    /**设备类型编码*/
    @Excel(name = "设备类型编码", width = 15)
    @ApiModelProperty(value = "设备类型编码")
    private  String  deviceTypeCode;
}
