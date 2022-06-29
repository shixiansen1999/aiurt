package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolStandardItemsMapper extends BaseMapper<PatrolStandardItems> {

    List<PatrolStandardItems> selectList(String id);

    /**
     * app-获取巡检项目信息
     * @param id
     * @return
     */
    List<PatrolStandardItems> getList(String id);

    /**
     * 逻辑删除父级下面所有子集
     * @param id
     */
    void updatPId(@Param("id") String id);
}
