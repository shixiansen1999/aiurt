package com.aiurt.modules.major.controller;


import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


 /**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-专业")
@RestController
@RequestMapping("/major")
@Slf4j
public class CsMajorController  {
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private IMaterialBaseTypeService materialBaseTypeService;
	@Autowired
	private IDeviceTypeService deviceTypeService;
     @Autowired
     private IPatrolStandardService patrolStandardService;
     @Autowired
     private IInspectionCodeService inspectionCodeService;
     @Autowired
     private IFaultService faultService;
	/**
	 * 分页列表查询
	 *
	 * @param csMajor
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="专业分页列表查询", notes="专业分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsMajor csMajor,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
		Page<CsMajor> page = new Page<CsMajor>(pageNo, pageSize);
		IPage<CsMajor> pageList = csMajorService.page(page, queryWrapper.lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0));
		return Result.OK(pageList);
	}

	 @ApiOperation(value="专业列表查询", notes="专业列表查询")
	 @GetMapping(value = "/selectList")
	 public Result<?> selectList(CsMajor csMajor,
									HttpServletRequest req) {
		 QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
		 List<CsMajor> pageList = csMajorService.list(queryWrapper.lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0));
		 return Result.OK(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param csMajor
	 * @return
	 */
	@AutoLog(value = "专业添加")
	@ApiOperation(value="专业添加", notes="专业添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsMajor csMajor) {
		return csMajorService.add(csMajor);
	}

	/**
	 *  编辑
	 *
	 * @param csMajor
	 * @return
	 */
	@AutoLog(value = "专业编辑")
	@ApiOperation(value="专业编辑", notes="专业编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsMajor csMajor) {
		return csMajorService.update(csMajor);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "专业通过id删除")
	@ApiOperation(value="专业通过id删除", notes="专业通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsMajor csMajor = csMajorService.getById(id);
		//判断是否被子系统使用
		LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CsSubsystem::getMajorCode,csMajor.getMajorCode());
		wrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<CsSubsystem> list = csSubsystemService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("该专业被子系统使用中，不能删除!");
		}
		//判断是否被设备类型使用
		LambdaQueryWrapper<DeviceType> deviceWrapper = new LambdaQueryWrapper<>();
		deviceWrapper.eq(DeviceType::getMajorCode,csMajor.getMajorCode());
		deviceWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<DeviceType> deviceList = deviceTypeService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该专业被设备类型使用中，不能删除!");
		}
		//判断是否被物资分类使用
		LambdaQueryWrapper<MaterialBaseType> materWrapper = new LambdaQueryWrapper<>();
		materWrapper.eq(MaterialBaseType::getMajorCode,csMajor.getMajorCode());
		materWrapper.eq(MaterialBaseType::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<MaterialBaseType> materList = materialBaseTypeService.list(materWrapper);
		if(!materList.isEmpty()){
			return Result.error("该专业被物资分类使用中，不能删除!");
		}
        // 判断是否被巡检标准使用
        LambdaQueryWrapper<PatrolStandard> standardWrapper = new LambdaQueryWrapper<>();
        standardWrapper.eq(PatrolStandard::getProfessionCode,csMajor.getMajorCode());
        standardWrapper.eq(PatrolStandard::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<PatrolStandard> standardList = patrolStandardService.list(standardWrapper);
        if(!standardList.isEmpty()){
            return Result.error("该专业被巡检标准使用中，不能删除!");
        }
        // 判断是否被检修标准使用
        LambdaQueryWrapper<InspectionCode> insWrapper = new LambdaQueryWrapper<>();
        insWrapper.eq(InspectionCode::getMajorCode,csMajor.getMajorCode());
        insWrapper.eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<InspectionCode> insList = inspectionCodeService.list(insWrapper);
        if(!insList.isEmpty()){
            return Result.error("该专业被检修标准使用中，不能删除!");
        }
        // 判断是否被故障上报使用
        LambdaQueryWrapper<Fault> faultWrapper = new LambdaQueryWrapper<>();
        faultWrapper.eq(Fault::getMajorCode,csMajor.getMajorCode());
        List<Fault> faultList = faultService.list(faultWrapper);
        if(!faultList.isEmpty()){
            return Result.error("该专业被故障上报使用中，不能删除!");
        }
		csMajor.setDelFlag(CommonConstant.DEL_FLAG_1);
		csMajorService.updateById(csMajor);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="专业通过id查询", notes="专业通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsMajor csMajor = csMajorService.getById(id);
		if(csMajor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csMajor);
	}



}
