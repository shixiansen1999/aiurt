package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/19 09:44
 */
@Data
public class WorkLoadVO {
    @Excel(name = "姓名",width = 30)
    @ApiModelProperty(value = "姓名")
    private String userName;
    @Excel(name = "检修时长(分钟)",width = 30)
    @ApiModelProperty(value = "检修时长(分钟)")
    private Integer repairDuration;
    @Excel(name = "总检修数",width = 30)
    @ApiModelProperty(value = "总检修数")
    private Integer repaireAmount;
    @Excel(name = "已确认数",width = 30)
    @ApiModelProperty(value = "已确认数")
    private Integer confirmAmount;
    @Excel(name = "待确认数",width = 30)
    @ApiModelProperty(value = "待确认数")
    private Integer unconfirmAmout;
    @Excel(name = "已验收数",width = 30)
    @ApiModelProperty(value = "已验收数")
    private Integer acceptAmount;
    @Excel(name = "待验收数",width = 30)
    @ApiModelProperty(value = "待验收数")
    private Integer unacceptAmount;
}
