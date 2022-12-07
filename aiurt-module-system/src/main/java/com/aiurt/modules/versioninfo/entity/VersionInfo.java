package com.aiurt.modules.versioninfo.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @Description: bd_version_info
 * @Author: jeecg-boot
 * @Date:   2021-05-10
 * @Version: V1.0
 */
@Data
@TableName("sys_version_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_version_info对象", description="sys_version_info")
public class VersionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id，自增*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id，自增")
    private Integer id;
	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
    private String versionId;
	/**更新内容描述*/
	@Excel(name = "更新内容描述", width = 15)
    @ApiModelProperty(value = "更新内容描述")
    @Valid
    @Length(max = 16, message = "更新内容描述不能超过200个字符")
    private String updateContent;
	/**app版本还是服务版本，1=app，2=服务*/
	@Excel(name = "app版本还是服务版本，1=app，2=服务", width = 15)
    @ApiModelProperty(value = "app版本还是服务版本，1=app，2=服务")
    private Integer appOrService;
	/**是否强制更新，0=否，1=是*/
	@Excel(name = "是否强制更新，0=否，1=是", width = 15)
    @ApiModelProperty(value = "是否强制更新，0=否，1=是")
    @Dict(dicCode = "mustUpdateCode")
    private Integer mustUpdate;
	/**ios文件名*/
	@Excel(name = "ios文件名", width = 15)
    @ApiModelProperty(value = "ios文件名")
    private String iosPackageName;
	/**ios包文件大小（字节数）*/
	@Excel(name = "ios包文件大小（字节数）", width = 15)
    @ApiModelProperty(value = "ios包文件大小（字节数）")
    private Integer iosPackageSize;
	/**android文件名*/
	@Excel(name = "android文件名", width = 15)
    @ApiModelProperty(value = "android文件名")
    private String androidPackageName;
	/**包文件大小（字节数）*/
	@Excel(name = "android包文件大小（字节数）", width = 15)
    @ApiModelProperty(value = "android包文件大小（字节数）")
    private Integer androidPackageSize;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**updateBy*/
    @ApiModelProperty(value = "updateBy")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
	/**ios文件url*/
    @ApiModelProperty(value = "ios文件url")
	private String iosUrl;
    /**android文件url*/
    @ApiModelProperty(value = "android文件url")
	private String androidUrl;

}
