package com.aiurt.modules.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.planMountFind.dto.BdStationCopyDTO;
import com.aiurt.modules.planMountFind.mapper.BdOperatePlanDeclarationFormMonthMapper;
import com.aiurt.modules.weeklyplan.dto.*;
import com.aiurt.modules.weeklyplan.entity.*;
import com.aiurt.modules.weeklyplan.mapper.*;
import com.aiurt.modules.weeklyplan.service.*;
import com.aiurt.modules.weeklyplan.util.ExportExcelUtil;
import com.aiurt.modules.weeklyplan.util.ImportExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 周计划表
 * @Author: Lai W.
 * @Version: V1.0
 */
@Service
public class BdOperatePlanDeclarationFormServiceImpl
        extends ServiceImpl<BdOperatePlanDeclarationFormMapper, BdOperatePlanDeclarationForm>
        implements IBdOperatePlanDeclarationFormService {

    @Autowired
    private BdOperatePlanStateChangeMapper stateChangeMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ImportExcelUtil importExcelUtil;
    @Autowired
    private BdTeamMapper bdTeamMapper;
    @Autowired
    private BdOperatePlanDeclarationFormMapper bdOperatePlanDeclarationFormMapper;
    @Autowired
    private BdOperatePlanDeclarationFormMonthMapper bdOperatePlanDeclarationFormMonthMapper;
    @Autowired
    private BdSiteMapper siteMapper;
    @Autowired
    private IBdSiteService bdSiteService;
    @Autowired
    private BdLineMapper lineMapper;
    @Autowired
    private IBdLineService bdLineService;
    @Autowired
    private IBdStationService bdStationService;
    @Autowired
    private IBdOverhaulReportService bdOverhaulReportService;


    @Override
    public List<BdConstructionTypeDTO> getConstructionType() {
        return baseMapper.queryConstructionTypeList();
    }


    @Override
    public List<BdStaffInfoReturnTypeDTO> getMemberByTeamId(String teamId) {
        return baseMapper.queryStaffByTeamId(teamId);
    }

    @Override
    public List<BdStaffInfoReturnTypeDTO> getStaffsByRoleType(String roleName, String deptId) {
        return baseMapper.queryStaffByRoleType(roleName, deptId);
    }

    @Override
    public List<BdStaffInfoReturnTypeDTO> getStaffsByRoleName(String roleName, String deptId) {
        return baseMapper.queryStaffByRoleName(roleName, deptId);
    }

    @Override
    public List<BdStationReturnTypeDTO> getStationList(Integer teamId) {
        return baseMapper.queryStations(teamId);
    }

    @Override
    @Transactional
    public BdOperatePlanDeclarationForm convertRequestBody(BdOperatePlanDeclarationForm declarationForm) {
        Date applyDate = new Date();

        //检查计划是否已经被更改过.
        if (declarationForm.getPlanChange() == 3 && declarationForm.getChangeCorrelation() != 0) {
            if (checkFormIfEdited(declarationForm)) {
                throw new RuntimeException("Alrightly Edited");
            }
        }

        //检查施工负责人是否已经负责另一个施工计划
        /*if (checkChargeStaffIfConflict(declarationForm)) {
            throw new IllegalStateException("Time Conflict");
        }*/

        //默认值.
        declarationForm.setDateTime(new Timestamp(applyDate.getTime()));
        declarationForm.setApplyFormStatus(0);
        declarationForm.setLineFormStatus(0);
        declarationForm.setDispatchFormStatus(0);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //以防万一.
        if (declarationForm.getWeekday() == null) {
            declarationForm.setWeekday(0);
        }

        if (declarationForm.getLargeAppliances() == null) {
            declarationForm.setLargeAppliances("无");
        }

        //提前转换辅站负责人id->name
        if (declarationForm.getAssistStationManagerNames() == null
                || ("").equals(declarationForm.getAssistStationManagerNames())
                && declarationForm.getAssistStationManagerIds() != null) {
            if(!"".equals(declarationForm.getAssistStationManagerIds())){
                declarationForm.setAssistStationManagerNames(getNamesByIds(declarationForm.getAssistStationManagerIds()));
            }
        } else if (declarationForm.getAssistStationManagerNames() == null
                || ("").equals(declarationForm.getAssistStationManagerNames())
                && declarationForm.getAssistStationManagerIds() == null){
            declarationForm.setAssistStationManagerNames("无");
        }
        this.save(declarationForm);

        //发送消息,草稿保存不发送消息
        if(ObjectUtil.isNotEmpty(declarationForm.getFormStatus()) && declarationForm.getFormStatus() != 3){
            if (declarationForm.getPlanChange() == 0) {
                this.sendMessage(String.valueOf(declarationForm.getId()), sysUser.getUsername(), declarationForm.getLineStaffId(),
                        "你有新的待审批周计划", 13, 1, true);
            } else { //如果是补充计划, 设置补充计划专属字段
                this.sendMessage(String.valueOf(declarationForm.getId()), sysUser.getUsername(), declarationForm.getLineStaffId(),
                        "你有新的待审批补充计划/变更计划", 45, 1, false);
                declarationForm.setManagerFormStatus(0);
                declarationForm.setDirectorFormStatus(0);
            }
        }

        return declarationForm;
    }

    /**
     * 发送消息
     * @param afterStatus 2状态不发送
     * @param isLineallpeople 是否是总线路负责人，true是false否
     */
    private void sendMessage(String busId, String fromUser, String toUserId, String content, int typeId, int afterStatus, boolean isLineallpeople){
        if(ObjectUtil.isNotEmpty(toUserId) && afterStatus != 2){
            //发送消息
            BusMessageDTO busMessageDTO = new BusMessageDTO();
            busMessageDTO.setBusType(SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType());
            busMessageDTO.setBusId(busId);
            busMessageDTO.setFromUser(fromUser);
            busMessageDTO.setTitle("周计划");
            busMessageDTO.setCategory("3");
            String toLineStaffIdUser = sysBaseApi.getUserById(toUserId).getUsername();
            busMessageDTO.setToUser(toLineStaffIdUser);
            busMessageDTO.setContent(content);
            busMessageDTO.setAnnouncementTypeId(typeId);
            sysBaseApi.sendBusAnnouncement(busMessageDTO);

            if(isLineallpeople){
                //发送给所有总线路负责人
                List<String> lineallpeopleList = bdOperatePlanDeclarationFormMapper.queryUsernameByLineallpeople();
                for (String username: lineallpeopleList) {
                    busMessageDTO.setToUser(username);
                    sysBaseApi.sendBusAnnouncement(busMessageDTO);
                }
            }
        }
    }

    /**
     * Helper function to query AssistStationManager Names.
     * @param assistStationManagerIds A string of ids.
     * @return A string of names.
     */
    private String getNamesByIds(String assistStationManagerIds) {
        String[] parseIds = assistStationManagerIds.split(",");
        String result = baseMapper.queryStaffNamesByIds(parseIds);
        return result;
    }

    @Override
    public List<BdLineDTO> getLines() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Integer teamId = bdTeamMapper.queryByUserId(sysUser.getId());
        List<BdTeam> teamList = bdTeamMapper.queryManagedTeam(teamId);
        List<Integer> idList = new ArrayList<>();

        teamList.forEach(s -> {
            List<String> ids = Arrays.asList(s.getLineId().split(","));
            ids.forEach(id -> idList.add(Convert.toInt(id)));
        });

        return baseMapper.queryLines(idList.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public BdOperatePlanDeclarationFormReturnTypeDTO getFormInfoById (Integer id) {
        BdOperatePlanDeclarationFormReturnTypeDTO result = baseMapper.queryFormInfoById(id);
        //转换辅站id->names
        if (result != null && result.getAssistStationName() != null) {

            List<BdStationCopyDTO> stationInfoResult = bdOperatePlanDeclarationFormMonthMapper.queryAllStationInfo();

            List<String> assistStationIdList = Arrays.asList(result.getAssistStationName().split(","));
            String assistStationName = assistStationIdList.stream().filter(assistStationId -> {
                long count = stationInfoResult.stream().filter(station -> station.getId().equals(assistStationId)).count();
                if(count > 0){
                    return true;
                }
                return false;
            }).map(assistStationId -> {
                Optional<BdStationCopyDTO> stationCopyDtoOptional = stationInfoResult.stream().filter(station -> station.getId().equals(assistStationId)).collect(Collectors.toList()).stream().findAny();
                if(stationCopyDtoOptional.isPresent()){
                    return stationCopyDtoOptional.get().getName();
                }
                return null;
            }).collect(Collectors.joining(","));
            result.setAssistStationName(assistStationName);
        }

        //李想要求，这俩字段如果是 null 就改成0，普通计划不改
        if(result.getPlanChange() != null && result.getPlanChange() != 0){
            if(result.getDirectorFormStatus() == null){
                result.setDirectorFormStatus(0);
            }
            if(result.getManagerFormStatus() == null){
                result.setManagerFormStatus(0);
            }
        }

        //请点车站和销点车站 拼接线路
        List<BdStationReturnTypeDTO> list =  baseMapper.queryAllStations(null,null);
        list.forEach(s -> {
            if(result.getFirstStationId().equals(s.getId())){
                result.setFirstStationName(s.getLineName() + "--" + s.getName());
            }
            if(result.getSecondStationId().equals(s.getId())){
                result.setSecondStationName(s.getLineName() + "--" + s.getName());
            }
            if(null!=result.getSubstationId() && result.getSubstationId().equals(s.getId())){
                result.setSubstationName(s.getLineName() + "--" + s.getName());
            }
        });

        //判断这条计划是否可以结束
        if(result.getFormStatus() == 0 && ObjectUtil.isNotEmpty(result.getDispatchFormStatus()) &&
                ObjectUtil.isNotEmpty(result.getManagerFormStatus()) && ObjectUtil.isNotEmpty(result.getDirectorFormStatus()) &&
                ObjectUtil.isNotEmpty(result.getLineFormStatus()) &&
                result.getDispatchFormStatus() == 1 && result.getManagerFormStatus() == 1 &&
                result.getDirectorFormStatus() == 1 && result.getLineFormStatus() == 1 && result.getPlanChange() != 0){
            result.setIsCanEnd(1);
        }else if(result.getFormStatus() == 0 && ObjectUtil.isNotEmpty(result.getDispatchFormStatus()) &&
                ObjectUtil.isNotEmpty(result.getLineFormStatus()) && result.getDispatchFormStatus() == 1 &&
                result.getLineFormStatus() == 1 &&
            result.getPlanChange() == 0){
            result.setIsCanEnd(1);
        }else{
            result.setIsCanEnd(0);
        }

        //图片路径有逗号去掉
        if(ObjectUtil.isNotEmpty(result.getPicture())){
            result.setPicture(result.getPicture().replace(",", ""));
        }

        //格式化作业时间
        result.setTimeFormat(formatTaskTime(result.getTaskTime()));

        return result;
    }


    @Override
    public List<BdUserInfoDTO> getUserInfo(String id) {
        return baseMapper.queryUserInfo(id);
    }


    @Override
    public List<BdStation> getStations() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户管辖班组
        Integer teamId = bdTeamMapper.queryByUserId(sysUser.getId());
        List<BdLine> lineList = bdLineService.list();
        // 查询当前班组关联的工区信息
        LambdaQueryWrapper<BdSite> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.apply(Objects.nonNull(teamId), "FIND_IN_SET({0}, team_id) >0", teamId);
        // 一个班组关联多个工区
        List<BdSite> siteList = bdSiteService.getBaseMapper().selectList(queryWrapper);
        //管辖站点
        List<String> positionList = new ArrayList<>();
        siteList.forEach(s -> {
            if(ObjectUtil.isNotEmpty(s.getPosition())){
                positionList.addAll(Arrays.asList(s.getPosition().split(",")));
            }
        });
        List<BdStation> newStationList = new ArrayList<>();
        //查询当前登录用户的站点
        List<BdStationReturnTypeDTO> nowList =  baseMapper.queryAllStations(positionList.stream().distinct().collect(Collectors.toList()),null);
        //查询所有站点
        List<BdStation> allStationList =  bdStationService.list();

        allStationList.forEach(all->{
            List<BdStationReturnTypeDTO> BdStation = nowList.stream().filter(station->null!=station.getPid() &&station.getPid().equals(all.getId()) ).collect(Collectors.toList());
            if(!BdStation.isEmpty()){
                newStationList.add(all);
            }
        });
        newStationList.forEach(station -> {
            //添加线路名称
            List<BdLine> line = lineList.stream().filter(l-> l.getId().equals(station.getLineId())).collect(Collectors.toList());
            if(!line.isEmpty()){
                station.setName(line.get(0).getName() + "--" + station.getName());
            }
            station.setChildren(allStationList.stream().filter(all -> station.getId().equals(all.getPid())).collect(Collectors.toList()));
        });
        return newStationList;
    }

    @Override
    public Boolean checkChargeStaffIfConflict(BdOperatePlanDeclarationForm declarationForm) {
        String chargerStaffId = declarationForm.getChargeStaffId();
        Date taskDate = declarationForm.getTaskDate();
        try {
            String result = baseMapper.checkChargeStaffIfConflict(chargerStaffId, taskDate).toString();
            return !(result == null || ("").equals(result));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean checkFormIfEdited(BdOperatePlanDeclarationForm declarationForm) {
        Integer changeCorrelation = declarationForm.getChangeCorrelation();
        try {
            String result =  baseMapper.checkFormIfEdited(changeCorrelation).toString();
            return !(result == null || ("").equals(result));
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public Page<BdOperatePlanDeclarationFormReturnTypeDTO> queryPages(
            QueryPagesParams queryPagesParams, Integer pageNo, Integer pageSize, String busId) {
        //init page
        Integer currentIndex = (pageNo-1) * pageSize;
        List<BdOperatePlanDeclarationFormReturnTypeDTO> temp;
        List<BdOperatePlanDeclarationFormReturnTypeDTO> record;
        Page<BdOperatePlanDeclarationFormReturnTypeDTO> resultPage = new Page<>();

        //管辖班组
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> roleList = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        //查询当前登录账号的角色是否为“调度员”、“生产调度”
        roleList = roleList.stream().filter(s -> s.contains("dispatch") || s.contains("production_scheduling") ).collect(Collectors.toList());
        if(roleList.isEmpty()){
            Integer teamId = bdTeamMapper.queryByUserId(sysUser.getId());
            List<BdTeam> bdTeamList = bdTeamMapper.queryManagedTeam(teamId);
            queryPagesParams.setTeamIdList(bdTeamList.stream().map(s -> s.getId()).collect(Collectors.toList()));
        }else{
            queryPagesParams.setRoleId(null);
            queryPagesParams.setStaffID(null);
        }
        //查询
        List<String> formStatusesList = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(queryPagesParams.getFormStatuses())){
            formStatusesList = Arrays.asList(queryPagesParams.getFormStatuses().split(","));
        }
        queryPagesParams.setFormStatusList(formStatusesList);
        record = baseMapper.queryPages(queryPagesParams, busId);

        if (queryPagesParams.getIsChange() == 1) {
            record = record.stream().filter(s -> s.getFormStatus().equals(1) || !ObjectUtil.isEmpty(s.getChangeCorrelation())).collect(Collectors.toList());
        }

        //分页
        if (record.size() - currentIndex >= pageSize) {
            temp = record.subList(currentIndex, (currentIndex + pageSize));
        } else {
            temp = record;
        }
        getAssistStationName(temp);

        //作业时间字段特殊处理
/*        temp = temp.stream().map(s -> {
            s.setTimeFormat(formatTaskTime(s.getTaskTime()));
            return s;
        }).collect(Collectors.toList());*/
        for(BdOperatePlanDeclarationFormReturnTypeDTO dto: temp){
            dto.setTimeFormat(formatTaskTime(dto.getTaskTime()));
        }
        resultPage.setRecords(temp);

        //bullshit
        resultPage.setTotal(record.size());
        resultPage.setCurrent(pageNo);
        resultPage.setSize(pageSize);
        return resultPage;
    }

    @Override
    public List<ProductPlanDTO> queryAllProductPlan(ProductPlanDTO dto) {
        List<ProductPlanDTO> list = baseMapper.readAll(dto);
        int number = 1;
        for (ProductPlanDTO planDTO : list) {
            planDTO.setNumber(number++);
        }
        return list;
    }

    private List<BdOperatePlanDeclarationFormReturnTypeDTO>
        changeableSorter(List<BdOperatePlanDeclarationFormReturnTypeDTO> record) {
        List<BdOperatePlanDeclarationFormReturnTypeDTO> listTemp = new ArrayList<>();
        List<BdOperatePlanDeclarationFormReturnTypeDTO> list = new ArrayList<>();
        //排序
        List<BdOperatePlanDeclarationFormReturnTypeDTO> addlist = new ArrayList<>();
        for(BdOperatePlanDeclarationFormReturnTypeDTO objs : record) {
            if(objs.getChangeCorrelation()!=null&&objs.getChangeCorrelation()!=0) {
                addlist.add(objs);
            }else {
                listTemp.add(objs);
            }
        }
        for(BdOperatePlanDeclarationFormReturnTypeDTO objs : listTemp) {
            list.add(objs);
            for(BdOperatePlanDeclarationFormReturnTypeDTO os:addlist) {
                if(objs.getId()==os.getChangeCorrelation().intValue()) {
                    list.add(os);
                }
            }
        }
        return list;
    }

    @Override
    public List<BdOperatePlanDeclarationFormReturnTypeDTO> getListByQuery(QueryPagesParams queryPagesParams) {
        if(queryPagesParams.getIsChange() == 0){
            //管辖班组
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Integer teamId = bdTeamMapper.queryByUserId(sysUser.getId());
            List<BdTeam> bdTeamList = bdTeamMapper.queryManagedTeam(teamId);
            queryPagesParams.setTeamIdList(bdTeamList.stream().map(s -> s.getId()).collect(Collectors.toList()));
        }

        List<BdOperatePlanDeclarationFormReturnTypeDTO> record = baseMapper.queryPages(queryPagesParams, null);
        if (queryPagesParams.getIsChange() == 1) {
            record = changeableSorter(record);
        }
        getAssistStationName(record);

        //按时间升序排列， 辅站为空，替换成无
        record = record.stream().map(s -> {
            if(ObjectUtil.isEmpty(s.getAssistStationName())){
                s.setAssistStationName("无");
            }
            s.setTaskTime(formatTaskTime(s.getTaskTime()));
            return s;
        }).sorted(Comparator.comparing(BdOperatePlanDeclarationFormReturnTypeDTO::getTaskDate)).collect(Collectors.toList());
        return record;
    }

    @Override
    public List<BdOperatePlanDeclarationReturnDTO> queryListByDate(String taskDate) {
        //查询
        List<BdOperatePlanDeclarationReturnDTO> list = bdOperatePlanDeclarationFormMapper.queryListByDate(taskDate);
        return list;
    }

    public String StringNoNull(String str){
        if(ObjectUtil.isEmpty(str)){
           return "";
        }
        return str;
    }

    /**
     * taskTime 字段特殊处理
     * @param taskTime
     * @return
     */
    public String formatTaskTime(String taskTime){
        try {
            //字符串截取
            List<String> timeList = Arrays.asList(taskTime.split(","));
            Date datea = DateUtil.parse(timeList.get(0), "yyyy-MM-dd HH:mm");
            Date dateb = DateUtil.parse(timeList.get(1), "yyyy-MM-dd HH:mm");
            //判断是否是次日
            if(DateUtil.year(datea) == DateUtil.year(dateb) &&
                    DateUtil.month(datea) == DateUtil.month(dateb) &&
                    DateUtil.dayOfMonth(datea) == DateUtil.dayOfMonth(dateb)){
                //当日
                return DateUtil.format(datea, "HH:mm") + " - " + DateUtil.format(dateb, "HH:mm");
            }else{
                //次日
                return DateUtil.format(datea, "HH:mm") + " -次日 " + DateUtil.format(dateb, "HH:mm");
            }
        }catch (Exception e){
            if(taskTime.contains("次日")){
                return taskTime;
            }else{
                return "";
            }
        }
    }

    @Override
    public void exportExcel(List<BdOperatePlanDeclarationFormReturnTypeDTO> record,
                            HttpServletResponse response, QueryPagesParams queryPagesParams) {
        String[] titleRow =
                {"序号","作业性质","作业类别","作业单位","作业时间","线路作业范围","供电要求","作业内容",
                        "防护措施","施工负责人","配合部门","请点车站","销点车站","辅站","作业人数","大中型器具"};

        String newTitle = "施工计划表.xls";
        String sheetName = "运营施工及行车计划申报表";
        try {
            List<BdLine> bdLineList = lineMapper.selectList(new LambdaQueryWrapper<BdLine>());
            //获取线路名称
            /**/
            String lineNames = "";
            //todo 1234
            if(queryPagesParams.getLineID().equals(0)){
                lineNames = "3号线,4号线,8号线";
            }else{
                lineNames = bdLineList.stream().filter(s -> s.getId().equals(queryPagesParams.getLineID())).map(s -> s.getName()).collect(Collectors.joining(","));
            }
            newTitle = lineNames + "施工计划表.xls";
            sheetName = lineNames + "运营施工及行车计划申报表";

            ExportExcelUtil.writeToExcel(record, sheetName, titleRow, newTitle, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportExcelChangeable(List<BdOperatePlanDeclarationFormReturnTypeDTO> record,
                                      HttpServletResponse response, QueryPagesParams queryPagesParams) {
        String[] titleRow =
                {"序号","作业性质","作业类别","作业单位","作业时间","线路作业范围","供电要求","作业内容",
                        "防护措施","施工负责人","配合部门","请点车站","销点车站","辅站","作业人数","大中型器具"};

        String newTitle = "变更计划表.xls";
        String sheetName = "变更计划表";

        try {
            ExportExcelUtil.writeToExcelChangeable(record, sheetName, titleRow, newTitle, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BdOperatePlanDeclarationForm> importExcel(MultipartFile excel) {
        try {
           return importExcelUtil.importToExcelOperate(new MultipartFile[]{excel});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result<?> updateOperateForm(String voice,
                                       String picture, BdOperatePlanStateChange bdOperatePlanStateChange) {
        BdOperatePlanDeclarationForm declarationForm =
                this.getById(bdOperatePlanStateChange.getBdOperatePlanDeclarationFormId());
        //检查审批状态
        Result<?> result = checkUpdateConflict(declarationForm, bdOperatePlanStateChange);
        if (result.getErrCode() != null && result.getErrCode() == 600) {
            return result;
        }

        //补充字段
        bdOperatePlanStateChange.setId(declarationForm.getId().toString());
        bdOperatePlanStateChange.setForwardLineStatus(declarationForm.getLineFormStatus());
        bdOperatePlanStateChange.setForwardDispatchStatus((declarationForm.getDispatchFormStatus()));
        bdOperatePlanStateChange.setForwardDirectorStatus(declarationForm.getDirectorFormStatus());
        bdOperatePlanStateChange.setForwardManagerStatus(declarationForm.getManagerFormStatus());

        declarationForm.setVoice(voice);
        declarationForm.setPicture(picture);

        Integer afterStatus = bdOperatePlanStateChange.getAfterStatus();

        // 修改上一个消息状态为已读
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //readMessage(Convert.toStr(declarationForm.getId()), SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), sysUser.getUsername());
        readMessage(Convert.toStr(declarationForm.getId()), SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), null);

        //根据roleType判断是谁在审批
        String content = "";
        int typeId = 0;
        if(declarationForm.getPlanChange().equals(0)){
            content = "你有新的待审批周计划";
            typeId = 14;
        }else{
            content = "你有新的待审批补充计划/变更计划";
            typeId = 46;
        }
        try {//todo 1234
            if (("1392313109028872194").equals(bdOperatePlanStateChange.getRoleId()) || ("1392313109028872199").equals(bdOperatePlanStateChange.getRoleId())) {
                this.sendMessage(String.valueOf(declarationForm.getId()), sysUser.getUsername(), declarationForm.getDispatchStaffId(),
                        content, typeId, afterStatus, false);
                declarationForm.setLineFormStatus(afterStatus);
                declarationForm.setActualLineStaffId(sysUser.getId());
            } else if (("1393531024349736962").equals(bdOperatePlanStateChange.getRoleId())) {
                this.sendMessage(String.valueOf(declarationForm.getId()), sysUser.getUsername(), declarationForm.getDirectorStaffId(),
                        content, 43, afterStatus, false);
                declarationForm.setDispatchFormStatus(afterStatus);
            } else if (("1393530834351960066").equals(bdOperatePlanStateChange.getRoleId())) {
                this.sendMessage(String.valueOf(declarationForm.getId()), sysUser.getUsername(), declarationForm.getManagerStaffId(),
                        content, 44, afterStatus, false);
                declarationForm.setDirectorFormStatus(afterStatus);
            } else if (("1393530645335650305").equals(bdOperatePlanStateChange.getRoleId())) {
                declarationForm.setManagerFormStatus(afterStatus);
            } else {
                return Result.error(600, "请检查账户角色和部门，没有权限审批！");
            }
        } catch (NullPointerException e) {
            return Result.error(500, "roleId不能为空");
        }

        //一人拒绝则直接驳回
        if (afterStatus == 2) {
            declarationForm.setFormStatus(2);
            declarationForm.setRejectedReason(bdOperatePlanStateChange.getChangeReason());
            /*sysBaseAPI.sendSysAnnouncement(new MessageDTO(
                    sysBaseAPI.getUserById(bdOperatePlanStateChange.getChangeStaffId()).getRealname(),
                    sysBaseAPI.getUserById(declarationForm.getApplyStaffId()).getUsername(),
                    "你有申请中的的周计划被驳回",
                    "你有申请中的的周计划被驳回, 原因：" + bdOperatePlanStateChange.getChangeReason(), "1"));*/
        }
        //所有人同意则通过
        if (declarationForm.getPlanChange() == 0) {
            if (declarationForm.getLineFormStatus() == 1 && declarationForm.getDispatchFormStatus() == 1) {
                //declarationForm.setFormStatus(1);
                /*String username = sysBaseAPI.getUserById(declarationForm.getApplyStaffId()).getUsername();
                sysBaseAPI.sendSysAnnouncement(new MessageDTO("管理员",
                        username,
                        "你的周计划已通过申请", "你的周计划已通过申请", "1"));*/
            }
        } else {
            Integer status = 1;
            if (  status.equals(declarationForm.getLineFormStatus()) &&  status.equals(declarationForm.getDispatchFormStatus())
                    &&  status.equals(declarationForm.getManagerFormStatus()) &&   status.equals(declarationForm.getDirectorFormStatus())) {
                //declarationForm.setFormStatus(1);
                /*sysBaseAPI.sendSysAnnouncement(new MessageDTO("管理员",
                        sysBaseAPI.getUserById(declarationForm.getApplyStaffId()).getUsername(),
                        "你的周计划已通过申请", "你的周计划已通过申请", "1"));*/
            }
        }

        //更新表
        this.updateById(declarationForm);
        stateChangeMapper.insert(bdOperatePlanStateChange);

        return Result.OK();
    }

    public void readMessage(String busId, String busType, String username){
        //修改之前状态为已读
        List<String> idList = bdOperatePlanDeclarationFormMapper.queryBusAnnouncement(busId, busType, username);
        if(!ObjectUtil.isEmpty(idList)){
            bdOperatePlanDeclarationFormMapper.updateBusAnnouncement(idList);
        }
    }

    @Override
    public List<FormStatusTup> getStatus(Integer isAdditional) {
        return FormStatusTup.initWeekly(isAdditional);
    }

    @Override
    public Result<?> setApplyFormStatus(Integer formId, Integer applyFormStatus) {
        BdOperatePlanDeclarationForm buff = new BdOperatePlanDeclarationForm();
        buff.setApplyFormStatus(applyFormStatus);
        buff.setId(formId);
        baseMapper.updateById(buff);
        return Result.OK();
    }

    @Override
    public void edit(BdOperatePlanDeclarationForm bdOperatePlanDeclarationForm) {
        //修改
        bdOperatePlanDeclarationFormMapper.updateById(bdOperatePlanDeclarationForm);

        if(bdOperatePlanDeclarationForm.getFormStatus() != 3){
            //1是申请计划，2修改，不传,驳回也不传
            if(ObjectUtil.isNotEmpty(bdOperatePlanDeclarationForm.getIsApply())){
                if(bdOperatePlanDeclarationForm.getIsApply() == 1){
                    //申请计划
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    this.sendMessage(String.valueOf(bdOperatePlanDeclarationForm.getId()), sysUser.getUsername(), bdOperatePlanDeclarationForm.getLineStaffId(),
                            "你有新的待审批周计划", 13, 1, true);
                }else if(bdOperatePlanDeclarationForm.getIsApply() == 2){
                    //修改主页消息提醒
                    readMessage(Convert.toStr(bdOperatePlanDeclarationForm.getId()), SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), null);
                    //申请计划
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    this.sendMessage(String.valueOf(bdOperatePlanDeclarationForm.getId()), sysUser.getUsername(), bdOperatePlanDeclarationForm.getLineStaffId(),
                            "你有新的待审批周计划", 13, 1, true);
                }
            }else{
                //修改主页消息提醒
                readMessage(Convert.toStr(bdOperatePlanDeclarationForm.getId()), SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), null);
            }
        }
        //点击“取消计划”按钮
        if(bdOperatePlanDeclarationForm.getFormStatus() == 4){
            //取消检修任务
            bdOverhaulReportService.cancelTask(bdOperatePlanDeclarationForm.getId()+"");
        }
    }

    @Override
    public List<BdStaffInfoReturnTypeDTO> queryLineStaff() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询当前登录人班组
        Integer teamId = bdTeamMapper.queryByUserId(sysUser.getId());
        TeamByIdDTO teamByIdDTO = bdTeamMapper.queryTeamById(Convert.toStr(teamId));

        //当前登录人线路集合
        List<String> myLineList = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(teamByIdDTO.getLineId())){
            myLineList = Arrays.asList(teamByIdDTO.getLineId().split(","));
        }

        //查询所有线路负责人
        List<BdStaffInfoReturnTypeDTO> lineList = bdOperatePlanDeclarationFormMapper.queryLineStaff();

        //按线路查询
        List<String> finalMyLineList = myLineList;
        lineList = lineList.stream().filter(s -> {
            if(ObjectUtil.isNotEmpty(s.getLineId())){
                long coun = Arrays.asList(s.getLineId().split(",")).stream()
                        .filter(lineId -> finalMyLineList.stream().filter(myLineId -> myLineId.equals(lineId)).count() > 0).count();
                return coun > 0;
            }
            return false;
        }).collect(Collectors.toList());

        return lineList;
    }

    /**
     * 审批冲突检测
     * @param declarationForm 周计划表.
     * @return Result.
     */
    private Result<?> checkUpdateConflict(BdOperatePlanDeclarationForm declarationForm,
                                          BdOperatePlanStateChange planStateChange) {
        Integer falseStatus = 0;
        Integer trueStatus = 1;

        if (declarationForm.getFormStatus() == 1) {
            return Result.error(600, "计划已被通过，不可继续审批");
        } else if (declarationForm.getFormStatus() == 2) {
            return Result.error(600, "计划已被驳回，不可继续审批");
        } else {//todo 1234
            if (("1392313109028872194").equals(planStateChange.getRoleId())) {
                if (declarationForm.getDispatchFormStatus() != null
                        &&  !falseStatus.equals(declarationForm.getDispatchFormStatus())) {
                    return Result.error(600, "生产调度已审批，不可二次操作");
                }
            } else if (("1393531024349736962").equals(planStateChange.getRoleId())) {
                if (declarationForm.getLineFormStatus() == null
                        ||  !trueStatus.equals(declarationForm.getLineFormStatus())) {
                    return Result.error(600, "线路负责人未同意，不可继续审批");
                } else if (declarationForm.getDirectorFormStatus() != null
                        &&  !falseStatus.equals(declarationForm.getDirectorFormStatus())) {
                    return Result.error(600, "分部主任已审批，不可二次操作");
                }
            } else if (("1393530834351960066").equals(planStateChange.getRoleId())) {
                if (declarationForm.getDispatchFormStatus() == null
                        ||  !trueStatus.equals(declarationForm.getDispatchFormStatus())) {
                    return Result.error(600, "生产调度未同意，不可继续审批");
                }
                /*else if (declarationForm.getDispatchFormStatus() != null
                        && declarationForm.getManagerFormStatus() != falseStatus) {
                    return Result.error(600, "公司经理已审批，不可二次操作");
                }*/
            } else if (("1393530645335650305").equals(planStateChange.getRoleId())) {
                if (declarationForm.getDispatchFormStatus() == null
                        ||  !trueStatus.equals(declarationForm.getDirectorFormStatus())) {
                    return Result.error(600, "分部主任未同意，不可继续审批");
                }
            }
            return Result.OK();
        }
    }


    /**
     * Helper Function to get Assist Station's names.
     * @param record List of Declaration Form.
     */
    private void getAssistStationName(List<BdOperatePlanDeclarationFormReturnTypeDTO> record) {
        for (BdOperatePlanDeclarationFormReturnTypeDTO item: record) {
            if (ObjectUtil.isNotEmpty(item.getAssistStationName())) {
                String[] parseId = item.getAssistStationName().split(",");
                String concatName = baseMapper.queryStationNamesById(parseId);
                item.setAssistStationName(concatName);
            }
        }
    }

    /**
     * Helper function to init line table.
     * @param range the range of line to init.
     * @return A hash table contain Integer-String pair of station's line.
     */
    private Hashtable<Integer, String> initLineHash(int range) {
        Hashtable<Integer, String> lineTable = new Hashtable<>();
        for (int i = 0; i < range + 1; i++) {
            lineTable.put(i, ("(" + i + "号线)"));
        }
        return lineTable;
    }

    /**
     * 重新申请
     * @param id
     */
    @Override
    public void reapply(Integer id) {
        BdOperatePlanDeclarationForm operatePlanDeclarationForm = bdOperatePlanDeclarationFormMapper.selectById(id);
        //不是草稿保存状态，才修改
        if(operatePlanDeclarationForm.getFormStatus() != 3){
            operatePlanDeclarationForm.setFormStatus(0);
            operatePlanDeclarationForm.setDispatchFormStatus(0);
            operatePlanDeclarationForm.setLineFormStatus(0);
            operatePlanDeclarationForm.setDirectorFormStatus(0);
            operatePlanDeclarationForm.setManagerFormStatus(0);
            bdOperatePlanDeclarationFormMapper.updateById(operatePlanDeclarationForm);
        }

        //修改之前状态为已读
        readMessage(Convert.toStr(operatePlanDeclarationForm.getId()), SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), null);

        //发送消息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (operatePlanDeclarationForm.getPlanChange() == 0) {
            this.sendMessage(String.valueOf(operatePlanDeclarationForm.getId()), sysUser.getUsername(), operatePlanDeclarationForm.getLineStaffId(),
                    "你有新的待审批周计划", 13, 1, true);
        } else { //如果是补充计划, 设置补充计划专属字段
            this.sendMessage(String.valueOf(operatePlanDeclarationForm.getId()), sysUser.getUsername(), operatePlanDeclarationForm.getLineStaffId(),
                    "你有新的待审批补充计划/变更计划", 45, 1, false);
        }
    }

    /**
     * 周计划表-是否有权限审批
     * @param id
     * @return
     */
    @Override
    public IsApproveDTO isApprove(Integer id) {
        IsApproveDTO isApproveDTO = new IsApproveDTO();
        //查询详情
        BdOperatePlanDeclarationForm operatePlanDeclarationForm = bdOperatePlanDeclarationFormMapper.selectById(id);

        //是否进入流程
        if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getFormStatus()) && operatePlanDeclarationForm.getFormStatus() == 0){
            isApproveDTO.setIsBegin(1);
        }else{
            isApproveDTO.setIsBegin(0);
        }

        //是否有审批权限
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //查询是否是总线路负责人
        List<String> roleList = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        long count = roleList.stream().filter(s -> ("line_all_people").equals(s)).count();
        //总线路负责人 有权限审批线路下所有的
        if((ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getLineStaffId()) && operatePlanDeclarationForm.getLineStaffId().equals(sysUser.getId())) ||
                (count > 0 && ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getLineStaffId()) && operatePlanDeclarationForm.getLineFormStatus() == 0)){
            //线路
            isApproveDTO.setIsApprove(1);
        }else if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getDispatchStaffId()) && operatePlanDeclarationForm.getDispatchStaffId().equals(sysUser.getId())){
            //调度
            //线路审批通过
            if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getLineFormStatus()) && operatePlanDeclarationForm.getLineFormStatus() == 1){
                isApproveDTO.setIsApprove(1);
            }else{
                isApproveDTO.setIsApprove(0);
            }
        }else if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getDirectorStaffId()) && operatePlanDeclarationForm.getDirectorStaffId().equals(sysUser.getId())){
            //主任
            //线路、调度审批通过
            if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getLineFormStatus()) && operatePlanDeclarationForm.getLineFormStatus() == 1 &&
                    ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getDispatchFormStatus()) && operatePlanDeclarationForm.getDispatchFormStatus() == 1){
                isApproveDTO.setIsApprove(1);
            }else{
                isApproveDTO.setIsApprove(0);
            }
        }else if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getManagerStaffId()) && operatePlanDeclarationForm.getManagerStaffId().equals(sysUser.getId())){
            //经理
            //线路、调度、主任审批通过
            if(ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getLineFormStatus()) && operatePlanDeclarationForm.getLineFormStatus() == 1 &&
                    ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getDispatchFormStatus()) && operatePlanDeclarationForm.getDispatchFormStatus() == 1 &&
                    ObjectUtil.isNotEmpty(operatePlanDeclarationForm.getDirectorFormStatus()) && operatePlanDeclarationForm.getDirectorFormStatus() == 1){
                isApproveDTO.setIsApprove(1);
            }else{
                isApproveDTO.setIsApprove(0);
            }
        }else{
            isApproveDTO.setIsApprove(0);
        }

        return isApproveDTO;
    }

    @Override
    public void removeById(String id) {
        bdOperatePlanDeclarationFormMapper.deleteById(id);
        this.readMessage(id, SysAnnmentTypeEnum.BDOPERATEPLANDECLARATIONFORM.getType(), null);
    }

}
