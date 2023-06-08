package com.aiurt.modules.system.mapper;

import com.aiurt.modules.system.entity.SysUserPerf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.RadarPerformanceModel;

import java.util.Date;
import java.util.List;

/**
 * @Description: sys_user_perf
 * @Author: aiurt
 * @Date: 2023-06-07
 * @Version: V1.0
 */
public interface SysUserPerfMapper extends BaseMapper<SysUserPerf> {
    /**
     * 人员画像-获取用户的绩效信息
     *
     * @param date
     * @return
     */
    List<RadarPerformanceModel> getPerformance(@Param("date") Date date);
}
