package com.aiurt.common.system.base.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author fgw
 */
@Data
public class DynamicTableDataEntity implements Serializable {

    private static final long serialVersionUID = -570595707655536178L;

    /**
     *   动态的数据
     *  `${数据对应参数名}`:数据
     */
    private Map<String, Object>  dynamicData;

}
