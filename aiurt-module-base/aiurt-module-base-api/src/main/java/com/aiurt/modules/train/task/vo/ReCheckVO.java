package com.aiurt.modules.train.task.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * Administrator
 * 2022/4/22
 * 复核管理实体
 */
@Data
public class ReCheckVO {
    @ApiModelProperty(value = "考试记录id")
    private String id;
    @Dict(dicCode = "recheckState")
    @ApiModelProperty(value = "复核状态")
    private Integer isRelease;
    @ApiModelProperty(value = "培训部门")
    private String sysOrgCode;
    @ApiModelProperty(value = "参考人员")
    private String examPersonName;
    @ApiModelProperty(value = "参考人员账号")
    private String userName;
    @ApiModelProperty(value = "关联考试计划")
    private String examPlanSubName;
    @ApiModelProperty(value = "考卷名称")
    private String examName;
    @Dict(dicCode = "exam_classify")
    @ApiModelProperty(value = "考试类别")
    private String examClassify;
    @ApiModelProperty(value = "考试结果")
    private String examResult;
    @Dict(dicCode = "is_pass")
    @ApiModelProperty(value = "是否及格")
    private String isPass;
    @ApiModelProperty(value = "问答题分数")
    private String daScore;
    @ApiModelProperty(value = "任务表id")
    private String taskId;
    @ApiModelProperty(value = "任务状态")
    private String taskState;
    /**复核状态显示*/
    @Excel(name = "是否显示  0不显示  1显示", width = 15)
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    @TableField(exist = false)
    private Integer reviewDisplayStatus ;
}
