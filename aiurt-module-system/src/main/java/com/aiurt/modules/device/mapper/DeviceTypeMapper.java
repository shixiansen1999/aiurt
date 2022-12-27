package com.aiurt.modules.device.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface DeviceTypeMapper extends BaseMapper<DeviceType> {
    /**
     * 读取所有
     * @return
     */
    List<DeviceType> readAll();
    /**
     * 查询专业
     * @return
     * @param majorCodeName
     */
    CsUserMajorModel selectCsMajor(@Param("majorCodeName") String majorCodeName);

    /**
     * 查询子系统
     *
     * @param code
     * @param systemName
     * @return
     */
    String selectSystemCode(@Param("systemName") String  systemName,@Param("code")String code);
}
