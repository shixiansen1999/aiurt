package com.aiurt.modules.sparepart.mapper;

import java.util.List;

import com.aiurt.modules.sparepart.entity.SparePartLend;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartLendVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;


/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartLendMapper extends BaseMapper<SparePartLend> {

    IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page,
                                         @Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);

    List<SparePartLendExcel> queryExportXls(@Param("sparePartLendQuery") SparePartLendQuery sparePartLendQuery);
}
