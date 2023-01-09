package com.aiurt.boot.team.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constants.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.entity.RecordData;
import com.aiurt.boot.team.listener.TeamExcelListener;
import com.aiurt.boot.team.mapper.EmergencyTeamMapper;
import com.aiurt.boot.team.model.CrewModel;
import com.aiurt.boot.team.model.TeamModel;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsWorkAreaModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;
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
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        List<EmergencyTeam> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (EmergencyTeam record : records) {
                this.translate(record);
            }
        }
        GlobalThreadLocal.setDataFilter(b);
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
                emergencyCrew.setDelFlag(TeamConstant.DEL_FLAG0);
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
        List<EmergencyCrew> crewList = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(crewList)) {
            for (EmergencyCrew emergencyCrew : crewList) {
                emergencyCrew.setDelFlag(TeamConstant.DEL_FLAG1);
                emergencyCrewService.updateById(emergencyCrew);
            }
        }
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
    public Result<List<EmergencyTeam>> getTeamByCode() {
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
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
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(EmergencyTeam::getId,EmergencyTeam::getEmergencyTeamname, EmergencyTeam::getEmergencyTeamcode,EmergencyTeam::getPositionCode);
        queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
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
    @Transactional(rollbackFor = Exception.class)
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
                return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            try {
                TeamExcelListener teamExcelListener = new TeamExcelListener();
                try {
                    EasyExcel.read(file.getInputStream(), RecordData.class, teamExcelListener).sheet().doRead();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TeamModel team = teamExcelListener.getTeamModel();
                List<CrewModel> crewList = teamExcelListener.getCrewList();

                Iterator<CrewModel> iterator = crewList.iterator();
                while (iterator.hasNext()) {
                    CrewModel model = iterator.next();
                    boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
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
                emergencyTeam.setPeopleNum(emergencyCrews.size());
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
        String scheduleItem = crewModel.getScheduleItem();
        String postName = crewModel.getPostName();
        String realName = crewModel.getRealName();
        String userPhone = crewModel.getUserPhone();
        if (StrUtil.isNotBlank(scheduleItem)) {
            emergencyCrew.setScheduleItem(scheduleItem);
        }else {
            stringBuilder1.append("班次不能为空");
        }
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

        if (StrUtil.isNotBlank(majorName)) {
            JSONObject major = iSysBaseAPI.getCsMajorByName(majorName);
            if (ObjectUtil.isNotNull(major)) {
                emergencyTeam.setMajorCode(major.getString("majorCode"));
            } else {
                stringBuilder.append("系统不存在该专业，");
            }
        } else {
            stringBuilder.append("所属专业不能为空，");
        }

        if (StrUtil.isNotBlank(departName)) {
            JSONObject depart = iSysBaseAPI.getDepartByName(departName);
            if (ObjectUtil.isNotNull(depart)) {
                emergencyTeam.setOrgCode(depart.getString("orgCode"));
                //校验部门负责人
                if (StrUtil.isNotBlank(manager)&& StrUtil.isNotBlank(managerPhone)) {
                    List<LoginUser> userByRealName = iSysBaseAPI.getUserByRealName(manager, managerWorkNo);
                    if (userByRealName.size() != 1) {
                        stringBuilder.append("负责人姓名存在同名，请填写工号，");
                    } else {
                        LoginUser loginUser = userByRealName.get(0);
                        if (loginUser.getOrgCode().equals(depart.getString("orgCode"))) {
                            emergencyTeam.setManagerId(loginUser.getId());
                        } else {
                            stringBuilder.append("负责人不是该部门的人");
                        }
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
            } else {
                stringBuilder.append("系统不存在该部门，");
            }
        }else {
            stringBuilder.append("所属部门不能为空，");
        }
        if (StrUtil.isNotBlank(emergencyTeamName)) {
            LambdaQueryWrapper<EmergencyTeam> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EmergencyTeam::getEmergencyTeamname, emergencyTeamName).eq(EmergencyTeam::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
            EmergencyTeam team1 = this.getBaseMapper().selectOne(wrapper);
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
        }else {
            stringBuilder.append("应急队伍名称不能为空，");
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
                emergencyTeam.setStationCode(stationByName.getString("stationCode"));
                //校验工区
                if (StrUtil.isNotBlank(workArea)) {
                    List<CsWorkAreaModel> workAreaByCode = iSysBaseAPI.getWorkAreaByCode(emergencyTeam.getStationCode());
                    List<String> collect = Optional.ofNullable(workAreaByCode).orElse(Collections.emptyList()).stream().map(CsWorkAreaModel::getName).collect(Collectors.toList());
                    if (!collect.contains(workArea)) {
                        stringBuilder.append("该站点不存在该工区，");
                    }
                }
            } else {
                stringBuilder.append("系统不存在该站点，");
            }
            if (ObjectUtil.isNotNull(positionByName)) {
                emergencyTeam.setPositionCode(positionByName.getString("positionCode"));
            } else {
                stringBuilder.append("系统不存在该线路站点下的位置，");
            }
        } else {
            stringBuilder.append("线路，站点，驻扎地不能为空，");
        }


        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            team.setMistake(stringBuilder.toString());
            errorLines++;
        }

        return errorLines;
    }

    /**错误报告模板导出*/
    private Result<?> getErrorExcel(int errorLines,List<String> errorMessage,TeamModel team, List<CrewModel> crewList,int successLines ,String url,String type) throws IOException {
        TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyTeamError.xlsx");
        Map<String, Object> errorMap = new HashMap<String, Object>();
        errorMap.put("majorName", team.getMajorName());
        errorMap.put("orgName", team.getOrgName());
        errorMap.put("emergencyTeamname", team.getEmergencyTeamname());
        errorMap.put("emergencyTeamcode", team.getEmergencyTeamcode());
        errorMap.put("lineName", team.getLineName());
        errorMap.put("stationName", team.getStationName());
        errorMap.put("positionName", team.getPositionName());
        errorMap.put("workAreaName", team.getWorkAreaName());
        errorMap.put("managerName", team.getManagerName());
        errorMap.put("managerPhone", team.getManagerPhone());
        errorMap.put("mistake", team.getMistake());


        List<Map<String, String>> crewMapList = new ArrayList<>();
        Map<String, String> crewMap = new HashMap<>();
        for (CrewModel crewModel : crewList) {
            crewMap.put("scheduleItem", crewModel.getScheduleItem());
            crewMap.put("postName", crewModel.getPostName());
            crewMap.put("realName", crewModel.getRealName());
            crewMap.put("userPhone", crewModel.getUserPhone());
            crewMap.put("remark", crewModel.getRemark());
            crewMap.put("mistake", crewModel.getMistake());
        }
        crewMapList.add(crewMap);
        errorMap.put("crewMapList", crewMapList);

        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        sheetsMap.put(0, errorMap);
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

        try {
            String fileName = "应急队伍导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,url);
    }


    @Override
    public void exportTemplateXls(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("templates/emergencyTeam.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp= new File("templates/emergencyTeam.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
        Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        String fileName = "应急队伍导入模板.xlsx";

        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename="+"应急队伍导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModelAndView exportTeamXls(HttpServletRequest request, EmergencyTeamDTO emergencyTeamDTO) {
        IPage<EmergencyTeam> emergencyTeamIPage = this.queryPageList(emergencyTeamDTO, 1, Integer.MAX_VALUE);
        List<EmergencyTeam> records = emergencyTeamIPage.getRecords();
        List<EmergencyTeam> exportList = null;
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = records.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = records;
        }

        List<TeamModel> teamModels = new ArrayList<>();
        if (CollUtil.isNotEmpty(exportList)) {
            int sort = 1;
            for (EmergencyTeam record : exportList) {
                TeamModel teamModel = new TeamModel();
                BeanUtil.copyProperties(record,teamModel);
                teamModel.setSort(Convert.toStr(sort));
                teamModels.add(teamModel);
                sort++;
            }
        }
        String title = "应急队伍台账";
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, TeamModel.class);
        ExportParams exportParams=new ExportParams(title, title);
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, teamModels);
        return mv;
    }

    @Override
    public ModelAndView exportCrewXls(HttpServletRequest request, String id) {
        EmergencyTeam emergencyTeam = this.getById(id);
        EmergencyTeam team = this.getCrew(emergencyTeam);
        List<EmergencyCrewVO> emergencyCrewVOList = team.getEmergencyCrewVOList();
        List<CrewModel> crewModels = new ArrayList<>();
        if (CollUtil.isNotEmpty(emergencyCrewVOList)) {
            int sort = 1;
            for (EmergencyCrewVO record : emergencyCrewVOList) {
                CrewModel crewModel = new CrewModel();
                crewModel.setSort(Convert.toStr(sort));
                crewModel.setScheduleItem(record.getScheduleItem());
                crewModel.setRealName(record.getRealname());
                crewModel.setUserPhone(record.getPhone());
                crewModel.setRoleNames(record.getRoleNames());
                Integer post = record.getPost();
                String s = iSysBaseAPI.translateDict(TeamConstant.EMERGENCY_POST, Convert.toStr(post));
                crewModel.setPostName(s);
                crewModel.setMajorName(emergencyTeam.getMajorName());
                crewModel.setLineStation(emergencyTeam.getLineName()+emergencyTeam.getStationName());
                crewModels.add(crewModel);
                sort++;
            }
        }
        String title = emergencyTeam.getOrgName() + emergencyTeam.getEmergencyTeamname() + "成员明细表";
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, CrewModel.class);
        ExportParams exportParams=new ExportParams(title, title);
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, crewModels);
        return mv;
    }
}
