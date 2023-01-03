package com.aiurt.modules.maplocation.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.weeklyplan.entity.BdStation;
import com.aiurt.boot.weeklyplan.service.IBdStationService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.maplocation.constant.BdMapConstant;
import com.aiurt.modules.maplocation.dto.*;
import com.aiurt.modules.maplocation.mapper.BdMapListMapper;
import com.aiurt.modules.maplocation.service.IBdMapListService;
import com.aiurt.modules.maplocation.utils.MapDistance;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.positionwifi.entity.CsPositionWifi;
import com.aiurt.modules.positionwifi.service.ICsPositionWifiService;
import com.aiurt.modules.system.entity.SysUserPosition;
import com.aiurt.modules.system.service.ISysUserPositionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: wgp
 * @Date: 2021-04-19
 * @Version: V1.0
 */
@Service
public class BdMapListServiceImpl extends ServiceImpl<BdMapListMapper, CurrentTeamPosition> implements IBdMapListService {
    @Autowired
    private IBdStationService bdStationService;
    @Autowired
    private ICsStationService csStationService;
    @Autowired
    private ICsStationPositionService csStationPositionService;
    @Autowired
    private ICsLineService csLineService;
    @Autowired
    private ICommonService commonService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private ICsPositionWifiService csPositionWifiService;

    @Autowired
    private ISysUserPositionService sysUserPositionService;

    /**
     * 查询人员的位置信息
     *
     * @param teamId       班组id
     * @param userInfoList 用户id
     * @param stationId    站点id
     * @param stateId      状态id
     * @return
     */
    @Override
    public List<CurrentTeamPosition> queryPositionById(String teamId, String userInfoList, String stationId, String stateId) {

        List<CurrentTeamPosition> currentTeamPositions = new ArrayList<>();
        if (StrUtil.isNotEmpty(userInfoList)) {

            // 获得人员位置
            List<String> userIdList = new ArrayList<>();
            if ("all".equals(userInfoList)) {
                List<UserInfo> userByTeamIdList = this.getUserByTeamIdList(teamId);
                if (CollUtil.isNotEmpty(userByTeamIdList)) {
                    userIdList = userByTeamIdList.stream().map(user -> user.getId()).collect(Collectors.toList());
                }
            } else {
                userIdList = Arrays.asList(userInfoList);
            }

            // 获取登录人员
            Set<String> userNameSet = getUserInfo();

            // 封装用户的在离线信息
            List<CurrentTeamPosition> currentTeamPositionList = baseMapper.queryPositionById(userIdList);
            if (CollUtil.isNotEmpty(currentTeamPositionList)) {
                for (CurrentTeamPosition teamPosition : currentTeamPositionList) {
                    if (ObjectUtil.isNotEmpty(teamPosition)) {
                        teamPosition.setCurrentStaffStatusName(userNameSet.contains(teamPosition.getUsername()) ? "在线" : "离线");
                        teamPosition.setCurrentStaffStatusId(userNameSet.contains(teamPosition.getUsername()) ? "1" : "0");
                    }

                    // 获取坐标系
                    // 模糊查询
                    LambdaQueryWrapper<SysUserPosition> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SysUserPosition::getCreateBy, teamPosition.getUsername()).isNotNull(SysUserPosition::getLatitude)
                            .isNotNull(SysUserPosition::getLongitude)
                            .orderByDesc(SysUserPosition::getUploadTime).last("limit 1");
                    SysUserPosition sysUserPosition = sysUserPositionService.getOne(wrapper);
                    if (Objects.nonNull(sysUserPosition)) {
                        String bssid = sysUserPosition.getBssid();
                        // 连接wifi
                        if (StrUtil.isNotBlank(bssid)) {
                            String subBssid = StrUtil.sub(bssid, 0, bssid.length() - 3);
                            UserStationDTO station = baseMapper.getStationByMac(subBssid);
                            if (Objects.nonNull(station)) {
                                teamPosition.setPositionX(station.getPositionX());
                                teamPosition.setPositionY(station.getPositionY());
                            }else {
                                //
                                teamPosition.setPositionX(sysUserPosition.getLongitude().doubleValue());
                                teamPosition.setPositionY(sysUserPosition.getLatitude().doubleValue());
                            }
                        }
                    }
                }
                // 过滤状态
                if (StrUtil.isNotEmpty(stateId)) {
                    List<CurrentTeamPosition> currentTeamPositionTempList = currentTeamPositionList.stream().filter(new Predicate<CurrentTeamPosition>() {
                        @Override
                        public boolean test(CurrentTeamPosition currentTeamPosition) {
                            return StrUtil.isNotEmpty(currentTeamPosition.getCurrentStaffStatusId()) && currentTeamPosition.getCurrentStaffStatusId().equals(stateId) ? true : false;
                        }
                    }).collect(Collectors.toList());
                    return currentTeamPositionTempList;
                } else {
                    return currentTeamPositionList;
                }
            }
        }

        // 按站点查询
        if (StrUtil.isNotEmpty(stationId)) {
            BdStation byId = bdStationService.getById(stationId);
            if (ObjectUtil.isNotEmpty(byId) && byId.getPositionY() != null && byId.getPositionX() != null) {
                CurrentTeamPosition currentTeamPosition = new CurrentTeamPosition();
                currentTeamPosition.setPositionX(byId.getPositionX());
                currentTeamPosition.setPositionY(byId.getPositionY());
                currentTeamPosition.setStationId(byId.getId());
                currentTeamPosition.setStationName(byId.getName());
                currentTeamPosition.setFlag(1);
                currentTeamPositions.add(currentTeamPosition);
            }
        }

        return currentTeamPositions;
    }

    // 根据机构查询人员
    @Override
    public List<UserInfo> getUserByTeamIdList(String teamId) {
        List<TreeNode> teamList = baseMapper.getAllTeam();
        List<String> teamChild = new ArrayList<>();
        if (StrUtil.isEmpty(teamId)) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<String> teamIdList = iSysBaseAPI.getUserSysDepart(sysUser.getId()).stream().map(team -> team.getId()).collect(Collectors.toList());
            for (String teamsInt : teamIdList) {
                teamChild.add(teamsInt);
            }
        } else {
//            teamChild = TreeUtils.treeMenuList(teamList, Integer.parseInt(teamId), new ArrayList<TreeNode>()).stream().map(treeNode -> treeNode.getId()).collect(Collectors.toList());
            teamChild.add(teamId);
        }

        return baseMapper.getUserByTeamIdList(teamChild);
    }


    /**
     * 根据人员id查询人员信息
     *
     * @param id
     * @return
     */
    @Override
    public List<UserInfo> getUserById(String id) {
        List<UserInfo> userById = baseMapper.getUserById(id);
        // 封装角色信息
        if (CollUtil.isNotEmpty(userById)) {
            userById.forEach(user -> {
                List<String> roleNamesByUsername = iSysBaseAPI.getRoleNamesById(user.getId());
                if (CollUtil.isNotEmpty(roleNamesByUsername)) {
                    user.setRoleName(StrUtil.join(",", roleNamesByUsername));
                }
            });
        }
        return userById;
    }

    // 根据人员id查询附近设备
    @Override
    public Page<EquipmentHistoryDTO> getEquipmentByUserId(String id, String stationId, Page<EquipmentHistoryDTO> pageList) {
        String stationIdStr = null;
        // 管理的班组  暂时不启用查询自己管理班组的设备 默认查询所有
        //  LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // List<String> teamIdList = iSysBaseAPI.getUserSysDepart(sysUser.getId()).stream().map(teamId -> teamId.getOrgCode()).collect(Collectors.toList());
        // 如果是按站点查，直接用站点的id,如果是按人员查，就是人员的位置信息和哪个站点离得最近，就取那个站点的id
        if (StrUtil.isNotEmpty(id)) {
            UserStationDTO userStation = baseMapper.getStationId(id);
            LambdaQueryWrapper<CsPositionWifi> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(CsPositionWifi::getDelFlag, 0);
            if (ObjectUtil.isNotEmpty(userStation) && StrUtil.isNotEmpty(userStation.getBssid())) {
                lambdaQueryWrapper.eq(CsPositionWifi::getMac, userStation.getBssid());
                CsPositionWifi csPositionWifis = csPositionWifiService.getOne(lambdaQueryWrapper);
                if (ObjectUtil.isNotEmpty(csPositionWifis)) {
                    LambdaQueryWrapper<CsStation> stationLambdaQueryWrapper = new LambdaQueryWrapper<CsStation>();
                    stationLambdaQueryWrapper.isNotNull(CsStation::getLongitude)
                            .isNotNull(CsStation::getLatitude)
                            .eq(CsStation::getDelFlag, 0);
                    stationLambdaQueryWrapper.eq(CsStation::getStationCode, csPositionWifis.getStationCode())
                            .eq(CsStation::getLineCode, csPositionWifis.getLineCode());
                    CsStation bdStation = csStationService.getBaseMapper().selectOne(stationLambdaQueryWrapper);
                    stationIdStr = bdStation.getStationCode() == null ? null : bdStation.getStationCode();
                }
            } else if (StrUtil.isEmpty(stationIdStr)){
                List<CsStation> bdStationList = csStationService.getBaseMapper().selectList(new LambdaQueryWrapper<CsStation>()
                        .isNotNull(CsStation::getLongitude)
                        .isNotNull(CsStation::getLatitude)
                        .eq(CsStation::getDelFlag, 0));
                if (ObjectUtil.isNotEmpty(userStation)
                        && userStation.getPositionX() != null
                        && userStation.getPositionY() != null
                        && CollUtil.isNotEmpty(bdStationList)) {

                    GlobalCoordinates userDistance = new GlobalCoordinates(userStation.getPositionY(), userStation.getPositionX());
                    // 2000米范围内
                    double distance = 2000.0d;
                    for (CsStation bdStation : bdStationList) {
                        BigDecimal latitude = bdStation.getLatitude();
                        BigDecimal longitude = bdStation.getLongitude();
                        if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
                            GlobalCoordinates stationDistance = new GlobalCoordinates(latitude.doubleValue(), longitude.doubleValue());
                            double meter = MapDistance.getDistanceMeter(userDistance, stationDistance, Ellipsoid.Sphere);
                            if (meter <= distance) {
                                stationIdStr = bdStation.getStationCode();
                                distance = meter;
                            }
                        }
                    }
                }
            }
        }

        if (StrUtil.isNotEmpty(stationId)) {
            CsStation csStation = csStationService.getBaseMapper().selectById(stationId);
            stationIdStr = csStation.getStationCode();
        }

        if (StrUtil.isEmpty(stationIdStr)) {
            return pageList.setRecords(new ArrayList<>());
        }

        List<EquipmentHistoryDTO> equipmentHistoryDTOS = baseMapper.selectEquipment(pageList, null, stationIdStr);
        equipmentHistoryDTOS.forEach(s->{
            if (s.getPositionCode()!=null){
                CsStationPosition stationPosition = csStationPositionService.getOne(new LambdaQueryWrapper<CsStationPosition>()
                        .eq(CsStationPosition::getPositionCode,s.getPositionCode()).eq(CsStationPosition::getDelFlag,0));
               s.setPosition(stationPosition.getPositionName());
            }
        });

        return pageList.setRecords(equipmentHistoryDTOS);
    }

    /**
     * 根据机构获取机构下的人员状态
     *
     * @param teamId
     * @return
     */
    @Override
    public List<AssignUserDTO> getUserStateByTeamId(String teamId,String userId,Integer stateId) {
        List<AssignUserDTO> result = new ArrayList<>();
        // 获取登录人员
        Set<String> userNameSet = getUserInfo();

        // 班组下的人员
        List<UserInfo> userByTeamIdList = getUserByTeamIdList(teamId);
        if (CollUtil.isNotEmpty(userByTeamIdList)) {
            userByTeamIdList.stream().forEach(entity -> {
                AssignUserDTO assign = new AssignUserDTO();
                assign.setRealname(entity.getName());
                assign.setId(entity.getId());
                if (userNameSet.contains(entity.getUserName())) {
                    assign.setStatus("已登录"); assign.setNum(1);
                    UserStationDTO userStation = baseMapper.getStationId(entity.getId());
                    LambdaQueryWrapper<CsPositionWifi> lambdaQueryWrapper = new LambdaQueryWrapper();
                    lambdaQueryWrapper.eq(CsPositionWifi::getDelFlag, 0);

                    if (ObjectUtil.isNotEmpty(userStation) && StrUtil.isNotEmpty(userStation.getBssid())) {
                        String bssid = userStation.getBssid();
                        // 模糊查询
                        String subBssid = StrUtil.sub(bssid, 0, bssid.length() - 3);
                        lambdaQueryWrapper.likeRight(CsPositionWifi::getMac, subBssid).last("limit 1");
                        CsPositionWifi csPositionWifis = csPositionWifiService.getOne(lambdaQueryWrapper);
                        if (ObjectUtil.isNotEmpty(csPositionWifis)){
                            LambdaQueryWrapper<CsStation> stationLambdaQueryWrapper = new LambdaQueryWrapper<CsStation>();
                            stationLambdaQueryWrapper.isNotNull(CsStation::getLongitude)
                                    .isNotNull(CsStation::getLatitude)
                                    .eq(CsStation::getDelFlag, 0);
                            stationLambdaQueryWrapper.eq(CsStation::getStationCode, csPositionWifis.getStationCode())
                                    .eq(CsStation::getLineCode, csPositionWifis.getLineCode());
                            CsStation bdStation = csStationService.getBaseMapper().selectOne(stationLambdaQueryWrapper);
                            assign.setStationName(bdStation.getStationName()==null ?null :bdStation.getStationName());
                        }
                    }else if (StrUtil.isEmpty(assign.getStationName())){
                    QueryWrapper<CsStation> bdStationQueryWrapper = new QueryWrapper<>();
                    bdStationQueryWrapper.lambda().isNotNull(CsStation::getLongitude);
                    bdStationQueryWrapper.lambda().isNotNull(CsStation::getLatitude);
                    List<CsStation> bdStationList = csStationService.getBaseMapper().selectList(bdStationQueryWrapper);
                    if (ObjectUtil.isNotEmpty(userStation)
                            && userStation.getPositionX() != null
                            && userStation.getPositionY() != null
                            && CollUtil.isNotEmpty(bdStationList)) {

                        GlobalCoordinates userDistance = new GlobalCoordinates(userStation.getPositionY(), userStation.getPositionX());
                        // 2000米范围内
                        double distance = 2000.0d;
                        for (CsStation bdStation : bdStationList) {
                            BigDecimal latitude = bdStation.getLatitude();
                            BigDecimal longitude = bdStation.getLongitude();
                            if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
                                GlobalCoordinates stationDistance = new GlobalCoordinates(latitude.doubleValue(), longitude.doubleValue());
                                double meter = MapDistance.getDistanceMeter(userDistance, stationDistance, Ellipsoid.Sphere);
                                if (meter <= distance) {
                                    assign.setStationName(bdStation.getStationName());
                                    distance = meter;
                                }
                            }
                        }
                    }
                  }
                } else {
                    assign.setStatus("未登录");assign.setNum(2);
                }
                result.add(assign);
            });
        }
        if (StrUtil.isNotEmpty(userId)&&stateId==null){
            List<AssignUserDTO> list = new ArrayList<>();
         list =  result.stream().filter(l->l.getId().equals(userId)).collect(Collectors.toList());
         return list;
        }
        if(stateId!=null){
            if (1 == stateId){
            List<AssignUserDTO> list1 = new ArrayList<>();
            list1 =  result.stream().filter(l->l.getNum()==2).collect(Collectors.toList());
                if (StrUtil.isNotEmpty(userId)){
                    list1 =  list1.stream().filter(l->l.getId().equals(userId)).collect(Collectors.toList());
                }
            return list1;
        }else if (0==stateId){
            List<AssignUserDTO> list2 = new ArrayList<>();
            list2 =  result.stream().filter(l->l.getNum()==1).collect(Collectors.toList());
            return list2;
        }}
        result.stream().sorted(Comparator.comparing(AssignUserDTO::getNum)).collect(Collectors.toList());
        return result;
    }

    /**
     * 发送消息给对应的用户
     *
     * @param username 用户账号
     * @param msg      消息
     * @return
     */
    @Override
    public void sendSysAnnouncement(String username, String msg) {
        MessageDTO messageDTO = new MessageDTO();
        if (StrUtil.isNotEmpty(username) && StrUtil.isNotEmpty(msg)) {
            messageDTO.setToUser(username);
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            messageDTO.setFromUser(sysUser.getId());
            messageDTO.setContent(msg);
            messageDTO.setTitle("");
            messageDTO.setCategory("1");
            iSysBaseAPI.sendSysAnnouncement(messageDTO);
        } else {
            throw new JeecgBootException("发送失败");
        }
    }

    @Override
    public List<LineDTO> getStation() {
        LineDTO lineDTO3 = new LineDTO();
        LineDTO lineDTO4 = new LineDTO();
        LineDTO lineDTO8 = new LineDTO();
        CsLine csLine3 = csLineService.getOne(new LambdaQueryWrapper<CsLine>().eq(CsLine::getLineName,BdMapConstant.THREELINE).eq(CsLine::getDelFlag,0));
        lineDTO3.setId(csLine3.getId());
        lineDTO3.setTitle(csLine3.getLineName());
        lineDTO3.setLineCode(csLine3.getLineCode());
        CsLine csLine4 = csLineService.getOne(new LambdaQueryWrapper<CsLine>().eq(CsLine::getLineName,BdMapConstant.FOURLINE).eq(CsLine::getDelFlag,0));
        lineDTO4.setId(csLine4.getId());
        lineDTO4.setTitle(csLine4.getLineName());
        lineDTO4.setLineCode(csLine4.getLineCode());
        CsLine csLine8 = csLineService.getOne(new LambdaQueryWrapper<CsLine>().eq(CsLine::getLineName,BdMapConstant.EIGHTLINE).eq(CsLine::getDelFlag,0));
        lineDTO8.setId(csLine8.getId());
        lineDTO8.setTitle(csLine8.getLineName());
        lineDTO8.setLineCode(csLine8.getLineCode());
        lineDTO3.setChildren(csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsStation::getLineName,BdMapConstant.THREELINE)));
        lineDTO4.setChildren(csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsStation::getLineName,BdMapConstant.FOURLINE)));
        lineDTO8.setChildren(csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsStation::getLineName,BdMapConstant.EIGHTLINE)));
        lineDTO3.getChildren().stream().forEach(s->{s.setTitle(s.getStationName());});
        lineDTO4.getChildren().stream().forEach(s->{s.setTitle(s.getStationName());});
        lineDTO8.getChildren().stream().forEach(s->{s.setTitle(s.getStationName());});
        List<LineDTO>list = new ArrayList<>();
        list.add(lineDTO3);list.add(lineDTO4);list.add(lineDTO8);
        return list;
    }

    /**
     * 获取登录人员信息
     *
     * @return
     */
    @NotNull
    private Set<String> getUserInfo() {
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        Set<String> userNameSet = new HashSet<>();
        for (String key : keys) {
            String token = (String) redisUtil.get(key);
            if (StringUtils.isNotEmpty(token)) {
                String username = JwtUtil.getUsername(token);
                if (StrUtil.isNotBlank(username)) {
                    userNameSet.add(username);
                }
            }
        }
        return userNameSet;
    }


}
