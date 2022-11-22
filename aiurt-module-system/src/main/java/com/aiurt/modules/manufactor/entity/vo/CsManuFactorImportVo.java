package com.aiurt.modules.manufactor.entity.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("cs_manufactor")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_manufactor对象", description="cs_manufactor")
public class CsManuFactorImportVo implements Serializable {

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**厂家编码*/
    @ApiModelProperty(value = "厂家编码")
    private String code;
	/**厂商名称*/
	@Excel(name = "厂商名称", width = 15)
    @ExcelExtend(isRequired = true,remark = "必填字段")
    @ApiModelProperty(value = "厂商名称")
    private String name;
	/**厂商等级(1:较好/2:良好/3:较差)*/
	@Excel(name = "厂商等级", width = 15,dicCode = "manufactor_level")
    @ExcelExtend(isRequired = true,remark = "必填字段，且与系统下拉项保持一致")
    @ApiModelProperty(value = "厂商等级")
    @Dict(dicCode = "manufactor_level")
    private String level;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @ExcelExtend(remark = "选填字段")
    @ApiModelProperty(value = "联系人")
    private String linkPerson;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ExcelExtend(remark = "选填字段，11位数的手机号码")
    @ApiModelProperty(value = "联系电话")
    private String linkPhoneNo;
	/**联系地址*/
	@Excel(name = "联系地址", width = 15)
    @ExcelExtend(remark = "选填字段")
    @ApiModelProperty(value = "联系地址")
    private String linkAddress;
	/**企业资质文件*/
	@Excel(name = "企业资质文件", width = 15)
    @ExcelExtend(remark = "选填字段，支持PNG，JP图片格式；pdf请在系统中直接上传")
    @ApiModelProperty(value = "企业资质文件")
    private String filePath;

    /**错误原因*/
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    private String errorCause;

}
