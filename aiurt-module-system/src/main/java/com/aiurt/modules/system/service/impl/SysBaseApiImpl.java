package com.aiurt.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.service.impl.PatrolStandardItemsServiceImpl;
import com.aiurt.common.api.dto.message.*;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.aspect.UrlMatchEnum;
import com.aiurt.common.constant.*;
import com.aiurt.common.constant.enums.MessageTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.SpareResult;
import com.aiurt.common.util.HTMLUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.YouBianCodeUtil;
import com.aiurt.common.util.dynamic.db.FreemarkerParseFactory;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.basic.service.ISysAttachmentService;
import com.aiurt.modules.common.entity.DeviceTypeTable;
import com.aiurt.modules.common.entity.SelectDeviceType;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceAssemblyMapper;
import com.aiurt.modules.device.mapper.DeviceMapper;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.message.entity.SysMessageTemplate;
import com.aiurt.modules.message.handle.impl.DdSendMsgHandle;
import com.aiurt.modules.message.handle.impl.EmailSendMsgHandle;
import com.aiurt.modules.message.handle.impl.QywxSendMsgHandle;
import com.aiurt.modules.message.handle.impl.SystemSendMsgHandle;
import com.aiurt.modules.message.service.ISysMessageTemplateService;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.positionwifi.mapper.CsPositionWifiMapper;
import com.aiurt.modules.quartz.entity.QuartzJob;
import com.aiurt.modules.quartz.service.IQuartzJobService;
import com.aiurt.modules.sensorinformation.entity.SensorInformation;
import com.aiurt.modules.sensorinformation.mapper.SensorInformationMapper;
import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.aiurt.modules.sm.entity.SafetyRelatedForm;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.sm.mapper.SafetyRelatedFormMapper;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.mapper.CsSubsystemMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.*;
import com.aiurt.modules.system.mapper.*;
import com.aiurt.modules.system.service.*;
import com.aiurt.modules.system.util.SecurityUtil;
import com.aiurt.modules.system.vo.SysUserDepVo;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.aiurt.modules.workarea.entity.WorkAreaOrg;
import com.aiurt.modules.workarea.mapper.WorkAreaLineMapper;
import com.aiurt.modules.workarea.mapper.WorkAreaMapper;
import com.aiurt.modules.workarea.mapper.WorkAreaOrgMapper;
import com.aiurt.modules.workarea.service.IWorkAreaService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.dto.OnlineAuthDTO;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;
    /**
     * 当前系统数据库类型
     */
    private static String DB_TYPE = "";
    @Autowired
    private ISysMessageTemplateService sysMessageTemplateService;
    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    private CsUserMajorMapper csUserMajorMapper;
    @Autowired
    private CsUserDepartMapper csUserDepartMapper;
    @Autowired
    private ISysDictService sysDictService;
    @Resource
    private SysAnnouncementMapper sysAnnouncementMapper;
    @Resource
    private SysAnnouncementSendMapper sysAnnouncementSendMapper;
    @Resource
    private WebSocket webSocket;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysDepartMapper departMapper;
    @Resource
    private SysCategoryMapper categoryMapper;

    @Autowired
    private ISysDataSourceService dataSourceService;
    @Autowired
    private ISysUserDepartService sysUserDepartService;
    @Resource
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private ISysPermissionDataRuleService sysPermissionDataRuleService;

    @Autowired
    private ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
    @Autowired
    private ThirdAppDingtalkServiceImpl dingtalkService;
    @Autowired
    private CsStationMapper csStationMapper;
    @Autowired
    ISysCategoryService sysCategoryService;

    @Autowired
    private ISysAttachmentService sysAttachmentService;

    @Autowired
    private ICsUserDepartService iCsUserDepartService;

    @Autowired
    private ICsUserMajorService iCsUserMajorService;

    @Autowired
    private CsUserStaionMapper csUserStaionMapper;

    @Autowired
    private CsUserSubsystemMapper csUserSubsystemMapper;

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @Autowired
    private IQuartzJobService quartzJobService;

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceAssemblyMapper deviceAssemblyMapper;
    @Autowired
    private WorkAreaMapper workAreaMapper;
    @Autowired
    private IWorkAreaService workAreaService;

    @Autowired
    private ICsMajorService majorService;
    @Autowired
    private CsSubsystemMapper subsystemMapper;
    @Autowired
    @Lazy
    private ICsSubsystemService csSubsystemService;
    @Autowired
    private CsLineMapper lineMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private CsSafetyAttentionMapper csSafetyAttentionMapper;

    @Autowired
    private ISysUserPositionCurrentService sysUserPositionCurrentService;

    @Autowired
    @Lazy
    private PatrolStandardItemsServiceImpl patrolStandardItemsService;

    @Autowired
    private CsStationPositionMapper csStationPositionMapper;

    @Autowired
    private FaultRepairRecordMapper faultRepairRecordMapper;
    @Autowired
    private SafetyRelatedFormMapper safetyRelatedFormMapper;

    @Autowired
    @Lazy
    private FlowApiService flowApiService;
    @Autowired
    private SparePartApplyMapper sparePartApplyMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Autowired
    private MaterialBaseMapper materialBaseMapper;
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    @Lazy
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private SparePartLendMapper sparePartLendMapper;
    @Autowired
    private ISysHolidaysService sysHolidaysService;
    @Autowired
    private DeviceChangeSparePartMapper sparePartMapper;
    @Autowired
    private FaultMapper faultMapper;
    @Autowired
    private WorkAreaOrgMapper workAreaOrgMapper;
    @Autowired
    private WorkAreaLineMapper workAreaLineMapper;
    @Autowired
    private SensorInformationMapper sensorInformationMapper;
    @Autowired
    private CsPositionWifiMapper csPositionWifiMapper;
    @Autowired
    private ISysParamAPI sysParamApi;
    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key = "#username")
    public LoginUser getUserByName(String username) {
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = userMapper.getUserByName(username);
        if (sysUser == null) {
            return null;
        }

        List<String> roleNameList = sysUserRoleMapper.getRoleName(sysUser.getId());
        List<String> roleCodeList = sysUserRoleMapper.getRoleByUserName(sysUser.getUsername());
        List<String> roleIdList = sysUserRoleMapper.getRoleIdByUserName(sysUser.getUsername());
        // 用户角色
        BeanUtils.copyProperties(sysUser, loginUser);
        loginUser.setRoleCodes(StrUtil.join(",", roleCodeList));
        loginUser.setRoleNames(StrUtil.join(",", roleNameList));
        loginUser.setRoleIds(StrUtil.join(",", roleIdList));
        return loginUser;
    }

    @Override
    public String translateDictFromTable(String table, String text, String code, String key) {
        return sysDictService.queryTableDictTextByKey(table, text, code, key);
    }

    @Override
    public String translateDict(String code, String key) {
        return sysDictService.queryDictTextByKey(code, key);
    }

    @Override
    public List<SysPermissionDataRuleModel> queryPermissionDataRule(String component, String requestPath, String username) {
        List<SysPermission> currentSyspermission = null;
        if (oConvertUtils.isNotEmpty(component)) {
            //1.通过注解属性pageComponent 获取菜单
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, 0);
            query.eq(SysPermission::getComponent, component);
            currentSyspermission = sysPermissionMapper.selectList(query);
        } else {
            //1.直接通过前端请求地址查询菜单
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getMenuType, 2);
            query.eq(SysPermission::getDelFlag, 0);
            query.eq(SysPermission::getUrl, requestPath);
            currentSyspermission = sysPermissionMapper.selectList(query);
            //2.未找到 再通过自定义匹配URL 获取菜单
            if (currentSyspermission == null || currentSyspermission.size() == 0) {
                //通过自定义URL匹配规则 获取菜单（实现通过菜单配置数据权限规则，实际上针对获取数据接口进行数据规则控制）
                String userMatchUrl = UrlMatchEnum.getMatchResultByUrl(requestPath);
                LambdaQueryWrapper<SysPermission> queryQserMatch = new LambdaQueryWrapper<SysPermission>();
                queryQserMatch.eq(SysPermission::getDelFlag, 0);
                queryQserMatch.eq(SysPermission::getUrl, userMatchUrl);
                if (oConvertUtils.isNotEmpty(userMatchUrl)) {
                    currentSyspermission = sysPermissionMapper.selectList(queryQserMatch);
                }
            }
            //3.未找到 再通过正则匹配获取菜单
            if (currentSyspermission == null || currentSyspermission.size() == 0) {
                //通过正则匹配权限配置
                String regUrl = getRegexpUrl(requestPath);
                if (regUrl != null) {
                    currentSyspermission = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getMenuType, 2).eq(SysPermission::getUrl, regUrl).eq(SysPermission::getDelFlag, 0));
                }
            }
        }
        if (currentSyspermission != null && currentSyspermission.size() > 0) {
            List<SysPermissionDataRuleModel> dataRules = new ArrayList<SysPermissionDataRuleModel>();
            for (SysPermission sysPermission : currentSyspermission) {
                // update-begin--Author:scott Date:20191119 for：数据权限规则编码不规范，项目存在相同包名和类名 #722
                List<SysPermissionDataRule> temp = sysPermissionDataRuleService.queryPermissionDataRules(username, sysPermission.getId());
                if (temp != null && temp.size() > 0) {
                    dataRules = oConvertUtils.entityListToModelList(temp, SysPermissionDataRuleModel.class);
                }
                // update-end--Author:scott Date:20191119 for：数据权限规则编码不规范，项目存在相同包名和类名 #722
            }
            return dataRules;
        }
        return null;
    }

    /**
     * 匹配前端传过来的地址 匹配成功返回正则地址
     * AntPathMatcher匹配地址
     * ()* 匹配0个或多个字符
     * ()**匹配0个或多个目录
     */
    private String getRegexpUrl(String url) {
        List<String> list = sysPermissionMapper.queryPermissionUrlWithStar();
        if (list != null && list.size() > 0) {
            for (String p : list) {
                PathMatcher matcher = new AntPathMatcher();
                if (matcher.match(p, url)) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
        LoginUser user = this.getUserByName(username);
        if (user != null) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());
        } else {
            return null;
        }
        //多部门支持in查询
        List<SysDepart> list = departMapper.queryUserDeparts(user.getId());
        List<String> sysMultiOrgCode = new ArrayList<String>();
        if (list == null || list.size() == 0) {
            //当前用户无部门
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

    @Override
    public LoginUser getUserById(String id) {
        if (oConvertUtils.isEmpty(id)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = userMapper.selectById(id);
        if (sysUser == null) {
            return null;
        }
        List<String> roleNameList = sysUserRoleMapper.getRoleName(sysUser.getId());
        List<String> roleCodeList = sysUserRoleMapper.getRoleByUserName(sysUser.getUsername());
        List<String> roleIdList = sysUserRoleMapper.getRoleIdByUserName(sysUser.getUsername());
        // 用户角色
        BeanUtils.copyProperties(sysUser, loginUser);
        loginUser.setRoleCodes(StrUtil.join(",", roleCodeList));
        loginUser.setRoleNames(StrUtil.join(",", roleNameList));
        loginUser.setRoleIds(StrUtil.join(",", roleIdList));
        return loginUser;
    }

    @Override
    public List<String> getRolesByUsername(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    @Override
    public List<String> getDepartIdsByUsername(String username) {
        List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
        List<String> result = new ArrayList<>(list.size());
        for (SysDepart depart : list) {
            result.add(depart.getId());
        }
        return result;
    }

    @Override
    public List<String> getDepartNamesByUsername(String username) {
        List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
        List<String> result = new ArrayList<>(list.size());
        for (SysDepart depart : list) {
            result.add(depart.getDepartName());
        }
        return result;
    }

    @Override
    public DictModel getParentDepartId(String departId) {
        SysDepart depart = departMapper.getParentDepartId(departId);
        DictModel model = new DictModel(depart.getId(), depart.getParentId());
        return model;
    }

    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_CACHE, key = "#code", unless = "#result == null ")
    public List<DictModel> queryDictItemsByCode(String code) {
        return sysDictService.queryDictItemsByCode(code);
    }

    @Override
    @Cacheable(value = CacheConstant.SYS_ENABLE_DICT_CACHE, key = "#code", unless = "#result == null ")
    public List<DictModel> queryEnableDictItemsByCode(String code) {
        return sysDictService.queryEnableDictItemsByCode(code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        //update-begin-author:taoyan date:20200820 for:【Online+系统】字典表加权限控制机制逻辑，想法不错 LOWCOD-799
        if (table.indexOf("#{") >= 0) {
            table = QueryGenerator.getSqlRuleValue(table);
        }
        //update-end-author:taoyan date:20200820 for:【Online+系统】字典表加权限控制机制逻辑，想法不错 LOWCOD-799
        return sysDictService.queryTableDictItemsByCode(table, text, code);
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return sysDictService.queryAllDepartBackDictModel();
    }

    @Override
    public void sendSysAnnouncement(MessageDTO message) {
        this.sendSysAnnouncement(message.getFromUser(),
                message.getToUser(),
                message.getTitle(),
                message.getContent(),
                message.getCategory());
        try {
            // 同步发送第三方APP消息
//            wechatEnterpriseService.sendMessage(message, true);
//            dingtalkService.sendMessage(message, true);
        } catch (Exception e) {
            log.error("同步发送第三方APP消息失败！", e);
        }
    }

    @Override
    public void sendBusAnnouncement(BusMessageDTO message) {
        sendBusAnnouncement(message.getFromUser(),
                message.getToUser(),
                message.getTitle(),
                message.getContent(),
                message.getCategory(),
                message.getBusType(),
                message.getBusId(),
                message.getLevel(),
                message.getStartTime(),
                message.getEndTime(),
                message.getPriority());
        try {
            // 同步发送第三方APP消息
//            wechatEnterpriseService.sendMessage(message, true);
//            dingtalkService.sendMessage(message, true);
        } catch (Exception e) {
            log.error("同步发送第三方APP消息失败！", e);
        }
    }

    @Override
    public void sendTemplateAnnouncement(TemplateMessageDTO message) {
        String templateCode = message.getTemplateCode();
        String title = message.getTitle();
        Map<String, String> map = message.getTemplateParam();
        String fromUser = message.getFromUser();
        String toUser = message.getToUser();

        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new AiurtBootException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板标题
        title = title == null ? sysSmsTemplate.getTemplateName() : title;
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                if (oConvertUtils.isNotEmpty(title)) {
                    title = title.replace(str, entry.getValue());
                }
                content = content.replace(str, entry.getValue());
            }
        }

        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitile(title);
        announcement.setMsgContent(content);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = toUser;
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for (int i = 0; i < userIds.length; i++) {
            if (oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if (sysUser == null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }
        try {
            // 同步企业微信、钉钉的消息通知
            dingtalkService.sendActionCardMessage(announcement, true);
            wechatEnterpriseService.sendTextCardMessage(announcement, true);
        } catch (Exception e) {
            log.error("同步发送第三方APP消息失败！", e);
        }

    }

    @Override
    public void sendBusTemplateAnnouncement(BusTemplateMessageDTO message) {
        String templateCode = message.getTemplateCode();
        String title = message.getTitle();
        Map<String, String> map = message.getTemplateParam();
        String fromUser = message.getFromUser();
        String toUser = message.getToUser();
        String busId = message.getBusId();
        String busType = message.getBusType();

        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new AiurtBootException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板标题
        title = title == null ? sysSmsTemplate.getTemplateName() : title;
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                title = title.replace(str, entry.getValue());
                content = content.replace(str, entry.getValue());
            }
        }
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitile(title);
        announcement.setMsgContent(content);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        announcement.setBusId(busId);
        announcement.setBusType(busType);
        announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
        announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = toUser;
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for (int i = 0; i < userIds.length; i++) {
            if (oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if (sysUser == null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }
        try {
            // 同步企业微信、钉钉的消息通知
            dingtalkService.sendActionCardMessage(announcement, true);
            wechatEnterpriseService.sendTextCardMessage(announcement, true);
        } catch (Exception e) {
            log.error("同步发送第三方APP消息失败！", e);
        }

    }

    @Override
    public String parseTemplateByCode(TemplateDTO templateDTO) {
        String templateCode = templateDTO.getTemplateCode();
        Map<String, String> map = templateDTO.getTemplateParam();
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if (sysSmsTemplates == null || sysSmsTemplates.size() == 0) {
            throw new AiurtBootException("消息模板不存在，模板编码：" + templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                content = content.replace(str, entry.getValue());
            }
        }
        return content;
    }

    @Override
    public void updateSysAnnounReadFlag(String busType, String busId) {
        SysAnnouncement announcement = sysAnnouncementMapper.selectOne(new QueryWrapper<SysAnnouncement>().eq("bus_type", busType).eq("bus_id", busId));
        if (announcement != null) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String userId = sysUser.getId();
            LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
            updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
            updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
            updateWrapper.last("where annt_id ='" + announcement.getId() + "' and user_id ='" + userId + "'");
            SysAnnouncementSend announcementSend = new SysAnnouncementSend();
            sysAnnouncementSendMapper.update(announcementSend, updateWrapper);
        }
    }

    /**
     * 获取数据库类型
     *
     * @param dataSource
     * @return
     * @throws SQLException
     */
    private String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException {
        if ("".equals(DB_TYPE)) {
            Connection connection = dataSource.getConnection();
            try {
                DatabaseMetaData md = connection.getMetaData();
                String dbType = md.getDatabaseProductName().toLowerCase();
                if (dbType.indexOf("mysql") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
                } else if (dbType.indexOf("oracle") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
                } else if (dbType.indexOf("sqlserver") >= 0 || dbType.indexOf("sql server") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
                } else if (dbType.indexOf("postgresql") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
                } else if (dbType.indexOf("mariadb") >= 0) {
                    DB_TYPE = DataBaseConstant.DB_TYPE_MARIADB;
                } else {
                    log.error("数据库类型:[" + dbType + "]不识别!");
                    //throw new JeecgBootException("数据库类型:["+dbType+"]不识别!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                connection.close();
            }
        }
        return DB_TYPE;

    }

    @Override
    public List<DictModel> queryAllDict() {
        // 查询并排序
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>();
        queryWrapper.orderByAsc("create_time");
        List<SysDict> dicts = sysDictService.list(queryWrapper);
        // 封装成 model
        List<DictModel> list = new ArrayList<DictModel>();
        for (SysDict dict : dicts) {
            list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
        }

        return list;
    }

    @Override
    public List<SysCategoryModel> queryAllSysCategory() {
        List<SysCategory> ls = categoryMapper.selectList(null);
        List<SysCategoryModel> res = oConvertUtils.entityListToModelList(ls, SysCategoryModel.class);
        return res;
    }

    @Override
    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
        return sysDictService.queryTableDictItemsByCodeAndFilter(table, text, code, filterSql);
    }

    @Override
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
        return sysDictService.queryTableDictByKeys(table, text, code, Joiner.on(",").join(keyArray));
    }

    @Override
    public List<ComboModel> queryAllUserBackCombo() {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().eq("status", 1).eq("del_flag", 0));
        for (SysUser user : userList) {
            ComboModel model = new ComboModel();
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setUsername(user.getUsername());
            list.add(model);
        }
        return list;
    }

    @Override
    public JSONObject queryAllUser(String userIds, Integer pageNo, Integer pageSize) {
        JSONObject json = new JSONObject();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status", 1).eq("del_flag", 0);
        List<ComboModel> list = new ArrayList<ComboModel>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        IPage<SysUser> pageList = userMapper.selectPage(page, queryWrapper);
        for (SysUser user : pageList.getRecords()) {
            ComboModel model = new ComboModel();
            model.setUsername(user.getUsername());
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setEmail(user.getEmail());
            if (oConvertUtils.isNotEmpty(userIds)) {
                String[] temp = userIds.split(",");
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].equals(user.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        json.put("list", list);
        json.put("total", pageList.getTotal());
        return json;
    }

    @Override
    public List<ComboModel> queryAllRole() {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for (SysRole role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            list.add(model);
        }
        return list;
    }


    @Override
    public List<CsRoleUserModel>queryRoleUserTree(){
        List<CsRoleUserModel> list = new ArrayList<>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for (SysRole role : roleList) {
            CsRoleUserModel csRoleUserModel = new CsRoleUserModel();
            csRoleUserModel.setValue(role.getId());
            csRoleUserModel.setKey(role.getRoleCode());
            csRoleUserModel.setLabel(role.getRoleName());
            csRoleUserModel.setIsOrg(true);
            LambdaQueryWrapper<SysUserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysUserRole::getRoleId,role.getId());
            List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(sysUserRoles)){
                List<String> collect = sysUserRoles.stream().map(SysUserRole::getUserId).collect(Collectors.toList());
                List<SysUser> sysUsers = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .in(SysUser::getId, collect));
                if (CollUtil.isNotEmpty(sysUsers)){
                    List<SysUserModel> sysUserModelList = new ArrayList<>();
                    for (SysUser sysUser : sysUsers) {
                        SysUserModel sysUserModel = new SysUserModel();
                        sysUserModel.setValue(sysUser.getId());
                        sysUserModel.setLabel(sysUser.getRealname());
                        sysUserModel.setIsOrg(false);
                        sysUserModelList.add(sysUserModel);
                    }
                    csRoleUserModel.setChildren(sysUserModelList);
                }
            }
            list.add(csRoleUserModel);
        }
        return  list;
    }

    @Override
    public List<PostModel>queryPostUserTree(){
        List<PostModel> list = new ArrayList<>();
        List<DictModel> sysPost = this.getDictItems("sys_post");
        if (CollUtil.isNotEmpty(sysPost)){
            for (DictModel dictModel : sysPost) {
                 PostModel postModel = new PostModel();
                 postModel.setLabel(dictModel.getText());
                 postModel.setIsOrg(true);
                 //根据岗位查询用户信息
                List<SysUser> sysUsers = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .eq(SysUser::getJobName, dictModel.getValue()));
                if (CollUtil.isNotEmpty(sysUsers)){
                    List<SysUserModel> sysUserModelList = new ArrayList<>();
                    for (SysUser sysUser : sysUsers) {
                        SysUserModel sysUserModel = new SysUserModel();
                        sysUserModel.setValue(sysUser.getId());
                        sysUserModel.setLabel(sysUser.getRealname());
                        sysUserModel.setIsOrg(false);
                        sysUserModelList.add(sysUserModel);
                    }
                    postModel.setChildren(sysUserModelList);
                }
                list.add(postModel);
            }
        }
        return list;
    }

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for (SysRole role : roleList) {
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if (oConvertUtils.isNotEmpty(roleIds)) {
                for (int i = 0; i < roleIds.length; i++) {
                    if (roleIds[i].equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

    @Override
    public List<String> getRoleIdsByUsername(String username) {
        return sysUserRoleMapper.getRoleIdByUserName(username);
    }

    @Override
    public List<String> getUserNameByRealName(String realName) {
        return sysUserRoleMapper.getUserNameByRealName(realName);
    }

    @Override
    public List<CsLine> getAllLine() {
        LambdaQueryWrapper<CsLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByAsc(CsLine::getSort);
        return lineMapper.selectList(queryWrapper);
    }

    @Override
    public List<JSONObject> getAllSystem() {
        List<CsSubsystem> csSubsystems = subsystemMapper.selectList(new LambdaQueryWrapper<CsSubsystem>().eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0));
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (CsSubsystem subsystem : csSubsystems) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",subsystem.getSystemCode());
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    @Override
    public String getDepartByWarehouseCode(String applyWarehouseCode) {
        return sparePartApplyMapper.getDepartByWarehouseCode(applyWarehouseCode);
    }

    @Override
    public String getWarehouseNameByCode(String warehouseCode) {
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getWarehouseCode,warehouseCode);
        wrapper.eq(SparePartStockInfo::getDelFlag,CommonConstant.DEL_FLAG_0);
        SparePartStockInfo one = sparePartStockInfoMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new AiurtBootException("找不到对应仓库！");
        }
        return one.getWarehouseName();
    }

    @Override
    public String getMaterialNameByCode(String materialCode) {
        LambdaQueryWrapper<MaterialBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialBase::getCode,materialCode);
        wrapper.eq(MaterialBase::getDelFlag,CommonConstant.DEL_FLAG_0);
        MaterialBase one = materialBaseMapper.selectOne(wrapper);
        return one.getName();
    }
    @Override
    public String getMaterialNameByCodes(String materialCodes) {
        if(ObjectUtil.isNotEmpty(materialCodes)){
            List<String> list = StrUtil.splitTrim(materialCodes, ",");
            List<DeviceAssembly> deviceAssemblies = deviceAssemblyMapper.selectList(new LambdaQueryWrapper<DeviceAssembly>().eq(DeviceAssembly::getDelFlag,0).in(DeviceAssembly::getCode,list));
            if(CollUtil.isNotEmpty(deviceAssemblies)){
                List<String> materialNames = new ArrayList<>();
                for (DeviceAssembly deviceAssembly : deviceAssemblies) {
                    String materialName = deviceAssembly.getMaterialName()+"-"+deviceAssembly.getCode();
                   materialNames.add(materialName);
                }
                String collect = materialNames.stream().collect(Collectors.joining(","));
                return collect;
            }
          return null;
        }
     return null;
    }


    @Override
    public String getDepartIdsByOrgCode(String orgCode) {
        return departMapper.queryDepartIdByOrgCode(orgCode);
    }

    @Override
    public String getDepartNameByOrgCode(String orgCode) {
        return departMapper.queryDepartNameByOrgCode(orgCode);
    }

    @Override
    public List<SysDepartModel> getAllSysDepart() {
        List<SysDepartModel> departModelList = new ArrayList<SysDepartModel>();
        List<SysDepart> departList = departMapper.selectList(new QueryWrapper<SysDepart>().eq("del_flag", "0"));
        for (SysDepart depart : departList) {
            SysDepartModel model = new SysDepartModel();
            BeanUtils.copyProperties(depart, model);
            departModelList.add(model);
        }
        return departModelList;
    }

    @Override
    public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
        SysDataSource dbSource = dataSourceService.getById(dbSourceId);
        if (dbSource != null && StringUtils.isNotBlank(dbSource.getDbPassword())) {
            String dbPassword = dbSource.getDbPassword();
            String decodedStr = SecurityUtil.jiemi(dbPassword);
            dbSource.setDbPassword(decodedStr);
        }
        return new DynamicDataSourceModel(dbSource);
    }

    @Override
    public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
        SysDataSource dbSource = dataSourceService.getOne(new LambdaQueryWrapper<SysDataSource>().eq(SysDataSource::getCode, dbSourceCode));
        if (dbSource != null && StringUtils.isNotBlank(dbSource.getDbPassword())) {
            String dbPassword = dbSource.getDbPassword();
            String decodedStr = SecurityUtil.jiemi(dbPassword);
            dbSource.setDbPassword(decodedStr);
        }
        return new DynamicDataSourceModel(dbSource);
    }

    @Override
    public List<String> getDeptHeadByDepId(String deptId) {
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().like("depart_ids", deptId).eq("status", 1).eq("del_flag", 0));
        List<String> list = new ArrayList<>();
        for (SysUser user : userList) {
            list.add(user.getUsername());
        }
        return list;
    }

    @Override
    public void sendWebSocketMsg(String[] userIds, String cmd) {
        JSONObject obj = new JSONObject();
        obj.put(WebsocketConst.MSG_CMD, cmd);
        webSocket.sendMessage(userIds, obj.toJSONString());
    }

    @Override
    public List<LoginUser> queryAllUserByIds(String[] userIds) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status", 1).eq("del_flag", 0);
        if (userIds != null && userIds.length > 0) {
            queryWrapper.in("id", userIds);
            List<LoginUser> loginUsers = new ArrayList<>();
            List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
            for (SysUser user : sysUsers) {
                LoginUser loginUser = new LoginUser();
                BeanUtils.copyProperties(user, loginUser);
                loginUsers.add(loginUser);
            }
            return loginUsers;
        }

        return CollUtil.newArrayList();
    }

    /**
     * 推送签到人员信息
     *
     * @param userId
     */
    @Override
    public void meetingSignWebsocket(String userId) {
        JSONObject obj = new JSONObject();
        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_SIGN);
        obj.put(WebsocketConst.MSG_USER_ID, userId);
        //TODO 目前全部推送，后面修改
        webSocket.sendMessage(obj.toJSONString());
    }

    @Override
    public List<LoginUser> queryUserByNames(String[] userNames) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status", 1).eq("del_flag", 0);
        queryWrapper.in("username", userNames);
        List<LoginUser> loginUsers = new ArrayList<>();
        List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
        for (SysUser user : sysUsers) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }

    @Override
    public SysDepartModel selectAllById(String id) {
        SysDepart sysDepart = sysDepartService.getById(id);
        SysDepartModel sysDepartModel = new SysDepartModel();
        // fix 属性copy 空指针异常。
        if (Objects.isNull(sysDepart)) {
            return sysDepartModel;
        }
        BeanUtils.copyProperties(sysDepart, sysDepartModel);
        return sysDepartModel;
    }

    @Override
    public List<String> queryDeptUsersByUserId(String userId) {
        List<String> userIds = new ArrayList<>();
        List<SysUserDepart> userDepartList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().eq("user_id", userId));
        if (userDepartList != null) {
            //查找所属公司
//            String orgCodes = "";
            StringBuilder orgCodes = new StringBuilder();
            for (SysUserDepart userDepart : userDepartList) {
                //查询所属公司编码
                SysDepart depart = sysDepartService.getById(userDepart.getDepId());
                int length = YouBianCodeUtil.ZHANWEI_LENGTH;
                String compyOrgCode = "";
                if (depart != null && depart.getOrgCode() != null) {
                    compyOrgCode = depart.getOrgCode().substring(0, length);
                    if (orgCodes.indexOf(compyOrgCode) == -1) {
                        orgCodes = orgCodes.append(orgCodes + "," + compyOrgCode);
                    }
                }
            }
            if (oConvertUtils.isNotEmpty(orgCodes)) {
                orgCodes = new StringBuilder(orgCodes.substring(1));
                List<String> listIds = departMapper.getSubDepIdsByOrgCodes(orgCodes.toString().split(","));
                List<SysUserDepart> userList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().in("dep_id", listIds));
                for (SysUserDepart userDepart : userList) {
                    if (!userIds.contains(userDepart.getUserId())) {
                        userIds.add(userDepart.getUserId());
                    }
                }
            }
        }
        return userIds;
    }

    /**
     * 查询用户拥有的角色集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getUserRoleSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles);
    }

    /**
     * 查询用户拥有的权限集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getUserPermissionSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username, null);
        for (SysPermission po : permissionList) {
            // TODO URL规则有问题？
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + (permissionSet == null ? 0 : permissionSet.size()));
        return permissionSet;
    }

    /**
     * 判断online菜单是否有权限
     *
     * @param onlineAuthDTO
     * @return
     */
    @Override
    public boolean hasOnlineAuth(OnlineAuthDTO onlineAuthDTO) {
        String username = onlineAuthDTO.getUsername();
        List<String> possibleUrl = onlineAuthDTO.getPossibleUrl();
        String onlineFormUrl = onlineAuthDTO.getOnlineFormUrl();
        //查询菜单
        LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
        query.eq(SysPermission::getDelFlag, 0);
        query.in(SysPermission::getUrl, possibleUrl);
        List<SysPermission> permissionList = sysPermissionMapper.selectList(query);
        if (permissionList == null || permissionList.size() == 0) {
            //没有配置菜单 找online表单菜单地址
            SysPermission sysPermission = new SysPermission();
            sysPermission.setUrl(onlineFormUrl);
            int count = sysPermissionMapper.queryCountByUsername(username, sysPermission);
            if (count <= 0) {
                return false;
            }
        } else {
            //找到菜单了
            boolean has = false;
            for (SysPermission p : permissionList) {
                int count = sysPermissionMapper.queryCountByUsername(username, p);
                has = has || (count > 0);
            }
            return has;
        }
        return true;
    }

    /**
     * 查询用户拥有的角色集合 common api 里面的接口实现
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> queryUserRoles(String username) {
        return getUserRoleSet(username);
    }

    /**
     * 查询用户拥有的权限集合 common api 里面的接口实现
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> queryUserAuths(String username) {
        return getUserPermissionSet(username);
    }

    /**
     * 36根据多个用户账号(逗号分隔)，查询返回多个用户信息
     *
     * @param usernames
     * @return
     */
    @Override
    public List<JSONObject> queryUsersByUsernames(String usernames) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getUsername, usernames.split(","));
        return JSON.parseArray(JSON.toJSONString(userMapper.selectList(queryWrapper))).toJavaList(JSONObject.class);
    }

    @Override
    public LoginUser queryUser(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = userMapper.selectOne(queryWrapper);
        LoginUser loginUser = new LoginUser();
        if (ObjectUtil.isNotEmpty(sysUser)) {
            BeanUtils.copyProperties(sysUser, loginUser);
        }
        return loginUser;
    }

    @Override
    public List<JSONObject> queryUsersByIds(String ids) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getId, ids.split(","));
        return JSON.parseArray(JSON.toJSONString(userMapper.selectList(queryWrapper))).toJavaList(JSONObject.class);
    }


    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    @Override
    public boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    @Override
    public boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");     // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 37根据多个部门编码(逗号分隔)，查询返回多个部门信息
     *
     * @param orgCodes
     * @return
     */
    @Override
    public List<JSONObject> queryDepartsByOrgcodes(String orgCodes) {
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDepart::getOrgCode, orgCodes.split(","));
        return JSON.parseArray(JSON.toJSONString(sysDepartService.list(queryWrapper))).toJavaList(JSONObject.class);
    }

    /**
     * 根据多个部门编码，查询返回多个部门名称
     *
     * @param orgCodes
     * @return
     */
    @Override
    public List<String> queryOrgNamesByOrgCodes(List<String> orgCodes) {
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDepart::getOrgCode, orgCodes);
        List<String> collect = sysDepartService.list(queryWrapper).stream().map(SysDepart::getDepartName).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据多个部门编码，查询返回多个部门id
     *
     * @param orgCodes
     * @return
     */
    @Override
    public List<String> queryOrgIdsByOrgCodes(List<String> orgCodes) {
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDepart::getOrgCode, orgCodes);
        List<String> collect = sysDepartService.list(queryWrapper).stream().map(SysDepart::getId).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<JSONObject> queryDepartsByIds(String ids) {
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDepart::getId, ids.split(","));
        return JSON.parseArray(JSON.toJSONString(sysDepartService.list(queryWrapper))).toJavaList(JSONObject.class);
    }

    /**
     * 发消息
     *
     * @param fromUser
     * @param toUser
     * @param title
     * @param msgContent
     * @param setMsgCategory
     */
    private void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory) {
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender(fromUser);
        announcement.setPriority(CommonConstant.PRIORITY_M);
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(setMsgCategory);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = toUser;
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for (int i = 0; i < userIds.length; i++) {
            if (oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if (sysUser == null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }

    }

    /**
     * 发消息 带业务参数
     *
     * @param fromUser
     * @param toUser
     * @param title
     * @param msgContent
     * @param setMsgCategory
     * @param busType
     * @param busId
     */
    private void sendBusAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory, String busType, String busId, String level, Date startTime, Date endTime, String priority) {
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender(fromUser);
        if (StringUtils.isNotBlank(priority)) {
            announcement.setPriority(priority);
        } else {
            announcement.setPriority(CommonConstant.PRIORITY_M);
        }
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(setMsgCategory);
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        announcement.setBusId(busId);
        announcement.setBusType(busType);
        announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
        announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
        announcement.setUserIds(toUser);
        announcement.setLevel(level);
        announcement.setStartTime(startTime);
        announcement.setEndTime(endTime);
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = toUser;
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for (int i = 0; i < userIds.length; i++) {
            if (oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if (sysUser == null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }
    }

    /**
     * 发送邮件消息
     *
     * @param email
     * @param title
     * @param content
     */
    @Override
    public void sendEmailMsg(String email, String title, String content) {
        EmailSendMsgHandle emailHandle = new EmailSendMsgHandle();
        emailHandle.sendMsg(email, title, content);
    }

    /**
     * 获取公司下级部门和所有用户id信息
     *
     * @param orgCode
     * @return
     */
    @Override
    public List<Map> getDeptUserByOrgCode(String orgCode) {
        //1.获取公司信息
        SysDepart comp = sysDepartService.queryCompByOrgCode(orgCode);
        if (comp != null) {
            //2.获取公司下级部门
            List<SysDepart> departs = sysDepartService.queryDeptByPid(comp.getId());
            //3.获取部门下的人员信息
            List<Map> list = new ArrayList();
            //4.处理部门和下级用户数据
            for (SysDepart dept : departs) {
                Map map = new HashMap(5);
                //部门名称
                String departName = dept.getDepartName();
                //根据部门编码获取下级部门id
                List<String> listIds = departMapper.getSubDepIdsByDepId(dept.getId());
                //根据下级部门ids获取下级部门的所有用户
                List<SysUserDepart> userList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().in("dep_id", listIds));
                List<String> userIds = new ArrayList<>();
                for (SysUserDepart userDepart : userList) {
                    if (!userIds.contains(userDepart.getUserId())) {
                        userIds.add(userDepart.getUserId());
                    }
                }
                map.put("name", departName);
                map.put("ids", userIds);
                list.add(map);
            }
            return list;
        }
        return null;
    }

    /**
     * 查询分类字典翻译
     *
     * @param ids 分类字典表id
     * @return
     */
    @Override
    public List<String> loadCategoryDictItem(String ids) {
        return sysCategoryService.loadDictItem(ids, false);
    }

    /**
     * 根据字典code加载字典text
     *
     * @param dictCode 顺序：tableName,text,code
     * @param keys     要查询的key
     * @return
     */
    @Override
    public List<String> loadDictItem(String dictCode, String keys) {
        String[] params = dictCode.split(",");
        return sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys, false);
    }

    /**
     * 根据字典code查询字典项
     *
     * @param dictCode 顺序：tableName,text,code
     * @param dictCode 要查询的key
     * @return
     */
    @Override
    public List<DictModel> getDictItems(String dictCode) {
        List<DictModel> ls = sysDictService.getDictItems(dictCode);
        if (ls == null) {
            ls = new ArrayList<>();
        }
        return ls;
    }

    /**
     * 根据多个字典code查询多个字典项
     *
     * @param dictCodeList
     * @return key = dictCode ； value=对应的字典项
     */
    @Override
    public Map<String, List<DictModel>> getManyDictItems(List<String> dictCodeList) {
        return sysDictService.queryDictItemsByCodeList(dictCodeList);
    }

    /**
     * 【下拉搜索】
     * 大数据量的字典表 走异步加载，即前端输入内容过滤数据
     *
     * @param dictCode 字典code格式：table,text,code
     * @param keyword  过滤关键字
     * @return
     */
    @Override
    public List<DictModel> loadDictItemByKeyword(String dictCode, String keyword, Integer pageSize) {
        return sysDictService.loadDict(dictCode, keyword, pageSize);
    }

    /**
     * 根据部门编号集合查询对应的人员信息
     *
     * @param deptCodes 部门编码集合
     * @return
     */
    @Override
    public List<LoginUser> getUserByDepIds(List<String> deptCodes) {
        List<LoginUser> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(deptCodes)) {
            List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().in("org_code", deptCodes).eq("status", 1).eq("del_flag", 0));
            for (SysUser user : userList) {
                LoginUser loginUser = new LoginUser();
                loginUser.setId(user.getId());
                loginUser.setUsername(user.getUsername());
                loginUser.setRealname(user.getRealname());
                list.add(loginUser);
            }
        }
        return list;
    }

    @Override
    public List<LoginUser> getUserPersonnel(String deptId) {
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().in("org_id", deptId).eq("status", 1).eq("del_flag", 0));
        List<LoginUser> list = new ArrayList<>();
        for (SysUser user : userList) {
            LoginUser loginUser = new LoginUser();
            loginUser.setId(user.getId());
            loginUser.setUsername(user.getUsername());
            loginUser.setRealname(user.getRealname());
            list.add(loginUser);
        }
        return list;
    }

    @Override
    public List<LoginUser> getUseList(List<String> orgIds) {
        List<LoginUser> list = new ArrayList<>();
        if (CollUtil.isEmpty(orgIds)) {
            return list;
        }
        List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().in("org_id", orgIds).eq("status", 1).eq("del_flag", 0));

        for (SysUser user : userList) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user,loginUser);
            loginUser.setPassword(null);
//            loginUser.setId(user.getId());
//            loginUser.setUsername(user.getUsername());
//            loginUser.setRealname(user.getRealname());
            list.add(loginUser);
        }
        return list;
    }


    @Override
    public Map<String, List<DictModel>> translateManyDict(String dictCodes, String keys) {
        List<String> dictCodeList = Arrays.asList(dictCodes.split(","));
        List<String> values = Arrays.asList(keys.split(","));
        return sysDictService.queryManyDictByKeys(dictCodeList, values);
    }

    @Override
    public List<DictModel> translateDictFromTableByKeys(String table, String text, String code, String keys) {
        return sysDictService.queryTableDictTextByKeys(table, text, code, Arrays.asList(keys.split(",")));
    }

    /**
     * 更新文件业务id
     *
     * @param id                文件主键即文件路径
     * @param businessId        业务数据
     * @param businessTableName 业务模块
     */
    @Override
    public void updateSysAttachmentBiz(String id, String businessId, String businessTableName) {
        LambdaUpdateWrapper<SysAttachment> queryWrapper = new LambdaUpdateWrapper<>();
        id = StrUtil.contains(id, '?') ? id.substring(0, id.indexOf('?')) : id;
        queryWrapper.eq(SysAttachment::getId, id).set(StrUtil.isNotBlank(businessId), SysAttachment::getBusinessId, businessId)
                .set(StrUtil.isNotBlank(businessTableName), SysAttachment::getBusinessTableName, businessTableName);
        sysAttachmentService.update(queryWrapper);
    }

    /**
     * 批量更新文件业务id
     *
     * @param idList            文件主键即文件路径
     * @param businessId        业务数据
     * @param businessTableName 业务模块
     */
    @Override
    public void updateSysAttachmentBiz(List<String> idList, String businessId, String businessTableName) {
        LambdaUpdateWrapper<SysAttachment> queryWrapper = new LambdaUpdateWrapper<>();
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        List<String> list = idList.stream().map(id -> {
            id = StrUtil.contains(id, '?') ? id.substring(0, id.indexOf('?')) : id;
            return id;
        }).collect(Collectors.toList());
        queryWrapper.in(SysAttachment::getId, list).set(StrUtil.isNotBlank(businessId), SysAttachment::getBusinessId, businessId)
                .set(StrUtil.isNotBlank(businessTableName), SysAttachment::getBusinessTableName, businessTableName);
        sysAttachmentService.update(queryWrapper);
    }


    @Override
    public List<SysAttachment> querySysAttachmentByBizIdList(List<String> businessIdList) {
        LambdaQueryWrapper<SysAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysAttachment::getBusinessId, businessIdList);
        List<SysAttachment> attachmentList = sysAttachmentService.getBaseMapper().selectList(wrapper);
        return attachmentList;
    }

    @Override
    public List<SysAttachment> querySysAttachmentByIdList(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        List<String> list = idList.stream().map(id -> {
            id = StrUtil.contains(id, '?') ? id.substring(0, id.indexOf('?')) : id;
            return id;
        }).collect(Collectors.toList());
        LambdaQueryWrapper<SysAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysAttachment::getId, list);
        List<SysAttachment> attachmentList = sysAttachmentService.getBaseMapper().selectList(wrapper);
        return attachmentList;
    }

    @Override
    public List<CsStation> queryAllStation() {
        List<CsStation> stationList = csStationMapper.selectList(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0));
        return stationList;
    }

    @Override
    public List<DeviceTypeTable> selectList(String majorCode, String systemCode, String deviceCode, String name) {

        QueryWrapper<DeviceType> deviceTypeQueryWrapper = new QueryWrapper<DeviceType>();
        deviceTypeQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if (majorCode != null && !"".equals(majorCode)) {
            deviceTypeQueryWrapper.eq("major_code", majorCode);
        }
        if (systemCode != null && !"".equals(systemCode)) {
            deviceTypeQueryWrapper.eq("system_code", systemCode);
        }
        deviceTypeQueryWrapper.orderByDesc("create_time");

        //所选故障的设备类型集合
        ArrayList<String> arrayList = new ArrayList<>();
        if (StrUtil.isNotEmpty(deviceCode)) {
            String[] split = deviceCode.split(",");
            List<String> deviceCodes = Arrays.asList(split);
            arrayList.addAll(deviceCodes);

            LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
            List<Device> devices = deviceMapper.selectList(queryWrapper.in(Device::getCode, arrayList));
            List<String> deviceTypeCodeList = devices.stream().map(Device::getDeviceTypeCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(deviceTypeCodeList)) {
                deviceTypeQueryWrapper.in("`code`", deviceTypeCodeList);
            }
        }
        List<DeviceType> deviceTypeList = deviceTypeService.list(deviceTypeQueryWrapper);
        List<DeviceTypeTable> list = new ArrayList<>();
        deviceTypeList.forEach(deviceType -> {
            DeviceTypeTable deviceTypeTable = new DeviceTypeTable();
            BeanUtils.copyProperties(deviceType, deviceTypeTable);
            deviceTypeTable.setKey(deviceType.getId());
            deviceTypeTable.setValue(deviceType.getCode());
            deviceTypeTable.setLabel(deviceType.getName());
            list.add(deviceTypeTable);
        });
        List<DeviceTypeTable> deviceTypeTree = getDeviceTypeTree(list, "0");
        return deviceTypeTree;
    }
    @Override
    public List<SelectDeviceType> selectDeviceTypeList(String value) {
        List<SelectDeviceType> selectDeviceTypes = new ArrayList<>();
        if (StrUtil.isEmpty(value)){
            // 一级专业显示
            List<DeviceType> deviceTypes = deviceTypeService.lambdaQuery().eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0).list();
            List<String> collect = deviceTypes.stream().map(DeviceType::getMajorCode).distinct().collect(Collectors.toList());
            List<CsMajor> majors = majorService.lambdaQuery().eq(CsMajor::getDelFlag,CommonConstant.DEL_FLAG_0)
                                                             .in(CsMajor::getMajorCode,collect).list();
            majors.forEach(m->{
                SelectDeviceType selectDeviceType = new SelectDeviceType(m.getId(),"0",m.getMajorCode(),m.getMajorName(),false,false);
                selectDeviceTypes.add(selectDeviceType);
            });
        }else {
            // 作为二级子系统显示
            List<CsSubsystem> csSubsystems = csSubsystemService.lambdaQuery().eq(CsSubsystem::getDelFlag,CommonConstant.DEL_FLAG_0).eq(CsSubsystem::getMajorCode,value).list();
            if (CollectionUtil.isNotEmpty(csSubsystems)){
                csSubsystems.forEach(s->{
                    List<DeviceType> deviceTypeList = deviceTypeService.lambdaQuery().eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0)
                            .eq(DeviceType::getMajorCode,s.getMajorCode()).eq(DeviceType::getSystemCode,s.getSystemCode()).list();
                    String str = sysUserRoleMapper.getMajorId(s.getMajorCode());
                    SelectDeviceType selectDeviceType = new SelectDeviceType(s.getId(),str,s.getSystemCode(),s.getSystemName(),CollectionUtil.isEmpty(deviceTypeList),false);
                    selectDeviceTypes.add(selectDeviceType);
                });
            }
            // 二级分类显示
            List<DeviceType> deviceTypes = deviceTypeService.lambdaQuery().eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0)
                    .eq(DeviceType::getMajorCode,value).isNull(DeviceType::getSystemCode).list();
            if (CollectionUtil.isNotEmpty(deviceTypes)){
                deviceTypes.forEach(d->{
                    String str = sysUserRoleMapper.getMajorId(d.getMajorCode());
                    SelectDeviceType selectDeviceType = new SelectDeviceType(d.getId(),str,d.getId(),d.getName(),d.getIsEnd()==1?true:false,true);
                    selectDeviceTypes.add(selectDeviceType);
                });
            }
            //在子系统下的三级分类
            List<DeviceType> deviceTypes1 = deviceTypeService.lambdaQuery().eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0).eq(DeviceType::getSystemCode,value).list();
            if (CollectionUtil.isNotEmpty(deviceTypes1)){
                deviceTypes1.forEach(d->{
                    SelectDeviceType selectDeviceType = new SelectDeviceType(d.getId(),sysUserRoleMapper.getSubsystemId(d.getMajorCode(),d.getSystemCode()),d.getId(),d.getName(),d.getIsEnd()==1?true:false,true);
                    selectDeviceTypes.add(selectDeviceType);
                });
            }
            //无限层级分类
            List<DeviceType> deviceTypes2 = deviceTypeService.lambdaQuery().eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0).eq(DeviceType::getPid,value).list();
            if (CollectionUtil.isNotEmpty(deviceTypes2)){
                deviceTypes2.forEach(d->{
                    SelectDeviceType selectDeviceType = new SelectDeviceType(d.getId(),d.getPid(),d.getId(),d.getName(),d.getIsEnd()==1?true:false,true);
                    selectDeviceTypes.add(selectDeviceType);
                });
            }
        }
        return selectDeviceTypes;
    }
    public List<DeviceTypeTable> getDeviceTypeTree(List<DeviceTypeTable> list, String pid) {
        List<DeviceTypeTable> children = list.stream().filter(deviceTypeTable -> deviceTypeTable.getPid().equals(pid)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(children)) {
            for (DeviceTypeTable deviceTypeTable : children) {
                deviceTypeTable.setChildren(getDeviceTypeTree(list, deviceTypeTable.getId()));
            }
        }
        return children;
    }

    @Override
    public List<CsUserDepartModel> getDepartByUserId(String id) {
        return iCsUserDepartService.getDepartByUserId(id);
    }

    @Override
    public List<CsUserMajorModel> getMajorByUserId(String id) {
        return iCsUserMajorService.getMajorByUserId(id);
    }

    @Override
    public List<CsUserStationModel> getStationByUserId(String id) {
        return csUserStaionMapper.getStationByUserId(id);
    }

    @Override
    public List<CsUserSubsystemModel> getSubsystemByUserId(String id) {
        return csUserSubsystemMapper.getSubsystemByUserId(id);
    }

    public List<SysUser> getOrgUsersByOrgid(String orgId) {
        return userMapper.selectList(new QueryWrapper<SysUser>().eq("org_id", orgId));
    }

    @Override
    public List<String> getUserListByName(String realName) {
        return userMapper.getUserListByName(realName);
    }

    @Override
    public DictModel dictById(String id) {
        return sysDictService.dictById(id);
    }

    @Override
    public String getLineNameByCode(String code) {
        LambdaQueryWrapper<CsLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsLine::getLineCode,code).eq(CsLine::getDelFlag,0);
        return lineMapper.selectOne(wrapper).getLineName();
    }

    /**
     * 添加定时任务
     *
     * @param quartzJobDTO
     */
    @Override
    public void saveAndScheduleJob(QuartzJobDTO quartzJobDTO) {
        QuartzJob quartzJob = new QuartzJob();
        BeanUtils.copyProperties(quartzJobDTO, quartzJob);
        quartzJobService.saveAndScheduleJob(quartzJob);
        quartzJobDTO.setId(quartzJob.getId());
    }

    /**
     * 删除定时任务
     *
     * @param quartzJobDTO
     */
    @Override
    public void deleteAndStopJob(QuartzJobDTO quartzJobDTO) {
        QuartzJob quartzJob = new QuartzJob();
        BeanUtils.copyProperties(quartzJobDTO, quartzJob);
        quartzJobService.deleteAndStopJob(quartzJob);
    }

    @Override
    public SysDepartModel getDepartByOrgCode(String orgCode) {
        SysDepart sysDepart = departMapper.queryDepartByOrgCode(orgCode);
        if (Objects.isNull(sysDepart)) {
            return null;
        }
        SysDepartModel sysDepartModel = new SysDepartModel();
        BeanUtils.copyProperties(sysDepart, sysDepartModel);
        return sysDepartModel;
    }

    @Override
    public String getPosition(String code) {
        String name = null;
        CsLine line = csStationMapper.getLineName(code);
        CsStation station = csStationMapper.getStationName(code);
        CsStationPosition position = csStationMapper.getPositionName(code);
        if (ObjectUtil.isNotEmpty(line)) {
            name = line.getLineName();
        }
        if (ObjectUtil.isNotEmpty(station)) {
            name = station.getStationName();
        }
        if (ObjectUtil.isNotEmpty(position)) {
            name = position.getPositionName();
        }
        return name;
    }

    /**
     * 通过用户id查询角色名称
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getRoleNamesById(String userId) {
        List<String> roles = sysUserRoleMapper.getRoleName(userId);
        return roles;
    }

    /**
     * 通过部门编码查询工区信息
     *
     * @param orgCode
     * @return
     */
    @Override
    public List<SiteModel> getSiteByOrgCode(String orgCode) {
        if (StrUtil.isEmpty(orgCode)) {
            return CollUtil.newArrayList();
        }
        List<SiteModel> result = workAreaMapper.getSiteByOrgCode(orgCode);
        return result;
    }

    /**
     * 通过线路查询对应的站点
     *
     * @param lineCode
     * @return
     */
    @Override
    public List<String> getStationCodeByLineCode(String lineCode) {
        if (StrUtil.isNotEmpty(lineCode)) {
            List<String> lineCodeList = StrUtil.split(lineCode, ',');
            LambdaQueryWrapper<CsStation> csStationLambdaQueryWrapper = new LambdaQueryWrapper<>();
            csStationLambdaQueryWrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
            if (CollUtil.isNotEmpty(lineCodeList)) {
                csStationLambdaQueryWrapper.in(CsStation::getLineCode, lineCodeList);
            }
            List<CsStation> csStations = csStationMapper.selectList(csStationLambdaQueryWrapper);
            if (CollUtil.isNotEmpty(csStations)) {
                return csStations.stream().map(CsStation::getStationCode).collect(Collectors.toList());
            }
        }
        return CollUtil.newArrayList();
    }

    @Override
    public List<String> getStationCodeByLineCode(List<String> lineCodes) {
        List<String> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(lineCodes)) {
            LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0).in(CsStation::getLineCode, lineCodes);
            List<CsStation> stationList = csStationMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(stationList)) {
                return stationList.stream().map(CsStation::getStationCode).collect(Collectors.toList());
            }
        }
        return list;
    }

    @Override
    public CsStation getPositionCodeByStationCode(String stationCode) {
        CsStation csStation1 = new CsStation();
        if (StrUtil.isNotBlank(stationCode)) {
            LambdaQueryWrapper<CsStation> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0).eq(CsStation::getStationCode, stationCode);
            CsStation csStation = csStationMapper.selectOne(wrapper1);

            LambdaQueryWrapper<CsStationPosition> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0).eq(CsStationPosition::getStaionCode, stationCode);
            List<CsStationPosition> csStationPositions = csStationPositionMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(csStationPositions)) {
                csStation.setCsStationPositionList(csStationPositions);
            }
            return csStation;

        }
        return csStation1;
    }

    /**
     * 通过线路和专业过滤出班组
     *
     * @param lineCode
     * @return
     */
    @Override
    public List<String> getTeamBylineAndMajor(String lineCode) {

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(user)) {
            throw new AiurtBootException("请重新登录");
        }

        // 线路筛选
        List<String> lineCodeList = CollUtil.newArrayList();
        if (StrUtil.isNotEmpty(lineCode)) {
            lineCodeList = StrUtil.split(lineCode, ',');
            List<String> lineList = workAreaMapper.getTeamBylineAndMajor(lineCodeList, new ArrayList<>());
            if (CollUtil.isEmpty(lineList)) {
                return CollUtil.newArrayList();
            }
        }

        // 专业筛选
        List<CsUserMajorModel> majorByUserId = this.getMajorByUserId(user.getId());
        List<String> majorList = CollUtil.newArrayList();

        // 筛选无数据，则直接返回
        if (CollUtil.isEmpty(majorByUserId)) {
            return CollUtil.newArrayList();
        }

        if (CollUtil.isNotEmpty(majorByUserId)) {
            majorList = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
            List<String> majors = workAreaMapper.getTeamBylineAndMajor(new ArrayList<>(), majorList);
            if (CollUtil.isEmpty(majors)) {
                return CollUtil.newArrayList();
            }
        }

        return workAreaMapper.getTeamBylineAndMajor(lineCodeList, majorList);
    }

    /**
     * 通过线路和专业过滤出班组详细信息
     *
     * @param lineCode
     * @return
     */
    @Override
    public List<SysDepartModel> getTeamBylineAndMajors(String lineCode) {
        return workAreaService.getTeamBylineAndMajors(lineCode);
    }

    @Override
    public List<SysDepartModel> getUserSysDepart(String userId) {
        List<SysDepartModel> sysDepartModels = sysDepartMapper.getUserDepart(userId);
        if (CollUtil.isEmpty(sysDepartModels)) {
            return CollUtil.newArrayList();
        } else {
            List<SysDepartModel> list = new ArrayList<>();
            for (SysDepartModel model : sysDepartModels) {
                List<SysDepartModel> models = sysDepartMapper.getUserOrgCategory(model.getOrgCode());
                if (CollUtil.isNotEmpty(models)) {
                    list.addAll(models);
                }
            }
            if (CollUtil.isEmpty(list)) {
                return CollUtil.newArrayList();
            } else {
                list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SysDepartModel::getId))), ArrayList::new));
                return list;
            }

        }
    }

    @Override
    public JSONObject getCsMajorByCode(String majorCode) {
        LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsMajor::getMajorCode, majorCode).eq(CsMajor::getDelFlag,CommonConstant.DEL_FLAG_0).last("limit 1");
        CsMajor csMajor = majorService.getBaseMapper().selectOne(wrapper);
        if (Objects.isNull(csMajor)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(csMajor));
    }

    @Override
    public List<String> getCsMajorNamesByCodes(List<String> majorCode) {
        LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CsMajor::getMajorCode, majorCode).eq(CsMajor::getDelFlag, 0);
        if (CollectionUtil.isEmpty(majorCode)) {
            return new ArrayList<>();
        }
        List<CsMajor> csMajor = majorService.getBaseMapper().selectList(wrapper);
        if (CollectionUtil.isEmpty(csMajor)) {
            return new ArrayList<>();
        }
        return csMajor.stream().map(CsMajor::getMajorName).distinct().collect(Collectors.toList());
    }

    @Override
    public JSONObject getCsMajorByName(String majorName) {
        LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsMajor::getMajorName, majorName).eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        CsMajor csMajor = majorService.getBaseMapper().selectOne(wrapper);
        if (Objects.isNull(csMajor)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(csMajor));
    }

    @Override
    public JSONObject getSystemName(String majorCode, String systemName) {
        LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsSubsystem::getSystemName, systemName).eq(CsSubsystem::getMajorCode, majorCode).eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        CsSubsystem subsystem = subsystemMapper.selectOne(wrapper);
        if (Objects.isNull(subsystem)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(subsystem));
    }

    @Override
    public List<String> getSystemNames(List<String> systemCodes) {
        LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CsSubsystem::getSystemCode, systemCodes).eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (CollectionUtil.isEmpty(systemCodes)) {
            return new ArrayList<>();
        }
        List<CsSubsystem> subsystem = subsystemMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(subsystem)) {
            return new ArrayList<>();
        }
        return subsystem.stream().map(CsSubsystem::getSystemName).distinct().collect(Collectors.toList());
    }

    @Override
    public DeviceType getCsMajorByCodeTypeName(String majorCode, String deviceTypeName, String systemCode) {
        LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceType::getMajorCode, majorCode).eq(DeviceType::getName, deviceTypeName).eq(DeviceType::getSystemCode, systemCode).eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        DeviceType deviceType = deviceTypeService.getBaseMapper().selectOne(wrapper);
        if (Objects.isNull(deviceType)) {
            return null;
        }
        return deviceType;
    }

    @Override
    public Map<String, String> getLineNameByCode(List<String> lineCodes) {
        LambdaQueryWrapper<CsLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (CollectionUtil.isNotEmpty(lineCodes)) {
            wrapper.in(CsLine::getLineCode, lineCodes);
        }
        List<CsLine> lines = lineMapper.selectList(wrapper);
        Map<String, String> lineMap = lines.stream()
                .collect(Collectors.toMap(k -> k.getLineCode(), v -> v.getLineName(), (a, b) -> a));
        return lineMap;
    }

    @Override
    public Map<String, String> getStationNameByCode(List<String> stationCodes) {
        LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (CollectionUtil.isNotEmpty(stationCodes)) {
            wrapper.in(CsStation::getStationCode, stationCodes);
        }
        List<CsStation> stations = csStationMapper.selectList(wrapper);
        Map<String, String> stationMap = stations.stream()
                .collect(Collectors.toMap(k -> k.getStationCode(), v -> v.getStationName(), (a, b) -> a));
        return stationMap;
    }

    /**
     * 根据用户名或者用户账号查询用户信息
     *
     * @param userNameList
     * @return
     */
    @Override
    public List<LoginUser> getLoginUserList(List<String> userNameList) {
        if (CollUtil.isEmpty(userNameList)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUserList = new ArrayList<>();
        List<SysUser> sysUserList = userMapper.queryUserListByName(userNameList);
        if (CollectionUtil.isEmpty(sysUserList)) {
            return loginUserList;
        }
        sysUserList.forEach(sysUser -> {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(sysUser, loginUser);
            loginUserList.add(loginUser);
        });
        return loginUserList;
    }

    @Override
    public String getUserName(String realName) {
        return userMapper.getUserName(realName);
    }

    @Override
    public List<String> getUserLikeName(String realName) {
        return userMapper.getUserLikeName(realName);
    }

    /**
     * 根据站点id获取站点信息
     *
     * @param station
     * @return
     */
    @Override
    public JSONObject getCsStationById(String station) {
        CsStation csStation = csStationMapper.selectById(station);

        if (Objects.isNull(csStation)) {
            return null;
        }
        return JSONObject.parseObject(JSON.toJSONString(csStation));
    }

    @Override
    public String remoteUploadLocal(String remoteFileUrl, String bizPath) {
        if (StrUtil.isEmpty(remoteFileUrl)) {
            return "";
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            // 转义url
            String newUrl = escapeUrl(remoteFileUrl);
            // 发送远程请求获取图片资源
            URL url = new URL(newUrl);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(5 * 1000);
            httpUrlConnection.connect();

            // 输入流
            is = httpUrlConnection.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;

            String ctxPath = uploadpath;
            File file = new File(ctxPath + File.separator + bizPath + File.separator);
            if (!file.exists()) {
                // 创建文件根目录
                file.mkdirs();
            }

            // 获取文件名
            String fileName = remoteFileUrl.substring(remoteFileUrl.lastIndexOf("/") + 1);

            os = new FileOutputStream(file.getPath() + "\\" + fileName);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            String dbpath = null;
            if (oConvertUtils.isNotEmpty(bizPath)) {
                dbpath = bizPath + File.separator + fileName;
            } else {
                dbpath = fileName;
            }
            if (dbpath.contains(SymbolConstant.DOUBLE_BACKSLASH)) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            // 完毕，关闭所有链接
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public List<SysUserRoleModel> getUserByRoleId(String roleId) {
        List<SysUserRoleModel> list = sysUserRoleMapper.getUserByRoleId(roleId);
        return list;
    }

    @Override
    public String getRoleIdByCode(String roleCode) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysRole::getRoleCode, roleCode).last("limit 1");
        SysRole sysRole = sysRoleMapper.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(sysRole)) {
            return sysRole.getId();
        }
        return "";
    }

    @Override
    public List<CsWorkAreaModel> getWorkAreaInfo() {
        List<WorkArea> workAreas = workAreaMapper.selectList(new QueryWrapper<>());
        List<CsWorkAreaModel> list = new ArrayList<>();
        for (WorkArea workArea : workAreas) {
            CsWorkAreaModel workAreaModel = new CsWorkAreaModel();
            BeanUtils.copyProperties(workArea, workAreaModel);
            list.add(workAreaModel);
        }
        return list;
    }

    @Override
    public List<String> getWorkAreaStationCodeByUserId(String userId) {
        return workAreaMapper.getWorkAreaStationCodeByUserId(userId);
    }

    @Override
    public List<CsStation> getStationInfoByNameAndLineId(String cellText, String line) {
        QueryWrapper<CsLine> lineWrapper = new QueryWrapper<>();
        lineWrapper.lambda().eq(CsLine::getId, line).last("limit 1");
        CsLine csLine = lineMapper.selectOne(lineWrapper);
        if (ObjectUtil.isEmpty(csLine) || ObjectUtil.isEmpty(csLine.getLineCode())) {
            return Collections.emptyList();
        }
        QueryWrapper<CsStation> stationWrapper = new QueryWrapper<>();
        stationWrapper.lambda().eq(CsStation::getLineCode, csLine.getLineCode())
                .eq(CsStation::getStationName, cellText);
        List<CsStation> stations = csStationMapper.selectList(stationWrapper);
        return stations;
    }

    @Override
    public String getWorkAreaNameByCode(String workAreaCode) {
        return workAreaMapper.getWorkAreaNameByCode(workAreaCode);
    }

    @Override
    public List<SysDepartModel> getUserDepartCodes() {
        //获取用户的所属部门及所属部门子部门
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = user.getOrgCode();
        return sysDepartMapper.getUserOrgCategory(orgCode);
    }

    @Override
    public List<SysDeptUserModel> getDeptUserGanged() {
        QueryWrapper<SysUser> userWrapper = new QueryWrapper<>();
        userWrapper.lambda().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysUser> userList = userMapper.selectList(userWrapper);
        if (CollectionUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUsers = new ArrayList<>();
        userList.forEach(user -> {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        });
        Map<String, List<LoginUser>> listMap = loginUsers.stream()
                .filter(l -> StrUtil.isNotEmpty(l.getOrgCode()))
                .collect(Collectors.groupingBy(LoginUser::getOrgCode));

        QueryWrapper<SysDepart> deptWrapper = new QueryWrapper<>();
        deptWrapper.lambda().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(SysDepart::getOrgCode, listMap.keySet());
        List<SysDepart> departs = departMapper.selectList(deptWrapper);
        Map<String, String> deptMap = departs.stream()
                .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));

        List<SysDeptUserModel> deptUserModels = new ArrayList<>();
        for (String key : listMap.keySet()) {
            deptUserModels.add(new SysDeptUserModel(key, deptMap.get(key), listMap.get(key)));
        }
        return deptUserModels;
    }

    @Override
    public List<LoginUser> getUserByDeptCode(String deptCode) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (StrUtil.isNotEmpty(deptCode)) {
            wrapper.lambda().eq(SysUser::getOrgCode, deptCode);
        }
        List<SysUser> users = userMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUsers = new ArrayList<>();
        for (SysUser user : users) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }

    @Override
    public Set<SysDepartModel> getDeptByUserId(String ids) {
        String[] split = ids.split(",");
        Set<SysDepartModel> sysDepartModels = new HashSet<>();
        if(split.length!=0){
            List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, split));
            if(CollUtil.isNotEmpty(userList)){
                List<String> userOrgIds = userList.stream().map(SysUser::getOrgId).collect(Collectors.toList());
                List<SysDepart> sysDepartList = sysDepartService.list(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0).in(SysDepart::getId, userOrgIds));
               if(CollUtil.isNotEmpty(sysDepartList)){
                   for (SysDepart sysDepart : sysDepartList) {
                       SysDepartModel sysDepartModel = new SysDepartModel();
                       BeanUtils.copyProperties(sysDepart,sysDepartModel);
                       sysDepartModels.add(sysDepartModel);
                   }
               }
            }
        }
        return sysDepartModels;
    }

    @Override
    public List<PatrolStandardItemsModel> patrolStandardList(String id) {
        List<PatrolStandardItemsModel> patrolStandardItemsModels = new ArrayList<>();
        List<PatrolStandardItems> patrolStandardItems = patrolStandardItemsService.queryPageList(id);
        if (CollUtil.isNotEmpty(patrolStandardItems)) {
            //父级
            patrolStandardItems.forEach(e -> {
                PatrolStandardItemsModel patrolStandardItemsModel = new PatrolStandardItemsModel();
                BeanUtils.copyProperties(e, patrolStandardItemsModel);

                //子级
                if (CollUtil.isNotEmpty(e.getChildren())) {
                    List<PatrolStandardItemsModel> patrolStandardItemsModels1 = new ArrayList<>();
                    e.getChildren().forEach(q -> {
                        PatrolStandardItemsModel patrolStandardItemsModel1 = new PatrolStandardItemsModel();
                        BeanUtils.copyProperties(q, patrolStandardItemsModel1);
                        patrolStandardItemsModels1.add(patrolStandardItemsModel1);
                    });
                    patrolStandardItemsModel.setChildren(patrolStandardItemsModels1);
                }

                patrolStandardItemsModels.add(patrolStandardItemsModel);
            });
        }
        return patrolStandardItemsModels;
    }

    private String escapeUrl(String remoteFileUrl) throws UnsupportedEncodingException {
        // 先替换空格
        remoteFileUrl = remoteFileUrl.replaceAll(" ", "%20");

        // 中文正则
        String pattern = "[\u4e00-\u9fa5]+";

        // 匹配
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(remoteFileUrl);
        StringBuffer stringBuffer = new StringBuffer();
        // m.find()查找
        while (m.find()) {
            // m.start()连续中文的字符串的开始下标， m.end()连续中文的字符串的最后一个字符下标
            String substring = remoteFileUrl.substring(m.start(), m.end());
            // m.group()获取字符
            String group = m.group();
            // 中文转义
            String encode = URLEncoder.encode(group, "utf-8");
            m.appendReplacement(stringBuffer, group.replace(substring, encode));
        }
        m.appendTail(stringBuffer);
        return ObjectUtil.isNotEmpty(stringBuffer) ? stringBuffer.toString() : remoteFileUrl;
    }

    @Override
    public Map<String, String> getDeviceNameByCode(List<String> deviceCodes) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (CollectionUtil.isNotEmpty(deviceCodes)) {
            wrapper.in(Device::getCode, deviceCodes);
        }
        List<Device> device = deviceMapper.selectList(wrapper);
        Map<String, String> deviceMap = device.stream()
                .filter(l -> ObjectUtil.isNotEmpty(l.getName()))
                .collect(Collectors.toMap(k -> k.getCode(), v -> v.getName(), (a, b) -> a));
        return deviceMap;
    }

    @Override
    public JSONObject getDeviceByCode(String code) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(code)) {
            wrapper.eq(Device::getCode, code);
        }
        Device device = deviceMapper.selectOne(wrapper);
        if (Objects.isNull(device)) {
            return null;
        }
        return JSONObject.parseObject(JSON.toJSONString(device));
    }

    @Override
    public String getLineCodeById(String lineId) {
        CsLine line = lineMapper.selectById(lineId);
        String lineCode = null;
        if (ObjectUtil.isNotEmpty(line)) {
            lineCode = line.getLineCode();
        }
        return lineCode;
    }

    @Override
    public boolean isNullSafetyPrecautions(String majorCode, String systemCode, String code, Integer status) {
        List<CsSafetyAttention> csSafetyAttentions = new ArrayList<>();
        LambdaQueryWrapper<SafetyRelatedForm> wrapper = new LambdaQueryWrapper<>();
        if (0 == status) {
            wrapper.eq(SafetyRelatedForm::getPatrolStandardCode, code);
        }
        if (1 == status) {
            wrapper.eq(SafetyRelatedForm::getInspectionCode, code);
        }
        List<SafetyRelatedForm> safetyRelatedForms = safetyRelatedFormMapper.selectList(wrapper);
        //判断是否修改过关联表
        if (CollectionUtil.isNotEmpty(safetyRelatedForms)) {
            //如果修改 查询已经保存的数据
            wrapper.eq(SafetyRelatedForm::getDelFlag, 0);
            List<SafetyRelatedForm> list = safetyRelatedFormMapper.selectList(wrapper);
            List<String> str = list.stream().map(l -> l.getSafetyAttentionId()).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(str)) {
                csSafetyAttentions = csSafetyAttentionMapper.selectList(new LambdaQueryWrapper<CsSafetyAttention>().in(CsSafetyAttention::getId, str));
            }
        } else {
            //没有修改按照专业子系统查询
            LambdaQueryWrapper<CsSafetyAttention> wrapper1 = new LambdaQueryWrapper<CsSafetyAttention>();
            wrapper1.eq(CsSafetyAttention::getMajorCode, majorCode);
            if (StrUtil.isNotEmpty(systemCode)) {
                wrapper1.eq(CsSafetyAttention::getSystemCode, systemCode);
            }
            //需要查询启动和未删除
            wrapper1.eq(CsSafetyAttention::getState, 1).eq(CsSafetyAttention::getDelFlag, 0);
            csSafetyAttentions = csSafetyAttentionMapper.selectList(wrapper1);
        }
        if (CollUtil.isNotEmpty(csSafetyAttentions)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getLineIdByCode(String lineCode) {
        QueryWrapper<CsLine> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsLine::getLineCode, lineCode)
                .last("limit 1");
        CsLine csLine = lineMapper.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(csLine)) {
            return csLine.getId();
        }
        return "";
    }


    @Override
    public List<LoginUser> getUserByPost(int post) {
        QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUser::getJobName, String.valueOf(post));
        List<SysUser> users = userMapper.selectList(userQueryWrapper);
        if (CollectionUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUsers = new ArrayList<>();
        for (SysUser user : users) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }

    /**
     * 根据业务类型及业务id查询消息
     *
     * @param busType 业务类型
     * @param busId   业务id
     * @return 消息的id
     */
    @Override
    public String getSysAnnounByBusTypeAndBusId(String busType, String busId) {
        SysAnnouncement announcement = sysAnnouncementMapper.selectOne(new QueryWrapper<SysAnnouncement>().eq("bus_type", busType).eq("bus_id", busId));
        return ObjectUtil.isNotEmpty(announcement) ? announcement.getId() : "";
    }

    /**
     * 根据部门，角色编码查询人员账号
     *
     * @param orgCode  组织机构编码
     * @param roleCode 角色编码
     * @return 人员账号用逗号隔开
     */
    @Override
    public String getUserNameByOrgCodeAndRoleCode(List<String> orgCode, List<String> roleCode) {
        if (CollUtil.isEmpty(orgCode) || CollUtil.isEmpty(roleCode)) {
            return "";
        }
        List<String> result = userMapper.getUserNameByOrgCodeAndRoleCode(orgCode, roleCode);
        return CollUtil.isNotEmpty(result) ? StrUtil.join(",", result) : "";
    }
    /**
     * 根据部门，角色编码查询人员姓名
     *
     * @param orgCode  组织机构编码
     * @param roleCode 角色编码
     * @return 人员账号用逗号隔开
     */
    @Override
    public String getRealNameByOrgCodeAndRoleCode(List<String> orgCode, List<String> roleCode) {
        if (CollUtil.isEmpty(orgCode) || CollUtil.isEmpty(roleCode)) {
            return "";
        }
        List<String> result = userMapper.getRealNameByOrgCodeAndRoleCode(orgCode, roleCode);
        return CollUtil.isNotEmpty(result) ? StrUtil.join(",", result) : "";
    }

    @Override
    public List<CsWorkAreaModel> getWorkAreaByCode(String stationCode) {
        List<WorkArea> workAreas = workAreaMapper.selectWorkAreaList(stationCode);
        List<CsWorkAreaModel> csWorkAreaModels = new ArrayList<>();
        if (CollUtil.isNotEmpty(workAreas)) {
            for (WorkArea workArea : workAreas) {
                CsWorkAreaModel csWorkAreaModel = new CsWorkAreaModel();
                BeanUtils.copyProperties(workArea, csWorkAreaModel);
                List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getWorkAreaCode,workArea.getCode()));
                List<String> orgCodeList = workAreaOrgList.stream().map(WorkAreaOrg::getOrgCode).collect(Collectors.toList());
                csWorkAreaModel.setOrgCodeList(orgCodeList);
                csWorkAreaModels.add(csWorkAreaModel);
            }
        }
        return csWorkAreaModels;
    }

    @Override
    public List<CsWorkAreaModel> getWorkAreaByLineCode(String lineCode) {
        List<WorkArea> workAreas = workAreaMapper.selectWorkAreaListByLineCode(lineCode);
        List<CsWorkAreaModel> csWorkAreaModels = new ArrayList<>();
        if (CollUtil.isNotEmpty(workAreas)) {
            for (WorkArea workArea : workAreas) {
                CsWorkAreaModel csWorkAreaModel = new CsWorkAreaModel();
                BeanUtils.copyProperties(workArea, csWorkAreaModel);
                List<WorkAreaOrg> workAreaOrgList = workAreaOrgMapper.selectList(new LambdaQueryWrapper<WorkAreaOrg>().eq(WorkAreaOrg::getWorkAreaCode,workArea.getCode()));
                List<String> orgCodeList = workAreaOrgList.stream().map(WorkAreaOrg::getOrgCode).collect(Collectors.toList());
                csWorkAreaModel.setOrgCodeList(orgCodeList);
                csWorkAreaModels.add(csWorkAreaModel);
            }
        }
        return csWorkAreaModels;
    }

    @Override
    public JSONObject getPositionMessage(String code) {
        String json = null;
        CsLine line = csStationMapper.getLineName(code);
        CsStation station = csStationMapper.getStationName(code);
        CsStationPosition position = csStationMapper.getPositionName(code);
        if (ObjectUtil.isNotEmpty(line)) {
            json = JSONObject.toJSONString(line);
        }
        if (ObjectUtil.isNotEmpty(station)) {
            json = JSONObject.toJSONString(station);
        }
        if (ObjectUtil.isNotEmpty(position)) {
            json = JSONObject.toJSONString(position);
        }
        return JSONObject.parseObject(json);
    }

    @Override
    public String getFullNameByPositionCode(String positionCode) {
        if (StrUtil.isEmpty(positionCode)) {
            return null;
        }
        return csStationPositionMapper.getFullNameByPositionCode(positionCode);
    }

    @Override
    public String getUserNameByDeptAuthCodeAndRoleCode(List<String> orgCodes, List<String> roleCodes) {
        if (CollUtil.isEmpty(orgCodes) || CollUtil.isEmpty(roleCodes)) {
            return "";
        }
        List<String> result = userMapper.getUserNameByDeptAuthCodeAndRoleCode(orgCodes, roleCodes);
        return CollUtil.isNotEmpty(result) ? StrUtil.join(",", result) : "";
    }


    @Override
    public JSONObject getDepartByName(String departName) {
        LambdaQueryWrapper<SysDepart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDepart::getDepartName, departName).eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        SysDepart sysDepart = departMapper.selectOne(wrapper);
        if (Objects.isNull(sysDepart)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(sysDepart));
    }

    @Override
    public JSONObject getDepartByNameAndParentId(String departName, String parentId) {
        LambdaQueryWrapper<SysDepart> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(parentId)) {
            wrapper.eq(SysDepart::getParentId, parentId);
        }
        wrapper.eq(SysDepart::getDepartName, departName).eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        SysDepart sysDepart = departMapper.selectOne(wrapper);
        if (Objects.isNull(sysDepart)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(sysDepart));
    }

    @Override
    public List<SysDepartModel> getDepartByParentId(String parentId) {
        LambdaQueryWrapper<SysDepart> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(parentId)) {
            wrapper.eq(SysDepart::getParentId, parentId);
        }
        wrapper.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysDepart> list = departMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        List<SysDepartModel> list1 = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                SysDepartModel sysDepartModel = new SysDepartModel();
                BeanUtil.copyProperties(list.get(i),sysDepartModel);
                list1.add(sysDepartModel);
                List<SysDepartModel> departByParentId = getDepartByParentId(list.get(i).getId());
                if(CollectionUtil.isNotEmpty(departByParentId)){
                    list1.addAll(departByParentId);
                }
            }
        }
        return list1;
    }

    @Override
    public  List<String> sysDepartList(String orgCode){
        List<SysDepart> list = departMapper.selectList(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0));
        List<SysDepartModel> modelList = new ArrayList<>();
        for (SysDepart sysDepart : list) {
            SysDepartModel model = new SysDepartModel();
            BeanUtils.copyProperties(sysDepart,model);
            modelList.add(model);
        }
        SysDepart sysDepart = departMapper.selectOne(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SysDepart::getOrgCode,orgCode));
        if(ObjectUtil.isEmpty(sysDepart)){
            return Collections.emptyList();
        }
        SysDepartModel model = new SysDepartModel();
        BeanUtils.copyProperties(sysDepart,model);
        List<SysDepartModel> allChildren = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(model)&&CollUtil.isNotEmpty(modelList)){
            List<SysDepartModel> sysDepartList = treeMenuList(modelList, model, allChildren);
            sysDepartList.add(model);
            if (CollectionUtil.isEmpty(sysDepartList)) {
                return Collections.emptyList();
            }
            List<String> codeList = sysDepartList.stream().map(s -> s.getOrgCode()).collect(Collectors.toList());
            return codeList;
        }
        return null;
    }
    /**
     * 获取某个父节点下面的所有子节点
     * @param list
     * @param depart
     * @param allChildren
     * @return
     */
    public static List<SysDepartModel> treeMenuList(List<SysDepartModel> list, SysDepartModel depart, List<SysDepartModel> allChildren) {
        for (SysDepartModel sysDepart : list) {
            //遍历出父id等于参数的id，add进子节点集合
            if (sysDepart.getParentId().equals(depart.getId())) {
                //递归遍历下一级
                treeMenuList(list, sysDepart, allChildren);
                allChildren.add(sysDepart);
            }
        }
        return allChildren;
    }


    @Override
    public JSONObject getLineByName(String lineName) {
        LambdaQueryWrapper<CsLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsLine::getLineName, lineName).eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        CsLine csLine = lineMapper.selectOne(wrapper);
        if (Objects.isNull(csLine)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(csLine));
    }

    @Override
    public JSONObject getStationByName(String stationName) {
        LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStation::getStationName, stationName).eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        CsStation csStation = csStationMapper.selectOne(wrapper);
        if (Objects.isNull(csStation)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(csStation));
    }

    @Override
    public JSONObject getPositionByName(String positionName, String lineCode, String stationCode) {
        LambdaQueryWrapper<CsStationPosition> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(lineCode)) {
            wrapper.eq(CsStationPosition::getLineCode, lineCode);
        }
        if (StrUtil.isNotBlank(lineCode)) {
            wrapper.eq(CsStationPosition::getStaionCode, stationCode);
        }
        wrapper.eq(CsStationPosition::getPositionName, positionName).eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
        CsStationPosition stationPosition = csStationPositionMapper.selectOne(wrapper);
        if (Objects.isNull(stationPosition)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(stationPosition));
    }

    @Override
    public List<LoginUser> getUserByRealName(String realName, String workNo) {
        return userMapper.getUserByRealName(realName, workNo);
    }


    @Override
    public SysAttachment getFilePath(String filePath) {
        SysAttachment sysAttachment = sysAttachmentService.getById(filePath);
        if (Objects.isNull(sysAttachment)) {
            return null;
        }
        return sysAttachment;

    }


    @Override
    public List<LoginUser> getAllUsers() {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);

        List<SysUser> users = userMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUsers = new ArrayList<>();
        for (SysUser user : users) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }

    @Override
    public JSONObject getCsStationByCode(String stationCode) {
        CsStation csStation = csStationMapper.selectOne(new LambdaQueryWrapper<CsStation>().eq(CsStation::getStationCode, stationCode));
        if (Objects.isNull(csStation)) {
            return null;
        }
        return JSONObject.parseObject(JSON.toJSONString(csStation));
    }

    @Override
    public List<String> getSublevelOrgCodes(String orgCode) {
        SysDepart depart = sysDepartService.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysDepart::getOrgCode, orgCode).one();
        if (ObjectUtil.isEmpty(depart)) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>(Arrays.asList(depart.getId()));
        List<String> tempIds = new ArrayList<>();
        tempIds.addAll(ids);
        do {
            List<SysDepart> departList = sysDepartService.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(SysDepart::getParentId, tempIds).list();
            tempIds = departList.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode())).map(SysDepart::getId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(tempIds)) {
                ids.addAll(tempIds);
            }
        } while (CollectionUtil.isNotEmpty(tempIds));
        List<SysDepart> orgCodeList = sysDepartService.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(SysDepart::getId, ids)
                .list();
        List<String> orgCodes = orgCodeList.stream().map(SysDepart::getOrgCode).collect(Collectors.toList());
        return orgCodes;
    }

    @Override
    public void processingTreeList(String name, List<SelectTable> list) {
        Iterator<SelectTable> iterator = list.iterator();
        while (iterator.hasNext()) {
            SelectTable next = iterator.next();
            if (StrUtil.containsAnyIgnoreCase(next.getLabel(),name)) {
                //名称匹配则赋值颜色
                next.setColor("#FF5B05");
            }
            List<SelectTable> children = next.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                processingTreeList(name, children);
            }
            //如果没有子级，并且当前不匹配，则去除
            if (CollUtil.isEmpty(next.getChildren()) && StrUtil.isEmpty(next.getColor())) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean selectTableName(String dbName, String tableName) {
        String string = userMapper.selectTableName(dbName, tableName);
        if (StrUtil.isNotBlank(string)){
            return true;
        }else {
            return false;
        }
    }


    //-------------------------------------流程节点发送模板消息-----------------------------------------------
    @Autowired
    private QywxSendMsgHandle qywxSendMsgHandle;

    @Autowired
    private SystemSendMsgHandle systemSendMsgHandle;

    @Autowired
    private EmailSendMsgHandle emailSendMsgHandle;

    @Autowired
    private DdSendMsgHandle ddSendMsgHandle;

    @Override
    public void sendTemplateMessage(MessageDTO message) {
        String type = message.getType();
        //update-begin-author:taoyan date:2022-7-9 for: 将模板解析代码移至消息发送, 而不是调用的地方
        String templateCode = message.getTemplateCode();
        String content = null;
        if(StrUtil.isNotBlank(templateCode)){
            SysMessageTemplate templateEntity = getTemplateEntity(templateCode);
            boolean isMarkdown =CommonConstant.MSG_TEMPLATE_TYPE_MD.equals(templateEntity.getTemplateType());
            content = templateEntity.getTemplateContent();
            if(StrUtil.isNotBlank(content) && null!=message.getData()){
                content = FreemarkerParseFactory.parseTemplateContent(content, message.getData(), isMarkdown);
            }
            message.setIsMarkdown(isMarkdown);
            message.setContent(content);
        }
        /*if(StrUtil.isBlank(message.getContent())){
            throw new AiurtBootException("发送消息失败,消息内容为空！");
        }*/
        if(StrUtil.isBlank(type)){
            throw new AiurtBootException("发送消息失败,消息发送渠道没有配置！");
        }
        List<String> messageTypes = StrUtil.splitTrim(type, ",");

        //保存信息
        Map<String,Object> data = message.getData();
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setProcessName(message.getProcessName());
        announcement.setProcessCode(message.getProcessCode());
        if(data!=null){
            // 任务节点ID
            Object taskId = data.get(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID);
            if(taskId!=null){
                announcement.setBusId(taskId.toString());
                // announcement.setBusType(Vue3MessageHrefEnum.BPM_TASK.getBusType());
            }
            Object busType = data.get(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE);
            if (busType != null) {
                announcement.setBusType(busType.toString());
                announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType.toString()).getOpenType());
                announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType.toString()).getOpenPage());
            }
        }
        //摘要信息
        announcement.setMsgAbstract(message.getMsgAbstract());
        announcement.setPublishingContent(message.getPublishingContent());
        announcement.setTitile(message.getTitle());
        announcement.setMsgContent(message.getContent());
        announcement.setSender(message.getFromUser());
        if (StringUtils.isNotBlank(message.getPriority())) {
            announcement.setPriority(message.getPriority());
        } else {
            announcement.setPriority(com.aiurt.common.constant.CommonConstant.PRIORITY_M);
        }
        announcement.setMsgType(org.jeecg.common.constant.CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(org.jeecg.common.constant.CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(message.getCategory());
        announcement.setDelFlag(String.valueOf(org.jeecg.common.constant.CommonConstant.DEL_FLAG_0));
        announcement.setUserIds(message.getToUser());
        announcement.setStartTime(message.getStartTime());
        announcement.setEndTime(message.getEndTime());
        announcement.setLevel(message.getLevel());
        announcement.setProcessCode(message.getProcessCode());
        announcement.setProcessName(message.getProcessName());
        announcement.setTaskId(message.getTaskId());
        announcement.setProcessInstanceId(message.getProcessInstanceId());
        announcement.setProcessDefinitionKey(message.getProcessDefinitionKey());
        sysAnnouncementMapper.insert(announcement);

        // 2.插入用户通告阅读标记表记录
        String userId = message.getToUser();
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for(int i=0;i<userIds.length;i++) {
            if(org.jeecg.common.util.oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if(sysUser==null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(org.jeecg.common.constant.CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
               /* JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, message.getTitle());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());*/
            }
        }
        message.setMessageId(anntId);

        //根据发送渠道发送消息
        for (String messageType : messageTypes) {
            //update-end-author:taoyan date:2022-7-9 for: 将模板解析代码移至消息发送, 而不是调用的地方
            if(MessageTypeEnum.XT.getType().equals(messageType)){
                if (message.isMarkdown()) {
                    // 系统消息要解析Markdown
                    message.setContent(HTMLUtils.parseMarkdown(message.getContent()));
                }
                systemSendMsgHandle.sendMessage(message);
            }else if(MessageTypeEnum.YJ.getType().equals(messageType)){
                if (message.isMarkdown()) {
                    // 邮件消息要解析Markdown
                    message.setContent(HTMLUtils.parseMarkdown(message.getContent()));
                }
                emailSendMsgHandle.sendMessage(message);
            }else if(MessageTypeEnum.DD.getType().equals(messageType)){

                ddSendMsgHandle.sendMessage(message);
            }else if(MessageTypeEnum.QYWX.getType().equals(messageType)){
                if (message.isMarkdown()) {
                    // 系统消息要解析Markdown
                    message.setContent(HTMLUtils.parseMarkdown(message.getContent()));
                }
                message.setBusKey(announcement.getBusId());
                message.setBusType(announcement.getBusType());
                qywxSendMsgHandle.sendMessage(message);
            }
        }
    }

    /**
     * 根据模板编码获取模板内容【新，支持自定义推送类型】
     *
     * @param templateCode
     * @return
     */
    @Override
    public String getTemplateContent(String templateCode) {
        List<SysMessageTemplate> list = sysMessageTemplateService.selectByCode(templateCode);
        if(list==null || list.size()==0){
            return null;
        }
        return list.get(0).getTemplateContent();
    }

    /**
     * 获取模板内容，解析markdown
     *
     * @param code
     * @return
     */
    public SysMessageTemplate getTemplateEntity(String code) {
        List<SysMessageTemplate> list = sysMessageTemplateService.selectByCode(code);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 返回当前dictCode对应的字典项在数据库中最后更新的时间
     *
     * @param dictCode
     * @return
     */
    @Override
    public String getCurrentNewModified(String dictCode) {
        return sysDictService.getCurrentNewModified(dictCode);
    }

    @Override
    public List<String> getAllHolidays() {
        LambdaQueryWrapper<SysHolidays> wrapper = new LambdaQueryWrapper<>();
        List<SysHolidays> list = sysHolidaysService.list(wrapper);
        if (CollUtil.isNotEmpty(list)) {
            List<String> collect = list.stream().map(SysHolidays::getDate).collect(Collectors.toList());
            return collect;
        }
        return new ArrayList<String>();
    }

    @Override
    public List<SpareResult>  getSpareChange(String faultCode) {
        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, faultCode)
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1")
                .select(FaultRepairRecord::getId);
        FaultRepairRecord repairRecord = faultRepairRecordMapper.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(repairRecord)) {
            List<SpareResult> sparePart = sparePartMapper.getSparePart(faultCode, repairRecord.getId());
            return sparePart;
        }
        return new ArrayList<SpareResult>();
    }

    @Override
    public String getFaultRepairReuslt(String faultCode) {
        RepairRecordDetailDTO recordByFaultCode = faultRepairRecordMapper.getRecordByFaultCode(faultCode);
        if (ObjectUtil.isNotEmpty(recordByFaultCode)) {
            String s = "故障接报人："+recordByFaultCode.getAppointRealName() + ",处理结果："+recordByFaultCode.getMaintenanceMeasures();
            return s;
        }
        return null;
    }

    @Override
    public List<LoginUser> getOrgUsers() {
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysDepart> sysDepartList = sysDepartService.list(queryWrapper);
        boolean b = GlobalThreadLocal.setDataFilter(false);
        List<LoginUser> loginUsers = new ArrayList<>();
        for (SysDepart sysDepart : sysDepartList) {
            List<String> orgCodes = sysDepartList(sysDepart.getOrgCode());
            if(CollUtil.isNotEmpty(orgCodes)){
                List<LoginUser> users = userMapper.getUserByCodes(orgCodes);
                loginUsers.addAll(users);
            }
        }
        GlobalThreadLocal.setDataFilter(b);
        return loginUsers;
    }

    @Override
    public String getUserByUserName(String userName) {
        SysUser user = userMapper.getUserByName(userName);
        return user.getId();
    }

    @Override
    public void sendAllMessage() {
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_EXTERNAL_ORG);
        String value = sysParamModel.getValue();
        List<String> orgCodes = StrUtil.splitTrim(value, ",");
        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getOrgCode, orgCodes).eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollectionUtil.isNotEmpty(users)) {
            List<String> list = users.stream().map(SysUser::getUsername).collect(Collectors.toList());
            //发送通知
            MessageDTO messageDTO = new MessageDTO(null,CollUtil.join(list,","), "调度已产生新的故障，请及时处理！" + DateUtil.today(), null);

            //业务类型，消息类型，消息模板编码，摘要，发布内容
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.FAULT_EXTERNAL.getType());
            messageDTO.setData(map);
            messageDTO.setMsgAbstract("有新的故障信息");
            messageDTO.setPublishingContent("有新的故障信息，请查看");
            messageDTO.setIsRingBell(true);
            sendMessage(messageDTO);
        }
    }
    /**
     * 发送消息
     * @param messageDTO
     */
    private void sendMessage(MessageDTO messageDTO) {
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(new Date());
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sendTemplateMessage(messageDTO);
    }
    /**
     * 根据编码查询设备分类
     *
     * @param list
     * @return
     */
    @Override
    public List<DeviceType> selectDeviceTypeByCodes(Set<String> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<DeviceType> typeList = deviceTypeService.list(new LambdaQueryWrapper<DeviceType>().in(DeviceType::getCode, list));
        return typeList;
    }

    @Override
    public List<SensorInformation> getSensorList() {
        List<SensorInformation> sensorList = sensorInformationMapper.selectList(new LambdaQueryWrapper<SensorInformation>()
                .eq(SensorInformation::getDelFlag, CommonConstant.DEL_FLAG_0));
        return sensorList;
    }

    @Override
    public List<String> getDepartByUser(Integer flag) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据当前登录人班组权限获取班组,管理员获取全部
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        List<String> list = new ArrayList<>();
        if (!admin) {
            List<CsUserDepartModel>  departByUserId = this.getDepartByUserId(user.getId());
            if (CollUtil.isNotEmpty(departByUserId)) {
                if (flag == 0) {
                    list =departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
                }else {
                    list =departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
                }
            }

        } else {
            List<SysDepartModel> allSysDepart = this.getAllSysDepart();
            if (CollUtil.isNotEmpty(allSysDepart)) {
                if (flag == 0) {
                    list =allSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
                }else {
                    list =allSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
                }
            }
        }
        //过滤通信分部
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FILTERING_TEAM);
        boolean b = "1".equals(sysParamModel.getValue());
        if (b) {
            SysParamModel code = sysParamApi.selectByCode(SysParamCodeConstant.SPECIAL_TEAM);
            String orgCode = code.getValue();
            String departId = this.getDepartIdsByOrgCode(orgCode);
            if (StrUtil.isNotEmpty(departId)&&flag == 0) {
                list.remove(departId);
            } else if (StrUtil.isNotEmpty(departId)&&flag == 1) {
                list.remove(orgCode);
            }
        }
        return list;
    }

    @Override
    public List<String> getWifiMacByStationCode(List<String> stationCodes) {
        if (CollUtil.isNotEmpty(stationCodes)) {
            List<String> mac = csPositionWifiMapper.getMac(stationCodes);
            return mac;
        }
        return new ArrayList<>();
    }

    @Override
    public List<StationAndMacModel> getStationAndMacByCode(List<String> stationCodes) {
        if (CollUtil.isNotEmpty(stationCodes)) {
            List<StationAndMacModel> mac = csPositionWifiMapper.getStationAndMac(stationCodes);
            return mac;
        }
        return new ArrayList<>();
    }

    @Override
    public Date getRecentConnectTimeByStationCode(String username, String stationCode) {
        LambdaQueryWrapper<SysUserPositionCurrent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPositionCurrent::getCreateBy, username);
        SysUserPositionCurrent sysUserPositionCurrent;
        try{
            sysUserPositionCurrent = sysUserPositionCurrentService.getOne(queryWrapper);
        }catch (Exception e){
            throw new AiurtBootException(username + " 在用户实时位置表中有多条数据，请联系相关人员处理！");
        }
        if (ObjectUtil.isNull(sysUserPositionCurrent)) {
            return null;
        }
        // 如果参数的站点和用户当前所在站点相同，返回当前站点的连接时间
        // 如果参数的站点和用户上一站的站点相同，返回上一站的站点的连接时间
        if (StrUtil.equals(stationCode, sysUserPositionCurrent.getStationCode())) {
            return sysUserPositionCurrent.getUploadTime();
        }else if(StrUtil.equals(stationCode, sysUserPositionCurrent.getLastStationCode())){
            return sysUserPositionCurrent.getLastUploadTime();
        }else {
            return null;
        }
    }
    @Override
    public void saveSysAttachment(SysAttachment sysAttachment) {
        sysAttachmentService.save(sysAttachment);
    }


    @Override
    public String getStationCodeByMac(String mac) {
        return mac == null ? null : csPositionWifiMapper.getStationCodeByMac(mac);
    }

    @Override
    public JSONObject queryPageUserList(LoginUser loginUser, List<String> excludeUserIds, String isBelongOrg,
                                              String isPermissionOrg, Integer pageNo, Integer pageSize,
                                              HttpServletRequest req) {

        // 因为此方法基本是从/sys/user/list搬过来的，所以先把请求参数的LoginUser转化成SysUser
        SysUser user = new SysUser();
        BeanUtils.copyProperties(loginUser, user);
        // 因为LoginUser没有majorId、roleCode、systemId、stationId，后面有用到，从req获取
        user.setMajorId(req.getParameter("majorId"));
        user.setStationId(req.getParameter("roleCode"));
        user.setRoleCode(req.getParameter("systemId"));
        user.setSystemId(req.getParameter("stationId"));

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // Result<IPage<SysUser>> result = new Result<>();
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());

        // 查询当前登录人所属部门的用户
        if ("1".equals(isBelongOrg)) {
            if (StrUtil.isBlank(user.getOrgId())){
                queryWrapper.eq("org_id",sysUser.getOrgId());
            }
        }
        // 查询当前登录人拥有的权限的部门用户，只有当isBelongOrg=0才有效，因为isBelongOrg=0就直接获取所属部门的用户了
        // 这里使用了in，因为部门也不会太多
        if ("1".equals(isPermissionOrg) && "0".equals(isBelongOrg)){
            List<CsUserDepartModel> departModels = csUserDepartMapper.getDepartByUserId(user.getId());
            List<String> orgIdList = departModels.stream().map(CsUserDepartModel::getId).collect(Collectors.toList());
            queryWrapper.in("org_id",orgIdList);
        }

        // 过滤excludeUserIds, 这里使用了in，但是因为user的数量不会太多，不会造成sql很长的in
        if (CollUtil.isNotEmpty(excludeUserIds)){
            queryWrapper.notIn("id", excludeUserIds);
        }

        //用户ID
        String code = req.getParameter("code");
        if (oConvertUtils.isNotEmpty(code)) {
            queryWrapper.in("id", Arrays.asList(code.split(",")));
            pageSize = code.split(",").length;
        }
        //update-end-Author:wangshuai--Date:20211119--for:【vue3】通过部门id查询用户，通过code查询id

        //update-begin-author:taoyan--date:20220104--for: JTC-372 【用户冻结问题】 online授权、用户组件，选择用户都能看到被冻结的用户
        String status = req.getParameter("status");
        if (oConvertUtils.isNotEmpty(status)) {
            queryWrapper.eq("status", Integer.parseInt(status));
        }
        //update-end-author:taoyan--date:20220104--for: JTC-372 【用户冻结问题】 online授权、用户组件，选择用户都能看到被冻结的用户

        //TODO 外部模拟登陆临时账号，列表不显示
        queryWrapper.ne("username", "_reserve_user_external");

        // 根据角色， 站点， 系统， 专业
        queryWrapper.apply(StrUtil.isNotBlank(user.getMajorId()),
                "id in (select user_id from cs_user_major where 1=1 and major_id in (select id from cs_major where 1=1 and ( id = {0} or major_code = {0})))",
                user.getMajorId());
        queryWrapper.apply(StrUtil.isNotBlank(user.getRoleCode()), "id in (select user_id from sys_user_role where 1=1 and role_id in (select id from sys_role where 1=1 and (id = {0} or role_code ={0})))",
                user.getRoleCode());
        queryWrapper.apply(StrUtil.isNotBlank(user.getSystemId()), "id in (select user_id from cs_user_subsystem where 1=1 and system_id in (select id from cs_subsystem where 1=1 and (id ={0} or system_code = {0})))", user.getSystemId());

        queryWrapper.apply(StrUtil.isNotBlank(user.getStationId()), "id in (select user_id from cs_user_station where 1=1 and station_id in (select id from cs_station where 1=1 and (id ={0} or station_code = {0})))", user.getStationId());
        //如果是查全部，则把当前登录人所属部门的人排在前面
        String sql = "order by case when ( org_code = '"+sysUser.getOrgCode()+"') then 0 else 1 end,id DESC";
        queryWrapper.last("0".equals(isBelongOrg),sql);
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        // 这里修改了下面的，因为会有循环依赖
        // IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
        IPage<SysUser> pageList = userMapper.selectPage(page, queryWrapper);


        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
        List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        if (userIds != null && userIds.size() > 0) {
            // 这里修改了下面的，是因为会有循环依赖
            // Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            List<SysUserDepVo> list = userMapper.getDepNamesByUserIds(userIds);
            Map<String, String> useDepNames = new HashMap(5);
            list.forEach(item -> {
                        if (useDepNames.get(item.getUserId()) == null) {
                            useDepNames.put(item.getUserId(), item.getDepartName());
                        } else {
                            useDepNames.put(item.getUserId(), useDepNames.get(item.getUserId()) + "," + item.getDepartName());
                        }
                    }
            );

            pageList.getRecords().forEach(item -> {
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
                getUserDetail(item);
            });
        }
        // result.setSuccess(true);
        // result.setResult(pageList);
        log.info(pageList.toString());
        return JSONObject.parseObject(JSON.toJSONString(pageList, SerializerFeature.WriteMapNullValue));
    }

    private void getUserDetail(SysUser sysUser) {
        List<String> roleIds = sysUserRoleMapper.getRoleIds(sysUser.getId());
        List<String> roleNames = sysUserRoleMapper.getRoleNames(sysUser.getId());
        List<CsUserDepartModel> departModelList = csUserDepartMapper.getDepartByUserId(sysUser.getId());
        List<CsUserStationModel> stationList = csUserStaionMapper.getStationByUserId(sysUser.getId());
        List<CsUserMajorModel> majorList = csUserMajorMapper.getMajorByUserId(sysUser.getId());
        List<CsUserSubsystemModel> subsystemList = csUserSubsystemMapper.getSubsystemByUserId(sysUser.getId());
        sysUser.setRoleIds(roleIds);
        sysUser.setRoleNames(StrUtil.join(",", roleNames));

        List<String> departIds = departModelList.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        sysUser.setDepartCodes(departIds);
        List<String> departNameList = departModelList.stream().map(CsUserDepartModel::getDepartName).collect(Collectors.toList());
        sysUser.setDepartNames(StrUtil.join(",", departNameList));

        List<String> stationIds = stationList.stream().map(CsUserStationModel::getStationId).collect(Collectors.toList());
        sysUser.setStationIds(stationIds);

        List<String> stationNameList = stationList.stream().map(CsUserStationModel::getStationName).collect(Collectors.toList());
        sysUser.setStationNames(StrUtil.join(",", stationNameList));

        List<String> majorIds = majorList.stream().map(CsUserMajorModel::getMajorId).collect(Collectors.toList());
        sysUser.setMajorIds(majorIds);
        List<String> majorNameList = majorList.stream().map(CsUserMajorModel::getMajorName).collect(Collectors.toList());
        sysUser.setMajorNames(StrUtil.join(",", majorNameList));

        List<String> subsystemIds = subsystemList.stream().map(CsUserSubsystemModel::getSystemId).collect(Collectors.toList());
        sysUser.setSystemCodes(subsystemIds);
        List<String> systemNameList = subsystemList.stream().map(CsUserSubsystemModel::getSystemName).collect(Collectors.toList());
        sysUser.setSystemNames(StrUtil.join(",", systemNameList));


        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(sysUser.getOrgId())) {
            SysDepart depart = sysDepartService.getById(sysUser.getOrgId());
            depart = Optional.ofNullable(depart).orElse(new SysDepart());
            sysUser.setOrgCode(depart.getOrgCode());
            sysUser.setOrgName(depart.getDepartName());
        }

        // 处理
    }

    @Override
    public List<LoginUser> queryAllUsers() {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0).select(SysUser::getId,SysUser::getRealname);
        List<SysUser> users = userMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUsers = new ArrayList<>();
        for (SysUser user : users) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);
            loginUsers.add(loginUser);
        }
        return loginUsers;
    }
    @Override
    public List<String> getSysDepartList(String code) {
        if (ObjectUtil.isEmpty(code)){
            return  CollUtil.newArrayList();
        }
        String orgCode = "/" + code + "/";
        List<SysDepart> sysDeparts = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag,0).like(SysDepart::getOrgCodeCc, orgCode));
        if(CollUtil.isEmpty(sysDeparts)){
            return  CollUtil.newArrayList();
        }
        List<String> orgCodeList = sysDeparts.stream().map(SysDepart::getOrgCode).collect(Collectors.toList());
        return orgCodeList;
    }

    @Override
    public JSONObject queryByWorkNoUser(String workNo) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getWorkNo, workNo);
        SysUser sysUser = userMapper.selectOne(queryWrapper);
        LoginUser loginUser = new LoginUser();
        if (ObjectUtil.isNotEmpty(sysUser)) {
            BeanUtils.copyProperties(sysUser, loginUser);
        }
        return JSONObject.parseObject(JSON.toJSONString(loginUser));
    }
}
