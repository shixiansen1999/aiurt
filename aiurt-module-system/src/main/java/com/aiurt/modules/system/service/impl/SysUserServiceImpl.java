package com.aiurt.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.PasswordUtil;
import com.aiurt.common.util.UUIDGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.system.entity.*;
import com.aiurt.modules.system.mapper.*;
import com.aiurt.modules.system.model.SysUserSysDepartModel;
import com.aiurt.modules.system.service.ISysUserService;
import com.aiurt.modules.system.vo.SysUserDepVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.modules.base.service.BaseCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysDepartRoleUserMapper departRoleUserMapper;
    @Autowired
    private SysDepartRoleMapper sysDepartRoleMapper;
    @Resource
    private BaseCommonService baseCommonService;
    @Autowired
    private SysThirdAccountMapper sysThirdAccountMapper;
    @Autowired
    ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
    @Autowired
    ThirdAppDingtalkServiceImpl dingtalkService;

    @Autowired
    private CsUserDepartMapper csUserDepartMapper;
    @Autowired
    private CsUserStaionMapper csUserStaionMapper;
    @Autowired
    private CsUserMajorMapper csUserMajorMapper;
    @Autowired
    private CsUserSubsystemMapper csUserSubsystemMapper;

    @Lazy
    @Autowired
    private ICommonService commonService;


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword) {
        SysUser user = userMapper.getUserByName(username);
        String passwordEncode = PasswordUtil.encrypt(username, oldpassword, user.getSalt());
        if (!user.getPassword().equals(passwordEncode)) {
            return Result.error("旧密码输入错误!");
        }
        if (oConvertUtils.isEmpty(newpassword)) {
            return Result.error("新密码不允许为空!");
        }
        if (!newpassword.equals(confirmpassword)) {
            return Result.error("两次输入密码不一致!");
        }
        String password = PasswordUtil.encrypt(username, newpassword, user.getSalt());
        this.userMapper.update(new SysUser().setPassword(password), new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, user.getId()));
        return Result.ok("密码重置成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> changePassword(SysUser sysUser) {
        String salt = oConvertUtils.randomGen(8);
        sysUser.setSalt(salt);
        String password = sysUser.getPassword();
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
        sysUser.setPassword(passwordEncode);
        this.userMapper.updateById(sysUser);
        return Result.ok("密码修改成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        //1.删除用户
        this.removeById(userId);
        return false;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(String userIds) {
        //1.删除用户
        this.removeByIds(Arrays.asList(userIds.split(",")));
        return false;
    }

    @Override
    public SysUser getUserByName(String username) {
        return userMapper.getUserByName(username);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserWithRole(SysUser user, String roles) {
        this.save(user);
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void editUserWithRole(SysUser user, String roles) {
        this.updateById(user);
        //先删后加
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }


    @Override
    public List<String> getRole(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    @Override
    public Set<String> getUserRolesSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles);
    }

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    @Override
    public Set<String> getUserPermissionsSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username, null);
        for (SysPermission po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + (permissionSet == null ? 0 : permissionSet.size()));
        return permissionSet;
    }

    /**
     * 升级SpringBoot2.6.6,不允许循环依赖
     *
     * @param username
     * @return
     * @author:qinfeng
     * @update: 2022-04-07
     */
    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key = "#username")
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }

        //查询用户信息
        SysUser sysUser = userMapper.getUserByName(username);
        if (sysUser != null) {
            info.setSysUserCode(sysUser.getUsername());
            info.setSysUserName(sysUser.getRealname());
            info.setSysOrgCode(sysUser.getOrgCode());
        }

        //多部门支持in查询
        List<SysDepart> list = sysDepartMapper.queryUserDeparts(sysUser.getId());
        List<String> sysMultiOrgCode = new ArrayList<String>();
        if (list == null || list.size() == 0) {
            //当前用户无部门
            //sysMultiOrgCode.add("0");
        } else if (list.size() == 1) {
            sysMultiOrgCode.add(list.get(0).getOrgCode());
        } else {
            info.setOneDepart(false);
            for (SysDepart dpt : list) {
                sysMultiOrgCode.add(dpt.getOrgCode());
            }
        }
        info.setSysMultiOrgCode(sysMultiOrgCode);

        return info;
    }

    /**
     * 根据部门Id查询
     *
     * @param page
     * @param departId 部门id
     * @param username 用户账户名称
     * @return
     */
    @Override
    public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username) {
        return userMapper.getUserByDepId(page, departId, username);
    }

    @Override
    public IPage<SysUser> getUserByDepIds(Page<SysUser> page, List<String> departIds, String username) {
        return userMapper.getUserByDepIds(page, departIds, username);
    }

    @Override
    public Map<String, String> getDepNamesByUserIds(List<String> userIds) {
        List<SysUserDepVo> list = this.baseMapper.getDepNamesByUserIds(userIds);

        Map<String, String> res = new HashMap(5);
        list.forEach(item -> {
                    if (res.get(item.getUserId()) == null) {
                        res.put(item.getUserId(), item.getDepartName());
                    } else {
                        res.put(item.getUserId(), res.get(item.getUserId()) + "," + item.getDepartName());
                    }
                }
        );
        return res;
    }

    @Override
    public IPage<SysUser> getUserByDepartIdAndQueryWrapper(Page<SysUser> page, String departId, QueryWrapper<SysUser> queryWrapper) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = queryWrapper.lambda();

        lambdaQueryWrapper.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        lambdaQueryWrapper.inSql(SysUser::getId, "SELECT user_id FROM sys_user_depart WHERE dep_id = '" + departId + "'");

        return userMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUser userParams, IPage page) {
        List<SysUserSysDepartModel> list = baseMapper.getUserByOrgCode(page, orgCode, userParams);
        Integer total = baseMapper.getUserByOrgCodeTotal(orgCode, userParams);

        IPage<SysUserSysDepartModel> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(list);

        return result;
    }

    /**
     * 根据角色Id查询
     *
     * @param page
     * @param roleId   角色id
     * @param username 用户账户名称
     * @return
     */
    @Override
    public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username) {
        IPage<SysUser> userByRoleId = userMapper.getUserByRoleId(page, roleId, username);
        userByRoleId.getRecords().forEach(l -> {
            List<String> majorIds = csUserMajorMapper.getMajorIds(l.getId());
            l.setMajorIds(majorIds);
        });
        return userByRoleId;
    }


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, key = "#username")
    public void updateUserDepart(String username, String orgCode) {
        baseMapper.updateUserDepart(username, orgCode);
    }


    @Override
    public SysUser getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public SysUser getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserWithDepart(SysUser user, String selectedParts) {
//		this.save(user);  //保存角色的时候已经添加过一次了
        if (oConvertUtils.isNotEmpty(selectedParts)) {
            String[] arr = selectedParts.split(",");
            for (String deaprtId : arr) {
                SysUserDepart userDeaprt = new SysUserDepart(user.getId(), deaprtId);
                sysUserDepartMapper.insert(userDeaprt);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public void editUserWithDepart(SysUser user, String departs) {
        //更新角色的时候已经更新了一次了，可以再跟新一次
        this.updateById(user);
        String[] arr = {};
        if (oConvertUtils.isNotEmpty(departs)) {
            arr = departs.split(",");
        }
        //查询已关联部门
        List<SysUserDepart> userDepartList = sysUserDepartMapper.selectList(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
        if (userDepartList != null && userDepartList.size() > 0) {
            for (SysUserDepart depart : userDepartList) {
                //修改已关联部门删除部门用户角色关系
                if (!Arrays.asList(arr).contains(depart.getDepId())) {
                    List<SysDepartRole> sysDepartRoleList = sysDepartRoleMapper.selectList(
                            new QueryWrapper<SysDepartRole>().lambda().eq(SysDepartRole::getDepartId, depart.getDepId()));
                    List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                    if (roleIds != null && roleIds.size() > 0) {
                        departRoleUserMapper.delete(new QueryWrapper<SysDepartRoleUser>().lambda().eq(SysDepartRoleUser::getUserId, user.getId())
                                .in(SysDepartRoleUser::getDroleId, roleIds));
                    }
                }
            }
        }
        //先删后加
        sysUserDepartMapper.delete(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(departs)) {
            for (String departId : arr) {
                SysUserDepart userDepart = new SysUserDepart(user.getId(), departId);
                sysUserDepartMapper.insert(userDepart);
            }
        }
    }


    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(SysUser sysUser) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            result.error500("该用户不存在，请注册");
            baseCommonService.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        //update-begin---author:王帅   Date:20200601  for：if条件永远为falsebug------------
        if (CommonConstant.DEL_FLAG_1.equals(sysUser.getDelFlag())) {
            //update-end---author:王帅   Date:20200601  for：if条件永远为falsebug------------
            baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        return result;
    }

    @Override
    public List<SysUser> queryLogicDeleted() {
        return this.queryLogicDeleted(null);
    }

    @Override
    public List<SysUser> queryLogicDeleted(LambdaQueryWrapper<SysUser> wrapper) {
        if (wrapper == null) {
            wrapper = new LambdaQueryWrapper<>();
        }
        wrapper.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_1);
        return userMapper.selectLogicDeleted(wrapper);
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public boolean revertLogicDeleted(List<String> userIds, SysUser updateEntity) {
        String ids = String.format("'%s'", String.join("','", userIds));
        return userMapper.revertLogicDeleted(ids, updateEntity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeLogicDeleted(List<String> userIds) {
        String ids = String.format("'%s'", String.join("','", userIds));
        // 1. 删除用户
        int line = userMapper.deleteLogicDeleted(ids);
        // 2. 删除用户部门关系
        line += sysUserDepartMapper.delete(new LambdaQueryWrapper<SysUserDepart>().in(SysUserDepart::getUserId, userIds));
        //3. 删除用户角色关系
        line += sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
        //4.同步删除第三方App的用户
        try {
            dingtalkService.removeThirdAppUser(userIds);
            wechatEnterpriseService.removeThirdAppUser(userIds);
        } catch (Exception e) {
            log.error("同步删除第三方App的用户失败：", e);
        }
        //5. 删除第三方用户表（因为第4步需要用到第三方用户表，所以在他之后删）
        line += sysThirdAccountMapper.delete(new LambdaQueryWrapper<SysThirdAccount>().in(SysThirdAccount::getSysUserId, userIds));

        return line != 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNullPhoneEmail() {
        userMapper.updateNullByEmptyString("email");
        userMapper.updateNullByEmptyString("phone");
        return true;
    }

    @Override
    public void saveThirdUser(SysUser sysUser) {
        //保存用户
        String userid = UUIDGenerator.generate();
        sysUser.setId(userid);
        baseMapper.insert(sysUser);
        //获取第三方角色
        SysRole sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "third_role"));
        //保存用户角色
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(sysRole.getId());
        userRole.setUserId(userid);
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public List<SysUser> queryByDepIds(List<String> departIds, String username) {
        return userMapper.queryByDepIds(departIds, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SysUser user) {
        //step.1 保存用户
        this.save(user);
        String id = user.getId();
        //step.2 保存角色
        if (oConvertUtils.isNotEmpty(user.getRoleIds())) {
            for (String roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole(id, roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        //step.3 保存部门权限
        if (oConvertUtils.isNotEmpty(user.getDepartCodes())) {
            for (String departId : user.getDepartCodes()) {
                CsUserDepart csUserDepart = new CsUserDepart();
                csUserDepart.setUserId(id);
                csUserDepart.setDepartId(departId);
                csUserDepartMapper.insert(csUserDepart);
            }
        }
        //step.4 保存所属站所
        if (oConvertUtils.isNotEmpty(user.getStationIds())) {
            for (String stationId : user.getStationIds()) {
                CsUserStaion csUserStaion = new CsUserStaion();
                csUserStaion.setUserId(id);
                csUserStaion.setStationId(stationId);
                csUserStaionMapper.insert(csUserStaion);
            }
        }
        //step.5 保存专业
        if (oConvertUtils.isNotEmpty(user.getMajorIds())) {
            for (String majorId : user.getMajorIds()) {
                CsUserMajor csUserMajor = new CsUserMajor();
                csUserMajor.setUserId(id);
                csUserMajor.setMajorId(majorId);
                csUserMajorMapper.insert(csUserMajor);
            }
        }
        //step.6 保存子系统
        if (oConvertUtils.isNotEmpty(user.getSystemCodes())) {
            for (String systemId : user.getSystemCodes()) {
                CsUserSubsystem csUserSubsystem = new CsUserSubsystem();
                csUserSubsystem.setUserId(id);
                csUserSubsystem.setSystemId(systemId);
                csUserSubsystemMapper.insert(csUserSubsystem);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public void editUser(SysUser user) {
        //step.1 修改用户基础信息
        SysDepart sysDepart= sysDepartMapper.selectById(user.getOrgId());
        sysDepart = Optional.ofNullable(sysDepart).orElse(new SysDepart());
        user.setOrgCode(sysDepart.getOrgCode());
        user.setOrgName(sysDepart.getDepartName());
        this.updateById(user);
        String id = user.getId();
        //step.2 修改角色
        if (oConvertUtils.isNotEmpty(user.getRoleIds())) {
            //处理用户角色 先删后加
            sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));
            for (String roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        //step.3 修改部门权限
        if (oConvertUtils.isNotEmpty(user.getDepartCodes())) {
            //查询已关联部门
            List<SysUserDepart> userDepartList = sysUserDepartMapper.selectList(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
            if (userDepartList != null && userDepartList.size() > 0) {
                for (SysUserDepart depart : userDepartList) {
                    //修改已关联部门删除部门用户角色关系
                    if (!Arrays.asList(user.getDepartCodes()).contains(depart.getDepId())) {
                        List<SysDepartRole> sysDepartRoleList = sysDepartRoleMapper.selectList(
                                new QueryWrapper<SysDepartRole>().lambda().eq(SysDepartRole::getDepartId, depart.getDepId()));
                        List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                        if (roleIds != null && roleIds.size() > 0) {
                            departRoleUserMapper.delete(new QueryWrapper<SysDepartRoleUser>().lambda().eq(SysDepartRoleUser::getUserId, user.getId())
                                    .in(SysDepartRoleUser::getDroleId, roleIds));
                        }
                    }
                }
            }
            //先删后加
            csUserDepartMapper.delete(new QueryWrapper<CsUserDepart>().lambda().eq(CsUserDepart::getUserId, user.getId()));
            for (String departId : user.getDepartCodes()) {
                CsUserDepart csUserDepart = new CsUserDepart();
                csUserDepart.setUserId(id);
                csUserDepart.setDepartId(departId);
                csUserDepartMapper.insert(csUserDepart);
            }
        }

        //step.4 修改所属站所
        if (oConvertUtils.isNotEmpty(user.getStationIds())) {
            //先删后加
            csUserStaionMapper.delete(new QueryWrapper<CsUserStaion>().lambda().eq(CsUserStaion::getUserId, user.getId()));
            for (String stationId : user.getStationIds()) {
                CsUserStaion csUserStaion = new CsUserStaion();
                csUserStaion.setUserId(id);
                csUserStaion.setStationId(stationId);
                csUserStaionMapper.insert(csUserStaion);
            }
        }

        //step.5 修改专业
        if (oConvertUtils.isNotEmpty(user.getMajorIds())) {
            //先删后加
            csUserMajorMapper.delete(new QueryWrapper<CsUserMajor>().lambda().eq(CsUserMajor::getUserId, user.getId()));
            for (String majorId : user.getMajorIds()) {
                CsUserMajor csUserMajor = new CsUserMajor();
                csUserMajor.setUserId(id);
                csUserMajor.setMajorId(majorId);
                csUserMajorMapper.insert(csUserMajor);
            }
        }

        //step.6 修改子系统
        if (oConvertUtils.isNotEmpty(user.getSystemCodes())) {
            //先删后加
            csUserSubsystemMapper.delete(new QueryWrapper<CsUserSubsystem>().lambda().eq(CsUserSubsystem::getUserId, user.getId()));
            for (String systemId : user.getSystemCodes()) {
                CsUserSubsystem csUserSubsystem = new CsUserSubsystem();
                csUserSubsystem.setUserId(id);
                csUserSubsystem.setSystemId(systemId);
                csUserSubsystemMapper.insert(csUserSubsystem);
            }
        }
        //step.7 修改手机号和邮箱
        // 更新手机号、邮箱空字符串为 null
        userMapper.updateNullByEmptyString("email");
        userMapper.updateNullByEmptyString("phone");

    }

    @Override
    public List<String> userIdToUsername(Collection<String> userIdList) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getId, userIdList);
        List<SysUser> userList = super.list(queryWrapper);
        return userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<Object> departAndUserTree() {
        List<SysDepart> sysDeparts = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, 0));
        List<Object> departAndUserTrees = new ArrayList<>();
        sysDeparts.forEach(s -> {
            Map<String, Object> map = new LinkedHashMap<>();
            if (s.getParentId().equals("")) {
                map.put("id", s.getId());
                map.put("parentId", s.getParentId());
                map.put("departName", s.getDepartName());
                map.put("orgCode", s.getOrgCode());
                map.put("departOrder", s.getDepartOrder());
                map.put("status", s.getStatus());
                map.put("children", menuChild(s.getId()));
                departAndUserTrees.add(map);
            }
        });
        return departAndUserTrees;
    }

    @Override
    public IPage<SysUser> userByOrgCode(Page<SysUser> page, String orgCode, String phone, String realname, String username, Integer status) {
        List<String> orgId = new ArrayList<>();
        if (orgCode != "" && orgCode != null) {
            String code = "/" + orgCode + "/";
            List<SysDepart> sysDeparts = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepart>().like(SysDepart::getOrgCodeCc, code));
            sysDeparts.forEach(s -> orgId.add(s.getId()));
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        IPage<SysUser> users = baseMapper.queryByorgIds(page, orgId, phone, realname, username, status, sysUser.getUsername());
        return users;
    }

    public List<Object> menuChild(String id) {
        List<SysDepart> sysDeparts = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getParentId, id).eq(SysDepart::getDelFlag, 0));
        List<Object> trees = new ArrayList<>();
        sysDeparts.forEach(s -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("parentId", s.getParentId());
            map.put("departName", s.getDepartName());
            map.put("orgCode", s.getOrgCode());
            map.put("departOrder", s.getDepartOrder());
            map.put("status", s.getStatus());
            map.put("children", menuChild(s.getId()));
            trees.add(map);
        });
        return trees;
    }

    @Override
    public List<SysUser> queryManageUser() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(loginUser)) {
            log.info("当前用户没登录");
            return Collections.emptyList();
        }
        String orgId = loginUser.getOrgId();
        if (StrUtil.isBlank(orgId)) {
            log.info("当前用户没绑定机构");
            return Collections.emptyList();
        }

        List<String> subDepIdList = sysDepartMapper.getSubDepIdsByDepId(orgId);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getOrgId, subDepIdList).notIn(SysUser::getId, Collections.singleton(loginUser.getId()))
                .eq(SysUser::getStatus, 1).eq(SysUser::getDelFlag, 0);
        List<SysUser> sysUsers = baseMapper.selectList(wrapper);

        return sysUsers;
    }

    @Override
    public List<SelectTable> queryManageDepartUserTree() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(loginUser)) {
            log.info("当前用户没登录");
            return Collections.emptyList();
        }
        String orgId = loginUser.getOrgId();
        if (StrUtil.isBlank(orgId)) {
            log.info("当前用户没绑定机构");
            return Collections.emptyList();
        }

        List<String> subDepIdList = sysDepartMapper.getSubDepIdsByDepId(orgId);

        List<SelectTable> selectTables = commonService.queryDepartUserTree(subDepIdList, loginUser.getId());
        return selectTables;
    }

    @Override
    public List<SysUser> querySysUserForWorkTicket() {
        List<String> role = new ArrayList<>();
        List<SysUser> sysUserList = baseMapper.querySysUserForWorkTicket(role);
        return sysUserList;
    }
}
