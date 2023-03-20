package com.aiurt.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.WebsocketConst;
import com.aiurt.common.util.SqlInjectionUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.system.model.AnnouncementSendModel;
import com.aiurt.modules.system.service.ISysAnnouncementSendService;
import com.aiurt.modules.system.service.ISysAnnouncementService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Title: Controller
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date: 2019-02-21
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/sysAnnouncementSend")
@Slf4j
@Api(tags = "用户通告阅读标记表")
public class SysAnnouncementSendController {
    @Autowired
    private ISysAnnouncementSendService sysAnnouncementSendService;
    @Autowired
    private ISysAnnouncementService sysAnnouncementService;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    /**
     * 分页列表查询
     *
     * @param sysAnnouncementSend
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/list")
    public Result<IPage<SysAnnouncementSend>> queryPageList(SysAnnouncementSend sysAnnouncementSend,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        Result<IPage<SysAnnouncementSend>> result = new Result<IPage<SysAnnouncementSend>>();
        QueryWrapper<SysAnnouncementSend> queryWrapper = new QueryWrapper<SysAnnouncementSend>(sysAnnouncementSend);
        Page<SysAnnouncementSend> page = new Page<SysAnnouncementSend>(pageNo, pageSize);
        //排序逻辑 处理
        String column = req.getParameter("column");
        String order = req.getParameter("order");

        //issues/3331 SQL injection vulnerability
        SqlInjectionUtil.filterContent(column);
        SqlInjectionUtil.filterContent(order);

        if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
            String a = "asc";
            if (a.equals(order)) {
                queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
            } else {
                queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
            }
        }
        IPage<SysAnnouncementSend> pageList = sysAnnouncementSendService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sysAnnouncementSend
     * @return
     */
    @PostMapping(value = "/add")
    public Result<SysAnnouncementSend> add(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        try {
            sysAnnouncementSendService.save(sysAnnouncementSend);
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
     * @param sysAnnouncementSend
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<SysAnnouncementSend> eidt(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        SysAnnouncementSend sysAnnouncementSendEntity = sysAnnouncementSendService.getById(sysAnnouncementSend.getId());
        if (sysAnnouncementSendEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysAnnouncementSendService.updateById(sysAnnouncementSend);
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
    @DeleteMapping(value = "/delete")
    public Result<SysAnnouncementSend> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
        if (sysAnnouncementSend == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysAnnouncementSendService.removeById(id);
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
    @DeleteMapping(value = "/deleteBatch")
    public Result<SysAnnouncementSend> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysAnnouncementSendService.removeByIds(Arrays.asList(ids.split(",")));
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
    @GetMapping(value = "/queryById")
    public Result<SysAnnouncementSend> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
        if (sysAnnouncementSend == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysAnnouncementSend);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @param json
     * @return
     * @功能：更新用户系统消息阅读状态
     */
    @PutMapping(value = "/editByAnntIdAndUserId")
    @ApiOperation(value = "更新用户系统消息阅读状态", notes = "更新用户系统消息阅读状态")
    public Result<SysAnnouncementSend> editById(@RequestBody JSONObject json) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        String anntId = json.getString("anntId");
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
        updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
        updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
        updateWrapper.last("where annt_id ='" + anntId + "' and user_id ='" + userId + "'");
        SysAnnouncementSend announcementSend = new SysAnnouncementSend();
        sysAnnouncementSendService.update(announcementSend, updateWrapper);
        result.setSuccess(true);
        return result;
    }

    /**
     * @return
     * @功能：获取我的消息
     */
    @GetMapping(value = "/getMyAnnouncementSend")
    @ApiOperation(value = "获取我的消息-分页", notes = "获取我的消息-分页")
    public Result<IPage<AnnouncementSendModel>> getMyAnnouncementSend(AnnouncementSendModel announcementSendModel,
                                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<AnnouncementSendModel>> result = new Result<IPage<AnnouncementSendModel>>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        announcementSendModel.setUserId(userId);
        announcementSendModel.setPageNo((pageNo - 1) * pageSize);
        announcementSendModel.setPageSize(pageSize);
        Page<AnnouncementSendModel> pageList = new Page<AnnouncementSendModel>(pageNo, pageSize);
        pageList = sysAnnouncementSendService.getMyAnnouncementSendPage(pageList, announcementSendModel);
        List<AnnouncementSendModel> records = pageList.getRecords();
        for (AnnouncementSendModel record : records) {
            getUserNames(record);
        }
        result.setResult(pageList);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    private void getUserNames(@RequestBody AnnouncementSendModel announcementSendModel) {
        if (StrUtil.isNotBlank(announcementSendModel.getUserIds())) {
            String[] split = announcementSendModel.getUserIds().split(",");
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
                    announcementSendModel.setUserNames(str.deleteCharAt(str.length() - 1).toString());
                }
            }
        }
    }

    /**
     * @return
     * @功能：一键已读
     */
    @PutMapping(value = "/readAll")
    @ApiOperation(value = "一键已读", notes = "一键已读")
    @ApiImplicitParam(name = "msgCategory", value = "消息分类", required = false, dataTypeClass = String.class)
    public Result<SysAnnouncementSend> readAll(String msgCategory) {
        Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
        updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
        updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
        // 根据消息类型来已读消息
        String sql = "";
        if (StrUtil.isNotEmpty(msgCategory)) {
             sql = "and annt_id in (select id from sys_announcement where del_flag =0 and send_status =1 and msg_category in("+msgCategory+"))";
        }

        updateWrapper.last("where user_id ='" + userId + "' "+sql);
        SysAnnouncementSend announcementSend = new SysAnnouncementSend();
        sysAnnouncementSendService.update(announcementSend, updateWrapper);
        JSONObject socketParams = new JSONObject();
        socketParams.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
        webSocket.sendMessage(socketParams.toJSONString());
        result.setSuccess(true);
        result.setMessage("全部已读");
        return result;
    }
}
