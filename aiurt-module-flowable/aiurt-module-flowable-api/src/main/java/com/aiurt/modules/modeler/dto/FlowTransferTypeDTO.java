package com.aiurt.modules.modeler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wgp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowTransferTypeDTO implements Serializable {
    /**
     * 流转类型
     */
    private String value;

}
