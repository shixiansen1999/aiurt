package com.aiurt.modules.planMountFind.service.impl;


import com.aiurt.modules.planMountFind.dto.*;
import com.aiurt.modules.planMountFind.entity.BdOperatePlanDeclarationFormMonth;
import com.aiurt.modules.planMountFind.mapper.BdOperatePlanDeclarationFormMonthMapper;
import com.aiurt.modules.planMountFind.service.IBdOperatePlanDeclarationFormMonthService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 月计划工具类
 * @Author: 沙雕国王
 * @Date: 2021-05-18
 * @Version: V1.0
 */
@Service
public class BdOperatePlanDeclarationFormMonthServiceImpl extends ServiceImpl<BdOperatePlanDeclarationFormMonthMapper, BdOperatePlanDeclarationFormMonth> implements IBdOperatePlanDeclarationFormMonthService {


    @Autowired
    private BdOperatePlanDeclarationFormMonthMapper bdMapper;

    /**
     * 通过task_date字段,控制时间范围,line_id字段控制线路,page分页信息.
     * 此处再controller中有默认值.
     * 里面的神秘逻辑就是把查询出来的id,换成对应的名字,别纠结.
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param PageNo     与分页信息
     * @param PageSize   与分页信息
     * @param page       与分页信息
     * @param roleType
     * @param staffID
     * @return
     */
    @Override
    public Page<getAllByDateDTO> listByDate(String start_time, String end_time, String line_id, Integer PageNo, Integer PageSize, Page page, String roleType, String staffID) {
        List<BdStationCopyDTO> StationInfoList = queryAllStationInfo();
        Page<getAllByDateDTO> lls = bdMapper.getAllByDate(start_time, end_time, line_id, page, roleType, staffID);
        List<getAllByDateDTO> ls = lls.getRecords();
        int skip = PageNo * PageSize;
        int limit = PageNo * PageSize + PageSize;

        String[] TempStrArray;
        StringBuilder AppendStringBuilder = new StringBuilder();
        for (getAllByDateDTO dto : ls) {
            if (dto.getAssistStationIds() == null) {
                dto.setAssistStationIds("空");
                continue;
            }
            TempStrArray = dto.getAssistStationIds().split(",");
            {
                for (BdStationCopyDTO BDS : StationInfoList) {
                    for (int i = 0; i < TempStrArray.length; i++) {
                        if (TempStrArray[i].equals(BDS.getId())) {
                            AppendStringBuilder.append(BDS.getName());
                            if (TempStrArray.length != 1 && TempStrArray.length - 1 != i) {
                                AppendStringBuilder.append(",");
                            }
                        }
                    }
                }
            }
            dto.setAssistStationIds(AppendStringBuilder.toString());
            AppendStringBuilder = new StringBuilder();
            TempStrArray = null;
        }
        lls.setRecords(ls);
        return lls;
    }


    /**
     * 生产计划管理-月施工计划定制-添加下月任务-作业类别-下拉框接口
     *
     * @return List<BdWorkTypeDTO> 作业类别数据
     */
    @Override
    public List<BdWorkTypeDTO> queryAllContruction() {
        return bdMapper.queryAllContruction();
    }

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-施工负责人-下拉框接口
     *
     * @param team_id 当前人员的team_id
     * @return 施工负责人-下拉框接口
     */
    @Override
    public List<queryByTeamIdStaffDTO> queryByTeamIdStaff(String team_id) {
        return bdMapper.queryByTeamIdStaff(team_id);

    }

    /**
     * 生产计划管理-月施工计划定制-添加下月任务- 生产经理 与 线路负责人 -下拉框接口
     *
     * @param roleType 角色类型
     * @param deptId   ???忘了兄弟...
     * @return 线路负责人 -下拉框接口
     */
    @Override
    public List<queryStaffsByRoleTypeDTO> queryStaffsByRoleType(String roleType, String deptId) {
        return bdMapper.queryStaffsByRoleType(roleType, deptId);
    }

    /**
     * 查询站点信息,返回线路id,和线路名称
     *
     * @return List<BdStationCopyDTO> 包含 线路id和线路名称
     */
    @Override
    public List<BdStationCopyDTO> queryAllStationInfo() {
        List<BdStationCopyDTO> lineList = bdMapper.queryAllStationInfo();
        for (BdStationCopyDTO bds : lineList
        ) {
            bds.setName(bds.getLineName() + "--" + bds.getName());
        }
        return lineList;
    }

    /**
     * 查询线路信息,返回id和name
     *
     * @return List<BdLineInfoDTO>
     */
    @Override
    public List<BdLineInfoDTO> getLineInfo() {

        return bdMapper.getLineInfo();
    }


    /**
     * 导出excel,查询数据
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param roleType   角色类型
     * @param staffID    登陆人id
     * @return ModelAndView
     */
    @Override
    public ModelAndView ExportExcel(String start_time, String end_time, String line_id, String roleType, String staffID) {
        List<ExcelExportDTO> result = bdMapper.exportExcel(start_time, end_time, line_id, roleType, staffID);
        List<BdStationCopyDTO> stationInfoResult = bdMapper.queryAllStationInfo();

        StringBuilder AppendStrArray = new StringBuilder();
        String[] eedtoToSplit;

        int i = 0;
        for (ExcelExportDTO eedto : result) {
            if (eedto.getAssistStationIds() == null) {
                continue;
            }
            eedtoToSplit = eedto.getAssistStationIds().split(",");

            for (BdStationCopyDTO stationInfo : stationInfoResult) {
                if (eedtoToSplit[i].equals(stationInfo.getId())) {
                    AppendStrArray.append(stationInfo.getName());
                    i++;
                    if (eedtoToSplit.length != i && eedtoToSplit.length - 1 != 0) {
                        AppendStrArray.append(",");
                    }

                    if (eedtoToSplit.length == i) {
                        break;
                    }
                }
            }

            eedto.setAssistStationIds(AppendStrArray.toString());
            //据说有助于对内存的回收.
            AppendStrArray = null;
            //同上
            eedtoToSplit = null;
            //因为要用到新的空间所以new
            AppendStrArray = new StringBuilder();
            i = 0;
        }

        //添加序号
        Integer[] arr = {1};
        result = result.stream().peek(e -> e.setId(arr[0]++)).collect(Collectors.toList());


        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, "月计划表");
        mv.addObject(NormalExcelConstants.CLASS, ExcelExportDTO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, result);

        return mv;
    }

    /**
     * 通过id查询.
     *
     * @param id 数据行id
     * @return List<BdqueryByID> 返回当前数据行对应的月计划表一行数据.
     */
    @Override
    public List<BdqueryByID> queryByID(String id) {
        List<BdqueryByID> result = bdMapper.queryByID(id);
        List<BdStationCopyDTO> stationInfoResult = bdMapper.queryAllStationInfo();
        StringBuilder AppendStrArray = new StringBuilder();
        String[] eedtoToSplit;
        int i = 0;
        for (BdqueryByID eedto : result) {
            if (eedto.getAssistStationName() == null) {
                continue;
            }
            eedtoToSplit = eedto.getAssistStationName().split(",");
            for (BdStationCopyDTO stationInfo : stationInfoResult) {
                if (eedtoToSplit[i].equals(stationInfo.getId())) {
                    AppendStrArray.append(stationInfo.getName());
                    i++;
                    if (eedtoToSplit.length != i && eedtoToSplit.length - 1 != 0) {
                        AppendStrArray.append(",");
                    }
                    if (eedtoToSplit.length == i) {
                        break;
                    }
                }
            }
            eedto.setAssistStationName(AppendStrArray.toString());
            //据说有助于对内存的回收.
            AppendStrArray = null;
            //同上
            eedtoToSplit = null;
            //因为要用到新的空间所以new
            AppendStrArray = new StringBuilder();
            i = 0;
        }
        return result;
    }


    /**
     * 查询所有信息
     *
     * @param id 月计划表行id
     * @return List<BdOperatePlanDeclarationFormMonth> 一行数据
     */
    @Override
    public List<BdOperatePlanDeclarationFormMonth> queryAllInfoByID(String id) {
        return bdMapper.queryAllInfoByID(id);
    }


    /**
     * 审核按钮
     *
     * @param id           数据行id
     * @param formStatus   审核状态
     * @param roleType     角色类型
     * @param voice        录音
     * @param picture      图片
     * @param changeReason 审核意见
     * @param remark       没啥用
     *                     此方法内部是新版月计划审核逻辑,
     *                     上古注释已经删除
     *                     灰色部分单行注释,是老变电里面的代码,复制过来方便仿写其逻辑
     *                     多行注释绿色部分是仿写后针对老边点表的逻辑,
     *                     后期因为用户表和角色表有变化,
     *                     所以重写了其逻辑.
     *                     但是看到新版的流程图后,发现老版本逻辑不符合新版本流程图,所以重写了此处逻辑.
     *                     <p>
     *                     此方法大致逻辑如下:
     *                     1.判断是否是线路负责人,如果不是,提示他错误代码,如果是,那么进入mapper方法中开始执行sql语句.
     *                     2.如果此人没有角色类型提示他错误代码.
     *                     3.如果线路负责人已经审核过了,提示线路负责人.这个已经审核过了.
     * @return String
     */
    @Override
    public String auditButton(String id, int formStatus, String roleType,
                              String voice, String picture, String changeReason, String remark) {


        int PlanChange = 0;
        List<BdOperatePlanDeclarationFormMonth> operatePlanNew = queryAllInfoByID(id);
        if (operatePlanNew.get(0).getLineFormStatus().toString().equals("1")) {
            return "线路负责人已审核,请勿重新审核,操作错误代码:514";
        }
        //只能线路负责人审核.或者访问时,没传入roleType
        if (!roleType.equals("1392313109028872194")) {
            return "只能线路负责人来审核月计划.请您更换用户账号后操作,操作错误代码:516";
        }
        //只有线路负责人可以审核,并且只需要线路负责人可以审核,新版流程图标注
        if (roleType.equals("1392313109028872194")) {
            return bdMapper.updateBd_operate_plan_declaration_form_monthByID(formStatus, formStatus, voice, picture, PlanChange, id, changeReason).toString();
        }

        //如果账户没有设置角色类型,则提示如下.
        if (roleType.isEmpty()) {
            return "您的帐号没有任何角色类型,请系统联系管理员为您设置角色类型,操作错误代码:517";
        }
        return "操作错误代码:515";


    }


    /**
     * type表里的remark--------老项目翻译
     *
     * @param roleName
     * @return String类型的一个数字.
     */
    @Override
    public String getRoleType(String roleName) {
        return bdMapper.getRoleType(roleName);
    }

    /**
     * 老项目翻译
     *
     * @param roleName 获取当前登录人的角色类型相关信息.
     * @return 一个数字.
     */
    @Override
    public Integer getRoleId(String roleName) {
        Integer resultAll = bdMapper.getRoleId(getRoleType(roleName));
        return resultAll;
    }

    /**
     * 通过用户id获取roletype
     *
     * @return List<queryRoleTypeByIDDTO>
     */
    @Override
    public List<queryRoleTypeByIDDTO> getRoleTypeByID() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String staff_id = sysUser.getId();
        return bdMapper.queryRoleTypeByID(staff_id);
    }

    /**
     * 审批查询接口
     * 里面的神秘逻辑就是把查询出来的id,换成对应的名字,别纠结.
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param PageNo     页码
     * @param PageSize   页面尺寸
     * @param page       页面信息
     * @param roleType   角色类型
     * @param staffID    当前登录人id
     * @return Page<getAllByDateDTO> 与通过id查询,极其相似.
     */
    @Override
    public Page<getAllByDateDTO> ApproveQuery(String start_time, String end_time, String line_id, Integer PageNo, Integer PageSize, Page page, String roleType, String staffID, String busId) {
        List<BdStationCopyDTO> StationInfoList = queryAllStationInfo();
        Page<getAllByDateDTO> lls = bdMapper.ApproveQuery(start_time, end_time, line_id, page, roleType, staffID, busId);
        List<getAllByDateDTO> ls = lls.getRecords();
        int skip = PageNo * PageSize;
        int limit = PageNo * PageSize + PageSize;
        String[] TempStrArray;
        StringBuilder AppendStringBuilder = new StringBuilder();
        for (getAllByDateDTO dto : ls) {
            if (dto.getAssistStationIds() == null) {
                dto.setAssistStationIds("空");
                continue;
            }
            TempStrArray = dto.getAssistStationIds().split(",");
            {
                for (BdStationCopyDTO BDS : StationInfoList) {
                    for (int i = 0; i < TempStrArray.length; i++) {
                        if (TempStrArray[i].equals(BDS.getId())) {
                            AppendStringBuilder.append(BDS.getName());
                            if (TempStrArray.length != 1 && TempStrArray.length - 1 != i) {
                                AppendStringBuilder.append(",");
                            }
                        }
                    }
                }
            }
            dto.setAssistStationIds(AppendStringBuilder.toString());
            AppendStringBuilder = new StringBuilder();
            TempStrArray = null;
        }
        lls.setRecords(ls);
        return lls;
    }

    /**
     * 取消计划按钮
     *
     * @param userID     用户id,用于判断申请计划与取消计划,是否为同一人
     * @param formStatus 申请状态.
     * @param id         数据行id
     * @return 返回一个数字.
     */
    @Override
    public Integer cancelButton(String userID, int formStatus, String id, String rejectedReason) {
        List<BdqueryByID> query = queryByID(id);
        if (query.isEmpty()) {
            //没有查询到此id相关数据 返回-2 ,控制器识别到,返回相关错误信息
            return -2;
        }
        if (query.get(0).getLineFormStatus().equals("1")) {
            //线路负责人已经审核,无法取消.
            return -3;
        }
        Boolean condition = query.get(0).getApplyStaffId().equals(userID);
        if (condition) {
            return bdMapper.cancelButton(formStatus, id, rejectedReason);
        }
        //不是申请人点击的取消按钮,就给它返回-1,一判断,嘎?程序傻了...
        return -1;
    }


    /**
     * 新版审核按钮
     * 接下来的模块是,审核流程中,更改的流程.还没有应用到实际项目,不知道是否改版.
     * 如有改版,直接应用即可.
     * <p>
     * 更改后的逻辑为:
     * 总结有4种状态,如下:
     * 流程0: 驻班工程师提交申请 --- 线路负责人审核 -- 同意后 --- 状态:申请中
     * 流程1: 流程0结束后 --- 点击结束流程按钮 状态:通过或叫同意
     * 流程2: 驻班工程师提交申请 --- 线路负责人审核 -- 驳回 --- 状态:被驳回
     * 流程3: 驻班工程师提交申请 --- 驻班工程师取消申请 --- 状态:已取消
     * <p>
     * 数据库辅助字段说明:
     * 其中判断线路负责人是否申请过,用数据库中line_form_status来区分. 线路负责人审批状态 0 未审批 1 通过 2 驳回
     * form_status是区分是否同意的. 0申请中 1 同意 2驳回 3草稿保存 4 取消（apply_form_status为0可以修改申请条目与状态）
     * apply_form_status是区分流程是结束. 0 申请中 1 流程结束
     */
    @Override
    public String lineStaffAuditButton(BdOperatePlanDeclarationFormMonth monthEntity) {
        //线路负责人角色类型值
        final String LINESTAFFROLETYPE = "1392313109028872194";
        //通过用户id查询当前登录人是否为线路负责人.
        String roleType = getRoleTypeByID().get(0).getRoleType();
        List<BdOperatePlanDeclarationFormMonth> result = queryAllInfoByID(monthEntity.getId().toString());
        String lineFormStatus = result.get(0).getLineFormStatus().toString();

        if (lineFormStatus.equals("1")) {
            return "线路负责人已审核,请勿重新审核,操作错误代码:514";
        }
        //只能线路负责人审核.或者访问时,没传入roleType
        if (!roleType.equals(LINESTAFFROLETYPE)) {
            return "只能线路负责人来审核月计划.请您更换用户账号后操作,操作错误代码:516";
        }
        //只有线路负责人可以审核,并且只需要线路负责人可以审核,新版流程图标注
        if (roleType.equals(LINESTAFFROLETYPE)) {
            return bdMapper.updateById(monthEntity) + "";
        }

        //如果账户没有设置角色类型,则提示如下.
        if (roleType.isEmpty()) {
            return "您的帐号没有任何角色类型,请系统联系管理员为您设置角色类型,操作错误代码:517";
        }
        return "操作错误代码:515";
    }

    /**
     * 驻班工程师申请结束流程接口.
     * <p>
     * 数据库辅助字段说明:
     * 其中判断线路负责人是否申请过,用数据库中line_form_status来区分. 线路负责人审批状态 0 未审批 1 通过 2 驳回
     * form_status是区分是否同意的. 0申请中 1 同意 2驳回 3草稿保存 4 取消（apply_form_status为0可以修改申请条目与状态）
     * apply_form_status是区分流程是结束. 0 申请中 1 流程结束
     *
     * @return 成功与结束
     */
    @Override
    public String endOfTheProcess(BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth) {
        //获取一行数据.
        List<BdqueryByID> query = queryByID(bdOperatePlanDeclarationFormMonth.getId().toString());
        //在数据行中获取下路负责人审核状态,并判断是否为 未审核 状态.
        Boolean lineFormStatus0 = query.get(0).getLineFormStatus().equals("0");
        //在数据行中获取下路负责人审核状态,并判断是否为 驳回 状态.
        Boolean lineFormStatus2 = query.get(0).getLineFormStatus().equals("2");
        //在数据行中获取下路负责人审核状态,并判断是否为 已同意 状态.
        Boolean lineFormStatus1 = query.get(0).getLineFormStatus().equals("1");
        if (lineFormStatus0) {
            return "线路负责人没有审核,不能申请流程结束.";
        }
        if (lineFormStatus2) {
            return "已经被线路负责人驳回,流程已经结束.";
        }
        if (lineFormStatus1) {
            return baseMapper.updateById(bdOperatePlanDeclarationFormMonth) + "";
        }
        return "申请状态异常.code:538";
    }

}
