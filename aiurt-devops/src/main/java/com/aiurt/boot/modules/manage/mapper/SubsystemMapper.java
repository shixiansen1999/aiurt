package com.aiurt.boot.modules.manage.mapper;

import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: cs_subsystem
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface SubsystemMapper extends BaseMapper<Subsystem> {

    /**
     * 根据系统名称查询站点信息
     * @param systemName
     * @return
     */
    Subsystem selectByName(String systemName);

    List<Subsystem> getSubSystemByStationName(String stationName);

    @Select("select * from cs_subsystem where system_code = #{systemCode}")
    Subsystem selectByCode(String systemCode);
}
