package com.aiurt.modules.sparepart.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.springframework.stereotype.Component;

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
    Long stockCount(@Param("systemCode") String systemCode, @Param("baseTypeCode") String baseTypeCode);

    /**
     * 三级库的库存量
     * @param systemCode
     * @param baseTypeCode
     * @return
     */
    Long sparePartCount(@Param("systemCode") String systemCode, @Param("baseTypeCode") String baseTypeCode);

    /**
     * 根据userId查询子系统
     * @param id
     * @return
     */
    List<SparePartStatistics> getSubsystemByUserId(Page page,@Param("id") String id);

}
