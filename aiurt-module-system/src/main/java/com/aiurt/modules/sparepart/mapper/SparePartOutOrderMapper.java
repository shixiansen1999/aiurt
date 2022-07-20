package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.dto.SparePartOutExcel;
import com.aiurt.modules.sparepart.entity.vo.SparePartOutVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartOutOrderMapper extends BaseMapper<SparePartOutOrder> {

    IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery);

    List<SparePartOutExcel>  exportXls(@Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);
}
