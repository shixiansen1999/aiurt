package com.aiurt.boot.modules.repairManage.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.repairManage.entity.RepairPool;
import com.swsc.copsms.modules.repairManage.vo.RepairPoolListVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface RepairPoolMapper extends BaseMapper<RepairPool> {

    @Select("select id,type,repair_pool_content from repair_pool where id =#{repariPoolId} and del_flag = 0")
    RepairPoolListVO selectTypeAndContentById(String repariPoolId);
}
