package com.aiurt.boot.materials.mapper;


import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.MaterialBaseDTO;
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

   List<EmergencyMaterialsInvoicesItem> getMaterialInspection(@Param("pageList") Page<EmergencyMaterialsInvoicesItem> pageList,@Param("id") String id);

    /**
     * 查询台账信息
     * @param condition
     * @return
     */
    List<MaterialAccountDTO> getMaterialPatrolList(@Param("condition") MaterialAccountDTO condition);

    /**
     * 根据部门code、用户名查找用户信息
     * @param userName
     * @param orgCode
     * @return
     */
    String getUserId(@Param("userName")String userName, @Param("orgCode")String orgCode);

    /**
     * 根据部门名称,查找部门code
     * @param primaryName
     * @return
     */
    String getOrgCode(@Param("primaryName")String primaryName);

    /**
     * 根据线路code、站点名称，查询站点信息
     * @param lineCode
     * @param stationName
     * @return
     */
    String getStationCode(@Param("lineCode")String lineCode, @Param("stationName")String stationName);

    /**
     * 根据线路code、站点code，位置名称，查询位置信息
     * @param lineCode
     * @param stationCode
     * @param positionName
     * @return
     */
    String getPositionCode(@Param("lineCode")String lineCode, @Param("stationCode")String stationCode, @Param("positionName")String positionName);

    /**
     * 根据线路名称，查询线路信息
     * @param lineName
     * @return
     */
    String getLineCode(@Param("lineName")String lineName);

    /**
     * 获取物资主数据的信息
     * @param materialsCode
     * @param materialsName
     * @return
     */
    MaterialBaseDTO getMaterials(@Param("materialsCode")String materialsCode, @Param("materialsName")String materialsName);
}
