package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartScrapQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartScrapVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * @Description: 备件报损
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface SparePartScrapMapper extends BaseMapper<SparePartScrap> {

    IPage<SparePartScrapVO> queryPageList(Page<SparePartScrapVO> page, SparePartScrapQuery sparePartScrapQuery);

    List<SparePartScrapExcel> exportXls(@Param("sparePartScrapQuery") SparePartScrapQuery sparePartScrapQuery);
}
