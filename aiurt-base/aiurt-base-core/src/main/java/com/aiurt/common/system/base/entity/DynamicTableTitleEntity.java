package com.aiurt.common.system.base.entity;

import com.aiurt.modules.common.entity.SelectTable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class DynamicTableTitleEntity implements Serializable {

    /**
     * 表头显示名称
     */
    private String title;

    /**
     * 数据对应参数名
     */
    private String dataIndex;

    private String id;

    private String pid;

    /**
     * 子级
     */
    private List<DynamicTableTitleEntity> children;


    public void addChildren(DynamicTableTitleEntity child) {
        if (children == null) {
            children = new ArrayList<DynamicTableTitleEntity>();
        }
        children.add(child);
    }
}
