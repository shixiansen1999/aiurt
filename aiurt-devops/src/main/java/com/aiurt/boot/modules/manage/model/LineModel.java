package com.aiurt.boot.modules.manage.model;

import com.aiurt.boot.modules.manage.entity.Line;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class LineModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 对应Line中的id字段,前端数据树中的key
     */
    private String key;

    /**
     * 对应Line中的id字段,前端数据树中的value
     */
    private String value;

    /**
     * 对应lineName字段,前端数据树中的title
     */
    private String title;

    private String id;

    private String parentId;

    private String description;

    private String code;

    private boolean isLeaf;
    private List<LineModel> children = new ArrayList<>();

    public LineModel(Line line) {
        this.key = line.getId() + "";
        this.value = line.getId() + "";
        this.title = line.getLineName();
        this.id = line.getId() + "";
        this.code = line.getLineCode();
        this.description = line.getDescription();
        this.parentId = null;
        this.isLeaf = true;
    }

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineModel model = (LineModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(title, model.title) &&
                Objects.equals(code, model.code) &&
                Objects.equals(children, model.children);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, title, code, children);
    }
}
