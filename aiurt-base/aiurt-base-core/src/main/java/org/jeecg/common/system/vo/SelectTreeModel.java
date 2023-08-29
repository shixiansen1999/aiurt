package org.jeecg.common.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉树 model
 *
 * @author jeecg-boot
 */
@Data
public class SelectTreeModel implements Serializable {

    private String key;
    private String title;
    private String value;
    /**
     * 父Id
     */
    private String parentId;
    /**
     * 是否是叶节点
     */
    private boolean isLeaf;
    /**
     * 子节点
     */
    private List<SelectTreeModel> children = new ArrayList<>();

    /**
     *  添加子节点
     * @param child
     */
    public void addChild(SelectTreeModel child) {
        children.add(child);
    }

}
