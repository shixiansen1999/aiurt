package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Component
public interface SparePartReturnOrderMapper extends BaseMapper<SparePartReturnOrder> {
    List<SparePartReturnOrder> readAll(Page page, @Param("order") SparePartReturnOrder sparePartReturnOrder);
}
