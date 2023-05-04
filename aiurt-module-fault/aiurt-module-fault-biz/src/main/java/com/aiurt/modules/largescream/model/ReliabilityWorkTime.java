package com.aiurt.modules.largescream.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2023/4/27
 * @desc
 */
@Data
@TableName("reliability_work_time")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="reliability_work_time", description="系统可靠度-记录应工作时长")
public class ReliabilityWorkTime {
    /**id*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**线路编号*/
    @ApiModelProperty(value = "线路编号")
    private String lineCode;
    /**子系统编号*/
    @ApiModelProperty(value = "子系统编号")
    private String systemCode;
    /**应工作时长*/
    @ApiModelProperty(value = "应工作时长")
    private String shouldWorkTime;
}
