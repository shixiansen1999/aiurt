package com.aiurt.modules.param.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class ParamTypeTreeDTO implements Serializable {

    private static final long serialVersionUID = 5189828299385190109L;


    private String id;

    private String key;

    private String value;

    private String label;

    private String title;

    private String pid;

    private List<ParamTypeTreeDTO> children;


    public void addChildren(ParamTypeTreeDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }
}
