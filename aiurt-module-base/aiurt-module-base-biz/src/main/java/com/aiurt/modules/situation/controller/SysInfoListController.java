package com.aiurt.modules.situation.controller;


import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.aiurt.modules.situation.entity.SysAnnouncementSend;
import com.aiurt.modules.situation.mapper.SysInfoListMapper;
import com.aiurt.modules.situation.service.SysInfoListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: sys_info_list
 * @Author: jeecg-boot
 * @Date: 2021-04-19
 * @Version: V1.0
 */
@Api(tags = "特情发送消息表")
@RestController
@RequestMapping("/situation/sysInfoList")
@Slf4j
public class SysInfoListController  extends BaseController<SysAnnouncement, SysInfoListService> {
    @Autowired
    private SysInfoListService bdInfoListService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private SysInfoListMapper sysInfoListMapper;
    /**
     * 特情消息发送分页列表查询
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "/specialSituation")
    @ApiOperation(value = " 特情消息发送分页列表查询", notes = " 特情消息发送分页列表查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = SysAnnouncement.class)
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysAnnouncement>> queryPageList(SysAnnouncement sysAnnouncement,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<SysAnnouncement>> result = new Result<IPage<SysAnnouncement>>();
        sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
        String sTime = null;
        String eTime = null;
        if (StrUtil.isNotBlank(sysAnnouncement.getSTime()) || StrUtil.isNotBlank(sysAnnouncement.getETime())) {
            sTime = sysAnnouncement.getSTime();
            eTime = sysAnnouncement.getETime();
            eTime = eTime + " 23:59:59";
            sysAnnouncement.setSTime(null);
            sysAnnouncement.setETime(null);
        }
        QueryWrapper<SysAnnouncement> queryWrapper = new QueryWrapper<>();
        Page<SysAnnouncement> page = new Page<SysAnnouncement>(pageNo, pageSize);
        if (StrUtil.isNotEmpty(sysAnnouncement.getLevel())) {
            queryWrapper.lambda().eq(SysAnnouncement::getLevel, sysAnnouncement.getLevel());
        }
        if (StrUtil.isNotEmpty(sTime)) {
            queryWrapper.lambda().ge(SysAnnouncement::getSendTime, sTime);
        }
        if (StrUtil.isNotEmpty(eTime)) {
            queryWrapper.lambda().le(SysAnnouncement::getSendTime, eTime);
        }
        if (StrUtil.isNotEmpty(sysAnnouncement.getMsgContent())) {
            queryWrapper.lambda().like(SysAnnouncement::getMsgContent, sysAnnouncement.getMsgContent());
        }
        if (StrUtil.isNotEmpty(sysAnnouncement.getSender())) {
            List<String> userNameList = iSysBaseAPI.getUserListByName(sysAnnouncement.getSender());
            if (CollectionUtils.isNotEmpty(userNameList)) {
                queryWrapper.lambda().in(SysAnnouncement::getSender, userNameList);
            } else {
                result.setSuccess(true);
                result.setResult(new Page<>());
                return result;
            }
        }
        queryWrapper.lambda().eq(SysAnnouncement::getMsgCategory, CommonConstant.MSG_CATEGORY_3).eq(SysAnnouncement::getDelFlag,CommonConstant.DEL_FLAG_0.toString()).orderByDesc(SysAnnouncement::getCreateTime);
        IPage<SysAnnouncement> pageList = bdInfoListService.page(page, queryWrapper);
        List<SysAnnouncement> records = pageList.getRecords();
        for (SysAnnouncement announcement : records) {
            bdInfoListService.getOrgNames(announcement);
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加发布特情
     *
     * @param sysAnnouncement
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-添加发布", operateType =  2, operateTypeAlias = "添加-添加发布", permissionUrl = "/specialSituation")
    @ApiOperation(value = " 添加发布特情", notes = " 添加发布特情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = SysAnnouncement.class)
    })
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysAnnouncement> add(@RequestBody SysAnnouncement sysAnnouncement) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        try {
            // TODO wgp修改默认值
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            // 发消息
            BusMessageDTO messageDTO = new BusMessageDTO();
            messageDTO.setFromUser(sysUser.getUsername());
            messageDTO.setOrgIds(sysAnnouncement.getOrgIds());
            messageDTO.setToUser(sysAnnouncement.getUserIds());
            messageDTO.setToAll(false);
            messageDTO.setContent(sysAnnouncement.getMsgContent());
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_3);
            messageDTO.setTitle(sysAnnouncement.getTitile());
            messageDTO.setBusType(SysAnnmentTypeEnum.SITUATION.getType());
            messageDTO.setLevel(sysAnnouncement.getLevel());
            messageDTO.setStartTime(sysAnnouncement.getStartTime());
            messageDTO.setEndTime(sysAnnouncement.getEndTime());
            iSysBaseAPI.sendBusAnnouncement(messageDTO);

            result.success("发布成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 添加发布特情（移动端）
     *
     * @param sysAnnouncement
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-添加发布（移动端）", operateType =  2, operateTypeAlias = "添加-添加发布（移动端）", permissionUrl = "/specialSituation")
    @ApiOperation(value = " 添加发布特情（移动端）", notes = " 添加发布特情（移动端）")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = SysAnnouncement.class)
    })
    @RequestMapping(value = "/add2", method = RequestMethod.POST)
    public Result<SysAnnouncement> add2(@RequestBody SysAnnouncement sysAnnouncement) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        try {
            // TODO wgp修改默认值
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            // 发消息
            BusMessageDTO messageDTO = new BusMessageDTO();
            messageDTO.setFromUser(sysUser.getId());
            messageDTO.setToUser(sysAnnouncement.getUserIds());
            messageDTO.setContent(sysAnnouncement.getMsgContent());
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_3);
            messageDTO.setTitle(sysAnnouncement.getTitile());
            messageDTO.setBusType(SysAnnmentTypeEnum.SITUATION.getType());
            messageDTO.setLevel(sysAnnouncement.getLevel());
            messageDTO.setStartTime(sysAnnouncement.getStartTime());
            messageDTO.setEndTime(sysAnnouncement.getEndTime());
            iSysBaseAPI.sendBusAnnouncement(messageDTO);
            result.success("发布成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 通过id查看该通告的人是否已读
     *
     * @param id
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-通过id查看该通告", operateType =  1, operateTypeAlias = "查询-通过id查看该通告", permissionUrl = "/specialSituation")
    @ApiOperation(value = " 通过id查看该通告的人是否已读", notes = " 通过id查看该通告的人是否已读")
    @GetMapping(value = "/queryById")
    public Result<IPage<SysAnnouncementSend>> queryById(@RequestParam(name = "id", required = true) String id,
                                                        @RequestParam(name = "readFlag", required = false) String readFlag,
                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                               HttpServletRequest req) {
        Page<SysAnnouncementSend> page = new Page<>(pageNo, pageSize);
        List<SysAnnouncementSend> sysAnnouncementSends = sysInfoListMapper.getByAnntId(page,id,readFlag);
        if (sysAnnouncementSends == null) {
            return Result.error("未找到数据");
        }
        return Result.OK(page.setRecords(sysAnnouncementSends));
    }
    /**
     * 我的通知分页列表查询
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-我的通知分页列表查询", operateType =  1, operateTypeAlias = "查询-我的通知分页列表查询", permissionUrl = "/specialSituation")
    @ApiOperation(value = " 我的通知分页列表查询", notes = " 我的通知分页列表查询")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = SysAnnouncement.class)
    })
    @RequestMapping(value = "/getMyInfo", method = RequestMethod.GET)
    public Result<IPage<SysAnnouncement>> getMyInfo(SysAnnouncement sysAnnouncement,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<SysAnnouncement>> result = new Result<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Page<SysAnnouncement> page = new Page<>(pageNo, pageSize);
        List<SysAnnouncement> myInfo = sysInfoListMapper.getMyInfo(page, sysUser.getId());
        List<SysAnnouncement> collect = myInfo.stream().filter(s -> "1".equals(s.getReadFlag())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(myInfo)) {
            SysAnnouncement s = myInfo.get(0);
            s.setReadCount(collect.size());
            s.setUnreadCount(myInfo.size()-collect.size());
            result.setSuccess(true);
            result.setResult(page.setRecords(myInfo));
            return result;
        }
        return result;
    }

    /**
     * 修改阅读状态
     */
    @AutoLog(value = "特情消息-特情消息列表-修改阅读状态", operateType =  3, operateTypeAlias = "修改-修改阅读状态", permissionUrl = "/specialSituation")
    @ApiOperation(value = "修改阅读状态", notes = "修改阅读状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = SysAnnouncementSend.class)
    })
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
        sysInfoListMapper.updateReadFlag(sysAnnouncementSend.getId(),new Date());
        return Result.OK("编辑成功!");
    }

    /**
     *   通过id删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "特情消息-特情消息列表-通过id删除", operateType =  4, operateTypeAlias = "删除-通过id删除", permissionUrl = "/specialSituation/SpecialSituationList")
    @ApiOperation(value="特情管理-通过id删除", notes="特情管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="ids",required=true) String ids) {
        String[] selectionList = ids.split(",");
        for (String id : selectionList) {
            SysAnnouncement byId = bdInfoListService.getById(id);
            byId.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
            sysInfoListMapper.updateById(byId);
        }
        return Result.OK("删除成功!");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysAnnouncement
     */
    @AutoLog(value = "特情消息-特情消息列表-特情消息导出", operateType =  6, operateTypeAlias = "导出-特情消息导出", permissionUrl = "/specialSituation/SpecialSituationList")
    @ApiOperation(value = "特情消息导出", notes = "特情消息导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysAnnouncement sysAnnouncement) {
        return bdInfoListService.reportExport(request, sysAnnouncement, SysAnnouncement.class, "特情记录表");
    }

}
