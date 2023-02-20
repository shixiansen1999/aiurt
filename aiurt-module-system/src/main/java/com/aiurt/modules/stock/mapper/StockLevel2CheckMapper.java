package com.aiurt.modules.stock.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@EnableDataPerm
public interface StockLevel2CheckMapper extends BaseMapper<StockLevel2Check> {
    /**
     * 分页查询
     *
     * @param page
     * @param stockLevel2Check
     * @return
     */
    List<StockLevel2Check> pageList(Page<StockLevel2Check> page, @Param("condition") StockLevel2Check stockLevel2Check);
    /**
     * 导出数据
     * @param level2Check
     * @param ids
     * @return
     */
    List<StockLevel2Check> exportList(@Param("condition")StockLevel2Check level2Check,@Param("ids") List<String> ids);
}
