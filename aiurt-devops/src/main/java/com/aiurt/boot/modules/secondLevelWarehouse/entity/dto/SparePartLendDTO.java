package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/16
 */
@Data
public class SparePartLendDTO {

    /**物资编号*/
    @ApiModelProperty(value = "备件编号")
    @NotBlank(message = "备件编号不能为空")
    private  String  materialCode;

    /**借入数量*/
    @ApiModelProperty(value = "借入数量")
    @NotNull(message = "借入数量不能为空")
    private  Integer  lendNum;

    /**借出部门*/
    @ApiModelProperty(value = "借出部门")
    private  String  outDepart;

    /**申请时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
    private  java.util.Date  lendTime;

    /**借用人*/
    @ApiModelProperty(value = "借用人")
    @NotBlank(message = "借用人不能为空")
    private  String  lendPerson;

    /**借入部门*/
    @ApiModelProperty(value = "借入部门")
    @NotBlank(message = "借入部门不能为空")
    private  String  lendDepart;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private  String  remarks;

    /**附件列表*/
    @ApiModelProperty(value = "附件列表")
    public List<String> urlList;
}
