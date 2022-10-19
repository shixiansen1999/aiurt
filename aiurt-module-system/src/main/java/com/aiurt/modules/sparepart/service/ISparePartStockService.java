package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartConsume;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
public interface ISparePartStockService extends IService<SparePartStock> {
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    List<SparePartStock> selectList(Page page, SparePartStock sparePartStock);
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    List<SparePartStock> selectLendList(Page page, SparePartStock sparePartStock);

    /**
     * 备件类型数量统计分析表
     * @param page
     * @param sparePartStatistics
     * @return
     */
    List<SparePartStatistics> selectSparePartStatistics(Page page, SparePartStatistics sparePartStatistics);

    /**
     * 备件类型数量消耗态势
     * @param sparePartConsume
     * @return
     */
    List<SparePartConsume> selectConsume(SparePartConsume sparePartConsume);

    /**
     * 备件报表导出
     * @param request
     * @param sparePartStatistics
     * @return
     */
    ModelAndView reportExport(HttpServletRequest request, SparePartStatistics sparePartStatistics);
}
