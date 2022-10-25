package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartApply;

import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description: spare_part_apply
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface SparePartApplyMapper extends BaseMapper<SparePartApply> {
    /**
     * 导出
     * @param ids
     * @return
     */
    List<SparePartApply> selectExportXls(@Param("ids") List<String> ids);

    /**
     * 读取所有
     * @param page
     * @param sparePartApply
     * @return
     */
    List<SparePartApply> readAll(Page page,@Param("sparePartApply")  SparePartApply sparePartApply);
}
