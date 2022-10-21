package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolAccessory;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecg.common.system.vo.DictModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cgkj0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatrolCheckResultDTO extends PatrolCheckResult {
    /**
     * 巡检人
     */
    @ApiModelProperty(value = "巡检人")
     private  String checkUserName;
    /**
     * 翻译的检修值
     */
    @ApiModelProperty(value = "检查值名称")
     private  String checkDictName;
    /**
     * 子节点
     */
    @ApiModelProperty(value = "子节点")
    List<PatrolCheckResultDTO> children = new ArrayList<>();
    /**
     * 附件信息
     */
    @ApiModelProperty(value = "附件信息")
    private List<PatrolAccessory> accessoryInfo;
    /**
     * 附件信息
     */
    @ApiModelProperty(value = "附件信息")
    private List<PatrolAccessoryDTO> accessoryDTOList;
    /**
     * 字典下拉列表
     */
    @ApiModelProperty(value = "字典下拉列表")
    private  List<DictModel> list;
}
