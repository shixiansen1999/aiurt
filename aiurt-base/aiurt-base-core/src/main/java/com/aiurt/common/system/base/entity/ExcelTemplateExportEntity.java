package com.aiurt.common.system.base.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.common.system.vo.DictModel;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelTemplateExportEntity implements Serializable {

    private static final long serialVersionUID = -1578798108522251854L;

    /**
     * 编号
     */
    private int index;

    /**
     * 列明
     */
    private String name;

    /**
     * 字典值
     */
    private List<DictModel> dictModelList;

    /**
     * 批注
     */
    private String remark;

    /**
     * 是否必填
     */
    private Boolean isRequired;
}
