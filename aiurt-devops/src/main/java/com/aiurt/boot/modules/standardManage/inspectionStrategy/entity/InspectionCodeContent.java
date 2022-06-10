package com.aiurt.boot.modules.standardManage.inspectionStrategy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description: 检修策略管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("inspection_code_content")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "inspection_code_content对象", description = "检修策略管理")
public class InspectionCodeContent {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    public Integer id;
    /**
     * 检修规范ID
     */
    @Excel(name = "检修规范ID", width = 15)
    @ApiModelProperty(value = "检修规范ID")
    public Integer inspectionCodeId;
    /**
     * 类型 1-周检 2-月检 3-双月检 4-季检 5-半年检 6-年检
     */
    @Excel(name = "类型", width = 15)
    @Min(value = 1, message = "最小值为1")
    @Max(value = 6, message = "最大值为6")
    @ApiModelProperty(value = "类型 1-周检 2-月检 3-双月检 4-季检 5-半年检 6-年检")
    public Integer type;
    /**
     * 检修内容
     */
    @Excel(name = "检修内容", width = 15)
    @Size(min = 1, max = 200, message = "检修内容长度要求1到200之间")
    @ApiModelProperty(value = "检修内容")
    public String content;
    /**
     * 策略 （1）周检该字段为1，表示本周
     * （2）月检 示例 3，本月第三周
     * （3）双月检 示例5，次月第一周
     * （4）季检 示例 6，该季度第二个月的第二周
     * （5）半年检 示例7，该半年第二个月的第三周
     * （6）年检，示例 8，本年第二个月的第四周
     */
    @Excel(name = "策略")
    @Range(min = 1, max = 48, message = "范围为1至48")
    public Integer tactics;
    @Excel(name = "安全事项ID")
    public Long spId;

    //	@Excel(name = "排序")
    @Min(value = 0, message = "最小值为0")
    @Max(value = 100, message = "最大值为100")
    @ApiModelProperty(value = "排序")
    public Integer sortNo;

    @Excel(name = "是否营收", width = 15)
    @ApiModelProperty(value = "是否营收 0-否 1-是")
    public Integer isReceipt;

    @Excel(name = "更多说明", width = 100)
    @Size(max = 500, message = "更多说明长度要求1到500之间")
    @ApiModelProperty(value = "更多说明")
    public String remarks;

    /**
     * 删除状态
     */
    @Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态")
    public Integer delFlag;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    public String createBy;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    public String updateBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    public Date createTime;
    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    public Date updateTime;
}
