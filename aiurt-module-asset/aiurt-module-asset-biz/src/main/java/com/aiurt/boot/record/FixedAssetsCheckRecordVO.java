package com.aiurt.boot.record;

import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 盘点结果记录VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetsCheckRecordVO extends FixedAssetsCheckRecord {
    /**
     * 资产分类名称
     */
    @Excel(name = "资产分类名称", width = 15)
    @ApiModelProperty(value = "资产分类名称")
    private java.lang.String categoryName;
}
