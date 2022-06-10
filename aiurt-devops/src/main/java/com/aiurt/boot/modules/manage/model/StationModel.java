package com.aiurt.boot.modules.manage.model;

import com.swsc.copsms.modules.manage.entity.Station;
import com.swsc.copsms.modules.system.model.SysDepartTreeModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class StationModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 对应Station中的id字段,前端数据树中的key
     */
    private String key;

    /**
     * 对应Station中的id字段,前端数据树中的value
     */
    private String value;

    /**
     * 对应station_name字段,前端数据树中的title
     */
    private String title;

    private String id;

    private String parentId;

    private boolean isLeaf;
    private List<StationModel> children = new ArrayList<>();

    public StationModel(Station station) {
        this.key = station.getId() + "";
        this.value = station.getId() + "";
        this.title = station.getStationName();
        this.id = station.getId() + "";
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
        StationModel model = (StationModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(title, model.title) &&
                Objects.equals(children, model.children);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, title, children);
    }
}
