package com.aiurt.modules.modeler.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class FlowUserRelationAttributeModel  extends FlowUserAttributeModel implements Serializable{

    private static final long serialVersionUID = 1146301013746778335L;

    private String variable;

    /**
     * 	1:用户，2机构，3角色，4岗位
     */
    private String type;

    /**
     * 类名
     */
    private String className;
}
