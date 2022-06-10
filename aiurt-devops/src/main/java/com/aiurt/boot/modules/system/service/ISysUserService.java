package com.aiurt.boot.modules.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.aiurt.boot.modules.statistical.vo.StaffDataVo;
import com.aiurt.boot.modules.statistical.vo.UserScheduleVo;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.model.SysUserSysDepartModel;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.system.vo.SysUserCacheInfo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.system.model.DepartScheduleModel;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author swsc
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 重置密码
     *
     * @param username
     * @param oldpassword
     * @param newpassword
     * @param confirmpassword
     * @return
     */
    public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

    /**
     * 修改密码
     *
     * @param sysUser
     * @return
     */
    public Result<?> changePassword(SysUser sysUser);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    public boolean deleteUser(String userId);

    /**
     * 批量删除用户
     *
     * @param userIds
     * @return
     */
    public boolean deleteBatchUsers(String userIds);

    public SysUser getUserByName(String username);

    SysUser getUserByAccountAndOrgcode(String account, String orgCode);

    public List<SysUser> findUserByAccount(String account);

    public String getRoleCodeByName(String username);

    /**
     * 添加用户和用户角色关系
     *
     * @param user
     * @param roles
     */
    public void addUserWithRole(SysUser user, String roles);


    /**
     * 修改用户和用户角色关系
     *
     * @param user
     * @param roles
     */
    public void editUserWithRole(SysUser user, String roles);

    /**
     * 获取用户的授权角色
     *
     * @param username
     * @return
     */
    public List<String> getRole(String username);

    /**
     * 查询用户信息包括 部门信息
     *
     * @param username
     * @return
     */
    public SysUserCacheInfo getCacheUser(String username);

    /**
     * 根据部门Id查询
     *
     * @param
     * @return
     */
    public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username);

    /**
     * 根据部门 Id 和 QueryWrapper 查询
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    public IPage<SysUser> getUserByDepartIdAndQueryWrapper(Page<SysUser> page, String departId, QueryWrapper<SysUser> queryWrapper);

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     *
     * @param orgCode
     * @param userParams 用户查询条件，可为空
     * @param page       分页参数
     * @return
     */
    IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUser userParams, IPage page);

    /**
     * 根据角色Id查询
     *
     * @param
     * @return
     */
    public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username);

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    Set<String> getUserRolesSet(String username);

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    Set<String> getUserPermissionsSet(String username);

    /**
     * 根据用户名设置部门ID
     *
     * @param username
     * @param orgCode
     */
    void updateUserDepart(String username, String orgCode);

    /**
     * 根据手机号获取用户名和密码
     */
    public SysUser getUserByPhone(String phone);

    /**
     * 根据手机号查询或者注册用户
     */
    public SysUser registerUserOrSelectUser(String phone);


    /**
     * 根据邮箱获取用户
     */
    public SysUser getUserByEmail(String email);


    /**
     * 添加用户和用户部门关系
     *
     * @param user
     * @param selectedParts
     */
    void addUserWithDepart(SysUser user, String selectedParts);

    /**
     * 编辑用户和用户部门关系
     *
     * @param user
     * @param departs
     */
    void editUserWithDepart(SysUser user, String departs);

    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    Result checkUserIsEffective(SysUser sysUser);

    /**
     * 查询用户全部信息
     *
     * @param userId
     * @return
     */
    SysUser getUserAllInfoByUserId(String userId);


    List<SysUser> findUserListByUserId(String userId);

    /**
     * 用户是否能够查看意见
     *
     * @param userIds
     * @param isBackipinion
     * @return
     */
    List<SysUser> backipinionNoUser(List<String> userIds, Integer isBackipinion);

    SysUser getOtherInfo(String userId);

    public String getMaxUserName(String pinyin);

    public boolean isAdmin(String id);

    public SysUser getUserBySchoolCode(String schoolCode);

    void importUserExcel(List<Map<Integer, String>> userData, HttpServletRequest request);

    List<SysUser> selectUsersByRoleCode(String roleCode);

    public List<SysUser> selectUserByRoleAndDepartment(String roleCode, String department, String userName);

    /**
    * 批量选择用户数据权限
     * @param ids
     * @param departmentIds
     * @param systemCodes
     */
    void updateBatchUsersPermission(List<String> ids, List<String> departmentIds, List<String> systemCodes);

    /**
     * @param orgIdList:
     * @Description: 根据班组和日期查询当班用户
     * @author: niuzeyu
     * @date: 2022/1/19 10:44
     * @Return: java.util.List<com.swsc.copsms.modules.system.model.UserScheduleModel>
     */
    List<DepartScheduleModel> selectUserScheduleByOrgIdsAndDate(List<String> orgIdList, String date);

    /**
     * 大屏-人员统计
     */
    Integer getTotalNum(Map map);

    List<SysUser> getBanZhangByBanZu(String orgId);
    List<UserScheduleVo> getUserByBanZu(String orgId);
    List<StaffDataVo> getStaffData(Map map);
    List<String> getBanzuListByLine(Integer lineId);

    /**
     * 根据用户id获取用户角色
     * @param id
     * @return
     */
    List<String> getRoleCodeById(String id);

    String getUserIdByUsername(String username);

    List<SysUser> getUsersByOrgCode(String orgCode);

    List<SysUser> getSysUsersByLineCodeAndOrgId(String lineCode, String orgId);
}
