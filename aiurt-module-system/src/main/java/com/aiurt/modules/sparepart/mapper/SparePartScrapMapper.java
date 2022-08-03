package com.aiurt.modules.sparepart.mapper;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
public interface SparePartScrapMapper extends BaseMapper<SparePartScrap> {
    List<SparePartScrap> readAll(Page page, @Param("scrap") SparePartScrap sparePartScrap);
}
