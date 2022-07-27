package com.aiurt.modules.sparepart.mapper;


import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Component
public interface SparePartInOrderMapper extends BaseMapper<SparePartInOrder> {
    List<SparePartInOrder> readAll(Page page,@Param("order") SparePartInOrder sparePartInOrder);
}
