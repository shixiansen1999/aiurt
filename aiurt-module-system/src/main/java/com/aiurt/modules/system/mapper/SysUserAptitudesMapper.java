package com.aiurt.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.system.entity.SysUserAptitudes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.personnelportrait.dto.RadarAptitudeModelDTO;

/**
 * @Description: sys_user_aptitudes
 * @Author: aiurt
 * @Date: 2023-06-07
 * @Version: V1.0
 */
public interface SysUserAptitudesMapper extends BaseMapper<SysUserAptitudes> {
    /**
     * 雷达图-获取用户的资质信息
     *
     * @param orgCode
     * @return
     */
    List<RadarAptitudeModelDTO> getAptitude(@Param("orgCode") String orgCode);
}
