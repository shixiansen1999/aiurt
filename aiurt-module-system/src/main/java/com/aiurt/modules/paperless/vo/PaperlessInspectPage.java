package com.aiurt.modules.paperless.vo;

import java.util.List;
import com.aiurt.modules.paperless.entity.PaperlessInspect;
import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 安全检查记录
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Data
@ApiModel(value="paperless_inspectPage对象", description="安全检查记录")
public class PaperlessInspectPage {

	/**id*/
	@ApiModelProperty(value = "id")
    private String id;
	/**变电所名称*/
	@Excel(name = "变电所名称", width = 15)
	@ApiModelProperty(value = "变电所名称")
    private String paperlessName;
	/**日期*/
	@Excel(name = "日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "日期")
    private Date paperlessTime;
	/** 级别*/
	@Excel(name = " 级别", width = 15, dicCode = "level")
    @Dict(dicCode = "level")
	@ApiModelProperty(value = " 级别")
    private Integer paperlessTeam;
	/**检查内容*/
	@Excel(name = "检查内容", width = 15)
	@ApiModelProperty(value = "检查内容")
    private String paperlessInspect;
	/**整改内容*/
	@Excel(name = "整改内容", width = 15)
	@ApiModelProperty(value = "整改内容")
    private String paperlessRectification;
	/**改进意见*/
	@Excel(name = "改进意见", width = 15)
	@ApiModelProperty(value = "改进意见")
    private String paperlessOpinion;
	/**检查人员*/
	@Excel(name = "检查人员", width = 15)
	@ApiModelProperty(value = "检查人员")
    private String paperlessInspector;
	/**责任人*/
	@Excel(name = "责任人", width = 15)
	@ApiModelProperty(value = "责任人")
    private String paperlessLiable;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
    private String updateBy;

	@ExcelCollection(name="安全检查记录从表")
	@ApiModelProperty(value = "安全检查记录从表")
	private List<PaperlessInspectEntry> paperlessInspectEntryList;

}
