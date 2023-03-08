package com.aiurt.modules.message.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.message.entity.MsgParams;
import com.aiurt.modules.message.entity.SysMessageTemplate;
import com.aiurt.modules.message.service.ISysMessageTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Sate: 2019-04-09
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessageTemplate")
public class SysMessageTemplateController extends BaseController<SysMessageTemplate, ISysMessageTemplateService> {
	@Autowired
	private ISysMessageTemplateService sysMessageTemplateService;


	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private ISysParamAPI iSysParamAPI;
	/**
	 * 分页列表查询
	 *
	 * @param sysMessageTemplate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysMessageTemplate sysMessageTemplate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<SysMessageTemplate> queryWrapper = QueryGenerator.initQueryWrapper(sysMessageTemplate, req.getParameterMap());
		Page<SysMessageTemplate> page = new Page<SysMessageTemplate>(pageNo, pageSize);
		IPage<SysMessageTemplate> pageList = sysMessageTemplateService.page(page, queryWrapper);
        return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param sysMessageTemplate
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.save(sysMessageTemplate);
        return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param sysMessageTemplate
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.updateById(sysMessageTemplate);
        return Result.ok("更新成功！");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageTemplateService.removeById(id);
        return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.sysMessageTemplateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysMessageTemplate sysMessageTemplate = sysMessageTemplateService.getById(id);
        return Result.ok(sysMessageTemplate);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request,SysMessageTemplate sysMessageTemplate) {
		return super.exportXls(request, sysMessageTemplate, SysMessageTemplate.class,"推送消息模板");
	}

	/**
	 * excel导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysMessageTemplate.class);
	}

	/**
	 * 发送消息
	 */
	@PostMapping(value = "/sendMsg")
	@ApiOperation(value="消息发送测试", notes="消息发送测试")
	public Result<SysMessageTemplate> sendMessage(@RequestBody MsgParams msgParams) {
		Result<SysMessageTemplate> result = new Result<SysMessageTemplate>();
		try {
			MessageDTO md = new MessageDTO();
			md.setToAll(false);
			md.setTitle("消息发送测试");
			md.setTemplateCode(msgParams.getTemplateCode());
			md.setToUser(msgParams.getReceiver());
			md.setType(msgParams.getMsgType());
			String testData = msgParams.getTestData();
			/*if(StrUtil.isNotBlank(testData)){
				Map<String, Object> data = JSON.parseObject(testData, Map.class);
				md.setData(data);
			}*/
			HashMap<String, Object> map = new HashMap<>();
			map.put("testtime", "2023-03-08");
			map.put("testname", "123456789");
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID,"1632926394282512386");
			map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.INSPECTION.getType());
			md.setData(map);
			SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPECIAL_INFO_MESSAGE);
			md.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
			sysBaseApi.sendTemplateMessage(md);
			return result.success("消息发送成功！");
		} catch (Exception e) {
			log.error("发送消息出错", e.getMessage());
			return result.error500("发送消息出错！");
		}
	}
}
