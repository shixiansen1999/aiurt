package com.aiurt.boot.team.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.mapper.EmergencyTeamMapper;
import com.aiurt.boot.team.model.TeamModel;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.common.constant.CommonConstant;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTeamServiceImpl extends ServiceImpl<EmergencyTeamMapper, EmergencyTeam> implements IEmergencyTeamService {

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private IEmergencyCrewService emergencyCrewService;

    @Autowired
    private EmergencyTeamMapper emergencyTeamMapper;

    @Override
    public IPage<EmergencyTeam> queryPageList(EmergencyTeamDTO emergencyTeamDTO, Integer pageNo, Integer pageSize) {
        EmergencyTeam team = new EmergencyTeam();
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        BeanUtil.copyProperties(emergencyTeamDTO, team);
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = user.getRoleCodes();
        List<SysDepartModel> models = new ArrayList<>();
        if (StrUtil.isNotBlank(roleCodes)) {
            if (!roleCodes.contains(TeamConstant.ADMIN)) {
                //获取用户的所属部门及所属部门子部门
                models = iSysBaseAPI.getUserDepartCodes();
                if (CollUtil.isEmpty(models)) {
                    return new Page<>();
                }
                List<String> orgCodes = models.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
                queryWrapper.in(EmergencyTeam::getOrgCode, orgCodes);
            }
        }else {
            return new Page<>();
        }

        if (StrUtil.isNotBlank(team.getMajorCode())) {
            queryWrapper.eq(EmergencyTeam::getMajorCode, team.getMajorCode());
        }
        if (StrUtil.isNotBlank(team.getEmergencyTeamname())) {
            queryWrapper.like(EmergencyTeam::getEmergencyTeamname, team.getEmergencyTeamname());
        }
        queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
        queryWrapper.orderByDesc(EmergencyTeam::getCreateTime).orderByDesc(EmergencyTeam::getUpdateTime);
        Page<EmergencyTeam> page = new Page<EmergencyTeam>(pageNo, pageSize);
        IPage<EmergencyTeam> pageList = this.page(page, queryWrapper);
        List<EmergencyTeam> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (EmergencyTeam record : records) {
                this.translate(record);
            }
        }
        return pageList;
    }

    @Override
    public void translate(EmergencyTeam emergencyTeam) {
        JSONObject major = iSysBaseAPI.getCsMajorByCode(emergencyTeam.getMajorCode());
        emergencyTeam.setMajorName(major != null ? major.getString("majorName") : null);

        SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(emergencyTeam.getOrgCode());
        emergencyTeam.setOrgName(sysDepartModel != null ? sysDepartModel.getDepartName(): null);

        String lineName = iSysBaseAPI.getPosition(emergencyTeam.getLineCode());
        emergencyTeam.setLineName(lineName);

        String stationName = iSysBaseAPI.getPosition(emergencyTeam.getStationCode());
        emergencyTeam.setStationName(stationName);

        String positionName = iSysBaseAPI.getPosition(emergencyTeam.getPositionCode());
        emergencyTeam.setPositionName(positionName);

        String workareaCode = emergencyTeam.getWorkareaCode();
        if (StrUtil.isNotBlank(workareaCode)) {
            String workAreaNameByCode = iSysBaseAPI.getWorkAreaNameByCode(workareaCode);
            emergencyTeam.setWorkareaName(workAreaNameByCode);
        }

        LoginUser userById = iSysBaseAPI.getUserById(emergencyTeam.getManagerId());
        emergencyTeam.setManagerName(userById != null ? userById.getRealname(): null);

    }

    @Override
    public EmergencyTeam getCrew(EmergencyTeam emergencyTeam) {
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            List<EmergencyCrewVO> list = new ArrayList<>();
            for (EmergencyCrew emergencyCrew : emergencyCrews) {
                EmergencyCrewVO emergencyCrewVO = new EmergencyCrewVO();
                List<String> roleNamesById = iSysBaseAPI.getRoleNamesById(emergencyCrew.getUserId());
                if (CollUtil.isNotEmpty(roleNamesById)) {
                    String join = StrUtil.join(",", roleNamesById);
                    emergencyCrewVO.setRoleNames(join);
                }
                LoginUser userById = iSysBaseAPI.getUserById(emergencyCrew.getUserId());
                emergencyCrewVO.setScheduleItem(emergencyCrew.getScheduleItem());
                emergencyCrewVO.setUserId(emergencyCrew.getUserId());
                emergencyCrewVO.setRealname(userById.getRealname());
                emergencyCrewVO.setId(emergencyCrew.getId());
                emergencyCrewVO.setPost(emergencyCrew.getPost());
                emergencyCrewVO.setPhone(emergencyCrew.getUserPhone());
                SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(userById.getOrgCode());
                emergencyCrewVO.setOrgName(sysDepartModel.getDepartName());
                emergencyCrewVO.setRemark(emergencyCrew.getRemark());
                list.add(emergencyCrewVO);
            }
            emergencyTeam.setEmergencyCrewVOList(list);
        }
        translate(emergencyTeam);
        return emergencyTeam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(EmergencyTeam emergencyTeam) {
        save(emergencyTeam);
        List<EmergencyCrew> emergencyCrewList = emergencyTeam.getEmergencyCrewList();
        if (CollUtil.isNotEmpty(emergencyCrewList)) {
            for (EmergencyCrew emergencyCrew : emergencyCrewList) {
                emergencyCrew.setEmergencyTeamId(emergencyTeam.getId());
                emergencyCrewService.save(emergencyCrew);
            }
        }
        return Result.OK("添加成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(EmergencyTeam emergencyTeam) {
        EmergencyTeam byId = this.getById(emergencyTeam.getId());
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("未找到对应数据！");
        }
        updateById(emergencyTeam);
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, byId.getId());
        emergencyCrewService.getBaseMapper().delete(wrapper);
        List<EmergencyCrew> emergencyCrewList = emergencyTeam.getEmergencyCrewList();
        if (CollUtil.isNotEmpty(emergencyCrewList)) {
            for (EmergencyCrew emergencyCrew : emergencyCrewList) {
                emergencyCrew.setEmergencyTeamId(emergencyTeam.getId());
                emergencyCrew.setDelFlag(TeamConstant.DEL_FLAG0);
                emergencyCrewService.save(emergencyCrew);
            }
        }
        return Result.OK("编辑成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(EmergencyTeam emergencyTeam ) {
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            for (EmergencyCrew emergencyCrew : emergencyCrews) {
                emergencyCrew.setDelFlag(TeamConstant.DEL_FLAG1);
                emergencyCrewService.updateById(emergencyCrew);
            }
        }
        emergencyTeam.setDelFlag(TeamConstant.DEL_FLAG1);
        this.updateById(emergencyTeam);
    }

    @Override
    public Result<EmergencyTeam> getTrainingRecordById(String id) {
        EmergencyTeam emergencyTeam = this.getById(id);
        if(emergencyTeam==null) {
            return Result.error("未找到对应数据");
        }
        List<EmergencyTeamTrainingDTO> trainingRecord = emergencyTeamMapper.getTrainingRecord(id);
        translate(emergencyTeam);
        if (CollUtil.isNotEmpty(trainingRecord)) {
            for (EmergencyTeamTrainingDTO emergencyTeamDTO : trainingRecord) {
                emergencyTeamDTO.setManagerName(emergencyTeam.getManagerName());
            }
            emergencyTeam.setEmergencyTeamTrainingDTOS(trainingRecord);
        }
        return Result.OK(emergencyTeam);
    }


    @Override
    public Result<List<EmergencyTeam>> getTeamByCode(String orgCode) {
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(orgCode)) {
            queryWrapper.eq(EmergencyTeam::getOrgCode, orgCode);
        }
        queryWrapper.select(EmergencyTeam::getId,EmergencyTeam::getEmergencyTeamname, EmergencyTeam::getEmergencyTeamcode,EmergencyTeam::getManagerId);
        List<EmergencyTeam> emergencyTeams = this.getBaseMapper().selectList(queryWrapper);
        if (CollUtil.isNotEmpty(emergencyTeams)) {
            for (EmergencyTeam emergencyTeam : emergencyTeams) {
                LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
                wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
                Long crews = emergencyCrewService.getBaseMapper().selectCount(wrapper);
                emergencyTeam.setCrews(Convert.toStr(crews));

                LoginUser userById = iSysBaseAPI.getUserById(emergencyTeam.getManagerId());
                emergencyTeam.setManagerName(userById.getRealname());
            }
        }

        return Result.OK(emergencyTeams);
    }

    @Override
    public Result<List<EmergencyTeam>> getTeamByMajor() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> majorByUserId = iSysBaseAPI.getMajorByUserId(user.getId());
        if (CollUtil.isEmpty(majorByUserId)) {
            return Result.OK(new ArrayList<>());
        }
        List<String> collect = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(EmergencyTeam::getId,EmergencyTeam::getEmergencyTeamname, EmergencyTeam::getEmergencyTeamcode,EmergencyTeam::getPositionCode);
        queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
        queryWrapper.in(EmergencyTeam::getMajorCode, collect);
        List<EmergencyTeam> emergencyTeams = this.getBaseMapper().selectList(queryWrapper);
        if (CollUtil.isNotEmpty(emergencyTeams)) {
            for (EmergencyTeam emergencyTeam : emergencyTeams) {
                LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
                wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
                Long crews = emergencyCrewService.getBaseMapper().selectCount(wrapper);
                emergencyTeam.setCrews(Convert.toStr(crews));
                String positionName = iSysBaseAPI.getPosition(emergencyTeam.getPositionCode());
                emergencyTeam.setPositionName(positionName);
            }
        }
        return Result.OK(emergencyTeams);
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        String url = null;
        // 错误信息
        int  errorLines = 0;

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return iSysBaseAPI.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            try {
                //导入取得应急队伍信息
                ImportParams teamParams = new ImportParams();
                teamParams.setTitleRows(2);
                teamParams.setHeadRows(1);
                teamParams.setNeedSave(true);
                //数据为空校验
                List<TeamModel> list = ExcelImportUtil.importExcel(file.getInputStream(), TeamModel.class, teamParams);
                TeamModel team = list.get(0);
                boolean b = iSysBaseAPI.checkObjAllFieldsIsNull(team);
                if (b) {
                    return Result.error("文件导入失败:队伍内容不能为空！");
                }

                //导入取得应急队伍人员信息
                ImportParams crewParams = new ImportParams();
                crewParams.setTitleRows(6);
                crewParams.setHeadRows(1);
                crewParams.setNeedSave(true);

                List<EmergencyCrew> crewList = ExcelImportUtil.importExcel(file.getInputStream(), EmergencyCrew.class, crewParams);
                Iterator<EmergencyCrew> iterator = crewList.iterator();
                while (iterator.hasNext()) {
                    EmergencyCrew model = iterator.next();
                    boolean a = iSysBaseAPI.checkObjAllFieldsIsNull(model);
                    if (a) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(crewList)) {
                    return Result.error("文件导入失败:队伍人员内容不能为空！");
                }

                //数据校验
                StringBuilder stringBuilder = new StringBuilder();
                EmergencyTeam emergencyTeam = new EmergencyTeam();
                checkTeam(team,stringBuilder,emergencyTeam);






            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return Result.ok("文件导入失败！");
    }

    //队伍信息数据校验
    private void checkTeam(TeamModel team, StringBuilder stringBuilder,EmergencyTeam emergencyTeam) {
        String majorName = team.getMajorName();
        String departName = team.getOrgName();
        String emergencyTeamName = team.getEmergencyTeamname();
        String emergencyTeamCode = team.getEmergencyTeamcode();
        String lineName = team.getLineName();
        String stationName = team.getStationName();
        String positionName = team.getPositionName();
        String workArea = team.getWorkAreaName();
        String manager = team.getManagerName();
        String managerWorkNo = team.getManagerWorkNo();
        String managerPhone = team.getManagerPhone();

        if (StrUtil.isNotBlank(majorName) && StrUtil.isNotBlank(departName) && StrUtil.isNotBlank(emergencyTeamName)) {
            JSONObject major = iSysBaseAPI.getCsMajorByName(majorName);
            JSONObject depart = iSysBaseAPI.getDepartByName(departName);
            LambdaQueryWrapper<EmergencyTeam> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EmergencyTeam::getEmergencyTeamname, emergencyTeamName).eq(EmergencyTeam::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
            EmergencyTeam one = this.getBaseMapper().selectOne(wrapper);

            if (ObjectUtil.isNotNull(major)) {
                emergencyTeam.setMajorCode(major.getString("majorCode"));
            } else {
                stringBuilder.append("系统不存在该专业，");
            }
            if (ObjectUtil.isNotNull(depart)) {
                emergencyTeam.setOrgCode(depart.getString("orgCode"));
            } else {
                stringBuilder.append("系统不存在该部门，");
            }
            if (ObjectUtil.isNull(one)) {
                emergencyTeam.setEmergencyTeamname(emergencyTeamName);
            } else {
                stringBuilder.append("系统已存在该应急队伍名称，");
            }
        } else {
            stringBuilder.append("所属专业，所属部门，应急队伍名称不能为空，");
        }

        if (StrUtil.isNotBlank(lineName) && StrUtil.isNotBlank(stationName) && StrUtil.isNotBlank(positionName)) {
            JSONObject lineByName = iSysBaseAPI.getLineByName(lineName);
            JSONObject stationByName = iSysBaseAPI.getStationByName(stationName);
            JSONObject positionByName = iSysBaseAPI.getPositionByName(positionName,lineByName.getString("lineCode"),stationByName.getString("stationCode"));
            if (ObjectUtil.isNotNull(lineByName)) {
                emergencyTeam.setLineCode(lineByName.getString("lineCode"));
            } else {
                stringBuilder.append("系统不存在该线路，");
            }
            if (ObjectUtil.isNotNull(stationByName)) {
                emergencyTeam.setStationCode(lineByName.getString("stationCode"));
            } else {
                stringBuilder.append("系统不存在该站点，");
            }
            if (ObjectUtil.isNotNull(positionByName)) {
                emergencyTeam.setPositionCode(lineByName.getString("positionCode"));
            } else {
                stringBuilder.append("系统不存在该线路站点下的位置，");
            }
        } else {
            stringBuilder.append("线路，站点，驻扎地不能为空，");
        }
        if (StrUtil.isNotBlank(manager)&& StrUtil.isNotBlank(managerPhone)) {
            List<LoginUser> userByRealName = iSysBaseAPI.getUserByRealName(manager, managerWorkNo);
            if (userByRealName.size() != 1) {
                stringBuilder.append("负责人姓名存在同名，请填写工号，");
            } else {
                emergencyTeam.setManagerId(userByRealName.get(0).getId());
            }
            boolean matches = Pattern.matches("^1[3-9]\\d{9}$", managerPhone);
            if (!matches) {
                stringBuilder.append("手机号码格式不正确，");
            }
        } else {
            stringBuilder.append("负责人和联系电话不能为空，");
        }
    }

}
