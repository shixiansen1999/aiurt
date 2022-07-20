package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStockDTO;
import com.aiurt.modules.sparepart.entity.vo.SpareMaterialVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartStockMapper extends BaseMapper<SparePartStock> {

    IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page
            , @Param("sparePartStock") SparePartStockDTO sparePartStockDTO);

    List<SpareMaterialVO> queryMaterialByWarehouse(@Param("warehouseCode") String warehouseCode);
}
