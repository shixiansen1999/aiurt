package com.aiurt.modules.common.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;
/**
 * @author zwl
 */
@Data
@ApiModel("设备分类下拉列表")
public class DeviceTypeTable implements Serializable {

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**分类编号*/
    @Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String code;
    /**分类名称*/
    @Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String name;
    /**是否尾节点(1:是,0：否)*/
    @Excel(name = "是否尾节点(1:是,0：否)", width = 15)
    @ApiModelProperty(value = "是否尾节点(1:是,0：否)")
    @Dict(dicCode = "is_end")
    private Integer isEnd;
    /**上级节点*/
    @Excel(name = "上级节点", width = 15)
    @ApiModelProperty(value = "上级节点")
    private String pid;
    /**设备类型子集*/
    @Excel(name = "设备类型子集", width = 15)
    @ApiModelProperty(value = "设备类型子集")
    @TableField(exist = false)
    private List<DeviceTypeTable> children;

    private Boolean flag;

    private String color;

    private String key;

    private String value;

    private String label;
}
