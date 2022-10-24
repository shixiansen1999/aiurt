package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.dto.SysDictDTO;
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
    /**
     * 根据巡检标准表ID获取巡检标准项目列表
     * @param id
     * @return
     */
    List<PatrolStandardItems> selectItemList(String id);

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
    void updatPid(@Param("id") String id);

    /**
     * 查询数据字典
     * @param modules
     * @return
     */
    List<SysDictDTO> querySysDict(@Param("modules") Integer modules);
}
