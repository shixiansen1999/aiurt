package com.aiurt.modules.modeler.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class FlowUserModel implements Serializable {

    List<FlowUserAttributeModel> org;

    List<FlowUserAttributeModel> post;

    List<FlowUserAttributeModel> role;

    List<FlowUserAttributeModel> user;

    List<FlowUserRelationAttributeModel> relation;
}
