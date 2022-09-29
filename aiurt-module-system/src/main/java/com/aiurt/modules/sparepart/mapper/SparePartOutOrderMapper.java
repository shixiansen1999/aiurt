package com.aiurt.modules.sparepart.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liquibase.pro.packaged.P;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface SparePartOutOrderMapper extends BaseMapper<SparePartOutOrder> {
    List<SparePartOutOrder> readAll(Page page, @Param("out") SparePartOutOrder sparePartOutOrder);
    List<SparePartOutOrder> selectMaterial(Page page, @Param("out") SparePartOutOrder sparePartOutOrder);

    /**
     * 更新未使用的数量
     * @param id
     * @param num
     */
    void updateSparePartOutOrderUnused(@Param("id") String id, @Param("num") Integer num);
}
