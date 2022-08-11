package com.aiurt.boot.standard.service;

import cn.hutool.core.lang.tree.Tree;
import com.aiurt.boot.standard.dto.SysDictDTO;
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
     * @param id
     */
      List<PatrolStandardItems> queryPageList(String id);

    /**
     * 校验排序
     * @param order
     * @param id
     * @param parentId
     * @return
     */
    Boolean check(Integer order, String parentId, String id);

    /**
     * app-查询巡检工单检查项
     * @param id
     * @return
     */
    List<Tree<String>> getTaskPoolList(String id);

    /**
     * 查询数据字典
     * @param modules
     * @return
     */
    List<SysDictDTO> querySysDict(Integer modules);
    /**
     * 查询Code是否唯一
     *
     * @param id
     * @param code
     * @return
     */
    void checkCode( String code, String standardId,String id);
}
