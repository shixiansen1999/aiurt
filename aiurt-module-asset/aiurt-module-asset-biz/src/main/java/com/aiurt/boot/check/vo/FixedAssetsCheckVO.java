package com.aiurt.boot.check.vo;

import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.entity.FixedAssetsCheckDept;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 固定资产盘点任务信息表-详情VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetsCheckVO extends FixedAssetsCheck {
    /**
     * 组织机构信息
     */
    @ApiModelProperty(value = "组织机构信息")
    List<FixedAssetsCheckDept> depts;
    /**
     *
     */
    @ApiModelProperty(value = "资产分类信息")
    List<FixedAssetsCheckCategory> categorys;
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @TableField(exist = false)
    private String processInstanceId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;
    /**
     * 模板key，流程标识
     */
    @ApiModelProperty(value = "模板key，流程标识")
    @TableField(exist = false)
    private String modelKey;
}
