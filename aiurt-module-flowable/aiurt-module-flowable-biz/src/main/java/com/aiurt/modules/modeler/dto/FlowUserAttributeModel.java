package com.aiurt.modules.modeler.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class FlowUserAttributeModel implements Serializable {

    private static final long serialVersionUID = 1146301013746778335L;

    private String value;

    private String title;
}
