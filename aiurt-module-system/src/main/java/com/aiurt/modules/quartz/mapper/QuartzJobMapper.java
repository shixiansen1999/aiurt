package com.aiurt.modules.quartz.mapper;

import java.util.List;

import com.aiurt.modules.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

    /**
     * 根据jobClassName查询
     * @param jobClassName 任务类名
     * @return
     */
	public List<QuartzJob> findByJobClassName(@Param("jobClassName") String jobClassName);

}
