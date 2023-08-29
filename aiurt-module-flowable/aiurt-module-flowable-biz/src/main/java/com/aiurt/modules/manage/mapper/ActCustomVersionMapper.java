package com.aiurt.modules.manage.mapper;


import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 版本管理
 * @Author: aiurt
 * @Date:   2022-07-15
 * @Version: V1.0
 */
public interface ActCustomVersionMapper extends BaseMapper<ActCustomVersion> {

    /**
     *
     * @param page
     * @param modelId
     * @return
     */
    List<ActCustomVersion> queryPageList(@Param("pageList") Page<ActCustomVersion> page, @Param("modelId") String modelId);
}
