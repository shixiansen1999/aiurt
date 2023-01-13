package com.aiurt.boot.asset.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.asset.dto.FixedAssetsDTO;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.mapper.FixedAssetsMapper;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.mapper.FixedAssetsCategoryMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsServiceImpl extends ServiceImpl<FixedAssetsMapper, FixedAssets> implements IFixedAssetsService {

    @Autowired
    private FixedAssetsMapper assetsMapper;
    @Autowired
    private FixedAssetsCategoryMapper assetsCategoryMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public Page<FixedAssetsDTO> pageList(Page<FixedAssetsDTO> pageList, FixedAssetsDTO fixedAssetsDTO) {
        //线路
        List<String> stationCodeByLineCode = sysBaseAPI.getStationCodeByLineCode(fixedAssetsDTO.getLocation());
        if (CollUtil.isNotEmpty(stationCodeByLineCode)) {
            stationCodeByLineCode.add(fixedAssetsDTO.getLocation());
            fixedAssetsDTO.setLineStations(stationCodeByLineCode);
        }
        List<FixedAssetsDTO> list = assetsMapper.pageList(pageList, fixedAssetsDTO);
        for (FixedAssetsDTO dto : list) {
            //翻译存放地点
            if (ObjectUtil.isNotEmpty(dto.getLocation())) {
                JSONObject csStation = sysBaseAPI.getCsStationByCode(dto.getLocation());
                String position = sysBaseAPI.getPosition(dto.getLocation());
                if (ObjectUtil.isNotEmpty(csStation)) {
                    dto.setLocationName(csStation.getString("lineName")+position);
                }
                else {
                    if (ObjectUtil.isNotEmpty(position)) {
                        dto.setLocationName(position);
                    }
                }
            }
            //翻译责任人
            if (ObjectUtil.isNotEmpty(dto.getResponsibilityId())) {
                String[] userIds = dto.getResponsibilityId().split(",");
                List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(userIds);
                String userName = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                dto.setResponsibilityName(userName);
            }
        }
        return pageList.setRecords(list);
    }
}
