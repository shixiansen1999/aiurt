package com.aiurt.boot.modules.appMessage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.entity.MessageRead;
import com.aiurt.boot.modules.appMessage.mapper.MessageMapper;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.param.MessagePageParam;
import com.aiurt.boot.modules.appMessage.service.IMessageReadService;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.appMessage.utils.AppMessageUtils;
import com.aiurt.boot.modules.appMessage.vo.MessageSizeVO;
import com.aiurt.boot.modules.appMessage.vo.MessageStatusVO;
import com.aiurt.boot.modules.appMessage.vo.MessageUserVO;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 消息
 * @Author: lhz
 * @Date: 2021-10-29
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "消息")
@RestController
@RequestMapping("/appMessage/message")
public class MessageController {
	@Autowired
	private IMessageService messageService;
	@Autowired
	private MessageMapper messageMapper;
	@Autowired
	private IMessageReadService messageReadService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private AppMessageUtils appMessageUtils;

	/**
	 * 分页列表查询
	 *
	 * @param message
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "消息-分页列表查询")
	@ApiOperation(value = "消息-分页列表查询", notes = "消息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MessageUserVO>> queryPageList(MessageUserVO message,
	                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                                  HttpServletRequest req) {
		Result<IPage<MessageUserVO>> result = new Result<IPage<MessageUserVO>>();
		Page<MessageUserVO> page = new Page<MessageUserVO>(pageNo, pageSize);
		IPage<MessageUserVO> pageList = messageService.queryPageList(page, message);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	@AutoLog(value = "app分页列表查询")
	@ApiOperation(value = "app分页列表查询", notes = "app分页列表查询")
	@GetMapping(value = "/getMessageList")
	public Result<MessageSizeVO> getMessageList(HttpServletRequest req, MessagePageParam param) {
		Result<MessageSizeVO> result = new Result<>();
		MessageSizeVO vo = new MessageSizeVO();
		if (StringUtils.isBlank(param.getUserId())) {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
		}
		IPage<MessageStatusVO> pageList = messageService.getMessagePage(param);
		Integer readCount = this.messageMapper.selectMessageReadCount(param);
		Integer unReadCount = this.messageMapper.selectMessageUnReadCount(param);
		vo.setPage(pageList).setReadSize(readCount).setUnReadSize(unReadCount);
		result.setSuccess(true);
		result.setResult(vo);
		return result;
	}

	@AutoLog(value = "获取指定条数消息")
	@ApiOperation(value = "获取指定条数消息", notes = "获取指定条数消息")
	@GetMapping(value = "/getNewMessage")
	public Result<List<Message>> getNewMessage(HttpServletRequest req, MessagePageParam param) {
		Result<List<Message>> result = new Result<>();
		//用户id
		if (StringUtils.isBlank(param.getUserId())) {
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(user.getId());
		}
		List<Message> pageList = messageService.getNewMessage(param);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "消息-添加")
	@ApiOperation(value = "消息-添加", notes = "消息-添加")
	@PostMapping(value = "/add")
	@Transactional(rollbackFor = Exception.class)
	public Result<?> add(@RequestBody MessageAddParam param) {
		Message message = new Message();
		BeanUtils.copyProperties(param, message);
		try {
			Date date = new Date();
			message.setCreateTime(date);
			messageService.save(message);
			List<MessageRead> list = new ArrayList<>();
			for (String userId : param.getUserIds()) {
				MessageRead read = new MessageRead();
				read.setMessageId(message.getId())
						.setReadFlag(0)
						.setDelFlag(0)
						.setCreateTime(date)
						.setUpdateTime(date)
						.setStaffId(userId)
						.setStaffName(sysUserService.getById(userId).getRealname());
				list.add(read);
			}
			messageReadService.saveBatch(list);
		} catch (Exception e) {
			//报错强制手动回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error(e.getMessage(), e);
			Result.error("操作失败");
		}
		return Result.ok();
	}

	/**
	 * 改消息状态至已读
	 *
	 * @param messageIds 消息ids
	 * @return {@code Result<?>}
	 */
	@AutoLog(value = "改消息状态至已读")
	@ApiOperation(value = "改消息状态至已读", notes = "改消息状态至已读")
	@PostMapping(value = "/readMessage")
	@Transactional(rollbackFor = Exception.class)
	public Result<?> readMessage(@RequestBody @NotNull(message = "消息id不能为空") List<Long> messageIds) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		return appMessageUtils.readMessage(user.getId(), messageIds) ? Result.ok() : Result.error("转换状态错误");
	}


	/**
	 * 编辑
	 *
	 * @param message
	 * @return
	 */
	@AutoLog(value = "消息-编辑")
	@ApiOperation(value = "消息-编辑", notes = "消息-编辑")
	@PutMapping(value = "/edit")
	public Result<Message> edit(@RequestBody Message message) {
		Result<Message> result = new Result<Message>();
		Message messageEntity = messageService.getById(message.getId());
		if (messageEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = messageService.updateById(message);
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
	@AutoLog(value = "消息-通过id删除")
	@ApiOperation(value = "消息-通过id删除", notes = "消息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
		try {
			messageService.removeById(id);
			messageReadService.remove(new LambdaQueryWrapper<MessageRead>().eq(MessageRead::getMessageId, id));
		} catch (Exception e) {
			log.error("删除失败,{}", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "消息-批量删除")
	@ApiOperation(value = "消息-批量删除", notes = "消息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Message> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<Message> result = new Result<Message>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			List<String> list = Arrays.asList(ids.split(","));
			this.messageService.removeByIds(list);
			messageReadService.remove(new LambdaQueryWrapper<MessageRead>().in(MessageRead::getMessageId, list));
			result.success("删除成功!");
		}
		return result;
	}


}
