package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.result.ReportRepairResult;
import com.aiurt.boot.common.result.ReportWasteResult;
import com.aiurt.boot.common.result.ScrapReportResult;
import com.aiurt.boot.common.result.SpareConsumeNum;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportRepairDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportWasteDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ISparePartScrapService extends IService<SparePartScrap> {

    /**
     * 分页查询
     * @param page
     * @param sparePartScrapQuery
     * @return
     */
    IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery);

    /**
     * 备件报损信息导出
     * @param sparePartScrapQuery
     * @return
     */
    List<SparePartScrapExcel> exportXls(SparePartScrapQuery sparePartScrapQuery);

    /**
     * 报修
     * @param dto
     * @return
     */
    Result reportRepair(ReportRepairDTO dto);

    /**
     * 报废
     * @param dto
     * @return
     */
    Result reportWaste(ReportWasteDTO dto);

    /**
     * 根据id查询报损详情
     * @param id
     * @return
     */
    Result<ScrapReportResult> getDetailById(String id);

    /**
     * 获取送修详情
     * @param id
     * @return
     */
    Result<ReportRepairResult> getRepairDetailById(String id);

    /**
     * 获取报废详情
     * @param id
     * @return
     */
    Result<ReportWasteResult> getWasteDetailById(String id);

    /**
     * @Description: 获取消耗数
     * @Return:
     */
    Integer getConsumeNum(Map map);

    List<SpareConsumeNum> getSpareConsumeNumByTime(Map map);
}
