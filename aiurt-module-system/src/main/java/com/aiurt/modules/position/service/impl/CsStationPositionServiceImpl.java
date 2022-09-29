package com.aiurt.modules.position.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsStationPositionServiceImpl extends ServiceImpl<CsStationPositionMapper, CsStationPosition> implements ICsStationPositionService {
    @Autowired
    private CsStationPositionMapper csStationPositionMapper;
    @Autowired
    private CsStationMapper csStationMapper;
    @Autowired
    private CsLineMapper csLineMapper;

    /**
     * 查询列表
     * @param page
     * @return
     */
    @Override
    public List<CsStationPosition> readAll(Page<CsStationPosition> page, CsStationPosition csStationPosition){
        List<CsStationPosition> csStationPositions = csStationPositionMapper.queryCsStationPositionAll(page, csStationPosition);
        for (CsStationPosition stationPosition : csStationPositions) {
            if (stationPosition.getLevel().equals(2)) {
                CsStationPosition result = this.getById(stationPosition.getId());
                stationPosition.setPositionPhoneNum(result.getPositionPhoneNum());
            }
        }
        return csStationPositions;
    }
    /**
     * 添加
     *
     * @param csStationPosition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsStationPosition csStationPosition) {
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加 update 20220721 去掉第三级的名称重复判断
       /* LambdaQueryWrapper<CsStationPosition> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsStationPosition::getPositionName, csStationPosition.getPositionName());
        nameWrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsStationPosition> positionList = csStationPositionMapper.selectList(nameWrapper);
        if (!positionList.isEmpty()) {
            return Result.error("三级名称重复，请重新填写！");
        }*/
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csStationPosition.getPositionCode());
        if (!list.isEmpty()) {
            return Result.error("编码重复，请重新填写！");
        }
        //根据Station_code查询所属线路code
        LambdaQueryWrapper<CsStation> stationWrapper = new LambdaQueryWrapper<>();
        stationWrapper.eq(CsStation::getStationCode,csStationPosition.getStaionCode());
        stationWrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
        CsStation sta = csStationMapper.selectOne(stationWrapper);
        csStationPosition.setLineCode(sta.getLineCode());
        //拼接position_code_cc
        csStationPosition.setPositionCodeCc("/"+sta.getLineCode()+"/"+csStationPosition.getStaionCode()+"/"+csStationPosition.getPositionCode());
        csStationPosition.setUpdateTime(new Date());
        csStationPositionMapper.insert(csStationPosition);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csStationPosition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsStationPosition csStationPosition) {
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加 update 20220721 去掉第三级的名称重复判断
       /* LambdaQueryWrapper<CsStationPosition> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsStationPosition::getPositionName, csStationPosition.getPositionName());
        nameWrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsStationPosition> positionList = csStationPositionMapper.selectList(nameWrapper);
        if (!positionList.isEmpty() && !positionList.get(0).getId().equals(csStationPosition.getId())) {
            return Result.error("三级名称重复，请重新填写！");
        }*/
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csStationPosition.getPositionCode());
        if (!list.isEmpty() && !list.get(0).getId().equals(csStationPosition.getId())) {
            return Result.error("编码重复，请重新填写！");
        }

        //根据Station_code查询所属线路code
/*        LambdaQueryWrapper<CsStation> stationWrapper = new LambdaQueryWrapper<>();
        stationWrapper.eq(CsStation::getStationCode,csStationPosition.getStaionCode());
        stationWrapper.eq(CsStation::getDelFlag, 0);
        CsStation sta = csStationMapper.selectOne(stationWrapper);
        csStationPosition.setLineCode(sta.getLineCode());*/
        //拼接position_code_cc
/*        csStationPosition.setPositionCodeCc("/"+sta.getLineCode()+"/"+csStationPosition.getStaionCode()+"/"+csStationPosition.getPositionCode());*/
        csStationPositionMapper.updateById(csStationPosition);
        return Result.OK("编辑成功！");
    }

}
