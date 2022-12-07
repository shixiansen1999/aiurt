package com.aiurt.boot.materials.mapper;


import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.PatrolStandardDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyMaterialsMapper extends BaseMapper<EmergencyMaterials> {

   List<MaterialAccountDTO> getMaterialAccountList (@Param("pageList") Page<MaterialAccountDTO> pageList, @Param("condition") MaterialAccountDTO condition);

   List<EmergencyMaterialsInvoicesItem> getInspectionRecord (@Param("pageList") Page<EmergencyMaterialsInvoicesItem> pageList, @Param("condition") EmergencyMaterialsInvoicesItem condition);

   List<PatrolStandardDTO> getPatrolStandardList(@Param("majorCode")List<String> majorCode);

   List<EmergencyMaterialsInvoicesItem> getMaterialInspection(@Param("id") String id);

}
