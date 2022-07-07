package com.aiurt.boot.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.manager.mapper.InspectionManagerMapper;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.common.exception.AiurtBootException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                String lineName = inspectionManagerMapper.translateLine(stationDTO.getLineCode());
                if (StrUtil.isNotEmpty(lineName)) {
                    builder.append(lineName);
                }
            }
            if (StrUtil.isNotEmpty(stationDTO.getStationCode())) {
                String stationName = inspectionManagerMapper.translateStation(stationDTO.getStationCode());
                if (StrUtil.isNotEmpty(stationName)) {
                    builder.append(stationName);
                }
            }
            if (StrUtil.isNotEmpty(stationDTO.getPositionCode())) {
                String positionName = inspectionManagerMapper.translatePosition(stationDTO.getPositionCode());
                if (StrUtil.isNotEmpty(positionName)) {
                    builder.append(positionName);
                }
            }
            if (ObjectUtil.isNotEmpty(builder)) {
                builder.append(",");
            }
        }
        if (ObjectUtil.isNotEmpty(builder)) {
            return builder.substring(0, builder.length() - 1).toString();
        }
        return "";
    }


    /**
     * 根据设备编码集合查询设备信息（无分页）
     *
     * @param deviceCodes 设备编码集合
     * @return
     */
    public List<RepairDeviceDTO> queryDeviceByCodes(List<String> deviceCodes) {
        List<RepairDeviceDTO> repairDeviceDTOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(deviceCodes)) {
            repairDeviceDTOList = inspectionManagerMapper.queryDeviceByCodes(deviceCodes);
            if (CollUtil.isNotEmpty(repairDeviceDTOList)) {
                for (RepairDeviceDTO repairDeviceDTO : repairDeviceDTOList) {
                    repairDeviceDTO.setStatusName(sysBaseAPI.translateDict(DictConstant.DEVICE_STATUS, String.valueOf(repairDeviceDTO.getStatus())));
                    repairDeviceDTO.setTemporaryName(sysBaseAPI.translateDict(DictConstant.DEVICE_TEMPORARY, String.valueOf(repairDeviceDTO.getTemporary())));
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

    /**
     * 获取当前登录用户
     */
    public LoginUser checkLogin() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }
        return user;
    }

    /**
     * 根据设备编码集合查询设备信息(带分页)
     *
     * @param deviceCodes 设备编码集合
     * @return
     */
    public List<RepairDeviceDTO> queryDeviceByCodesPage(List<String> deviceCodes, Page<?> page) {
        List<RepairDeviceDTO> repairDeviceDTOList = new ArrayList<>();
        if (CollUtil.isNotEmpty(deviceCodes)) {
            repairDeviceDTOList = inspectionManagerMapper.queryDeviceByCodesPage(deviceCodes, page);
            if (CollUtil.isNotEmpty(repairDeviceDTOList)) {
                for (RepairDeviceDTO repairDeviceDTO : repairDeviceDTOList) {
                    repairDeviceDTO.setStatusName(sysBaseAPI.translateDict(DictConstant.DEVICE_STATUS, String.valueOf(repairDeviceDTO.getStatus())));
                    repairDeviceDTO.setTemporaryName(sysBaseAPI.translateDict(DictConstant.DEVICE_TEMPORARY, String.valueOf(repairDeviceDTO.getTemporary())));
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
     * 根据部门编号返回部门信息以及部门下的人员
     */
    public List<OrgDTO> queryUserByOrdCode(String orgStrs) {
        List<OrgDTO> result = new ArrayList<>();
        if(StrUtil.isEmpty(orgStrs)){
            return result;
        }
        List<JSONObject> jsonObjects = sysBaseAPI.queryDepartsByOrgcodes(orgStrs);
        if (CollUtil.isNotEmpty(jsonObjects)) {
            for (JSONObject jsonObject : jsonObjects) {
                if (ObjectUtil.isNotEmpty(jsonObject)) {
                    OrgDTO orgDTO = new OrgDTO();
                    orgDTO.setOrgCode(jsonObject.getString("orgCode"));
                    orgDTO.setDepartName(jsonObject.getString("departName"));
                    orgDTO.setUsers(sysBaseAPI.getUserByDepIds(Arrays.asList(orgDTO.getOrgCode())));
                    result.add(orgDTO);
                }
            }
        }

        return result;
    }

}
