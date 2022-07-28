package com.aiurt.modules.sparepart.mapper;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Component
public interface SparePartMalfunctionMapper extends BaseMapper<SparePartMalfunction> {
    List<SparePartMalfunction> readAll(@Param("malfunction") SparePartMalfunction sparePartMalfunction);
}
