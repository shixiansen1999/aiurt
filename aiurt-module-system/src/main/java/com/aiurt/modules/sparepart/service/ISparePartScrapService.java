package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
public interface ISparePartScrapService extends IService<SparePartScrap> {
    /**
     * 查询列表
     * @param page
     * @param sparePartScrap
     * @return
     */
    List<SparePartScrap> selectList(Page page, SparePartScrap sparePartScrap);

}
