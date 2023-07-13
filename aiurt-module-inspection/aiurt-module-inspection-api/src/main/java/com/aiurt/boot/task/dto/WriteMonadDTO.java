package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wgp
 * @Title:
 * @Description: 填写工单请求参数
 * @date 2022/7/618:23
 */
@Data
public class WriteMonadDTO {
    @NotBlank(message = "检修单id不能为空")
    @ApiModelProperty(value = "检修单id")
    private java.lang.String ordId;

    @NotBlank(message = "检修项id不能为空")
    @ApiModelProperty(value = "检修项id")
    private String itemId;

    @ApiModelProperty(value = "检修结果 1.正常 2.异常")
    @NotNull(message = "检修结果不能为空")
    private java.lang.Integer status;

    @ApiModelProperty(value = "检测值")
    private java.lang.Integer inspeciontValue;

    @Max(value = 300,message = "输入项填写内容最大长度为300")
    @ApiModelProperty(value = "输入项填写内容")
    private java.lang.String note;

    @Excel(name = "特殊字符输入结果", width = 15)
    @ApiModelProperty(value = "特殊字符输入结果")
    private java.lang.String specialCharactersResult;

    @Max(value = 300,message = "备注最大长度为300")
    @ApiModelProperty(value = "备注")
    private java.lang.String unNote;

    @ApiModelProperty(value = "多个附件使用英文逗号隔开")
    private java.lang.String appendix;
}
