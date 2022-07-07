package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.standard.dto.LineDTO;
import com.aiurt.boot.standard.dto.SelectTableDTO;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.dto.StationPositionDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.service.IPatrolTaskStationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
@Service
public class PatrolTaskStationServiceImpl extends ServiceImpl<PatrolTaskStationMapper, PatrolTaskStation> implements IPatrolTaskStationService {

    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Override
    public List<PatrolTaskStationDTO> selectStationByTaskCode(String taskCode) {
        return patrolTaskStationMapper.selectStationByTaskCode(taskCode);
    }

    @Override
    public List<SelectTableDTO> getStationTree() {
        List<LineDTO> lineList = patrolTaskStationMapper.getLineList();
        Map<String, String> lineMap = lineList.stream().collect(Collectors.toMap(LineDTO::getLineCode, LineDTO::getLineName, (t1, t2) -> t2));
        List<StationDTO> stationList = patrolTaskStationMapper.getStationList();
        Map<String, List<StationDTO>> stationMap = stationList.stream().collect(Collectors.groupingBy(StationDTO::getLineCode));
        List<StationPositionDTO> positionList = patrolTaskStationMapper.getStationPositionList();
        Map<String, List<StationPositionDTO>> positionMap = positionList.stream().collect(Collectors.groupingBy(StationPositionDTO::getStaionCode));
        List<SelectTableDTO> list = new ArrayList<>();
        stationMap.keySet().stream().forEach(lineCode -> {
            SelectTableDTO table = new SelectTableDTO();
            table.setLabel(lineMap.get(lineCode));
            table.setValue(lineCode);
            table.setLevel(1);
            table.setStationCode(lineCode);
            List<StationDTO> csStationList = stationMap.getOrDefault(lineCode, Collections.emptyList());
            List<SelectTableDTO> lv2List = csStationList.stream().map(csStation -> {
                SelectTableDTO selectTable = new SelectTableDTO();
                selectTable.setValue(csStation.getStationCode());
                selectTable.setLabel(csStation.getStationName());
                selectTable.setLevel(2);
                selectTable.setStationCode(csStation.getStationCode());
                List<StationPositionDTO> stationPositionList = positionMap.getOrDefault(csStation.getStationCode(), Collections.emptyList());
                List<SelectTableDTO> tableList = stationPositionList.stream().map(csStationPosition -> {
                    SelectTableDTO tableV = new SelectTableDTO();
                    tableV.setLabel(csStationPosition.getPositionName());
                    tableV.setValue(csStationPosition.getPositionCode());
                    tableV.setLevel(3);
                    tableV.setStationCode(csStationPosition.getPositionCode());
                    return tableV;
                }).collect(Collectors.toList());
                selectTable.setChildren(tableList);
                return selectTable;
            }).collect(Collectors.toList());
            table.setChildren(lv2List);
            list.add(table);
        });
        return  list;
    }
}
