package com.aiurt.boot.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.StatusEnum;
import com.aiurt.boot.common.system.base.service.CryptoService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.boot.modules.statistical.vo.StaffDataVo;
import com.aiurt.boot.modules.statistical.vo.UserScheduleVo;
import com.aiurt.boot.modules.system.entity.*;
import com.aiurt.boot.modules.system.mapper.*;
import com.aiurt.boot.modules.schedule.model.SysUserScheduleModel;
import com.aiurt.boot.modules.system.model.SysUserSysDepartModel;
import com.aiurt.boot.common.constant.CacheConstant;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.system.vo.SysUserCacheInfo;
import org.jeecg.common.api.vo.Result;
import com.aiurt.boot.modules.system.model.DepartScheduleModel;
import com.aiurt.boot.modules.system.service.ISysDictService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import com.aiurt.boot.common.util.PasswordUtil;
import com.aiurt.boot.common.util.oConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: swsc
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
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private ISysDictService dictService;
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private ScheduleRecordMapper scheduleRecordMapper;
    @Autowired
    private ILineService lineService;

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
        //2.删除用户部门关联关系
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        query.eq(SysUserDepart::getUserId, userId);
        sysUserDepartMapper.delete(query);
        //3.删除用户角色关联关系
        //TODO
        return false;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(String userIds) {
        //1.删除用户
        this.removeByIds(Arrays.asList(userIds.split(",")));
        //2.删除用户部门关系
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        for (String id : userIds.split(",")) {
            query.eq(SysUserDepart::getUserId, id);
            this.sysUserDepartMapper.delete(query);
        }
        //3.删除用户角色关系
        //TODO
        return false;
    }

    @Override
    public SysUser getUserByName(String username) {
        SysUser user = userMapper.getUserByName(username);
        return user;
    }

    @Override
    public SysUser getUserByAccountAndOrgcode(String account, String orgCode) {

        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().eq("account", account).eq("org_code", orgCode));
        return user;
    }

    @Override
    public List<SysUser> findUserByAccount(String account) {
        List<SysUser> userList = userMapper.findUserByAccount(account);
        return userList;
    }

    @Override
    public String getRoleCodeByName(String username) {
        return userMapper.getRoleCodeByName(username);
    }


    @Override
    @Transactional
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
    @Transactional
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
        List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username);
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

    @Override
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
//		SysUser user = userMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());


        LoginUser user = sysBaseAPI.getUserByName(username);
        if (user != null) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());
        }

        //多部门支持in查询
        List<SysDepart> list = sysDepartMapper.queryUserDeparts(user.getId());
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

    // 根据部门Id查询
    @Override
    public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username) {
        return userMapper.getUserByDepId(page, departId, username);
    }

    @Override
    public IPage<SysUser> getUserByDepartIdAndQueryWrapper(Page<SysUser> page, String departId, QueryWrapper<SysUser> queryWrapper) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = queryWrapper.lambda();

        lambdaQueryWrapper.eq(SysUser::getDelFlag, "0");
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

    // 根据角色Id查询
    @Override
    public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username) {
        return userMapper.getUserByRoleId(page, roleId, username);
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
    public SysUser registerUserOrSelectUser(String phone) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("role_code", "common-user");
        SysRole role = sysRoleMapper.selectOne(wrapper);
        SysUser user = this.getUserByPhone(phone);
        if (user == null) {
            user = new SysUser();
            user.setUsername(phone);
            user.setPhone(phone);
            user.setPassword("123456");
            //设置创建时间
            user.setCreateTime(new Date());
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
            user.setPassword(passwordEncode);
            user.setStatus(1);
            user.setDelFlag("0");
            //user.setType(0);
            this.addUserWithRole(user, role.getId());
        }
        return user;
    }


    @Override
    public SysUser getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional
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
    @Transactional
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public void editUserWithDepart(SysUser user, String departs) {
        this.updateById(user);  //更新角色的时候已经更新了一次了，可以再跟新一次
        //先删后加
        sysUserDepartMapper.delete(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(departs)) {
            String[] arr = departs.split(",");
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
            sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        if (CommonConstant.DEL_FLAG_1.toString().equals(sysUser.getDelFlag())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        return result;
    }

    @Override
    public SysUser getUserAllInfoByUserId(String userId) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(SysUser::getId, userId)
        );
        //获取部门信息
        SysDepart sysDepart = sysDepartMapper.selectOne(
                new LambdaQueryWrapper<SysDepart>()
                        .eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(SysDepart::getOrgCode, user.getOrgCode())
        );
        if (ObjectUtil.isNotNull(sysDepart)) {
            //加入部门名称
            user.setOrgName(sysDepart.getDepartName());
            //加入部门ID
//            user.setOrgId(sysDepart.getId());
            //加入人员归属（0总园，1分园）
//            if(sysDepart.getOrgCategory().equals("3")){
//                user.setOrgType(1);
//            }else{
//                user.setOrgType(0);
//            }
        }

        return user;
    }

    @Override
    public List<SysUser> findUserListByUserId(String userId) {
        List<SysUser> userList = userMapper.findUserListByUserId(userId);
        return userList;
    }

    @Override
    public List<SysUser> backipinionNoUser(List<String> userIds, Integer isBackipinion) {
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>()
                .lambda()
                //.eq(SysUser::getBackOpinionFlag,isBackipinion)
                .in(SysUser::getId, userIds));
        return userList;
    }

    @Override
    public SysUser getOtherInfo(String userId) {
        return this.baseMapper.getOtherInfo(userId);
    }

    @Override
    public String getMaxUserName(String cc) {
        return this.baseMapper.getMaxUserName(cc);
    }

    @Override
    public boolean isAdmin(String id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", id);
        List<SysUserRole> roleList = sysUserRoleMapper.selectList(wrapper);
        if (roleList != null && roleList.size() > 0) {
            for (SysUserRole sysUserRole : roleList) {
                SysRole role = sysRoleMapper.selectById(sysUserRole.getRoleId());
                if ("admin".equals(role.getRoleCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SysUser getUserBySchoolCode(String schoolCode) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("school_code", schoolCode);
        wrapper.eq("status", StatusEnum.ONE.getCode());
        wrapper.eq("del_flag", StatusEnum.ZERO.getCode());
        return this.baseMapper.selectOne(wrapper);
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public void importUserExcel(List<Map<Integer, String>> userData, HttpServletRequest request) {
        log.info("{}条数据，开始存储数据库！", userData.size() - 1);
        List<String> headerList = new ArrayList<>();
        for (int j = 0; j < userData.size(); j++) {
            if (ObjectUtil.isEmpty(userData.get(j).get(0)) && ObjectUtil.isEmpty(userData.get(j).get(2)) && ObjectUtil.isEmpty(userData.get(j).get(3))) {
                Map<Integer, String> nullDate = userData.get(j);
            } else {
                Map<Integer, String> integerStringMap = userData.get(j);
                //处理表头
                if (j == 0) {
                    List<String> beforeHeard = new ArrayList<>();
                    beforeHeard.addAll(userData.get(0).values());
                    headerList = getNotBlankHead(beforeHeard);
                    continue;
                }
                if (StringUtils.isBlank(integerStringMap.get(0))
                        && StringUtils.isBlank(integerStringMap.get(1))
                        && StringUtils.isBlank(integerStringMap.get(2))
                        && StringUtils.isBlank(integerStringMap.get(3))
                        && StringUtils.isBlank(integerStringMap.get(4))
                        && StringUtils.isBlank(integerStringMap.get(5))) {
                    continue;
                }

                saveUser(integerStringMap, j, request);

                log.info("存储数据库成功！");
            }
        }
    }

    private List<String> getNotBlankHead(List<String> beforeHeard) {
        List<String> headerList = new ArrayList<>();
        for (String header : beforeHeard) {
            if (StringUtils.isNotBlank(header)) {
                headerList.add(header);
            }
        }
        return headerList;
    }

    /*   //导入学生信息
       private void saveUser(Map<Integer, String> parMap, int rowNum, HttpServletRequest request) {
           try {
               SysUser user = new SysUser();
               if (StringUtils.isEmpty(parMap.get(0))) {
                   throw new RuntimeException("第" + (rowNum + 1) + "行账号为空，请检查。");
               } else {
                   //查询有无此账号
                   SysUser sysUser = userMapper.getUserByName(parMap.get(0));
                   if (ObjectUtil.isNotEmpty(sysUser)) {
                       throw new RuntimeException("第" + (rowNum + 1) + "行账号已存在，请检查。");
                   }
               }
               if (StringUtils.isEmpty(parMap.get(1))) {
                   throw new RuntimeException("第" + (rowNum + 1) + "行用户姓名为空，请检查。");
               }
               if (StringUtils.isEmpty(parMap.get(2))) {
                   throw new RuntimeException("第" + (rowNum + 1) + "行用户角色为空，请检查。");
               } else {
                   List<String> roles = Arrays.asList(parMap.get(2).split(","));
                   for (String s : roles) {
                       if (!checkRole(s)) {
                           throw new RuntimeException("第" + (rowNum + 1) + "行用户角色填写不正确，请检查。");
                       }
                   }
               }
               if (StringUtils.isEmpty(parMap.get(3))) {
                   throw new RuntimeException("第" + (rowNum + 1) + "行学校代码为空，请检查。");
               }
               if (StringUtils.isEmpty(parMap.get(4))) {
                   throw new RuntimeException("第" + (rowNum + 1) + "行学校名称为空，请检查。");
               }
               *//*if (StringUtils.isEmpty(parMap.get(5))){
                throw new RuntimeException("第" + (rowNum+1) + "行学校地址为空，请检查。");
            }*//*
            if (StringUtils.isEmpty(parMap.get(5))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行学校性质为空，请检查。");
            } else {
                List<DictModel> dicts = dictService.queryDictItemsByCode("school_nature");
                List<String> collect = dicts.stream().map(DictModel::getText).collect(Collectors.toList());
                if (!(collect.contains(parMap.get(5)))) {
                    throw new RuntimeException("第" + (rowNum + 1) + "行学校性质填写不准确，请检查。");
                }
            }

            if (StringUtils.isEmpty(parMap.get(6))) {
                //throw new RuntimeException("第" + (rowNum+1) + "行学校类型为空，请检查。");
            } else {
                List<DictModel> dicts = dictService.queryDictItemsByCode("school_type");
                List<String> collect = dicts.stream().map(DictModel::getText).collect(Collectors.toList());
                if (!(collect.contains(parMap.get(6)))) {
                    throw new RuntimeException("第" + (rowNum + 1) + "行学校类型填写不准确，请检查。");
                }
            }
            if (StringUtils.isEmpty(parMap.get(7))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行学段为空，请检查。");
            } else {
                List<String> periods = Arrays.asList(parMap.get(7).split(","));
                List<DictModel> dicts = dictService.queryDictItemsByCode("school_period");
                List<String> collect = dicts.stream().map(DictModel::getText).collect(Collectors.toList());
                for (String s : periods) {
                    if (!collect.contains(s)) {
                        throw new RuntimeException("第" + (rowNum + 1) + "行学段填写不准确，请检查。");
                    }
                }
            }
            user.setUsername(parMap.get(0));
            user.setRealname(parMap.get(1));
            //生成密码  xszz2021 以及密码盐值

            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(parMap.get(0), "123456", salt);
            user.setPassword(passwordEncode);

            user.setStatus(1);
            user.setDelFlag("0");
            userMapper.insert(user);

            List<String> roles = Arrays.asList(parMap.get(2).split(","));
            for (String s : roles) {
                SysRole role = sysRoleMapper.selectOne(new QueryWrapper<SysRole>().eq("role_name", s));
                SysUserRole userRole = new SysUserRole(user.getId(), role.getId());
                sysUserRoleMapper.insert(userRole);
            }


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }*/
    private void saveUser(Map<Integer, String> parMap, int rowNum, HttpServletRequest request) {
        try {
            SysUser user = new SysUser();
            SysUserDepart sysUserDepart;
            if (StringUtils.isEmpty(parMap.get(0))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行账号为空，请检查。");
            } else {
                //查询有无此账号
                SysUser sysUser = userMapper.getUserByName(parMap.get(0));
                if (ObjectUtil.isNotEmpty(sysUser)) {
                    throw new RuntimeException("第" + (rowNum + 1) + "行账号已存在，请检查。");
                }
            }
            if (StringUtils.isEmpty(parMap.get(1))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行用户名字为空，请检查。");
            }
            if (StringUtils.isEmpty(parMap.get(2))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行用户角色为空，请检查。");
            } else {
                List<String> roles = Arrays.asList(parMap.get(2).split(","));
                for (String s : roles) {
                    if (!checkRole(s)) {
                        throw new RuntimeException("第" + (rowNum + 1) + "行用户角色填写不正确，请检查。");
                    }
                }
            }
            if (StringUtils.isEmpty(parMap.get(3))) {
                throw new RuntimeException("第" + (rowNum + 1) + "行所属部门为空，请检查。");
            } else {
                SysDepart sysDepart = sysDepartMapper.selectOne(new LambdaQueryWrapper<SysDepart>()
                        .eq(SysDepart::getDepartName, parMap.get(3)));
                if (ObjectUtil.isEmpty(sysDepart)) {
                    throw new RuntimeException("第" + (rowNum + 1) + "行所属部门填写不正确，请检查。");
                }
                user.setOrgCode(sysDepart.getOrgCode());
                user.setOrgName(sysDepart.getDepartName());
                user.setOrgId(sysDepart.getId());
                sysUserDepart = new SysUserDepart(null, sysDepart.getId());
            }
            if (StringUtils.isEmpty(parMap.get(4))) {
                throw new RuntimeException("第" + (rowNum + 1) + "部门权限为空，请检查。");
            } else {
                SysDepart sysDepart = sysDepartMapper.selectOne(new LambdaQueryWrapper<SysDepart>()
                        .eq(SysDepart::getDepartName, parMap.get(4)));
                if (ObjectUtil.isEmpty(sysDepart)) {
                    throw new RuntimeException("第" + (rowNum + 1) + "部门权限填写不正确，请检查。");
                }
                user.setDepartmentIds(sysDepart.getId());
            }
            if (StringUtils.isEmpty(parMap.get(5))) {
                throw new RuntimeException("第" + (rowNum + 1) + "手机号码为空，请检查。");
            } else {
                user.setPhone(parMap.get(5));
            }


            user.setUsername(parMap.get(0));
            user.setRealname(parMap.get(1));
            //生成密码  xszz2021 以及密码盐值

            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(parMap.get(0), "swsc2021", salt);
            user.setPassword(passwordEncode);

            user.setStatus(1);
            user.setDelFlag("0");


            List<String> roles = Arrays.asList(parMap.get(2).split(","));
            user.setPost(roles.get(0));
            userMapper.insert(user);
            sysUserDepart.setUserId(user.getId());
            sysUserDepartMapper.insert(sysUserDepart);

            for (String s : roles) {
                SysRole role = sysRoleMapper.selectOne(new QueryWrapper<SysRole>().eq("role_name", s));
                SysUserRole userRole = new SysUserRole(user.getId(), role.getId());
                sysUserRoleMapper.insert(userRole);
            }


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*rivate boolean checkRole(String s) {
        if (s.equals("幼教学校管理员") || s.equals("义教学校管理员") || s.equals("普高学校管理员")) {
            return true;
        } else {
            return false;
        }
    }*/
    private boolean checkRole(String s) {
        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        List<String> roleNames = sysRoles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
        return roleNames.contains(s);
    }

    @Override
    public List<SysUser> selectUsersByRoleCode(String roleCode) {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        List userIds = sysUserRoleMapper.selectObjs(new LambdaQueryWrapper<SysUserRole>().select(SysUserRole::getUserId).eq(SysUserRole::getRoleId, role.getId()));
        List<SysUser> userList = this.baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds).eq(SysUser::getDelFlag, 0).eq(SysUser::getStatus, 1));
        return userList;
    }

    @Override
    public List<SysUser> selectUserByRoleAndDepartment(String roleCode, String department, String userName) {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        List userIds = sysUserRoleMapper.selectObjs(new LambdaQueryWrapper<SysUserRole>().select(SysUserRole::getUserId).eq(SysUserRole::getRoleId, role.getId()));
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds).eq(SysUser::getDelFlag, 0).eq(SysUser::getStatus, 1);
        if (StringUtils.isNotEmpty(department)) {
            wrapper.eq(SysUser::getOrgId, department);
        }
        if (StringUtils.isNotEmpty(userName)) {
            wrapper.like(SysUser::getRealname, userName);
        }
        List<SysUser> userList = this.baseMapper.selectList(wrapper);
        return userList;
    }

    @Override
    public void updateBatchUsersPermission(List<String> ids, List<String> departmentIds, List<String> systemCodes) {
        if (ids != null && ids.size() > 0) {
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper();
            queryWrapper.in("id", ids);
            List<SysUser> userList = userMapper.selectList(queryWrapper);
            String systemCode = "";
            for (String code : systemCodes) {
                systemCode += code + ",";
            }
            String departmentId = "";
            for (String id : departmentIds) {
                departmentId += id + ",";
            }
            systemCode = systemCode.substring(0, systemCode.length() - 1);
            departmentId = departmentId.substring(0, departmentId.length() - 1);
            for (SysUser sysUser : userList) {
                UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", sysUser.getId());
                updateWrapper.set("system_codes", systemCode);
                String orgId = sysUser.getOrgId();
                boolean boo = departmentIds.contains(orgId);
                String deptIds = departmentId;
                if (!boo) {
                    deptIds += "," + orgId;
                }
                updateWrapper.set("department_ids", deptIds);
                userMapper.update(sysUser, updateWrapper);
            }
        }

    }

    @Override
    public List<DepartScheduleModel> selectUserScheduleByOrgIdsAndDate(List<String> orgIdList, String date) {
        List<DepartScheduleModel> list = new ArrayList<>();
        if (!ObjectUtil.isEmpty(orgIdList) && !StringUtils.isBlank(date)) {
            for (String orgId : orgIdList) {
                DepartScheduleModel model = new DepartScheduleModel();
                List<SysUser> monitorList = this.baseMapper.selectUserByOrgIdAndRoleCode(orgId, "banzhang");
                if (monitorList.size()>0) {
                    model.setMonitor(monitorList.stream().map(SysUser::getRealname).collect(Collectors.joining(",")));
                }
                model.setNum(this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, orgId).eq(SysUser::getDelFlag, 0)));
                List<SysUserScheduleModel> dutyUsers = scheduleRecordMapper.getDutyUserByOrgIdAndDate(orgId, date);
                SysDepart sysDepart = this.sysDepartMapper.selectOne(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getId, orgId));
                model.setDepartName(sysDepart.getDepartName());
                model.setDutyUsers(dutyUsers);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public Integer getTotalNum(Map map) {
        if (ObjectUtil.isNotEmpty(map.get("lineId"))) {
            String lineCode = map.get("lineId").toString();
            Line line=lineService.getOne(new QueryWrapper<Line>().eq("line_code",lineCode));
            List<String> banzuList = this.baseMapper.getBanzuListByLine(line.getId());
            if (banzuList != null && banzuList.size() > 0) {
                String[] emp=new String[banzuList.size()];
                int i=0;
                for(String ce:banzuList){
                    emp[i]=ce;
                    i++;
                }
                map.put("orgIds", emp);
            }
        }
        return this.baseMapper.getTotalNum(map);
    }

    @Override
    public List<SysUser> getBanZhangByBanZu(String orgId) {
        return this.baseMapper.getBanZhangByBanZu(orgId);
    }
    @Override
    public List<UserScheduleVo> getUserByBanZu(String orgId) {
        return this.baseMapper.getUserByBanZu(orgId);
    }

    @Override
    public List<StaffDataVo> getStaffData(Map map) {
        List<StaffDataVo> list=new ArrayList<>();
        if (ObjectUtil.isNotEmpty(map.get("lineId"))) {
            String lineCode = map.get("lineId").toString();
            Line line=lineService.getOne(new QueryWrapper<Line>().eq("line_code",lineCode));
            List<String> banzuList = this.baseMapper.getBanzuListByLine(line.getId());
            if (banzuList != null && banzuList.size() > 0) {
                String[] emp=new String[banzuList.size()];
                int i=0;
                for(String ce:banzuList){
                    emp[i]=ce;
                    i++;
                }
                map.put("orgIds", emp);
            }
        }
        list=this.baseMapper.getStaffData(map);
        list.forEach(staff->{
            //1查询巡检数
            staff.setNum1(this.baseMapper.getPatrolTaskNumByStaffId(staff.getStaffId()));
            //2查询检修数
            staff.setNum2(this.baseMapper.getRepairTaskNumByStaffId(staff.getStaffId()));
            //3查询故障数
            staff.setNum3(this.baseMapper.getFaultNumByStaffId(staff.getStaffId()));
        });

        return list;
    }

    @Override
    public List<String> getBanzuListByLine(Integer lineId) {
        return this.baseMapper.getBanzuListByLine(lineId);
    }

    @Override
    public List<String> getRoleCodeById(String id) {
        return this.baseMapper.getRoleCodeById(id);
    }

    @Override
    public String getUserIdByUsername(String username) {
        return this.baseMapper.getUserIdByUsername(username);
    }

    @Override
    public List<SysUser> getUsersByOrgCode(String orgCode) {
        return this.baseMapper.getUsersByOrgCode(orgCode);
    }

    @Override
    public List<SysUser> getSysUsersByLineCodeAndOrgId(String lineCode, String orgId) {
        return this.baseMapper.getSysUsersByLineCodeAndOrgId(lineCode,orgId);
    }
}
