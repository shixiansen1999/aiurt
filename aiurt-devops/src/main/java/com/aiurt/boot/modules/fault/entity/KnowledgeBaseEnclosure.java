package com.aiurt.boot.modules.fault.entity;

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

import java.util.Date;

/**
 * @Description: 故障知识库附件表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("knowledge_base_enclosure")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="knowledge_base_enclosure对象", description="故障知识库附件表")
public class KnowledgeBaseEnclosure {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**故障知识库id*/
	@Excel(name = "故障知识库id", width = 15)
    @ApiModelProperty(value = "故障知识库id")
	private  Long  knowledgeBaseId;

	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
	private  Integer  type;

	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private  String  name;

	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
	private  String  url;

	/**删除状态:0.未删除 1已删除*/
	@Excel(name = "删除状态:0.未删除 1已删除", width = 15)
	@ApiModelProperty(value = "删除状态:0.未删除 1已删除")
	private Integer delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  Date  updateTime;


    public static final String ID = "id";

    public static final String KNOWLEDGE_BASE_ID = "knowledge_base_id";

    public static final String TYPE = "type";

    public static final String NAME = "name";

    public static final String URL = "url";

    public static final String CREATE_BY = "create_by";

    public static final String UPDATE_BY = "update_by";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";



}
