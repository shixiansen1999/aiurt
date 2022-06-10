package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface StockLevel2CheckDetailMapper extends BaseMapper<StockLevel2CheckDetail> {

    /**
     * 二级库盘点列表记录-分页查询
     * @param page
     * @param stockLevel2CheckDetailDTO
     * @return
     */
    IPage<StockLevel2CheckDetailVO> queryPageList(Page<StockLevel2CheckDetailVO> page,
                                                  @Param("stockLevel2CheckDetailDTO") StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);


    /**
     * 二级库盘点列表记录查询
     * @param stockLevel2CheckDetailDTO
     * @return
     */
    List<StockLevel2CheckDetailVO> queryList(@Param("stockLevel2CheckDetailDTO") StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);

    /**
     * 根据盘点任务单号计算实盘数量
     * @param code
     * @return
     */
    Integer selectActualNum(String code);
}
