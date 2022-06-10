package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.common.result.FaultDeviceChangSpareResult;
import com.aiurt.common.result.SpareResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface DeviceChangeSparePartMapper extends BaseMapper<DeviceChangeSparePart> {


    /**
     * 根据code查询更换备件
     * @param code
     * @return
     */
    List<SpareResult> querySpare(String code);

    /**
     * 根据设备编号查询故障信息
     * @param page
     * @param id
     * @param param
     * @return
     */
    IPage<FaultDeviceChangSpareResult> selectFaultDevice(IPage<FaultDeviceChangSpareResult> page, Long id, @Param("param") FaultDeviceParam param);

}
