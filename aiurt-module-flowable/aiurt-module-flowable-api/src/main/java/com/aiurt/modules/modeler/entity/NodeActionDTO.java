package com.aiurt.modules.modeler.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:wgp
 * @create: 2023-08-11 12:24
 * @Description: 节点附加操作
 */
@Data
public class NodeActionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stateUpdate;
    private String customInterface;
    private String customSql;
}
