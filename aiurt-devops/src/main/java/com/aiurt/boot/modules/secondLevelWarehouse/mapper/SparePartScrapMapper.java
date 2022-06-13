package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.aiurt.common.result.ReportRepairResult;
import com.aiurt.common.result.ReportWasteResult;
import com.aiurt.common.result.ScrapReportResult;
import com.aiurt.common.result.SpareConsumeNum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface SparePartScrapMapper extends BaseMapper<SparePartScrap> {

    /**
     * 备件报损分页查询
     * @param page
     * @param sparePartScrapQuery
     * @return
     */
    IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page,@Param("sparePartScrapQuery")  SparePartScrapQuery sparePartScrapQuery);

    /**
     * 备件报损导出excel所需数据
     * @param sparePartScrapQuery
     * @return
     */
    List<SparePartScrapExcel> exportXls(@Param("sparePartScrapQuery") SparePartScrapQuery sparePartScrapQuery);

    /**
     * 备件报损-批量插入
     * @param scrapList
     * @return
     */
    Integer insertBatchList(@Param("scrapList") List<SparePartScrap> scrapList);

    /**
     * 根据id查询报损详情
     * @param id
     * @return
     */
    ScrapReportResult selectDetailById(String id);


    /**
     * 获取送修详情
     * @param id
     * @return
     */
    ReportRepairResult selectRepairDetailById(String id);

    /**
     * 获取报废详情
     * @param id
     * @return
     */
    ReportWasteResult selectWasteDetailById(String id);

    /**
     * @Description: 获取备件消耗数量
     * @Return: java.lang.Integer
     */
    Integer getCountConsumeNum(Map map);

    List<SpareConsumeNum> selectSpareConsumeNumByTime(Map map);
}
