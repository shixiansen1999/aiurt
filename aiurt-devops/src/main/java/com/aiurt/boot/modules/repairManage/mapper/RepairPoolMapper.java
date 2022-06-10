package com.aiurt.boot.modules.repairManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.aiurt.boot.modules.repairManage.vo.RepairPoolListVO;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface RepairPoolMapper extends BaseMapper<RepairPool> {

    @Select("select id,type,repair_pool_content from repair_pool where id =#{repariPoolId}")
//    @Select("select id,type,repair_pool_content from repair_pool where id =#{repariPoolId} and del_flag = 0")
    RepairPoolListVO selectTypeAndContentById(String repariPoolId);
}
