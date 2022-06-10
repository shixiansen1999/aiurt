package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2Query;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2VO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 二级库库存信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface StockLevel2Mapper extends BaseMapper<StockLevel2> {

    /**
     * 二级库库存信息-分页查询
     * @param page
     * @param stockLevel2Query
     * @return
     */
    IPage<StockLevel2VO> queryPageList(Page<StockLevel2VO> page,@Param("stockLevel2Query") StockLevel2Query stockLevel2Query);

    /**
     * 导出excel
     * @param stockLevel2Query
     * @return
     */
    List<StockLevel2VO> exportXls(@Param("stockLevel2Query") StockLevel2Query stockLevel2Query);

    /**
     * 添加备注
     * @param id
     * @param remark
     * @return
     */
    int addRemark(Integer id,String remark);
}
