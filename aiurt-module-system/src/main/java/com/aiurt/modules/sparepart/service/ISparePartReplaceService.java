package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartReplace;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: spare_part_replace
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
public interface ISparePartReplaceService extends IService<SparePartReplace> {

    IPage<SparePartReplace> pageList(Page<SparePartReplace> page, SparePartReplace sparePartReplace);
}
