package com.aiurt.boot.modules.patrol.entity;

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

/**
 * @Description: 巡检-附件表
 * @Author: swsc
 * @Date:   2021-09-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_task_enclosure")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="patrol_task_enclosure对象", description="巡检-附件表")
public class PatrolTaskEnclosure {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**parent库名		指向某个库名,如:PatrolTask*/
	@Excel(name = "parent库名		指向某个库名,如:PatrolTask", width = 15)
    @ApiModelProperty(value = "parent库名		指向某个库名,如:PatrolTask")
	private  String  type;

	/**父类库id*/
	@Excel(name = "父类库id", width = 15)
    @ApiModelProperty(value = "父类库id")
	private  Long  parentId;

	/**url地址*/
	@Excel(name = "url地址", width = 15)
    @ApiModelProperty(value = "url地址")
	private  String  url;

	/**删除状态	0.未删除 1已删除*/
	@Excel(name = "删除状态	0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态	0.未删除 1已删除")
	private  Integer  delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间		CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间		CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间		CURRENT_TIMESTAMP")
	private  java.util.Date  createTime;

	/**修改时间		根据当前时间戳更新*/
	@Excel(name = "修改时间		根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间		根据当前时间戳更新")
	private  java.util.Date  updateTime;


    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String PARENT_ID = "parent_id";
    public static final String URL = "url";
    public static final String DEL_FLAG = "del_flag";
    public static final String CREATE_BY = "create_by";
    public static final String UPDATE_BY = "update_by";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";


}
