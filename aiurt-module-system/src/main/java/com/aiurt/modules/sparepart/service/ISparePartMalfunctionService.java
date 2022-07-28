package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
public interface ISparePartMalfunctionService extends IService<SparePartMalfunction> {
    /**
     * 查询列表
     * @param
     * @param sparePartMalfunction
     * @return
     */
    List<SparePartMalfunction> selectList(SparePartMalfunction sparePartMalfunction);
}
