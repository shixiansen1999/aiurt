package org.jeecg.common.system.api;


import com.aiurt.modules.fault.dto.SparePartStockDTO;
import com.aiurt.modules.sparepart.dto.DeviceChangeSparePartDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author  fgw
 * @Date 2022/08/19
 * @Version V1.0
 */
public interface ISparePartBaseApi {


    /**
     * 更新出库单, 更新实际使用的数量
     * @param updateMap
     */
    void updateSparePartOutOrder(Map<String, Integer> updateMap);

    /**
     * 处理备件更换
     * @param sparePartList
     */
    void dealChangeSparePart(List<DeviceChangeSparePartDTO> sparePartList);

    /**
     * 故障：备件更换（组件、易耗品）
     * @param nonConsumablesList
     * @param faultCode
     */
    void addSparePartOutOrder(List<SparePartStockDTO> nonConsumablesList,String faultCode);
}
