package com.aiurt.modules.workarea.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.weeklyplan.entity.BdSite;
import com.aiurt.boot.weeklyplan.entity.BdTeam;
import com.aiurt.boot.weeklyplan.mapper.BdSiteMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.mapper.CsMajorMapper;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.CsUserMajorMapper;
import com.aiurt.modules.system.mapper.SysDepartMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SiteModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date: 2022-08-11
 * @Version: V1.0
 */
@Service
public class WorkAreaServiceImpl extends ServiceImpl<WorkAreaMapper, WorkArea> implements IWorkAreaService {

    /**
     * 系统管理员角色编码
     */
    private static final String ADMIN = "admin";

    @Autowired
    private WorkAreaMapper workAreaMapper;
    @Autowired
    private WorkAreaLineMapper workAreaLineMapper;
    @Autowired
    private WorkAreaStationMapper workAreaStationMapper;
    @Autowired
    private WorkAreaOrgMapper workAreaOrgMapper;
    @Autowired
    private CsStationMapper csStationMapper;
    @Autowired
    private CsMajorMapper csMajorMapper;
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    private CsUserMajorMapper csUserMajorMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private BdSiteMapper bdSiteMapper;

    @Override
    public Page<WorkAreaDTO> getWorkAreaList(Page<WorkAreaDTO> pageList, WorkAreaDTO workArea) {
        List<WorkAreaDTO> workAreaDTOList = workAreaMapper.getWorkAreaList(pageList, workArea);
        workAreaDTOList.stream().forEach(w -> {
            List<WorkAreaStation> workAreaStationList = workAreaStationMapper.selectList(new LambdaQueryWrapper<WorkAreaStation>().eq(WorkAreaStation::getWorkAreaCode, w.getCode()));
            List<String> stationList = workAreaStationList.stream().map(WorkAreaStation::getStationCode).collect(Collectors.toList());
            w.setStationCodeList(stationList);
            List<WorkAreaLine> workAreaLineList = workAreaLineMapper.selectList(new LambdaQueryWrapper<WorkAreaLine>().eq(WorkAreaLine::getWorkAreaCode, w.getCode()));
            List<String> lineList = workAreaLineList.stream().map(WorkAreaLine::getLineCode).collect(Collectors.toList());
            w.setLineCodeList(lineList);
            List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getWorkAreaCode, w.getCode()));
            List<String> orgCodeList = workAreaOrgList.stream().map(WorkAreaOrg::getOrgCode).collect(Collectors.toList());
            List<SysDepart> sysDepartList = new ArrayList<>();
            for (String orgCode : orgCodeList) {
                SysDepart sysDepart = sysDepartMapper.selectOne(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getOrgCode, orgCode).eq(SysDepart::getDelFlag,0));
                if(ObjectUtil.isNotEmpty(sysDepart))
                {
                    sysDepartList.add(sysDepart);
                }
            }
            List<String> orgIdList = sysDepartList.stream().map(SysDepart::getId).collect(Collectors.toList());
            w.setOrgIdList(orgIdList);
            //查询该工区的站点
            List<String> lineStationNameList = workAreaStationMapper.getLineStationName(w.getCode());
            String lineStationName = lineStationNameList.stream().collect(Collectors.joining(";"));
            //查询该工区的组织
            List<String> orgNameList = workAreaOrgMapper.getOrgName(w.getCode());
            String orgName = orgNameList.stream().collect(Collectors.joining(";"));
            //查询专业名称
            String majorName = workAreaMapper.getMajorName(w.getMajorCode());
            //查询工区管理负责人
            SysUser managerName = sysUserMapper.selectById(w.getManagerId());
            //查询工区技术负责人
            SysUser technicalName = sysUserMapper.selectById(w.getTechnicalId());
            if (ObjectUtil.isNotEmpty(managerName)) {
                w.setManagerName(managerName.getRealname());
            }
            if (ObjectUtil.isNotEmpty(technicalName)) {
                w.setTechnicalName(technicalName.getRealname());
            }
            w.setMajorName(majorName);
            w.setLineStationName(lineStationName);
            w.setOrgName(orgName);
        });
        return pageList.setRecords(workAreaDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWorkArea(WorkAreaDTO workAreaDTO) {
        //保存工区
        WorkArea workArea = new WorkArea();
        BeanUtil.copyProperties(workAreaDTO, workArea);
        WorkArea area = workAreaMapper.selectOne(new LambdaQueryWrapper<WorkArea>().eq(WorkArea::getCode, workAreaDTO.getCode()));
        if (ObjectUtil.isNotEmpty(area)) {
            throw new AiurtBootException("该工区编码已存在，请重新填写!");
        }
        workAreaMapper.insert(workArea);
        //保存线路和站点
        for (String stationCode : workAreaDTO.getStationCodeList()) {
            CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>().eq(CsStation::getStationCode, stationCode).eq(CsStation::getDelFlag,0));
            WorkAreaLine areaLine = workAreaLineMapper.selectOne(new LambdaQueryWrapper<WorkAreaLine>()
                    .eq(WorkAreaLine::getWorkAreaCode, workArea.getCode()).eq(WorkAreaLine::getLineCode, csStation.getLineCode()));
            if (ObjectUtil.isEmpty(areaLine)) {
                WorkAreaLine workAreaLine = new WorkAreaLine();
                workAreaLine.setWorkAreaCode(workAreaDTO.getCode());
                workAreaLine.setLineCode(csStation.getLineCode());
                workAreaLineMapper.insert(workAreaLine);
            }
            WorkAreaStation workAreaStation = new WorkAreaStation();
            workAreaStation.setWorkAreaCode(workAreaDTO.getCode());
            workAreaStation.setStationCode(stationCode);
            workAreaStationMapper.insert(workAreaStation);
        }
        //保存组织机构
        for (String orgId : workAreaDTO.getOrgIdList()) {
            SysDepart sysDepart = sysDepartMapper.selectById(orgId);
            WorkAreaOrg workAreaOrg = new WorkAreaOrg();
            workAreaOrg.setWorkAreaCode(workAreaDTO.getCode());
            workAreaOrg.setOrgCode(sysDepart.getOrgCode());
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
        if (CollUtil.isNotEmpty(workAreaOrgList)) {
            workAreaOrgMapper.deleteBatchIds(workAreaOrgList);
        }
        //删除站点
        List<WorkAreaStation> workAreaStationList = workAreaStationMapper.selectList(new LambdaQueryWrapper<WorkAreaStation>().eq(WorkAreaStation::getWorkAreaCode, workArea.getCode()));
        if (CollUtil.isNotEmpty(workAreaOrgList)) {
            workAreaStationMapper.deleteBatchIds(workAreaStationList);
        }
        //删除线路
        List<WorkAreaLine> workAreaLineList = workAreaLineMapper.selectList(new LambdaQueryWrapper<WorkAreaLine>().eq(WorkAreaLine::getWorkAreaCode, workArea.getCode()));
        if (CollUtil.isNotEmpty(workAreaOrgList)) {
            workAreaLineMapper.deleteBatchIds(workAreaLineList);
        }
        //删除工区
        workAreaMapper.deleteById(id);
    }

    @Override
    public Page<MajorUserDTO> getMajorUser(Page<MajorUserDTO> pageList, String majorCode, String name, String orgId) {
        CsMajor csMajor = csMajorMapper.selectOne(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getMajorCode, majorCode).eq(CsMajor::getDelFlag,0));
        //查询该专业下的所有用户
        List<MajorUserDTO> majorUserDTOList = workAreaMapper.getMajorAllUser(pageList, csMajor.getId(), name, orgId);
        for (MajorUserDTO majorUserDTO : majorUserDTOList) {
            //1.获取子系统
            //1.1查询用户下所有的专业
            List<MajorDTO> majorDTOList = workAreaMapper.getUserAllMajor(majorUserDTO.getId());
            List<String> majorCodeList = majorDTOList.stream().map(MajorDTO::getMajorCode).collect(Collectors.toList());
            List<SubSystem> systemNameList = new ArrayList<>();
            for (MajorDTO majorDTO : majorDTOList) {
                //1.2查询专业下所有的子系统
                List<SubSystem> subSystemList = workAreaMapper.getMajorAllSubSystem(majorDTO.getMajorCode());
                systemNameList.addAll(subSystemList);
            }
            String majorNames = majorDTOList.stream().map(MajorDTO::getMajorName).collect(Collectors.joining("；"));
            String systemNames = systemNameList.stream().map(SubSystem::getSystemName).collect(Collectors.joining("；"));
            majorUserDTO.setMajorNames(majorNames);
            majorUserDTO.setSystemNames(systemNames);
            majorUserDTO.setMajorCodeList(majorCodeList);
        }
        return pageList.setRecords(majorUserDTOList);
    }

    /**
     * 根据组织结构编码查找对应的工区信息
     *
     * @param orgCode
     * @return
     */
    @Override
    public List<SiteModel> getSiteByOrgCode(String orgCode) {
        if (StrUtil.isEmpty(orgCode)) {
            return new ArrayList<>();
        }
        List<SiteModel> result = baseMapper.getSiteByOrgCode(orgCode);
        return result;
    }

    /**
     * 根据线路和登录用户专业获取班组信息
     *
     * @param lineCode 线路code
     * @return
     */
    @Override
    public List<SysDepartModel> getTeamBylineAndMajors(String lineCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(user)) {
            throw new AiurtBootException("请重新登录");
        }

        // 线路筛选
        List<String> lineCodeList = new ArrayList<>();
        if (StrUtil.isNotEmpty(lineCode)) {
            lineCodeList = StrUtil.split(lineCode, ',');
            List<String> lineList = baseMapper.getTeamBylineAndMajor(lineCodeList, new ArrayList<>());
            if (CollUtil.isEmpty(lineList)) {
                return CollUtil.newArrayList();
            }
        }

        // 专业筛选
        List<CsUserMajorModel> majorByUserId = csUserMajorMapper.getMajorByUserId(user.getId());
        List<String> majorList = new ArrayList<>();
        if(CollUtil.isEmpty(majorByUserId)){
            return CollUtil.newArrayList();
        }
        if (CollUtil.isNotEmpty(majorByUserId)) {
            majorList = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
            List<String> majors = baseMapper.getTeamBylineAndMajor(new ArrayList<>(), majorList);
            if (CollUtil.isEmpty(majors)) {
                return CollUtil.newArrayList();
            }
        }

        return baseMapper.getTeamBylineAndMajors(lineCodeList,majorList);
    }

    /**
     * 查询本工区的站所
     *
     * @param param 标志: 1:全部,0:本工区
     * @return
     */
    @Override
    public List<CsStation> queryOriginStation(String param) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = loginUser.getRoleCodes();
        // 判断是的为管理员
        boolean f = (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) || StrUtil.equalsIgnoreCase(param, "1");
        if (f) {
            List<CsStation> csStationList = csStationMapper.selectList(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));
            return csStationList;
        }

        String orgId = loginUser.getOrgId();

        SysDepart sysDepart = sysDepartMapper.selectById(orgId);

        if (Objects.isNull(sysDepart)) {
            return Collections.emptyList();
        }
        // 获取工区
        List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getOrgCode, sysDepart.getOrgCode()).eq(WorkAreaOrg::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isEmpty(workAreaOrgList)) {
            return Collections.emptyList();
        }

        Set<String> set = workAreaOrgList.stream().map(WorkAreaOrg::getWorkAreaCode).collect(Collectors.toSet());

        List<WorkAreaStation> workAreaStationList = workAreaStationMapper.selectList(new LambdaQueryWrapper<WorkAreaStation>().in(WorkAreaStation::getWorkAreaCode, set).eq(WorkAreaStation::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isEmpty(workAreaStationList)) {
            return Collections.emptyList();
        }

        Set<String> stationCodeSet = workAreaStationList.stream().map(WorkAreaStation::getStationCode).collect(Collectors.toSet());

        List<CsStation> csStationList = csStationMapper.selectList(new LambdaQueryWrapper<CsStation>().in(CsStation::getStationCode, stationCodeSet).eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));

        return csStationList;
    }

    /**
     * 查询本工区的人员
     *
     * @param param 标志: 1:全部,0:本工区
     * @return
     */
    @Override
    public List<SysUser> queryOriginUser(String param) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = loginUser.getRoleCodes();
        // 判断是的为管理员
        boolean f = (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) || StrUtil.equalsIgnoreCase(param, "1");
        if (f) {
            List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));
            return sysUserList;
        }

        String orgId = loginUser.getOrgId();

        SysDepart sysDepart = sysDepartMapper.selectById(orgId);

        if (Objects.isNull(sysDepart)) {
            return Collections.emptyList();
        }
        // 获取工区
        List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getOrgCode, sysDepart.getOrgCode()).eq(WorkAreaOrg::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isEmpty(workAreaOrgList)) {
            List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, orgId).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));

            return sysUserList;
        }

        Set<String> set = workAreaOrgList.stream().map(WorkAreaOrg::getWorkAreaCode).collect(Collectors.toSet());

        // 同一工区的
        List<WorkAreaOrg> orgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().in(WorkAreaOrg::getWorkAreaCode, set).eq(WorkAreaOrg::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isEmpty(orgList)) {
            List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, orgId).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));
            return sysUserList;
        }

        Set<String> orgCodeSet = orgList.stream().map(WorkAreaOrg::getOrgCode).collect(Collectors.toSet());
        // 查询机构
        List<SysDepart> departList = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepart>().in(SysDepart::getOrgCode, orgCodeSet));

        Set<String> orgIdSet = departList.stream().map(SysDepart::getId).collect(Collectors.toSet());
        orgIdSet.add(orgId);

        List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getOrgId, orgIdSet).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));

        return sysUserList;
    }

    @Override
    public List<SiteModel> querySiteByTeam() {

        //查询登录人
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String teamId = workAreaMapper.queryByUserId(sysUser.getId());
        List<SysDepart> sysDeparts = workAreaMapper.queryManagedTeam(teamId);
        sysDeparts = sysDeparts.stream().distinct().collect(Collectors.toList());
        List<String> collect = sysDeparts.stream().map(s -> s.getOrgCode()).collect(Collectors.toList());

        //按管辖班组查询用户表
//        QueryWrapper<BdSite> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("team_id", teamIdList);
        List<SiteModel> modelList = new ArrayList<>();
        collect.forEach(e->{
            List<SiteModel> siteByOrgCode = baseMapper.getSiteByOrgCode(e);
            modelList.addAll(siteByOrgCode);
        });

        return modelList;
    }
}
