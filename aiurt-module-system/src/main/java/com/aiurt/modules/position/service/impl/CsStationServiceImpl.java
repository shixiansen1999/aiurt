package com.aiurt.modules.position.service.impl;

import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.position.service.ICsStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: cs_station
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class CsStationServiceImpl extends ServiceImpl<CsStationMapper, CsStation> implements ICsStationService {
    @Autowired
    private CsStationMapper csStationMapper;
    @Autowired
    private CsLineMapper csLineMapper;
    /**
     * 添加
     *
     * @param csStation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsStation csStation) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csStation.getStationCode());
        if (!list.isEmpty()) {
            return Result.error("编码重复，请重新填写！");
        }
        //排序不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsStation> staWrapper = new LambdaQueryWrapper<>();
        staWrapper.eq(CsStation::getSort, csStation.getSort());
        staWrapper.eq(CsStation::getLineCode, csStation.getLineCode());
        staWrapper.eq(CsStation::getDelFlag, 0);
        List<CsStation> stationList = csStationMapper.selectList(staWrapper);
        if (!stationList.isEmpty()) {
            return Result.error("二级的排序重复，请重新填写！");
        }
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsStation> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsStation::getStationName, csStation.getStationName());
        nameWrapper.eq(CsStation::getDelFlag, 0);
        stationList = csStationMapper.selectList(nameWrapper);
        if (!stationList.isEmpty()) {
            return Result.error("二级名称重复，请重新填写！");
        }
        csStationMapper.insert(csStation);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csStation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsStation csStation) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csStation.getStationCode());
        if (!list.isEmpty() && !list.get(0).getId().equals(csStation.getId())) {
            return Result.error("编码重复，请重新填写！");
        }
        //排序不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsStation> staWrapper = new LambdaQueryWrapper<>();
        staWrapper.eq(CsStation::getSort, csStation.getSort());
        staWrapper.eq(CsStation::getLineCode, csStation.getLineCode());
        staWrapper.eq(CsStation::getDelFlag, 0);
        List<CsStation> stationList = csStationMapper.selectList(staWrapper);
        if (!stationList.isEmpty() && !stationList.get(0).getId().equals(csStation.getId())) {
            return Result.error("二级的排序重复，请重新填写！");
        }
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsStation> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsStation::getStationName, csStation.getStationName());
        nameWrapper.eq(CsStation::getDelFlag, 0);
        stationList = csStationMapper.selectList(nameWrapper);
        if (!stationList.isEmpty() && !stationList.get(0).getId().equals(csStation.getId())) {
            return Result.error("二级名称重复，请重新填写！");
        }
        csStationMapper.updateById(csStation);
        return Result.OK("编辑成功！");
    }
}
