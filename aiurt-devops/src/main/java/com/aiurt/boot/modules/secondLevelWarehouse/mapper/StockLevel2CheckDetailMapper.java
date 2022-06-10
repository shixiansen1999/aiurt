package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface StockLevel2CheckDetailMapper extends BaseMapper<StockLevel2CheckDetail> {

    IPage<StockLevel2CheckDetailVO> queryPageList(Page<StockLevel2CheckDetailVO> page,
                                                  @Param("stockLevel2CheckDetailDTO") StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);


    List<StockLevel2CheckDetailVO> queryList(@Param("stockLevel2CheckDetailDTO") StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);
}
