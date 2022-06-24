package com.aiurt.boot.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.mapper.InspectionManagerMapper;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 检修模块通用业务层
 * @date 2022/6/2216:56
 */
@Service
public class InspectionManager {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private InspectionManagerMapper inspectionManagerMapper;

    /**
     * 翻译专业、专业子系统信息
     *
     * @param codeList code值
     * @param type     类型：major代表专业、subsystem代表子系统
     * @return
     */
    public String translateMajor(List<String> codeList, String type) {
        if (CollUtil.isEmpty(codeList) || StrUtil.isEmpty(type)) {
            return "";
        }
        List<String> nameList = new ArrayList<>();
        if (InspectionConstant.MAJOR.equals(type)) {
            nameList = inspectionManagerMapper.translateMajor(codeList);
        }
        if (InspectionConstant.SUBSYSTEM.equals(type)) {
            nameList = inspectionManagerMapper.translateSubsystem(codeList);
        }
        String result = StrUtil.join(",", nameList);
        return CollUtil.isNotEmpty(nameList) ? StrUtil.join(",", nameList) : "";
    }

    /**
     * 翻译组织机构信息
     *
     * @param codeList code值
     * @return
     */
    public String translateOrg(List<String> codeList) {
        if (CollUtil.isEmpty(codeList)) {
            return "";
        }
        List<String> nameList = inspectionManagerMapper.translateOrg(codeList);
        return CollUtil.isNotEmpty(nameList) ? StrUtil.join(",", nameList) : "";
    }

    /**
     * 翻译站点信息
     *
     * @param codeList code值
     * @return
     */
    public String translateStation(List<StationDTO> codeList) {

        if (CollUtil.isEmpty(codeList)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        // 处理字符拼接
        for (StationDTO stationDTO : codeList) {
            if (StrUtil.isNotEmpty(stationDTO.getLineCode())) {
                builder.append(inspectionManagerMapper.translateLine(stationDTO.getLineCode()));
            }
            if (StrUtil.isNotEmpty(stationDTO.getStationCode())) {
                builder.append(inspectionManagerMapper.translateStation(stationDTO.getStationCode()));
            }
            if (StrUtil.isNotEmpty(stationDTO.getPositionCode())) {
                builder.append(inspectionManagerMapper.translatePosition(stationDTO.getPositionCode()));
            }
            builder.append(",");
        }
        if (ObjectUtil.isNotEmpty(builder)) {
            return builder.substring(0, builder.length() - 1).toString();
        }
        return "";
    }


    /**
     * 根据设备编码集合查询设备信息
     *
     * @param deviceCodes 设备编码集合
     * @return
     */
    public List<RepairDeviceDTO> queryDeviceByCodes(List<String> deviceCodes) {
        List<RepairDeviceDTO> repairDeviceDTOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(deviceCodes)) {
            repairDeviceDTOList = inspectionManagerMapper.queryDeviceByCodes(deviceCodes);
            if(CollUtil.isNotEmpty(repairDeviceDTOList)){
                for (RepairDeviceDTO repairDeviceDTO : repairDeviceDTOList) {
                    repairDeviceDTO.setStatusName(sysBaseAPI.translateDict(DictConstant.DEVICE_STATUS,String.valueOf(repairDeviceDTO.getStatus())));
                    repairDeviceDTO.setTemporaryName(sysBaseAPI.translateDict(DictConstant.DEVICE_TEMPORARY,String.valueOf(repairDeviceDTO.getTemporary())));
                    StationDTO stationDTO = new StationDTO();
                    stationDTO.setLineCode(repairDeviceDTO.getLineCode());
                    stationDTO.setStationCode(repairDeviceDTO.getStationCode());
                    stationDTO.setPositionCode(repairDeviceDTO.getPositionCode());
                    String positionCodeName = translateStation(Arrays.asList(stationDTO));
                    repairDeviceDTO.setPositionCodeName(positionCodeName);
                }
            }
        }
        return repairDeviceDTOList;

    }

    /**
     * 查询设备类型信息
     *
     * @param code code值
     * @return
     */
    public String queryNameByCode(String code) {
        return inspectionManagerMapper.queryNameByCode(code);
    }

}
