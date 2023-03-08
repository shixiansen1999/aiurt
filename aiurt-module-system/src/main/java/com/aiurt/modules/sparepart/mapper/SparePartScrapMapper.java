package com.aiurt.modules.sparepart.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface SparePartScrapMapper extends BaseMapper<SparePartScrap> {
    /**
     * 查询所有数据
     * @param page
     * @param sparePartScrap
     * @return
     */
    List<SparePartScrap> readAll(Page page, @Param("scrap") SparePartScrap sparePartScrap);
    /**
     * 查询所有数据不分页
     * @param sparePartScrap
     * @return
     */
    List<SparePartScrap> readAll(@Param("scrap") SparePartScrap sparePartScrap);
}
