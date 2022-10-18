package com.aiurt.modules.planMountFind.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.planMountFind.entity.BdOperatePlanDeclarationFormMonth;
import com.aiurt.modules.planMountFind.service.IBdOperatePlanDeclarationFormMonthService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description: 控制器 中的代码是 生产计划管理 中的 月计划 相关功能.
 * @author: Sand Sculpture King
 * @date: 2021年5月26日 19点34分
 * <p>
 * 广告时间到:
 * <p>
 * 我劝你,如果这个模块没有bug,别往下看了
 * 因为里面的智慧结晶,是很难参透的.
 * 我已经记不清上一个看懂我代码的人的模样了.
 * 那么问题来了,如果一个人把自己昨天拉出来的东西,在今天又呕吐出来,这个过程是不是叫?反编译:逆源;
 * Jetbrain idea有这个本事!
 */

@Api(tags = "月计划crud")
@RestController
@RequestMapping("/planMountFind/bdOperatePlanDeclarationFormMonth")
@Slf4j
public class BdOperatePlanDeclarationFormMonthController extends BaseController<BdOperatePlanDeclarationFormMonth, IBdOperatePlanDeclarationFormMonthService> {
    @Autowired
    private IBdOperatePlanDeclarationFormMonthService bdOperatePlanDeclarationFormMonthService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param bdOperatePlanDeclarationFormMonth 实体.还是他.
     * @param pageNo                            分页信息,页码
     * @param pageSize                          页面尺寸
     * @param req                               用于接收请求信息的req
     * @return 月计划表里的数据.
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-分页列表查询")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-分页列表查询", notes = "bd_operate_plan_declaration_form_month-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth,
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(name = "start_time", defaultValue = "1900-01-01") String start_time,
            @RequestParam(name = "end_time", defaultValue = "2099-01-01") String end_time,
            @RequestParam(name = "line_id", defaultValue = "-1") String line_id,
            @RequestParam(name = "roleType", defaultValue = "5") String roleType,
            @RequestParam(name = "staffID", defaultValue = "-1") String staffID,
            HttpServletRequest req) {

        // 当前登录用户id
        // staffID = TokenUtils.getLoginUser().getId();
        // 配置分页
        Page<BdOperatePlanDeclarationFormMonth> page = new Page<>(pageNo, pageSize);
        return Result.OK(bdOperatePlanDeclarationFormMonthService.listByDate(start_time,
                end_time, line_id, pageNo, pageSize, page,
                roleType, staffID

        ));

    }

    /**
     * 添加
     *
     * @param bdOperatePlanDeclarationFormMonth 用于接收添加信息的实体. (怎么这哥们那都能用到?)
     * @return 返回是否添加成功
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-添加")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-添加", notes = "bd_operate_plan_declaration_form_month-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //此处再老变电中直接set实体参数.此逻辑仿照老变电里逻辑.
        Date applyDate = new Date();
        //0 申请中 1 流程结束
        bdOperatePlanDeclarationFormMonth.setApplyFormStatus(0);
        //线路负责人审批状态
        bdOperatePlanDeclarationFormMonth.setLineFormStatus(0);
        //生产调度审批状态
        bdOperatePlanDeclarationFormMonth.setDispatchFormStatus(0);
        //老程序为3.但是3是保存草稿,因为月计划里根本没找到草稿功能,所以,直接默认保存即申请 0 .
        bdOperatePlanDeclarationFormMonth.setFormStatus(0);
        //申请时间
        bdOperatePlanDeclarationFormMonth.setDateTime(new Timestamp(applyDate.getTime()));
        bdOperatePlanDeclarationFormMonth.setApplyStaffId(sysUser.getId());
        //以下语句为jeecg-boot自动生成
        bdOperatePlanDeclarationFormMonthService.save(bdOperatePlanDeclarationFormMonth);

        //发送消息 (暂无规划)
        /*String toUser = sysBaseAPI.getUserById(bdOperatePlanDeclarationFormMonth.getLineStaffId()).getUsername();
        BusMessageDTO busMessageDTO = new BusMessageDTO();
        busMessageDTO.setBusType("operatePlanMonth");
        busMessageDTO.setBusId(String.valueOf(bdOperatePlanDeclarationFormMonth.getId()));
        busMessageDTO.setFromUser(sysUser.getUsername());
        busMessageDTO.setToUser(toUser);
        busMessageDTO.setTitle("生产计划");
        busMessageDTO.setCategory("3");
        busMessageDTO.setContent("你有新的待审批月计划");
        busMessageDTO.setAnnouncementTypeId(17);
        sysBaseAPI.sendBusAnnouncement(busMessageDTO);

        busMessageDTO.setAnnouncementTypeId(18);
        sysBaseAPI.sendBusAnnouncement(busMessageDTO);*/
        return Result.OK("添加成功！");
    }

    /**
     * 编辑, 月计划表.
     *
     * @param bdOperatePlanDeclarationFormMonth 用于接收参数的实体.
     * @return 是否编辑成功.
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-编辑")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-编辑", notes = "bd_operate_plan_declaration_form_month-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth) {
        bdOperatePlanDeclarationFormMonthService.updateById(bdOperatePlanDeclarationFormMonth);
        return Result.OK("编辑成功!");

    }

    /**
     * 通过id删除
     *
     * @param id 数据行id ,月计划表里的.
     * @return 返回是否删除成功.
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-通过id删除")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-通过id删除", notes = "bd_operate_plan_declaration_form_month-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        bdOperatePlanDeclarationFormMonthService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除 ,在这里估计是用不上这个方法了.
     *
     * @param ids 传入的id们.大概有很多个id一起传进来.
     * @return 返回是否批量删除成功.
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-批量删除")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-批量删除", notes = "bd_operate_plan_declaration_form_month-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bdOperatePlanDeclarationFormMonthService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出excel
     *
     * @param bdOperatePlanDeclarationFormMonth 一个实体,用于接收参数的
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth,
                                  @RequestParam(name = "lineID", defaultValue = "1") String line_id,
                                  @RequestParam(name = "beginDate", defaultValue = "1990-01-01") String start_time,
                                  @RequestParam(name = "endDate", defaultValue = "2099-01-01") String end_time,
                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
                                  @RequestParam(name = "roleType", defaultValue = "-1") String roleType,
                                  @RequestParam(name = "staffID", defaultValue = "-1") String staffID
    ) {

        //前端如果传入的开始时间为空字符,会覆盖掉defaultValue的默认值.所以在这里判断是否为空值,如果为空,并且赋值
        Boolean startTimeIsEmpty = start_time.equals("") || start_time.isEmpty();
        if (startTimeIsEmpty) {
            start_time = "1900-01-01";
        }
        //前端如果传入的结束时间为空字符,会覆盖掉defaultValue的默认值.所以在这里判断是否为空值,如果为空,并且赋值
        Boolean endTimeIsEmpty = end_time.isEmpty() || end_time.equals("");
        if (endTimeIsEmpty) {
            end_time = "2100-01-01";
        }
        return bdOperatePlanDeclarationFormMonthService.ExportExcel(start_time, end_time, line_id, roleType, staffID);
    }

    /**
     * 此方法暂时没用过->通过excel导入数据,
     * 但是后续项目功能升级,或者是甲方要求加一个功能,
     * 直接开箱即用
     * <p>
     * 通过excel导入数据
     *
     * @param request  请求信息
     * @param response 相应信息
     * @return 返回导入是否成功.
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BdOperatePlanDeclarationFormMonth.class);
    }

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-作业类别-下拉框接口
     *
     * @return 作业类别-下拉框接口
     */
    @GetMapping("/queryAllContruction")
    public Result<?> queryAllContruction() {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.queryAllContruction());
    }

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-施工负责人-下拉框接口
     *
     * @param request 获取了传入的team_id,这种写法,在生成javadoc注释的时候,是不会自动生成参数的.
     * @return 施工负责人-下拉框接口
     */
    @GetMapping("/queryStaffByTeamId")
    public Result<?> queryByTeamIdStaff(HttpServletRequest request) {
        String team_id = request.getParameter("team_id");
        return Result.OK(bdOperatePlanDeclarationFormMonthService.queryByTeamIdStaff(team_id));
    }

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-线路负责人-下拉框接口
     *
     * @param roleType 角色类型
     * @param deptId   ?
     * @return 返回线路负责人信息.包含id和名字.
     */
    @GetMapping("/queryStaffsByRoleType")
    public Result<?> queryStaffsByRoleType(@RequestParam(value = "roleType", defaultValue = "-1") String roleType,
                                           @RequestParam(value = "deptId", defaultValue = "0") String deptId) {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.queryStaffsByRoleType(roleType, deptId));
    }


    /**
     * 查询所有车站id,name,line_id
     *
     * @return 查询所有车站id, name, line_id
     */
    @GetMapping("/queryAllStationInfo")
    public Result<?> queryAllStationInfo() {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.queryAllStationInfo());
    }

    /**
     * 登录人TeamID信息接口
     *
     * @return 登录人TeamID信息接口
     */
    @GetMapping("/getTeamId")
    public Result<?> getTeamId() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return Result.OK(sysUser.getOrgId());
    }


    /**
     * 线路信息接口,此接口返回线路信息中站点名,站点id两个字段
     *
     * @return 返回线路信息中站点名, 站点id两个字段
     */
    @GetMapping("/getLineInfo")
    public Result<?> getLineInfo() {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.getLineInfo());
    }


    /**
     * 通过id查询
     *
     * @param id 数据行id
     * @return 返回一行月计划表中的数据
     */
    @GetMapping("/queryById")
    public Result<?> queryByID(@RequestParam(name = "id") String id) {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.queryByID(id));
    }

    /**
     * 审核按钮
     *
     * @param id           数据行id
     * @param formStatus   表单状态   0申请中 1 同意 2驳回 3草稿保存（apply_form_status为0可以修改申请条目与状态）
     * @param roleType     role角色表 中的 角色 类型
     * @param voice        前端点击同意同 此字段 传入 1
     * @param picture      图片
     * @param changeReason changeReason 测试   这就应该是意见了
     * @param remark       并没有传入值.
     * @return 返回的数据与分页查询类似.
     */
    @GetMapping("/auditButton")
    public Result<?> auditButton(
            @RequestParam(value = "id", defaultValue = "0") String id,
            @RequestParam(value = "formStatus", defaultValue = "0") int formStatus,
            @RequestParam(value = "roleType", defaultValue = "0") String roleType,
            @RequestParam(value = "voice", defaultValue = "0") String voice,
            @RequestParam(value = "picture", defaultValue = "无") String picture,
            @RequestParam(value = "rejectedReason", defaultValue = "0") String changeReason,
            @RequestParam(value = "remark", defaultValue = "0") String remark
    ) {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.auditButton(id, formStatus, roleType, voice, picture, changeReason, remark));
    }

    /**
     * 测试   获取type表里的remake列值
     *
     * @return 角色类型
     */
    @GetMapping("/getRoleType")
    public Result<?> getRoleType() {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.getRoleId("经理"));
    }


    /**
     * 获取当前登录人roletype
     *
     * @return 当前登录人的角色类型
     */
    @GetMapping("/getRoleTypeByID")
    public Result<?> getRoleTypeByID() {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.getRoleTypeByID());
    }

    /**
     * 返还当前登录人员部门信息
     *
     * @return 返回登陆人的部门的信息
     */
    @GetMapping("/getLoginUserDepartmentInfo")
    public Result<?> getLoginUserDepartmentInfo() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return Result.OK(sysUser.getDepartIds());
    }

    /**
     * 获取
     *
     * @return 当前登陆人id
     */
    @GetMapping("/getUserID")
    public Result<?> getUserID() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return Result.OK(sysUser.getId());
    }

    /**
     * 审批查询接口
     *
     * @param bdOperatePlanDeclarationFormMonth 实体
     * @param pageNo                            页码
     * @param pageSize                          页面尺寸
     * @param start_time                        开始时间
     * @param end_time                          结束时间
     * @param line_id                           线路id
     * @param roleType                          角色类型
     * @param staffID                           当前登录人id
     * @return 月计划表中的数据, 以分页
     */
    @AutoLog(value = "bd_operate_plan_declaration_form_month-分页列表查询")
    @ApiOperation(value = "bd_operate_plan_declaration_form_month-分页列表查询", notes = "bd_operate_plan_declaration_form_month-分页列表查询")
    @GetMapping(value = "/ApproveQuery")
    public Result<?> ApproveQuery(
            BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth,
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(name = "start_time", defaultValue = "1900-01-01") String start_time,
            @RequestParam(name = "end_time", defaultValue = "2099-01-01") String end_time,
            @RequestParam(name = "line_id", defaultValue = "-1") String line_id,
            @RequestParam(name = "roleType", defaultValue = "5") String roleType,
            @RequestParam(name = "staffID", defaultValue = "-1") String staffID,
            @RequestParam(name = "busId", required = false) String busId
    ) {

        // staffID = TokenUtils.getLoginUser().getId();
        Page<BdOperatePlanDeclarationFormMonth> page = new Page<>(pageNo, pageSize);
        return Result.OK(bdOperatePlanDeclarationFormMonthService.ApproveQuery(start_time,
                end_time, line_id, pageNo, pageSize, page,
                roleType, staffID, busId
        ));
    }

    /**
     * 取消按钮接口
     *
     * @param id         数据行id
     * @param formStatus 审核状态 0,1,2,3,4
     * @return 返回取消是否成功.
     */
    @GetMapping(value = "/cancelButton")
    public Result<?> cancelButton(@RequestParam(value = "id", defaultValue = "0") String id,
                                  @RequestParam(value = "formStatus", defaultValue = "0") int formStatus,
                                  @RequestParam(value = "rejectedReason", defaultValue = "空") String rejectedReason
    ) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userID = sysUser.getId();
        int status = bdOperatePlanDeclarationFormMonthService.cancelButton(userID, formStatus, id, rejectedReason);
        if (status == 0) {
            return Result.error("取消失败.code:639");
        }
        if (status == -1) {
            return Result.error("请使用申请人账号取消此计划.code:640");
        }
        if (status == -2) {
            return Result.error("无此行相关数据.code:641");
        }
        if (status == -3) {
            return Result.error("线路负责人已经审核,无法取消.code:642");
        }
        return Result.OK("取消成功");
    }

    /**
     * 新审核按钮
     *
     * @param bdOperatePlanDeclarationFormMonth 接收参数的实体
     * @return 成功与失败
     */
    @PutMapping(value = "/auditBT")
    public Result<?> auditBT(@RequestBody BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth) {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.lineStaffAuditButton(bdOperatePlanDeclarationFormMonth));
    }

    /**
     * 驻班工程师申请结束流程接口.
     * @return 成功与结束
     */
    @PutMapping(value = "/endOfTheProcess")
    public Result<?> endOfTheProcess(@RequestBody BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth) {
        return Result.OK(bdOperatePlanDeclarationFormMonthService.endOfTheProcess(bdOperatePlanDeclarationFormMonth));
    }
}

