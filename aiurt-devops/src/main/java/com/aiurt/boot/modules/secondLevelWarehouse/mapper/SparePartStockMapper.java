package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
