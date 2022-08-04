package com.aiurt.modules.sparepart.mapper;


import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_apply_material
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Component
public interface SparePartApplyMaterialMapper extends BaseMapper<SparePartApplyMaterial> {
    List<SparePartApplyMaterial> readAll();
}
