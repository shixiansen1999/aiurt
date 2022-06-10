package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartInOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartInVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 备件入库表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartInOrderService extends IService<SparePartInOrder> {

    /**
     * 分页查询
     * @param page
     * @param sparePartInQuery
     * @return
     */
    IPage<SparePartInVO> queryPageList(Page<SparePartInVO> page, SparePartInQuery sparePartInQuery);


    /**
     * excel导出
     * @param sparePartInQuery
     * @return
     */
    List<SparePartInExcel> exportXls(SparePartInQuery sparePartInQuery);

    /**
     * 批量确认
     * @param ids
     * @param req
     * @return
     */
    Result<?> confirmBatch(String ids,HttpServletRequest req);
}
