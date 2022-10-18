package com.aiurt.modules.planMountFind.service;

import com.aiurt.modules.planMountFind.dto.*;
import com.aiurt.modules.planMountFind.entity.BdOperatePlanDeclarationFormMonth;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @Description: 月计划工具类接口
 * @Author: 沙雕国王
 * @Date: 2021-05-18
 * @Version: V1.0
 */
public interface IBdOperatePlanDeclarationFormMonthService extends IService<BdOperatePlanDeclarationFormMonth> {

    /**
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param PageNo     与分页信息
     * @param PageSize   与分页信息
     * @param page       与分页信息
     * @param roleType
     * @param staffID
     * @return Page<getAllByDateDTO> 返回的是根据条件查询的结果集
     */
    Page<getAllByDateDTO> listByDate(String start_time, String end_time, String line_id, Integer PageNo, Integer PageSize, Page page, String roleType, String staffID);



    /**
     * 生产计划管理-月施工计划定制-添加下月任务-作业类别-下拉框接口
     *
     * @return List<BdWorkTypeDTO> 返回作业类别下拉框查询结果
     */
    List<BdWorkTypeDTO> queryAllContruction();

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-施工负责人-下拉框接口
     *
     * @param team_id 当前人员的team_id
     * @return List<queryByTeamIdStaffDTO> 返回的是与当前人员一队的人员(施工负责人)
     */
    List<queryByTeamIdStaffDTO> queryByTeamIdStaff(String team_id);

    /**
     * 生产计划管理-月施工计划定制-添加下月任务-生产经理 与 线路负责人 -下拉框接口
     *
     * @param roleType 角色类型
     * @param deptId   ???忘了兄弟...
     * @return List<queryStaffsByRoleTypeDTO> 返回的是线路负责人,根绝roletype查询的.
     */
    List<queryStaffsByRoleTypeDTO> queryStaffsByRoleType(String roleType, String deptId);//

    /**
     * 查询站点信息.
     *
     * @return List<BdStationCopyDTO> 包含了两个列,站点名称和id
     */
    List<BdStationCopyDTO> queryAllStationInfo();//

    /**
     * 获取线路信息,返回线路id和name
     *
     * @return List<BdLineInfoDTO>
     */
    List<BdLineInfoDTO> getLineInfo();


    /**
     * 导出excel的接口
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param roleType   角色类型
     * @param staffID    登陆人id
     * @return 一个excel表格里面的数据
     */
    ModelAndView ExportExcel(String start_time, String end_time, String line_id, String roleType, String staffID);

    /**
     * 根据id查询
     *
     * @param id 数据行id
     * @return 返回一个list里面存了月计划表里的很多数据
     */
    List<BdqueryByID> queryByID(String id);

    /**
     * 审核按钮
     *
     * @param id           数据行id
     * @param formStatus   审核状态 0,1,2,3,4
     * @param roleType     角色类型,作为判断审核人员用
     * @param voice        录音,新版系统中,此字段暂时无用
     * @param picture      图片
     * @param changeReason 审核意见
     * @param remark       忘记这是啥了.
     * @return 一个对数据库产生几条影响的数字.<String>类型的哈.
     */
    String auditButton(String id, int formStatus, String roleType,
                       String voice, String picture, String changeReason, String remark);

    /**
     * 查询所有信息列,通过id
     *
     * @param id 月计划表行id
     * @return List<BdOperatePlanDeclarationFormMonth> 里面存储了一行月计划表信息.
     */
    List<BdOperatePlanDeclarationFormMonth> queryAllInfoByID(String id);

    /**
     * 获取sys_role表中的id根据role_name
     *
     * @param roleName
     * @return String类型的返回值
     */
    String getRoleType(String roleName);

    /**
     * @param roleName 获取当前登录人的角色类型相关信息.
     * @return 角色类型?
     */
    Integer getRoleId(String roleName);

    /**
     * 通过id获取staff表的role_id,通过role_id,获取role表里的type
     *
     * @return List<queryRoleTypeByIDDTO>
     */
    List<queryRoleTypeByIDDTO> getRoleTypeByID();

    /**
     * 审批查询接口
     *
     * @param start_time 开始时间
     * @param end_time   结束时间
     * @param line_id    线路id
     * @param PageNo     页码
     * @param PageSize   页面尺寸
     * @param page       页面信息
     * @param roleType   角色类型
     * @param staffID    当前登录人id
     * @return Page<getAllByDateDTO> 做好了分页,此接口用于前端界面的查询信息
     */
    Page<getAllByDateDTO> ApproveQuery(String start_time, String end_time, String line_id, Integer PageNo, Integer PageSize, Page page, String roleType, String staffID, String busId);

    /**
     * 取消计划按钮
     * @param userID     用户id,用于判断申请计划与取消计划,是否为同一人
     * @param formStatus 申请状态.
     * @param id         数据行id
     * @return 对数据库表修改的行数
     */
    Integer cancelButton(String userID, int formStatus, String id,String rejectedReason);

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
    String lineStaffAuditButton(BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth);

    /**
     * 驻班工程师申请结束流程接口.
     * @return 成功与结束
     */
    String endOfTheProcess(BdOperatePlanDeclarationFormMonth bdOperatePlanDeclarationFormMonth);
}
