package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialApplyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/29 16:19
 */
@Data
public class EditApplyDTO {
    @ApiModelProperty("申领单号")
    @NotNull(message = "申领编号不能为空")
    private String code;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("物资的编号与数量")
    @NotNull(message = "物资的编号与数量不能为空")
    private List<MaterialApplyVO> materialVOList;
}
