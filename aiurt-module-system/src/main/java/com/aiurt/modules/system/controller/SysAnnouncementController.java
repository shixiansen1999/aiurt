package com.aiurt.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonSendStatus;
import com.aiurt.common.constant.WebsocketConst;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.common.util.TokenUtils;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.system.dto.SysAnnouncementDTO;
import com.aiurt.modules.system.dto.SysAnnouncementPageDTO;
import com.aiurt.modules.system.dto.SysMessageInfoDTO;
import com.aiurt.modules.system.dto.SysMessageTypeDTO;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.system.service.ISysAnnouncementSendService;
import com.aiurt.modules.system.service.ISysAnnouncementService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.aiurt.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import com.aiurt.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import com.aiurt.modules.system.util.XssUtils;
import com.aiurt.modules.todo.entity.SysTodoList;
import com.aiurt.modules.todo.service.ISysTodoListService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeecg.dingtalk.api.core.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.aiurt.common.constant.CommonConstant.ANNOUNCEMENT_SEND_STATUS_1;


/**
 * @Title: Controller
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/annountCement")
@Slf4j
@Api(tags = "系统通告表")
public class SysAnnouncementController {
    @Autowired
    private ISysAnnouncementService sysAnnouncementService;
    @Autowired
    private ISysAnnouncementSendService sysAnnouncementSendService;
    @Resource
    private WebSocket webSocket;
    @Autowired
    ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
    @Autowired
    ThirdAppDingtalkServiceImpl dingtalkService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;
    @Autowired
    private ISysTodoListService sysTodoListService;
    @Autowired
    @Lazy
    private RedisUtil redisUtil;

    /**
     * 分页列表查询
     *
     * @param sysAnnouncement
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "系统通告")
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
        QueryWrapper<SysAnnouncement> queryWrapper = QueryGenerator.initQueryWrapper(sysAnnouncement, req.getParameterMap());
        Page<SysAnnouncement> page = new Page<SysAnnouncement>(pageNo, pageSize);
        if (StrUtil.isNotEmpty(sTime)) {
            queryWrapper.lambda().ge(SysAnnouncement::getSendTime, sTime);
        }
        if (StrUtil.isNotEmpty(eTime)) {
            queryWrapper.lambda().le(SysAnnouncement::getSendTime, eTime);
        }
        queryWrapper.orderByDesc("create_time");
        //排序逻辑 处理
        IPage<SysAnnouncement> pageList = sysAnnouncementService.page(page, queryWrapper);
        List<SysAnnouncement> records = pageList.getRecords();
        for (SysAnnouncement record : records) {
            getUserNames(record);
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    private void getUserNames(@RequestBody SysAnnouncement sysAnnouncement) {
        if (StrUtil.isNotBlank(sysAnnouncement.getUserIds())) {
            String[] split = sysAnnouncement.getUserIds().split(",");
            if (split.length > 0) {
                StringBuilder str = new StringBuilder();
                for (String s : split) {
                    if (!Objects.isNull(s)) {
                        LoginUser userById = sysBaseApi.getUserByName(s);
                        if (!ObjectUtils.isEmpty(userById)) {
                            str.append(userById.getRealname()).append(",");
                        }
                    }
                }
                if (StrUtil.isNotBlank(str)) {
                    sysAnnouncement.setUserNames(str.deleteCharAt(str.length() - 1).toString());
                }
            }
        }
    }

    /**
     * 添加
     *
     * @param sysAnnouncement
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysAnnouncement> add(@RequestBody SysAnnouncement sysAnnouncement) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        try {
            // update-begin-author:liusq date:20210804 for:标题处理xss攻击的问题
            String title = XssUtils.striptXss(sysAnnouncement.getTitile());
            sysAnnouncement.setTitile(title);
            // update-end-author:liusq date:20210804 for:标题处理xss攻击的问题
            sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            //未发布
            sysAnnouncement.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);
            sysAnnouncementService.saveAnnouncement(sysAnnouncement);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param sysAnnouncement
     * @return
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<SysAnnouncement> eidt(@RequestBody SysAnnouncement sysAnnouncement) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        SysAnnouncement sysAnnouncementEntity = sysAnnouncementService.getById(sysAnnouncement.getId());
        if (sysAnnouncementEntity == null) {
            result.error500("未找到对应实体");
        } else {
            // update-begin-author:liusq date:20210804 for:标题处理xss攻击的问题
            String title = XssUtils.striptXss(sysAnnouncement.getTitile());
            sysAnnouncement.setTitile(title);
            // update-end-author:liusq date:20210804 for:标题处理xss攻击的问题
            boolean ok = sysAnnouncementService.upDateAnnouncement(sysAnnouncement);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysAnnouncement> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
        if (sysAnnouncement == null) {
            result.error500("未找到对应实体");
        } else {
            sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
            boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysAnnouncement> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String[] id = ids.split(",");
            for (int i = 0; i < id.length; i++) {
                SysAnnouncement announcement = sysAnnouncementService.getById(id[i]);
                announcement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
                sysAnnouncementService.updateById(announcement);
            }
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysAnnouncement> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
        if (sysAnnouncement == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysAnnouncement);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 更新发布操作
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/doReleaseData", method = RequestMethod.GET)
    public Result<SysAnnouncement> doReleaseData(@RequestParam(name = "id", required = true) String id, HttpServletRequest request) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
        if (sysAnnouncement == null) {
            result.error500("未找到对应实体");
        } else {
            //发布中
            sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);
            sysAnnouncement.setSendTime(new Date());
            String currentUserName = JwtUtil.getUserNameByToken(request);
            sysAnnouncement.setSender(currentUserName);
            boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
            if (ok) {
                result.success("该系统通知发布成功");
                if (sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
                    JSONObject obj = new JSONObject();
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
                    obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
                    obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
                    webSocket.sendMessage(obj.toJSONString());
                } else {
                    // 2.插入用户通告阅读标记表记录
                    String userId = sysAnnouncement.getUserIds();
                    String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
                    String anntId = sysAnnouncement.getId();
                    Date refDate = new Date();
                    JSONObject obj = new JSONObject();
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                    obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
                    obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
                    webSocket.sendMessage(userIds, obj.toJSONString());
                }
                try {
                    // 同步企业微信、钉钉的消息通知
                    Response<String> dtResponse = dingtalkService.sendActionCardMessage(sysAnnouncement, true);
                    wechatEnterpriseService.sendTextCardMessage(sysAnnouncement, true);

                    if (dtResponse != null && dtResponse.isSuccess()) {
                        String taskId = dtResponse.getResult();
                        sysAnnouncement.setDtTaskId(taskId);
                        sysAnnouncementService.updateById(sysAnnouncement);
                    }
                } catch (Exception e) {
                    log.error("同步发送第三方APP消息失败：", e);
                }
            }
        }

        return result;
    }

    /**
     * 更新撤销操作
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
    public Result<SysAnnouncement> doReovkeData(@RequestParam(name = "id", required = true) String id, HttpServletRequest request) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
        if (sysAnnouncement == null) {
            result.error500("未找到对应实体");
        } else {
            //撤销发布
            sysAnnouncement.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);
            sysAnnouncement.setCancelTime(new Date());
            boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
            if (ok) {
                result.success("该系统通知撤销成功");
                if (oConvertUtils.isNotEmpty(sysAnnouncement.getDtTaskId())) {
                    try {
                        dingtalkService.recallMessage(sysAnnouncement.getDtTaskId());
                    } catch (Exception e) {
                        log.error("第三方APP撤回消息失败：", e);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @return
     * @功能：补充用户数据，并返回系统消息
     */
    @ApiOperation(value = "补充用户数据，并返回系统消息", notes = "补充用户数据，并返回系统消息")
    @RequestMapping(value = "/listByUser", method = RequestMethod.GET)
    public Result<SysAnnouncementDTO> listByUser(@RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        SysAnnouncementDTO result = new SysAnnouncementDTO();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        // 1.将系统消息补充到用户通告阅读标记表中
        LambdaQueryWrapper<SysAnnouncement> querySaWrapper = new LambdaQueryWrapper<SysAnnouncement>();
        // 全部人员
        querySaWrapper.eq(SysAnnouncement::getMsgType, CommonConstant.MSG_TYPE_ALL);
        // 未删除
        querySaWrapper.eq(SysAnnouncement::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        // 已发布
        querySaWrapper.eq(SysAnnouncement::getSendStatus, CommonConstant.HAS_SEND);
        // 新注册用户不看结束通知
        querySaWrapper.ge(SysAnnouncement::getEndTime, sysUser.getCreateTime());
        querySaWrapper.notInSql(SysAnnouncement::getId, "select annt_id from sys_announcement_send where user_id='" + userId + "'");
        List<SysAnnouncement> announcements = sysAnnouncementService.list(querySaWrapper);
        if (announcements.size() > 0) {
            for (int i = 0; i < announcements.size(); i++) {
                // 通知公告消息重复
                // 因为websocket没有判断是否存在这个用户，要是判断会出现问题，故在此判断逻辑
                LambdaQueryWrapper<SysAnnouncementSend> query = new LambdaQueryWrapper<>();
                query.eq(SysAnnouncementSend::getAnntId, announcements.get(i).getId());
                query.eq(SysAnnouncementSend::getUserId, userId);
                SysAnnouncementSend one = sysAnnouncementSendService.getOne(query);
                if (null == one) {
                    log.info("listByUser接口新增了SysAnnouncementSend：pageSize{}：" + pageSize);
                    SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                    announcementSend.setAnntId(announcements.get(i).getId());
                    announcementSend.setUserId(userId);
                    announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                    sysAnnouncementSendService.save(announcementSend);
                    log.info("announcementSend.toString()", announcementSend.toString());
                }
            }
        }
        // 2.查询用户未读的系统消息
        // 通知
        Page<SysAnnouncement> anntMsgList = new Page<SysAnnouncement>(0, pageSize);
        anntMsgList = sysAnnouncementService.querySysCementPageByUserId(anntMsgList, userId, Arrays.asList(CommonConstant.MSG_CATEGORY_1));

        // 消息
        Page<SysAnnouncement> sysMsgList = new Page<SysAnnouncement>(0, pageSize);
        sysMsgList = sysAnnouncementService.querySysCementPageByUserId(sysMsgList, userId, Arrays.asList(CommonConstant.MSG_CATEGORY_2, CommonConstant.MSG_CATEGORY_3));

        // 我的待办任务
        Page<SysTodoList> listPage = new Page<SysTodoList>(0, pageSize);
        SysTodoList sysTodoList = new SysTodoList();
        sysTodoList.setCurrentUserName(sysUser.getUsername());
        // 待办或待阅
        sysTodoList.setTodoType(CommonConstant.TODO_TYPE_0 + "," + CommonConstant.TODO_TYPE_2);
        IPage<SysTodoList> todoTaskList = sysTodoListService.queryPageList(listPage, sysTodoList);

        // 封装结果
        result.setSysMsgList(sysMsgList.getRecords());
        result.setSysMsgTotal(sysMsgList.getTotal());
        result.setAnntMsgList(anntMsgList.getRecords());
        result.setAnntMsgTotal(anntMsgList.getTotal());
        result.setTodoTaskList(todoTaskList.getRecords());
        result.setTodoTaskTotal(todoTaskList.getTotal());
        return Result.OK(result);
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysAnnouncement sysAnnouncement, HttpServletRequest request) {
        // Step.1 组装查询条件
        LambdaQueryWrapper<SysAnnouncement> queryWrapper = new LambdaQueryWrapper<SysAnnouncement>(sysAnnouncement);
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        queryWrapper.eq(SysAnnouncement::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        List<SysAnnouncement> pageList = sysAnnouncementService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "系统通告列表");
        mv.addObject(NormalExcelConstants.CLASS, SysAnnouncement.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("系统通告列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysAnnouncement> listSysAnnouncements = ExcelImportUtil.importExcel(file.getInputStream(), SysAnnouncement.class, params);
                for (SysAnnouncement sysAnnouncementExcel : listSysAnnouncements) {
                    if (sysAnnouncementExcel.getDelFlag() == null) {
                        sysAnnouncementExcel.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
                    }
                    sysAnnouncementService.save(sysAnnouncementExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysAnnouncements.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 同步消息
     *
     * @param anntId
     * @return
     */
    @RequestMapping(value = "/syncNotic", method = RequestMethod.GET)
    public Result<SysAnnouncement> syncNotic(@RequestParam(name = "anntId", required = false) String anntId, HttpServletRequest request) {
        Result<SysAnnouncement> result = new Result<SysAnnouncement>();
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(anntId)) {
            SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(anntId);
            if (sysAnnouncement == null) {
                result.error500("未找到对应实体");
            } else {
                if (sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
                    obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
                    obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
                    obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
                    webSocket.sendMessage(obj.toJSONString());
                } else {
                    // 2.插入用户通告阅读标记表记录
                    String userId = sysAnnouncement.getUserIds();
                    if (oConvertUtils.isNotEmpty(userId)) {
                        String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
                        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                        obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
                        obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
                        webSocket.sendMessage(userIds, obj.toJSONString());
                    }
                }
            }
        } else {
            obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
            obj.put(WebsocketConst.MSG_TXT, "批量设置已读");
            webSocket.sendMessage(obj.toJSONString());
        }
        return result;
    }

    /**
     * 通告查看详情页面（用于第三方APP）
     *
     * @param modelAndView
     * @param id
     * @return
     */
    @GetMapping("/show/{id}")
    public ModelAndView showContent(ModelAndView modelAndView, @PathVariable("id") String id, HttpServletRequest request) {
        SysAnnouncement announcement = sysAnnouncementService.getById(id);
        if (announcement != null) {
            boolean tokenOk = false;
            try {
                // 验证Token有效性
                tokenOk = TokenUtils.verifyToken(request, sysBaseApi, redisUtil);
            } catch (Exception ignored) {
            }
            // 判断是否传递了Token，并且Token有效，如果传了就不做查看限制，直接返回
            // 如果Token无效，就做查看限制：只能查看已发布的
            if (tokenOk || ANNOUNCEMENT_SEND_STATUS_1.equals(announcement.getSendStatus())) {
                modelAndView.addObject("data", announcement);
                modelAndView.setViewName("announcement/showContent");
                return modelAndView;
            }
        }
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @AutoLog(value = "消息中心-消息类型")
    @ApiOperation(value="消息中心-消息类型", notes="消息中心-消息类型")
    @GetMapping(value = "/queryAnnouncementCount")
    public Result<List<SysMessageTypeDTO>> queryMessageType(){
         List<SysMessageTypeDTO> list = sysAnnouncementService.queryMessageType();
        return Result.ok(list);
    }

    @AutoLog(value = "消息中心-业务消息类型-详情")
    @ApiOperation(value="消息中心-业务消息类型-详情", notes="消息中心-业务消息类型-详情")
    @GetMapping(value = "/queryAnnouncementInfo")
    public Result<IPage<SysMessageInfoDTO>> queryAnnouncementInfo( @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                         @ApiParam(name = "messageFlag", value = "1:业务、2:流程 ")@RequestParam(name="messageFlag",required=true) String  messageFlag,
                                                                   @ApiParam(name = "msgCategory", value = "消息类型1:通知公告2:系统消息3:特情消息 ")@RequestParam(name="msgCategory",required=false) String  msgCategory,
                                                                 @ApiParam(name = "todoType", value = "0：待办、1：已办、2：待阅、3：已阅")@RequestParam(name="todoType",required=false) String  todoType,
                                                                 @ApiParam(name = "keyWord", value = "关键字")@RequestParam(name="keyWord",required=false) String  keyWord,
                                                                 @ApiParam(name = "busType", value = "fault:故障、situation:特情 、trainplan，trainrecheck:培训、worklog:工作日志、inspection_assign,inspection:检修、patrol_assign，patrol_audit:巡视、patrol:巡视流程、fault:故障流程、emergency:应急业务消息、inspection:检修流程")@RequestParam(name="busType",required=false) String  busType){
        Page<SysMessageInfoDTO> page = new Page<>(pageNo,pageSize);
        IPage<SysMessageInfoDTO> sysMessageInfoDTOS = sysAnnouncementService.queryMessageInfo(page,messageFlag, todoType, keyWord,busType,msgCategory);
        return Result.ok(sysMessageInfoDTOS);
    }

    @AutoLog(value = "消息中心-业务消息类型-查询未读消息页码")
    @ApiOperation(value="消息中心-业务消息类型-查询未读消息页码", notes="消息中心-业务消息类型-查询未读消息页码")
    @GetMapping(value = "/queryPageNumber")
    public Result<SysAnnouncementPageDTO> queryPageNumber(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          @ApiParam(name = "messageFlag", value = "1:业务、2:流程 ")@RequestParam(name="messageFlag",required=true) String  messageFlag,
                                                          @ApiParam(name = "msgCategory", value = "消息类型1:通知公告2:系统消息3:特情消息 ")@RequestParam(name="msgCategory",required=false) String  msgCategory,
                                                          @ApiParam(name = "todoType", value = "0：待办、1：已办、2：待阅、3：已阅")@RequestParam(name="todoType",required=false) String  todoType,
                                                          @ApiParam(name = "keyWord", value = "关键字")@RequestParam(name="keyWord",required=false) String  keyWord,
                                                          @ApiParam(name = "busType", value = "fault:故障、situation:特情 、trainplan，trainrecheck:培训、worklog:工作日志、inspection_assign,inspection:检修、patrol_assign，patrol_audit:巡视、patrol:巡视流程、fault:故障流程、emergency:应急业务消息、inspection:检修流程")@RequestParam(name="busType",required=false) String  busType){
        Page<Object> page = new Page<>(pageNo, pageSize);
        SysAnnouncementPageDTO sysAnnouncementPageDTO = sysAnnouncementService.queryPageNumber(page, messageFlag, todoType, keyWord, busType, msgCategory);
        return Result.ok(sysAnnouncementPageDTO);
    }


}
