package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.result.SparePartStockResult;
import com.aiurt.boot.modules.fault.param.SparePartStockParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartStockMapper extends BaseMapper<SparePartStock> {

    /**
     * 备件库存分页查询
     * @param page
     * @param sparePartStockDTO
     * @return
     */
    IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page
            ,@Param("sparePartStock")SparePartStockDTO sparePartStockDTO);

    /**
     * 物料-查询
     * @param orgId
     * @return
     */
    List<SpareMaterialVO> queryMaterialByWarehouse(@Param("orgId") String orgId);

    /**
     * 查询本班组备件
     * @param page
     * @param param
     * @return
     */
    IPage<SparePartStockResult> selectStockList(IPage<SparePartStockResult> page,@Param("param")SparePartStockParam param);

    /**
     * 添加备注
     * @param id
     * @param remark
     * @return
     */
    int addRemark(Integer id,String remark);
}
