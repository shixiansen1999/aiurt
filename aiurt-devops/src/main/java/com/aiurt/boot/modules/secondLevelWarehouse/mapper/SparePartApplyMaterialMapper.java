package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartApplyMaterialMapper extends BaseMapper<SparePartApplyMaterial> {

    /**
     * 备件申领物资详情/出库详情/出库确认的列表-分页列表查询
     * @param page
     * @param applyCode 申领编号
     * @return
     */
    IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, @Param("applyCode") String applyCode);

    /**
     * 批量插入备件申领物资详情
     * @param applyMaterialListAdd
     */
    void insertBatchList(@Param("applyMaterialListAdd") List<SparePartApplyMaterial> applyMaterialListAdd);

    /**
     * 计算实际出库数量
     * @param applyCode
     * @return
     */
    Integer selectActualNum(String applyCode);

    /**
     * 计算申请出库数量
     * @param applyCode
     * @return
     */
    Integer selectApplyNum(String applyCode);

}
