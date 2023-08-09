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
public class FlowRelationDTO implements Serializable {

    /**
     * 条件关系值
     */
    private String value;

    /**
     * 条件关系值别名
     */
    private String relationAlias;
}
