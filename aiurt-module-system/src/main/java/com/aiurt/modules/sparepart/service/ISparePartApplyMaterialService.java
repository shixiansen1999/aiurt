package com.aiurt.modules.sparepart.service;


import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_apply_material
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
public interface ISparePartApplyMaterialService extends IService<SparePartApplyMaterial> {
    /**
     * 查询列表
     * @param
     * @param
     * @return
     */
    List<SparePartApplyMaterial> selectList();
}
