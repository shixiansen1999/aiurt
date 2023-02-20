package com.aiurt.modules.paperless.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 安全检查记录从表
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@ApiModel(value="paperless_inspect_entry对象", description="安全检查记录从表")
@Data
@TableName("paperless_inspect_entry")
public class PaperlessInspectEntry implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**主表id*/
    @ApiModelProperty(value = "主表id")
    private String paperlessId;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String paperlessContent;
	/**上报日期*/
	@Excel(name = "上报日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上报日期")
    private Date paperlessTime;
	/**处理方法*/
	@Excel(name = "处理方法", width = 15)
    @ApiModelProperty(value = "处理方法")
    private String paperlessMethod;
	/**完结日期*/
	@Excel(name = "完结日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完结日期")
    private Date paperlessTimeend;
	/**责任人*/
	@Excel(name = "责任人", width = 15)
    @ApiModelProperty(value = "责任人")
    private String paperlessLiable;
	/**审核*/
	@Excel(name = "审核", width = 15)
    @ApiModelProperty(value = "审核")
    private String paperlessExamine;
}
