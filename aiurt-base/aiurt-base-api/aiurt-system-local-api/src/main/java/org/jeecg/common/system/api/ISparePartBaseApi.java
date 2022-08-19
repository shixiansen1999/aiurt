package org.jeecg.common.system.api;


import com.aiurt.modules.sparepart.dto.SparePartMalfunctionDTO;
import com.aiurt.modules.sparepart.dto.SparePartReplaceDTO;

import java.util.List;

/**
 * @Description
 * @Author  fgw
 * @Date 2022/08/19
 * @Version V1.0
 */
public interface ISparePartBaseApi {


    /**
     * 更新
     * @param malfunctionList
     */
    void updateSparePartMalfunction(List<SparePartMalfunctionDTO> malfunctionList);

    /**
     * 更新
     * @param replaceList
     */
    void updateSparePartReplace(List<SparePartReplaceDTO> replaceList);

    /**
     * 更新出库单
     */


    /**
     * 备件-》组件， 组件报废
     */


    /**
     * 报废
     */
}
