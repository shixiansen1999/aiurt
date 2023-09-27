package com.aiurt.modules.stock.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.stock.entity.StockLevel2;
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
public interface StockLevel2Mapper extends BaseMapper<StockLevel2> {
    /**
     * 分页查询
     * @param page
     * @param stockLevel2
     * @return
     */
    List<StockLevel2> pageList(Page<StockLevel2> page, @Param("condition") StockLevel2 stockLevel2);

    /**
     * 根据Id获取
     * @param id
     * @return
     */
    StockLevel2 getDetailById(@Param("id") String id);

    /**
     * 导出
     * @param ids
     * @return
     */
    List<StockLevel2> exportXls(@Param("condition") StockLevel2 stockLevel2,@Param("ids") List<String> ids);

    /**
     * 获取指定二级库仓库的物资的可使用数量
     * @param materialsCode  物资编码
     * @param leve2WarehouseCode 二级库变密码
     * @return
     */
    Integer getAvailableNum(@Param("materialsCode")String materialsCode, @Param("leve2WarehouseCode")String leve2WarehouseCode);
}
