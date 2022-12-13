package com.aiurt.boot.team.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
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
import com.aiurt.boot.team.model.CrewModel;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
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

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

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

                List<CrewModel> crewList = ExcelImportUtil.importExcel(file.getInputStream(), CrewModel.class, crewParams);
                Iterator<CrewModel> iterator = crewList.iterator();
                while (iterator.hasNext()) {
                    CrewModel model = iterator.next();
                    boolean a = iSysBaseAPI.checkObjAllFieldsIsNull(model);
                    if (a) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(crewList)) {
                    return Result.error("文件导入失败:队伍人员内容不能为空！");
                }

                //数据校验
                EmergencyTeam emergencyTeam = new EmergencyTeam();
                errorLines = checkTeam(team,emergencyTeam,errorLines);

                List<EmergencyCrew> emergencyCrews = new ArrayList<>();
                int i = 0;
                for (CrewModel crewModel : crewList) {
                    Map<Object, Integer> CrewModelData = new HashMap<>();
                    StringBuilder stringBuilder1 = new StringBuilder();
                    //重复数据校验
                    Integer s = CrewModelData.get(crewModel);
                    if (s == null) {
                        CrewModelData.put(crewModel, i);
                        EmergencyCrew emergencyCrew = new EmergencyCrew();
                        errorLines = checkCrews(crewModel,emergencyCrew,errorLines,stringBuilder1);
                        emergencyCrews.add(emergencyCrew);
                        i++;
                    } else {
                        stringBuilder1.append("该行数据存在相同数据,");
                    }
                }

                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, team, crewList, successLines, null, type);
                }
                //校验通过，添加数据
                save(emergencyTeam);
                for (EmergencyCrew emergencyCrew : emergencyCrews) {
                    emergencyCrew.setEmergencyTeamId(emergencyTeam.getId());
                    emergencyCrewService.save(emergencyCrew);
                }
                return Result.ok("文件导入成功！");
            } catch (Exception e) {
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**队伍人员信息数据校验*/
    private int checkCrews(CrewModel crewModel,EmergencyCrew emergencyCrew,int  errorLines, StringBuilder stringBuilder1) {

        String postName = crewModel.getPostName();
        String realName = crewModel.getRealName();
        String userPhone = crewModel.getUserPhone();

        if (StrUtil.isNotBlank(postName)) {
            List<DictModel> post = iSysBaseAPI.queryDictItemsByCode("emergency_post");
            DictModel model = Optional.ofNullable(post).orElse(Collections.emptyList()).stream().filter(dictModel -> dictModel.getText().equals(postName)).findFirst().orElse(null);
            if (model != null) {
                emergencyCrew.setPost(Convert.toInt(model.getValue()));
            } else {
                stringBuilder1.append("系统不存在该职务，");
            }
        } else {
            stringBuilder1.append("职务不能为空");
        }

        if (StrUtil.isNotBlank(realName)) {
            List<LoginUser> userByRealName = iSysBaseAPI.getUserByRealName(realName, crewModel.getManagerWorkNo());
            if (userByRealName.size() != 1) {
                stringBuilder1.append("负责人姓名存在同名，请填写工号，");
            } else {
                emergencyCrew.setUserId(userByRealName.get(0).getId());
            }
        } else {
            stringBuilder1.append("姓名不能为空");
        }

        if (StrUtil.isNotBlank(userPhone)) {
            boolean matches = Pattern.matches("^1[3-9]\\d{9}$", userPhone);
            if (!matches) {
                stringBuilder1.append("手机号码格式不正确，");
            } else {
                emergencyCrew.setUserPhone(userPhone);
            }
        } else {
            stringBuilder1.append("联系电话不能为空");
        }

        if (stringBuilder1.length() > 0) {
            // 截取字符
            stringBuilder1 = stringBuilder1.deleteCharAt(stringBuilder1.length() - 1);
            crewModel.setMistake(stringBuilder1.toString());
            errorLines++;
        }
        return errorLines;
    }


    /**队伍信息数据校验*/
    private int checkTeam(TeamModel team,EmergencyTeam emergencyTeam,int  errorLines ) {
        StringBuilder stringBuilder = new StringBuilder();
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
            EmergencyTeam team1 = this.getBaseMapper().selectOne(wrapper);
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
            if (ObjectUtil.isNull(team1)) {
                emergencyTeam.setEmergencyTeamname(emergencyTeamName);
            } else {
                stringBuilder.append("系统已存在该应急队伍名称，");
            }

            if (StrUtil.isNotBlank(emergencyTeamCode)) {
                LambdaQueryWrapper<EmergencyTeam> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.eq(EmergencyTeam::getEmergencyTeamcode, emergencyTeamCode).eq(EmergencyTeam::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
                EmergencyTeam team2 = this.getBaseMapper().selectOne(wrapper2);
                if (ObjectUtil.isNull(team2)) {
                    emergencyTeam.setEmergencyTeamcode(emergencyTeamCode);
                } else {
                    stringBuilder.append("系统已存在该应急队伍编码，");
                }
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
            } else {
                emergencyTeam.setManagerPhone(managerPhone);
            }
        } else {
            stringBuilder.append("负责人和联系电话不能为空，");
        }

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            team.setMistake(stringBuilder.toString());
            errorLines++;
        }
       /* if (StrUtil.isNotBlank(workArea)) {

        }*/
        return errorLines;
    }

    /**错误报告模板导出*/
    private Result<?> getErrorExcel(int errorLines,List<String> errorMessage,TeamModel team, List<CrewModel> crewList,int successLines ,String url,String type) throws IOException {
        TemplateExportParams exportParams = iSysBaseAPI.getErrorExcelModel("");
        Map<String, Object> errorMap = new HashMap<String, Object>();
        List<Map<String, String>> teamMapList = new ArrayList<>();
        Map<String, String> teamMap = new HashMap<>();
        teamMap.put("majorName", team.getMajorName());
        teamMap.put("orgName", team.getOrgName());
        teamMap.put("emergencyTeamname", team.getEmergencyTeamname());
        teamMap.put("emergencyTeamcode", team.getEmergencyTeamcode());
        teamMap.put("lineName", team.getLineName());
        teamMap.put("stationName", team.getStationName());
        teamMap.put("positionName", team.getPositionName());
        teamMap.put("workAreaName", team.getWorkAreaName());
        teamMap.put("managerName", team.getManagerName());
        teamMap.put("managerPhone", team.getManagerPhone());
        teamMapList.add(teamMap);
        errorMap.put("teamMapList", teamMapList);

        List<Map<String, String>> crewMapList = new ArrayList<>();
        Map<String, String> crewMap = new HashMap<>();
        for (CrewModel crewModel : crewList) {
            crewMap.put("scheduleItem", crewModel.getScheduleItem());
            crewMap.put("postName", crewModel.getPostName());
            crewMap.put("realName", crewModel.getRealName());
            crewMap.put("userPhone", crewModel.getUserPhone());
            crewMap.put("remark", crewModel.getRemark());
        }
        crewMapList.add(teamMap);
        errorMap.put("crewMapList", crewMapList);

        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        sheetsMap.put(0, errorMap);
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

        try {
            String fileName = "应急队伍导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iSysBaseAPI.importReturnRes(errorLines, successLines, errorMessage,true,url);
    }
}
