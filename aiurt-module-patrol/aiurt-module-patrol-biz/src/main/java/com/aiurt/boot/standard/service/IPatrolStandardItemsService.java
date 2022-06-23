package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolStandardItemsService extends IService<PatrolStandardItems> {

    /**
     * 查询配置巡检项树
     * @return
     */
      List<PatrolStandardItems> queryPageList();

    /**
     * 校验排序
     * @param order
     * @param parentId
     * @return
     */
    Boolean check(Integer order, String parentId);
}
