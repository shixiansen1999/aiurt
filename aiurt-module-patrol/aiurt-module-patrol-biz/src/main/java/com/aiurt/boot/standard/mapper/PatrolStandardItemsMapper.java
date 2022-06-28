package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolStandardItemsMapper extends BaseMapper<PatrolStandardItems> {

    List<PatrolStandardItems> selectList();

    /**
     * app-获取巡检项目信息
     * @param id
     * @return
     */
    List<PatrolStandardItems> getList(String id);
}
