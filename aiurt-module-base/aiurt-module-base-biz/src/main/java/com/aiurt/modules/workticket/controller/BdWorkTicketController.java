package com.aiurt.modules.workticket.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.workticket.dto.UploadPictureDTO;
import com.aiurt.modules.workticket.dto.WorkTicketReqDTO;
import com.aiurt.modules.workticket.dto.WorkTicketResDTO;
import com.aiurt.modules.workticket.entity.BdWorkTicket;
import com.aiurt.modules.workticket.service.IBdWorkTicketService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description: bd_work_ticket
 * @Author: aiurt
 * @Date:   2022-10-08
 * @Version: V1.0
 */
@Api(tags="bd_work_ticket")
@RestController
@RequestMapping("/workticket/bdWorkTicket")
@Slf4j
public class BdWorkTicketController extends BaseController<BdWorkTicket, IBdWorkTicketService> {
	@Autowired
	private IBdWorkTicketService bdWorkTicketService;

	/**
	 *   添加
	 *
	 * @param bdWorkTicket
	 * @return
	 */
	@AutoLog(value = "bd_work_ticket-添加")
	@ApiOperation(value="bd_work_ticket-添加", notes="bd_work_ticket-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BdWorkTicket bdWorkTicket) {
		bdWorkTicketService.save(bdWorkTicket);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdWorkTicket
	 * @return
	 */
	@AutoLog(value = "bd_work_ticket-编辑")
	@ApiOperation(value="bd_work_ticket-编辑", notes="bd_work_ticket-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BdWorkTicket bdWorkTicket) {
		bdWorkTicketService.updateById(bdWorkTicket);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_work_ticket-通过id删除")
	@ApiOperation(value="bd_work_ticket-通过id删除", notes="bd_work_ticket-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		bdWorkTicketService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bd_work_ticket-批量删除")
	@ApiOperation(value="bd_work_ticket-批量删除", notes="bd_work_ticket-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdWorkTicketService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="bd_work_ticket-通过id查询", notes="bd_work_ticket-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BdWorkTicket> queryById(@RequestParam(name="id",required=true) String id) {
		BdWorkTicket bdWorkTicket = bdWorkTicketService.getById(id);
		if(bdWorkTicket==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdWorkTicket);
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="工作票详情数据", notes="工作票详情数据")
	@GetMapping(value = "/queryBusData")
	public Result<Map<String, Object>> queryBusData(@RequestParam(name="id",required=true) String id) {
		BdWorkTicket bdWorkTicket = bdWorkTicketService.getById(id);
		Map<String, Object> map = BeanUtil.beanToMap(bdWorkTicket);
		Map<String, Object> data = new HashMap<>(16);
		// 驼峰转_
		map.keySet().stream().forEach(key->{
			String s = StrUtil.toUnderlineCase(key);
			Object o = map.get(key);
			// 数组转换
			data.put(s, o);
			if (StrUtil.equalsIgnoreCase("work_partner", s)) {
				if (Objects.nonNull(o)) {
					data.put(s,  JSONObject.parseArray((String) o));
				}else {
					data.put(s, Collections.emptyList());
				}

			}

			if (StrUtil.equalsIgnoreCase("work_start_time", s)) {
				if (Objects.nonNull(o)) {
					data.put(s,  JSONObject.parseArray((String) o));
				}else {
					data.put(s, Collections.emptyList());
				}
			}

			if (StrUtil.equalsIgnoreCase("picture_path", s)) {

				if (Objects.nonNull(o)) {
					data.put(s,  JSONObject.parseArray((String) o));
				}else {
					data.put(s, Collections.emptyList());
				}
			}

			if (StrUtil.equalsIgnoreCase("work_leader", s)) {
				if (Objects.nonNull(o)) {
					data.put(s,  JSONObject.parseArray((String) o));
				}else {
					data.put(s, Collections.emptyList());
				}
			}

		});
		return Result.OK(data);
	}

	 /**
	  * 工作票查询
	  * @param workTicketReqDTO
	  * @return
	  */
	 @PostMapping(value = "/historyGet")
	 @ApiOperation(value="工作票查询", notes="工作票查询")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = WorkTicketResDTO.class),
	 })
	 public Result<Page<WorkTicketResDTO>> historyGet(@RequestBody WorkTicketReqDTO workTicketReqDTO) {
		 Page<WorkTicketResDTO> pageList = new Page<>(workTicketReqDTO.getPageNo(),workTicketReqDTO.getPageSize());
		 pageList = bdWorkTicketService.historyGet(pageList, workTicketReqDTO);
		 return Result.OK(pageList);
	 }


	/**
	 * 工作票任务分页列表查询
	 *
	 * @param bdWorkTicket
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	 @AutoLog(value = "工作票待办任务-分页列表查询")
	 @ApiOperation(value="工作票任务-分页列表查询", notes="工作票任务-分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<?> queryPageList(BdWorkTicket bdWorkTicket,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

		 //获取当前用户
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 if (Objects.isNull(sysUser)) {
			 throw  new AiurtBootException("请重新登录!");
		 }
		 // 自己写查询
		 Page<BdWorkTicket> pageList = new Page<>(pageNo, pageSize);
		 pageList = bdWorkTicketService.queryPageList(pageList, sysUser.getUsername());
		 return Result.OK(pageList);
	 }

	@GetMapping("/authUpload/{id}")
	@ApiOperation(value = "权限验证",notes = "权限验证")
	public Result<Boolean> authUpload(@PathVariable("id") String id) {
		Boolean bl = bdWorkTicketService.authUpload(id);
		return Result.OK(bl);
	}

	@PostMapping("upload_picture")
	@ApiOperation(value = "工作票上传",notes = "工作票上传")
	public Result<?> uploadPicture(@RequestBody UploadPictureDTO uploadPictureDTO) {

		bdWorkTicketService.uploadPicture(uploadPictureDTO);
		return Result.OK();
	}

}
