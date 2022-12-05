package com.aiurt.boot.materials.mapper;


import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyMaterialsInvoicesItemMapper extends BaseMapper<EmergencyMaterialsInvoicesItem> {

    /**
     * 巡检记录查询
     * @param materialsCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<EmergencyMaterialsInvoicesItem> getPatrolRecord (@Param("materialsCode") String materialsCode, @Param("startTime") String startTime,@Param("endTime") String endTime);
}
