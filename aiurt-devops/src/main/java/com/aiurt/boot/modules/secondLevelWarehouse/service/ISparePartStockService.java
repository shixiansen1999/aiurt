package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.result.SparePartStockResult;
import com.aiurt.boot.modules.fault.param.SparePartStockParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartStockService extends IService<SparePartStock> {

    /**
     * 分页查询
     * @param page
     * @param sparePartStockDTO
     * @return
     */
    IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page, SparePartStockDTO sparePartStockDTO);


    /**
     * 物料信息-查询
     * @param req
     * @return
     */
    List<SpareMaterialVO> queryMaterialByWarehouse(HttpServletRequest req);


    /**
     * 查询本班组的备件信息
     * @param param
     * @return
     */
    IPage<SparePartStockResult> queryStockList (IPage<SparePartStockResult> page,SparePartStockParam param);

    /**
     * 添加备注
     * @param id
     * @param remark
     * @return
     */
    Result addRemark(Integer id,String remark);
}
