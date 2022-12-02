package com.aiurt.modules.modeler.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class ActOperationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String label;

    private String type;

    private Integer showOrder;

    private Integer isDisplayRemark;

    private Integer isRequireRemark;
}
