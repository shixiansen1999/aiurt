package com.aiurt.boot.weeklyplan.service;

import com.aiurt.boot.weeklyplan.dto.*;
import com.aiurt.boot.weeklyplan.entity.BdOperatePlanDeclarationForm;
import com.aiurt.boot.weeklyplan.entity.BdOperatePlanStateChange;
import com.aiurt.boot.weeklyplan.entity.BdStation;
import com.aiurt.modules.train.task.dto.UserDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.lettuce.core.dynamic.annotation.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SysUserRoleModel;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 周计划表
 * @Author: Lai W.
 * @Version: 1.0
 */
public interface IBdOperatePlanDeclarationFormService extends IService<BdOperatePlanDeclarationForm> {

    /**
     * 获取施工类型
     * @return List of Construction Type DTO.
     */
    List<BdConstructionTypeDTO> getConstructionType();

    /**
     * 通过teamId获取组内和管辖组内成员
     * @param teamId Team ID.
     * @return List of Staff Info RetrunType DTO.
     */
    List<BdStaffInfoReturnTypeDTO> getMemberByTeamId(String teamId);

    /**
     * 通过toleType和deptID获取用户信息
     * @param roleType  Role type.
     * @param deptId Department ID.
     * @return List of Staff Info ReturnType DTO.
     */
    List<BdStaffInfoReturnTypeDTO> getStaffsByRoleType(String roleType, String deptId);

    /**
     * 通过 角色 和deptID获取用户信息
     * @param roleName
     * @param deptId
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> getStaffsByRoleName(@Param("roleName") String roleName,@Param("deptId")  String deptId);

    /**
     * 通过teamId获取车站列表
     * @param teamId Team ID.
     * @return List of Station Info ReturnType DTO.
     */
    List<BdStationReturnTypeDTO> getStationList(Integer teamId);

    /**
     * 将传入的requestBody增加参数和条件判断.
     * @param declarationForm Entity
     * @return BdOperatePlanDeclarationForm Entity.
     */
    BdOperatePlanDeclarationForm convertRequestBody(BdOperatePlanDeclarationForm declarationForm);

    /**
     * 获取所有地铁线路.
     * @return List of Lines DTO.
     */
    List<BdLineDTO> getLines();

    /**
     * 根据id查询计划表信息.
     * @param id id.
     * @return Bd return type DTO.
     */
    BdOperatePlanDeclarationFormReturnTypeDTO getFormInfoById (Integer id);

    /**
     * 根据id查询用户信息
     * @param id the ID of the User.
     * @return BdUserInfoDTO.
     */
//    List<BdUserInfoDTO> getUserInfo(String id);
    BdUserInfoDTO getUserInfo();

    /**
     * 获取所有车站.
     * @return List of all Stations.
     */
    List<BdStation> getStations();


    /**
     * 检查施工负责人时间是否冲突.
     * @param declarationForm
     * @return
     */
    Boolean checkChargeStaffIfConflict(BdOperatePlanDeclarationForm declarationForm);

    /**
     * 检查计划表是否已经被更改过
     * @param declarationForm the declaration Form.
     * @return true/false for result.
     */
    Boolean checkFormIfEdited(BdOperatePlanDeclarationForm declarationForm);

    /**
     * 分页列表查询改进版.
     * @param queryPagesParams
     * @param pageNo
     * @param pageSize
     * @param busId
     * @return
     */
    Page<BdOperatePlanDeclarationFormReturnTypeDTO> queryPages(QueryPagesParams queryPagesParams,
                                                               Integer pageNo, Integer pageSize, String busId);
  /**
     * 分页列表查询改进版.
     * @param dto 参数DTO.
     * @return Current page.
     */
    List<ProductPlanDTO> queryAllProductPlan( ProductPlanDTO dto);

    /**
     * 根据条件查询周计划，不分页
     * 导出Excel用
     * @param queryPagesParams 参数DTO。
     * @return List of results.
     */
    List<BdOperatePlanDeclarationFormReturnTypeDTO> getListByQuery(QueryPagesParams queryPagesParams);

    /**
     * 导出周计划表
     * @param record 查询出来的数据
     * @param response
     * @param queryPagesParams
     */
    void exportExcel(List<BdOperatePlanDeclarationFormReturnTypeDTO> record,
                             HttpServletResponse response, QueryPagesParams queryPagesParams);

    /**
     * 导出变更计划表.
     * @param record
     * @param response
     * @param queryPagesParams
     */
    void exportExcelChangeable(List<BdOperatePlanDeclarationFormReturnTypeDTO> record,
                               HttpServletResponse response, QueryPagesParams queryPagesParams);

    /**
     * 导入
     * @param excel
     * @return
     */
    List<BdOperatePlanDeclarationForm> importExcel(MultipartFile excel);

    /**
     * 审批当前计划表
     * @param voice 录音路径
     * @param picture 图片路径
     * @param bdOperatePlanStateChange 变更计划表
     * @return Result.
     */
    Result<?> updateOperateForm(String voice, String picture, BdOperatePlanStateChange bdOperatePlanStateChange);

    /**
     * 获取可能的状态信息.
     * @param isAdditional 是否是补充计划.
     * @return Result.
     */
    List<FormStatusTup> getStatus(Integer isAdditional);

    /**
     * 审批结束后确认流程是否结束.
     *
     * @param id
     * @param applyFormStatus 流程状态.
     * @return Result.
     */
    Result<?> setApplyFormStatus(String id, Integer applyFormStatus);


    /**
     * 修改
     * @param bdOperatePlanDeclarationForm
     */
    void edit(BdOperatePlanDeclarationForm bdOperatePlanDeclarationForm);

    /**
     * 查找线路负责人
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> queryLineStaff();

    /**
     * 重新申请
     * @param id
     */
    void reapply(Integer id);

    /**
     * 周计划表-是否有权限审批
     * @param id
     * @return
     */
    IsApproveDTO isApprove(Integer id);

    /**
     * 删除
     * @param id
     */
    void removeById(String id);


    /**
     * 根据作业时间查询已同意的周计划.
     * @param taskDate
     * @return
     */
    List<BdOperatePlanDeclarationReturnDTO> queryListByDate(String taskDate);

    /**
     * 通过角色标识获取用户
     * @param roleCode
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> getStaffsByRoleCode(String roleCode);

    /**
     * 查询 角色是 工班长、助班工程师、工作负责人 的人员
     *
     * @return
     */
    List<SysUserRoleModel> queryUserByTeamRole();
}
