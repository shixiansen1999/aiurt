package com.aiurt.boot.check.vo;

import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 盘点变更明细VO对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedAssetsCheckDetailVO extends FixedAssetsCheckDetail {
    /**
     * 存放地点名称
     */
    @Excel(name = "存放地点名称", width = 15)
    @ApiModelProperty(value = "存放地点名称")
    private String locationName;
}
