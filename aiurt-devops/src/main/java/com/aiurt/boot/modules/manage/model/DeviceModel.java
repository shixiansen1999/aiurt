package com.aiurt.boot.modules.manage.model;

import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
public class DeviceModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 对应Device中的id字段,前端数据树中的key
     */
    private String key;

    /**
     * 对应Device中的id字段,前端数据树中的value
     */
    private String value;

    /**
     * 对应station_name字段,前端数据树中的title
     */
    private String title;

    private String id;

    private String parentId;

    private boolean isLeaf;

    private Integer type;

    private String parentName;
    private List<DeviceModel> children = new ArrayList<>();

    public DeviceModel(Subsystem subsystem) {
        this.key = subsystem.getId() + "";
        this.value = subsystem.getSystemName();
        this.title = subsystem.getSystemName();
        this.id = subsystem.getId() + "";
        this.type = 1;
        this.parentId = null;
        if (subsystem.getDeviceTypeList() == null || subsystem.getDeviceTypeList().size() == 0) {
            this.isLeaf = true;
        } else {
            this.isLeaf = false;
            List<DeviceModel> children = new ArrayList<>();
            for (DeviceType type : subsystem.getDeviceTypeList()) {
                DeviceModel model = new DeviceModel();
                model.setId(type.getId() + "");
                model.setKey(subsystem.getId() + "-" + type.getId());
                model.setTitle(type.getName());
                model.setParentId(subsystem.getId() + "");
                model.setValue(type.getName());
                model.setType(2);
                model.setLeaf(true);
                model.setParentName(subsystem.getSystemName());
                children.add(model);
            }
            this.children = children;
        }
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
        DeviceModel model = (DeviceModel) o;
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
