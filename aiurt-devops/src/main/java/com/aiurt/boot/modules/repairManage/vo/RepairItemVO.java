package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/19 17:18
 */
@Data
public class RepairItemVO {

    public RepairItemVO() {
        this.weekComplete = 0;
        this.monthComplete= 0;
        this.DmonthComplete= 0;
        this.quarterComplete= 0;
        this.semiAnnualComplete= 0;
        this.annualComplete= 0;
        this.weekUnComplete= 0;
        this.monthUnComplete= 0;
        this.DmonthUnComplete= 0;
        this.quarterUnComplete= 0;
        this.semiAnnualUnComplete= 0;
        this.annualUnComplete= 0;
    }

    @Excel(name = "周检-已完成",width = 30)
    @ApiModelProperty(value = "周检-已完成")
    private Integer weekComplete;
    @Excel(name = "月检-已完成",width = 30)
    @ApiModelProperty(value = "月检-已完成")
    private Integer monthComplete;
    @Excel(name = "双月检-已完成",width = 30)
    @ApiModelProperty(value = "双月检-已完成")
    private Integer DmonthComplete;
    @Excel(name = "季检-已完成",width = 30)
    @ApiModelProperty(value = "季检-已完成")
    private Integer quarterComplete;
    @Excel(name = "半年检-已完成",width = 30)
    @ApiModelProperty(value = "半年检-已完成")
    private Integer semiAnnualComplete;
    @Excel(name = "年检-已完成",width = 30)
    @ApiModelProperty(value = "年检-已完成")
    private Integer annualComplete;

    @Excel(name = "周检-未完成",width = 30)
    @ApiModelProperty(value = "周检-未完成")
    private Integer weekUnComplete;
    @Excel(name = "月检-未完成",width = 30)
    @ApiModelProperty(value = "月检-未完成")
    private Integer monthUnComplete;
    @Excel(name = "双月检-未完成",width = 30)
    @ApiModelProperty(value = "双月检-未完成")
    private Integer DmonthUnComplete;
    @Excel(name = "季检-未完成",width = 30)
    @ApiModelProperty(value = "季检-未完成")
    private Integer quarterUnComplete;
    @Excel(name = "半年检-未完成",width = 30)
    @ApiModelProperty(value = "半年检-未完成")
    private Integer semiAnnualUnComplete;
    @Excel(name = "年检-未完成",width = 30)
    @ApiModelProperty(value = "年检-未完成")
    private Integer annualUnComplete;
}
