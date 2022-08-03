package com.aiurt.boot.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.manager.mapper.PatrolManagerMapper;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.common.exception.AiurtBootException;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/11
 * @desc
 */
@Service
public class PatrolManager

{
    /**
     * 拼接巡检人
     *
     * @param code code值
     * @return
     */
    @Resource
    private PatrolManagerMapper patrolManagerMapper;
    public String spliceUsername(String code) {
        List<String> nameList = patrolManagerMapper.spliceUsername(code);
        return CollUtil.isNotEmpty(nameList) ? StrUtil.join("；", nameList) : "-";
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
        List<String> nameList = patrolManagerMapper.translateOrg(codeList);
        return CollUtil.isNotEmpty(nameList) ? StrUtil.join("；", nameList) : "";
    }

    /**
     * 翻译站点信息
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
                String lineName = patrolManagerMapper.translateLine(stationDTO.getLineCode());
                if (StrUtil.isNotEmpty(lineName)) {
                    builder.append(lineName);
                }
            }
            if (StrUtil.isNotEmpty(stationDTO.getStationCode())) {
                String stationName = patrolManagerMapper.translateStation(stationDTO.getStationCode());
                if (StrUtil.isNotEmpty(stationName)) {
                    builder.append("/");
                    builder.append(stationName);
                }
            }
            if (StrUtil.isNotEmpty(stationDTO.getPositionCode())) {
                String positionName = patrolManagerMapper.translatePosition(stationDTO.getPositionCode());
                if (StrUtil.isNotEmpty(positionName)) {
                    builder.append("/");
                    builder.append(positionName);
                }
            }
            if (ObjectUtil.isNotEmpty(builder)) {
                builder.append("；");
            }
        }
        if (ObjectUtil.isNotEmpty(builder)) {
            return builder.substring(0, builder.length() - 1).toString();
        }
        return "";
    }
    public LoginUser checkLogin() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }
        return user;
    }

    /**
     * 判断是否是当前任务人(领取||指派)
     * @return
     */
    public boolean checkTaskUser(String taskId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> userList = patrolManagerMapper.getUser(taskId);
        for (String s:userList)
        {
            if(s.equals(user.getId()))
            {
                return true;
            }
        }
        return false;
    }
}
