package com.aiurt.modules.sparepart.mapper;


import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface SparePartInOrderMapper extends BaseMapper<SparePartInOrder> {
    /**
     * 读取所有
     * @param page
     * @param sparePartInOrder
     * @return
     */
    List<SparePartInOrder> readAll(Page page,@Param("order") SparePartInOrder sparePartInOrder);


    /**
     * 根据仓库名称查找仓库code
     * @param wareHouseName
     * @return
     */
    String selectWareHouseCode(String wareHouseName);

    /**
     * 根据物资code查找物资名称
     * @param materialCode
     * @return
     */
    String selectMaterialName(String materialCode);
}
