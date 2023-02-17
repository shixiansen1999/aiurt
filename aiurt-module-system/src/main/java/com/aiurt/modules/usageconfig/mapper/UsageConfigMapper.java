package com.aiurt.modules.usageconfig.mapper;


import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
public interface UsageConfigMapper extends BaseMapper<UsageConfig> {

    Page<UsageConfigDTO> getList(@Param("pageList") Page<UsageConfigDTO> pageList, @Param("usageConfigDTO")UsageConfigDTO usageConfigDTO);

    List<UsageConfigDTO> getAllList();
    /**
     * 根据表名查询该表的所有数据记录
     * @param tableName
     * @return
     */
    Integer getTotal(@Param("tableName") String tableName,@Param("staCondition") String staCondition);


    /**
     * 根据表名查询该表在一定时间范围内的所有数据记录
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    Integer getNewNumber(@Param("tableName") String tableName,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("staCondition") String staCondition);

}
