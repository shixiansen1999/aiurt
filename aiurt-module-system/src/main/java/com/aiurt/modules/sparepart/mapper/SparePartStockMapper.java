package com.aiurt.modules.sparepart.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface SparePartStockMapper extends BaseMapper<SparePartStock> {
    List<SparePartStock> readAll(Page page, @Param("stock") SparePartStock sparePartStock);

    /**
     * 二级库的库存量
     * @param systemCode
     * @param baseTypeCode
     * @return
     */
    Long stockCount(@Param("systemCode") List<String> systemCode, @Param("baseTypeCode") List<String> baseTypeCode);

    /**
     * 三级库的库存量
     * @param systemCode
     * @param baseTypeCode
     * @return
     */
    Long sparePartCount(@Param("systemCode") List<String> systemCode, @Param("baseTypeCode") List<String> baseTypeCode);

    /**
     * 根据userId查询子系统
     * @param pageList
     * @param id
     * @param systemCode
     * @return
     */
    List<SparePartStatistics> getSubsystemByUserId(Page<SparePartStatistics> pageList,@Param("id") String id,@Param("systemCode") List<String> systemCode);

    /**
     * 查询年度消耗量(根据年月)
     * @param systemCode
     * @param baseTypeCode
     * @param year
     * @param month
     * @return
     */
    Long timeCount(@Param("systemCode") List<String> systemCode,
                   @Param("baseTypeCode") List<String> baseTypeCode ,
                   @Param("year") Integer year,
                   @Param("month") Integer month);

    /**
     *
     * 查询年度消耗量(根据时间节点)
     * @param systemCode
     * @param baseTypeCode
     * @param startDate
     * @param endDate
     * @return
     */
    Long getTimeCount(@Param("systemCode") List<String> systemCode,
                      @Param("baseTypeCode") List<String> baseTypeCode ,
                      @Param("startDate") Date startDate,
                      @Param("endDate") Date endDate);

}
