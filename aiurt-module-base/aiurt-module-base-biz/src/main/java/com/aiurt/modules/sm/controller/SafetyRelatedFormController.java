package com.aiurt.modules.sm.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sm.entity.SafetyRelatedForm;
import com.aiurt.modules.sm.service.ISafetyRelatedFormService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: safety_related_form
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
@Api(tags="safety_related_form")
@RestController
@RequestMapping("/safetyRelatedForm/safetyRelatedForm")
@Slf4j
public class SafetyRelatedFormController extends BaseController<SafetyRelatedForm, ISafetyRelatedFormService> {
	@Autowired
	private ISafetyRelatedFormService safetyRelatedFormService;

	/**
	 * 分页列表查询
	 *
	 * @param safetyRelatedForm
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "safety_related_form-分页列表查询")
	@ApiOperation(value="safety_related_form-分页列表查询", notes="safety_related_form-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SafetyRelatedForm>> queryPageList(SafetyRelatedForm safetyRelatedForm,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SafetyRelatedForm> queryWrapper = QueryGenerator.initQueryWrapper(safetyRelatedForm, req.getParameterMap());
		Page<SafetyRelatedForm> page = new Page<SafetyRelatedForm>(pageNo, pageSize);
		IPage<SafetyRelatedForm> pageList = safetyRelatedFormService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 * @return
	 */
	@AutoLog(value = "safety_related_form-添加")
	@ApiOperation(value="safety_related_form-添加", notes="safety_related_form-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestParam(name="code",required=true) String code,
							  @RequestParam(name = "status",required = true) Integer status,
							  @RequestParam(name = "safetyAttentionIds")String safetyAttentionIds) {
		LambdaQueryWrapper<SafetyRelatedForm> wrapper = new LambdaQueryWrapper<>();
		if(0==status){
			wrapper.eq(SafetyRelatedForm::getPatrolStandardCode,code);
		}
		if (1==status){
			wrapper.eq(SafetyRelatedForm::getInspectionCode,code);
		}
		List<SafetyRelatedForm>safetyRelatedForms = safetyRelatedFormService.list(wrapper);
		safetyRelatedFormService.removeBatchByIds(safetyRelatedForms);
		Arrays.asList(safetyAttentionIds.split(",")).stream().forEach(id->{
			SafetyRelatedForm safetyRelatedForm =new SafetyRelatedForm();
			safetyRelatedForm.setStatus(status);
			if(0==status){
			  safetyRelatedForm.setPatrolStandardCode(code);
			  safetyRelatedForm.setSafetyAttentionId(id);
			}
			if (1==status){
			  safetyRelatedForm.setInspectionCode(code);
			  safetyRelatedForm.setSafetyAttentionId(id);
			  wrapper.eq(SafetyRelatedForm::getInspectionCode,code);
			}
			safetyRelatedFormService.save(safetyRelatedForm);
		});
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param safetyRelatedForm
	 * @return
	 */
	@AutoLog(value = "safety_related_form-编辑")
	@ApiOperation(value="safety_related_form-编辑", notes="safety_related_form-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SafetyRelatedForm safetyRelatedForm) {
		safetyRelatedFormService.updateById(safetyRelatedForm);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param
	 * @return
	 */
	@AutoLog(value = "safety_related_form-通过code删除")
	@ApiOperation(value="safety_related_form-通过code删除", notes="safety_related_form-通过code删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="code",required=true) String code,
								 @RequestParam(name = "safetyAttentionId")String safetyAttentionId,
								 @RequestParam(name = "status",required = true) Integer status) {
		LambdaQueryWrapper<SafetyRelatedForm> wrapper = new LambdaQueryWrapper<>();
		if(0==status){
			wrapper.eq(SafetyRelatedForm::getPatrolStandardCode,code);
		}
		if (1==status){
			wrapper.eq(SafetyRelatedForm::getInspectionCode,code);
		}
		wrapper.eq(SafetyRelatedForm::getSafetyAttentionId,safetyAttentionId);
		SafetyRelatedForm safetyRelatedForm = safetyRelatedFormService.getBaseMapper().selectOne(wrapper);
		safetyRelatedForm.setDelFlag(1);
		safetyRelatedFormService.updateById(safetyRelatedForm);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param
	 * @return
	 */
	@AutoLog(value = "safety_related_form-批量删除")
	@ApiOperation(value="safety_related_form-批量删除", notes="safety_related_form-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="code",required=true) String code,
									  @RequestParam(name = "safetyAttentionIds")String safetyAttentionIds,
									  @RequestParam(name = "status",required = true) Integer status) {
		Arrays.asList(safetyAttentionIds.split(",")).stream().forEach(id->{
			this.delete(code,id,status);
		});
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "safety_related_form-通过id查询")
	@ApiOperation(value="safety_related_form-通过id查询", notes="safety_related_form-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SafetyRelatedForm> queryById(@RequestParam(name="id",required=true) String id) {
		SafetyRelatedForm safetyRelatedForm = safetyRelatedFormService.getById(id);
		if(safetyRelatedForm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(safetyRelatedForm);
	}

	 /**
	  * 通过巡视或者检修code查询
	  * @param code
	  * @param status
	  * @return
	  */
	 //@AutoLog(value = "safety_related_form-通过id查询")
	 @ApiOperation(value="通过巡视或者检修code查询数据", notes="通过巡视或者检修code查询数据")
	 @GetMapping(value = "/queryByCode")
	 public Result<List<SafetyRelatedForm>> queryByCode(@RequestParam(name="code",required=true) String code,
												  @RequestParam(name = "status",required = true) Integer status) {
		 LambdaQueryWrapper<SafetyRelatedForm> wrapper = new LambdaQueryWrapper<>();
		 if(0==status){
		 	wrapper.eq(SafetyRelatedForm::getPatrolStandardCode,code);
		 }
		 if (1==status){
			 wrapper.eq(SafetyRelatedForm::getInspectionCode,code);
		 }
		 List<SafetyRelatedForm> safetyRelatedForm = safetyRelatedFormService.getBaseMapper().selectList(wrapper);
		 return Result.OK(safetyRelatedForm);
	 }

}
