package com.aiurt.boot.index.mapper;

import com.aiurt.boot.index.dto.TaskDetailsDTO;
import com.aiurt.boot.index.dto.TaskDetailsReq;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 首页检修模块
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface IndexPlanMapper {

    /**
     * 分页聚合检修数据
     *
     * @param type
     * @param page
     * @param taskDetailsReq
     * @return
     */
    List<TaskDetailsDTO> getGropuByData(@Param("type") Integer type, @Param("page") Page<TaskDetailsDTO> page, @Param("taskDetailsReq") TaskDetailsReq taskDetailsReq);

}
