package com.aiurt.boot.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault_knowledge_base")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_knowledge_base对象", description="故障知识库")
public class FaultKnowledgeBaseDTO {

	/**主键id*/
	@TableId(type= IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**类型id*/
	@Excel(name = "类型id", width = 15)
	@ApiModelProperty(value = "类型id")
	private Integer typeId;

	/**故障类型*/
	@Excel(name = "故障类型", width = 15)
	@ApiModelProperty(value = "故障类型")
	private Integer faultType;

	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
	@ApiModelProperty(value = "故障现象")
	@NotBlank(message = "故障现象不能为空")
	private String faultPhenomenon;

	/**故障原因*/
	@Excel(name = "故障原因", width = 15)
	@ApiModelProperty(value = "故障原因")
	@NotBlank(message = "故障原因不能为空")
	private String faultReason;

	/**故障措施/解决方案*/
	@Excel(name = "故障措施/解决方案", width = 15)
	@ApiModelProperty(value = "故障措施/解决方案")
	@NotBlank(message = "故障措施不能为空")
	private String solution;

	/**关联故障集合,例:G101.2109.001，G101.2109.002*/
	@Excel(name = "关联故障集合,例:G101.2109.001，G101.2109.002", width = 15)
	@ApiModelProperty(value = "关联故障集合,例:G101.2109.001，G101.2109.002")
	private String faultCodes;


	/**系统编号*/
	@Excel(name = "系统编号", width = 15)
	@ApiModelProperty(value = "系统编号")
	@NotBlank(message = "系统名称不能为空")
	private String systemCode;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**创建时间,CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间,CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
	private Date createTime;

	/**修改时间,根据当前时间戳更新*/
	@Excel(name = "修改时间,根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间,根据当前时间戳更新")
	private Date updateTime;

	/**附件列表*/
	public List<String> urlList;
}
