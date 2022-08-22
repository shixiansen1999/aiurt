package com.aiurt.modules.workarea.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.workarea.dto.MajorDTO;
import com.aiurt.modules.workarea.dto.MajorUserDTO;
import com.aiurt.modules.workarea.dto.SubSystem;
import com.aiurt.modules.workarea.dto.WorkAreaDTO;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.aiurt.modules.workarea.entity.WorkAreaLine;
import com.aiurt.modules.workarea.entity.WorkAreaOrg;
import com.aiurt.modules.workarea.entity.WorkAreaStation;
import com.aiurt.modules.workarea.mapper.WorkAreaLineMapper;
import com.aiurt.modules.workarea.mapper.WorkAreaMapper;
import com.aiurt.modules.workarea.mapper.WorkAreaOrgMapper;
import com.aiurt.modules.workarea.mapper.WorkAreaStationMapper;
import com.aiurt.modules.workarea.service.IWorkAreaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date: 2022-08-11
 * @Version: V1.0
 */
@Service
public class WorkAreaServiceImpl extends ServiceImpl<WorkAreaMapper, WorkArea> implements IWorkAreaService {

    @Autowired
    private WorkAreaMapper workAreaMapper;
    @Autowired
    private WorkAreaLineMapper workAreaLineMapper;
    @Autowired
    private WorkAreaStationMapper workAreaStationMapper;
    @Autowired
    private WorkAreaOrgMapper workAreaOrgMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Override
    public Page<WorkAreaDTO> getWorkAreaList(Page<WorkAreaDTO> pageList, WorkAreaDTO workArea) {
        List<WorkAreaDTO> workAreaDTOList = workAreaMapper.getWorkAreaList(pageList,workArea);
        return pageList.setRecords(workAreaDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWorkArea(WorkAreaDTO workAreaDTO) {
        //保存工区
        WorkArea workArea = new WorkArea();
        workArea.setCode(workAreaDTO.getCode());
        workArea.setMajorCode(workAreaDTO.getMajorCode());
        workArea.setManagerId(workAreaDTO.getManagerId());
        workArea.setTechnicalId(workAreaDTO.getTechnicalId());
        workArea.setName(workAreaDTO.getName());
        workArea.setPosition(workAreaDTO.getPosition());
        workArea.setType(workAreaDTO.getType());
        WorkArea area = workAreaMapper.selectOne(new LambdaQueryWrapper<WorkArea>().eq(WorkArea::getCode, workAreaDTO.getCode()));
        if(ObjectUtil.isNotEmpty(area))
        {
            throw new AiurtBootException("该工区编码已存在，请重新填写!");
        }
        workAreaMapper.insert(workArea);
        //保存线路
        for(String lineCode:workAreaDTO.getLineCodeList())
        {
            WorkAreaLine workAreaLine =new WorkAreaLine();
            workAreaLine.setWorkAreaCode(workAreaDTO.getCode());
            workAreaLine.setLineCode(lineCode);
            workAreaLineMapper.insert(workAreaLine);
        }
       //保存站点
        for(String stationCode:workAreaDTO.getStationCodeList())
        {
            WorkAreaStation workAreaStation = new WorkAreaStation();
            workAreaStation.setWorkAreaCode(workAreaDTO.getCode());
            workAreaStation.setStationCode(stationCode);
            workAreaStationMapper.insert(workAreaStation);
        }
        //保存组织机构
        for(String orgCode :workAreaDTO.getOrgCodeList())
        {
            WorkAreaOrg workAreaOrg = new WorkAreaOrg();
            workAreaOrg.setWorkAreaCode(workAreaDTO.getCode());
            workAreaOrg.setOrgCode(orgCode);
            workAreaOrgMapper.insert(workAreaOrg);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkArea(WorkAreaDTO workAreaDTO) {
        deleteWorkArea(workAreaDTO.getId());
        addWorkArea(workAreaDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkArea(String id) {
        WorkArea workArea = workAreaMapper.selectById(id);
        //删除组织机构
        List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getWorkAreaCode, workArea.getCode()));
        if(CollUtil.isNotEmpty(workAreaOrgList))
        {
            workAreaOrgMapper.deleteBatchIds(workAreaOrgList);
        }
        //删除站点
        List<WorkAreaStation> workAreaStationList = workAreaStationMapper.selectList(new LambdaQueryWrapper<WorkAreaStation>().eq(WorkAreaStation::getWorkAreaCode, workArea.getCode()));
        if(CollUtil.isNotEmpty(workAreaOrgList))
        {
            workAreaStationMapper.deleteBatchIds(workAreaStationList);
        }
        //删除线路
        List<WorkAreaLine> workAreaLineList = workAreaLineMapper.selectList(new LambdaQueryWrapper<WorkAreaLine>().eq(WorkAreaLine::getWorkAreaCode, workArea.getCode()));
        if(CollUtil.isNotEmpty(workAreaOrgList))
        {
            workAreaLineMapper.deleteBatchIds(workAreaLineList);
        }
        //删除工区
        workAreaMapper.deleteById(id);
    }

    @Override
    public WorkAreaDTO getWorkAreaDetail(String id) {
        WorkArea workArea = workAreaMapper.selectById(id);
        WorkAreaDTO workAreaDTO= new WorkAreaDTO();
        BeanUtil.copyProperties(workArea,workAreaDTO);
         List<WorkAreaStation> workAreaStationList = workAreaStationMapper.selectList(new LambdaQueryWrapper<WorkAreaStation>().eq(WorkAreaStation::getWorkAreaCode, workAreaDTO.getCode()));
         List<String> stationList = workAreaStationList.stream().map(WorkAreaStation::getStationCode).collect(Collectors.toList());
         workAreaDTO.setStationCodeList(stationList);

        List<WorkAreaLine> workAreaLineList = workAreaLineMapper.selectList(new LambdaQueryWrapper<WorkAreaLine>().eq(WorkAreaLine::getWorkAreaCode, workAreaDTO.getCode()));
        List<String> lineList = workAreaLineList.stream().map(WorkAreaLine::getLineCode).collect(Collectors.toList());
        workAreaDTO.setLineCodeList(lineList);

        List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getWorkAreaCode, workAreaDTO.getCode()));
        List<String> orgList = workAreaOrgList.stream().map(WorkAreaOrg::getOrgCode).collect(Collectors.toList());
        workAreaDTO.setOrgCodeList(orgList);

        //查询该工区的站点
        List<String> lineStationNameList = workAreaStationMapper.getLineStationName(workAreaDTO.getCode());
        String lineStationName = lineStationNameList.stream().collect(Collectors.joining(";"));
        //查询该工区的组织
        List<String> orgNameList = workAreaOrgMapper.getOrgName(workAreaDTO.getCode());
        String orgName = orgNameList.stream().collect(Collectors.joining(";"));
        //查询专业名称
        String majorName = workAreaMapper.getMajorName(workAreaDTO.getMajorCode());
        //查询工区管理负责人
        LoginUser managerName = iSysBaseAPI.getUserById(workAreaDTO.getManagerId());
        //查询工区技术负责人
        LoginUser technicalName = iSysBaseAPI.getUserById(workAreaDTO.getTechnicalId());
        workAreaDTO.setManagerName(managerName.getRealname());
        workAreaDTO.setTechnicalName(technicalName.getRealname());
        workAreaDTO.setMajorName(majorName);
        workAreaDTO.setLineStationName(lineStationName);
        workAreaDTO.setOrgName(orgName);
        return workAreaDTO;
    }

    @Override
    public Page<MajorUserDTO> getMajorUser(Page<MajorUserDTO> pageList, String majorId,String name,String orgName) {
        List<MajorUserDTO> majorUserDTOList = workAreaMapper.getMajorAllUser(pageList,majorId,name,orgName);
        List<SubSystem> systemNameList = new ArrayList<>();
        for(MajorUserDTO majorUserDTO:majorUserDTOList)
        {
            //查询用户下所有的专业
            List<MajorDTO> majorDTOList = workAreaMapper.getUserAllMajor(majorUserDTO.getId());
            for(MajorDTO majorDTO:majorDTOList)
            {
                //查询专业下所有的子系统
                List<SubSystem> subSystemList = workAreaMapper.getMajorAllSubSystem(majorDTO.getMajorCode());
                systemNameList.addAll(subSystemList);
            }
            String majorNames = majorDTOList.stream().map(MajorDTO::getMajorName).collect(Collectors.joining("；"));
            String systemNames = systemNameList.stream().map(SubSystem::getSystemName).collect(Collectors.joining("；"));
            majorUserDTO.setMajorNames(majorNames);
            majorUserDTO.setSystemNames(systemNames);
        }
        return pageList.setRecords(majorUserDTOList);
    }
}
