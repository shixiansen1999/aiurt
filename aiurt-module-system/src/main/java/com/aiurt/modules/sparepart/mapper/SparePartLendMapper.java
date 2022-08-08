package com.aiurt.modules.sparepart.mapper;


import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Component
public interface SparePartLendMapper extends BaseMapper<SparePartLend> {
    List<SparePartLend> readAll(Page page, @Param("lend") SparePartLend sparePartLend);
}
