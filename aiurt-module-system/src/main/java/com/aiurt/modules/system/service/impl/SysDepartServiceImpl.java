package com.aiurt.modules.system.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.FillRuleConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.FillRuleUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.YouBianCodeUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.system.dto.SysDepartModel;
import com.aiurt.modules.system.entity.*;
import com.aiurt.modules.system.mapper.*;
import com.aiurt.modules.system.model.DepartIdModel;
import com.aiurt.modules.system.model.SysDepartTreeModel;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.util.FindsDepartsChildrenUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepart> implements ISysDepartService {

	@Autowired
	private SysUserDepartMapper userDepartMapper;
	@Autowired
	private SysDepartRoleMapper sysDepartRoleMapper;
	@Autowired
	private SysDepartPermissionMapper departPermissionMapper;
	@Autowired
	private SysDepartRolePermissionMapper departRolePermissionMapper;
	@Autowired
	private SysDepartRoleUserMapper departRoleUserMapper;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SysDepartMapper sysDepartMapper;
	@Lazy
	@Autowired
	private ISysBaseAPI iSysBaseAPI;
	@Lazy
	@Autowired
	private CommonAPI api;

	@Value("${jeecg.path.upload}")
	private String upLoadPath;

	@Override
	public List<SysDepartTreeModel> queryMyDeptTreeList(String departIds) {
		//根据部门id获取所负责部门
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		String[] codeArr = this.getMyDeptParentOrgCode(departIds);
		for(int i=0;i<codeArr.length;i++){
			query.or().likeRight(SysDepart::getOrgCode,codeArr[i]);
		}
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.orderByAsc(SysDepart::getDepartOrder);
		//将父节点ParentId设为null
		List<SysDepart> listDepts = this.list(query);
		for(int i=0;i<codeArr.length;i++){
			for(SysDepart dept : listDepts){
				if(dept.getOrgCode().equals(codeArr[i])){
					dept.setParentId(null);
				}
			}
		}
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(listDepts);
		return listResult;
	}

	/**
	 * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
	 */
	@Override
	public List<SysDepartTreeModel> queryTreeList() {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.orderByAsc(SysDepart::getDepartOrder);
		query.orderByDesc(SysDepart::getCreateTime);
		List<SysDepart> list = this.list(query);
        //update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
		//设置用户id,让前台显示
        this.setUserIdsByDepList(list);
        //update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
		return listResult;
	}

	@Override
	public List<SysDepartTreeModel> querySignTreeList(String sign) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<CsUserDepartModel> departByUserId = api.getDepartByUserId(sysUser.getId());
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		if(StrUtil.isNotBlank(sign) && CollectionUtil.isNotEmpty(departByUserId)){
			List<String> collect = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
			query.in(SysDepart::getOrgCode,collect);
		}
		query.orderByAsc(SysDepart::getDepartOrder);
		query.orderByDesc(SysDepart::getCreateTime);
		List<SysDepart> list = this.list(query);
		//update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
		//设置用户id,让前台显示
		this.setUserIdsByDepList(list);

		//update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
		return listResult;
	}

	/**
	 * queryTreeList 根据部门id查询,前端回显调用
	 */
	@Override
	public List<SysDepartTreeModel> queryTreeList(String ids) {
		List<SysDepartTreeModel> listResult=new ArrayList<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		if(oConvertUtils.isNotEmpty(ids)){
			query.in(true,SysDepart::getId,ids.split(","));
		}
		query.orderByAsc(SysDepart::getDepartOrder);
		query.orderByDesc(SysDepart::getCreateTime);
		List<SysDepart> list= this.list(query);
		for (SysDepart depart : list) {
			listResult.add(new SysDepartTreeModel(depart));
		}
		return  listResult;

	}

	/**
	 * 机构树
	 * @return
	 */
	@Override
	public List<DepartIdModel> queryDepartIdTreeList() {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.orderByAsc(SysDepart::getDepartOrder);
		query.orderByDesc(SysDepart::getCreateTime);
		List<SysDepart> list = this.list(query);
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<DepartIdModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToDepartIdTreeList(list);
		return listResult;
	}

	/**
	 * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveDepartData(SysDepart sysDepart, String username) {
		if (sysDepart != null && username != null) {
			if (sysDepart.getParentId() == null) {
				sysDepart.setParentId("");
			}
			String s = UUID.randomUUID().toString().replace("-", "");
			sysDepart.setId(s);
			// 先判断该对象有无父级ID,有则意味着不是最高级,否则意味着是最高级
			// 获取父级ID
			String parentId = sysDepart.getParentId();
			// 更新排序的序号
			List<SysDepart> departs = this.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
					.in(SysDepart::getParentId, parentId)
					.orderByDesc(SysDepart::getDepartOrder)
					.last("limit 1")
					.list();
			if (CollectionUtil.isNotEmpty(departs)) {
				SysDepart depart = departs.stream().findFirst().orElse(new SysDepart());
				Integer order = depart.getDepartOrder();
				sysDepart.setDepartOrder(1);
				if (ObjectUtil.isNotEmpty(order)) {
					sysDepart.setDepartOrder(order + 1);
				}
			}
			//update-begin--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
			JSONObject formData = new JSONObject();
			formData.put("parentId", parentId);
			String[] codeArray = (String[]) FillRuleUtil.executeRule(FillRuleConstant.DEPART,formData);
			//update-end--Author:baihailong  Date:20191209 for：部门编码规则生成器做成公用配置
			sysDepart.setOrgCode(codeArray[0]);
			String orgType = codeArray[1];
			sysDepart.setOrgType(String.valueOf(orgType));
			sysDepart.setCreateTime(new Date());
			sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
			if (StrUtil.isBlank(parentId)) {
				sysDepart.setOrgCodeCc("/"+sysDepart.getOrgCode()+"/");
			}else {
				// 查询上级的编码
				SysDepart depart = baseMapper.selectById(parentId);
				if (Objects.nonNull(depart) && StrUtil.isNotBlank(depart.getOrgCodeCc())) {
					sysDepart.setOrgCodeCc(depart.getOrgCodeCc()+""+sysDepart.getOrgCode()+"/");
				}
			}
			this.save(sysDepart);
            //update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
			//新增部门的时候新增负责部门
            if(oConvertUtils.isNotEmpty(sysDepart.getDirectorUserIds())){
			    this.addDepartByUserIds(sysDepart,sysDepart.getDirectorUserIds());
            }
            //update-end---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
         }

	}



	/**
	 * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateDepartDataById(SysDepart sysDepart, String username) {
		if (sysDepart != null && username != null) {
			sysDepart.setUpdateTime(new Date());
			sysDepart.setUpdateBy(username);
			this.updateById(sysDepart);
            //update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
			//修改部门管理的时候，修改负责部门
            this.updateChargeDepart(sysDepart);
            //update-begin---author:wangshuai ---date:20220307  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
			return true;
		} else {
			return false;
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatchWithChildren(List<String> ids) {
		List<String> idList = new ArrayList<String>();
		for(String id: ids) {
			idList.add(id);
			this.checkChildrenExists(id, idList);
		}
		this.removeByIds(idList);
		//根据部门id获取部门角色id
		List<String> roleIdList = new ArrayList<>();
		LambdaQueryWrapper<SysDepartRole> query = new LambdaQueryWrapper<>();
		query.select(SysDepartRole::getId).in(SysDepartRole::getDepartId, idList);
		List<SysDepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
		for(SysDepartRole deptRole : depRoleList){
			roleIdList.add(deptRole.getId());
		}
		//根据部门id删除用户与部门关系
		userDepartMapper.delete(new LambdaQueryWrapper<SysUserDepart>().in(SysUserDepart::getDepId,idList));
		//根据部门id删除部门授权
		departPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>().in(SysDepartPermission::getDepartId,idList));
		//根据部门id删除部门角色
		sysDepartRoleMapper.delete(new LambdaQueryWrapper<SysDepartRole>().in(SysDepartRole::getDepartId,idList));
		if(roleIdList != null && roleIdList.size()>0){
			//根据角色id删除部门角色授权
			departRolePermissionMapper.delete(new LambdaQueryWrapper<SysDepartRolePermission>().in(SysDepartRolePermission::getRoleId,roleIdList));
			//根据角色id删除部门角色用户信息
			departRoleUserMapper.delete(new LambdaQueryWrapper<SysDepartRoleUser>().in(SysDepartRoleUser::getDroleId,roleIdList));
		}
	}

	@Override
	public List<String> getSubDepIdsByDepId(String departId) {
		return this.baseMapper.getSubDepIdsByDepId(departId);
	}

	@Override
	public List<String> getMySubDepIdsByDepId(String departIds) {
		//根据部门id获取所负责部门
		String[] codeArr = this.getMyDeptParentOrgCode(departIds);
		if(codeArr==null || codeArr.length==0){
			return null;
		}
		return this.baseMapper.getSubDepIdsByOrgCodes(codeArr);
	}

	/**
	 * <p>
	 * 根据关键字搜索相关的部门数据
	 * </p>
	 */
	@Override
	public List<SysDepartTreeModel> searchByKeyWord(String keyWord,String myDeptSearch,String departIds) {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		List<SysDepartTreeModel> newList = new ArrayList<>();
		//myDeptSearch不为空时为我的部门搜索，只搜索所负责部门
		if(!StringUtil.isNullOrEmpty(myDeptSearch)){
			//departIds 为空普通用户或没有管理部门
			if(StringUtil.isNullOrEmpty(departIds)){
				return newList;
			}
			//根据部门id获取所负责部门
			String[] codeArr = this.getMyDeptParentOrgCode(departIds);
			//update-begin-author:taoyan date:20220104 for:/issues/3311 当用户属于两个部门的时候，且这两个部门没有上下级关系，我的部门-部门名称查询条件模糊搜索失效！
			if (codeArr != null && codeArr.length > 0) {
				query.nested(i -> {
					for (String s : codeArr) {
						i.or().likeRight(SysDepart::getOrgCode, s);
					}
				});
			}
			//update-end-author:taoyan date:20220104 for:/issues/3311 当用户属于两个部门的时候，且这两个部门没有上下级关系，我的部门-部门名称查询条件模糊搜索失效！
			query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		}
		query.like(SysDepart::getDepartName, keyWord);
		//update-begin--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索回显优化--------------------
		SysDepartTreeModel model = new SysDepartTreeModel();
		List<SysDepart> departList = this.list(query);
		if(departList.size() > 0) {
			for(SysDepart depart : departList) {
				model = new SysDepartTreeModel(depart);
				model.setChildren(null);
	    //update-end--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索功回显优化----------------------
				newList.add(model);
			}
			return newList;
		}
		return null;
	}

	@Override
	public void departmentEXls(HttpServletResponse response) throws IOException {
		//获取输入流，原始模板位置
		org.springframework.core.io.Resource resource = new ClassPathResource("/templates/department.xlsx");
		InputStream resourceAsStream = resource.getInputStream();

		//2.获取临时文件
		File fileTemp= new File("/templates/department.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		String path = fileTemp.getAbsolutePath();
		TemplateExportParams exportParams = new TemplateExportParams(path);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
		Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

		CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);

		//机构类型下拉列表
		List<DictModel> org_category = bean.queryDictItemsByCode("org_category");
		selectList(workbook, "机构类型", 2, 2, org_category);

		//班组类型下拉列表
		List<DictModel> team_type = bean.queryDictItemsByCode("team_type");
		selectList(workbook, "班组类型", 3, 3, team_type);

		String fileName = "组织机构导入模板.xlsx";

		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			response.setHeader("Content-Disposition", "attachment;filename="+"组织机构导入模板.xlsx");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			workbook.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0;
		String tipMessage = null;
		String url = null;
		int errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				tipMessage = "导入失败，文件类型错误！";
				return imporReturnRes(errorLines, successLines, tipMessage, false, null);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);

			try {
				List<SysDepartModel> sysDepartModelList = ExcelImportUtil.importExcel(file.getInputStream(), SysDepartModel.class, params);
				Iterator<SysDepartModel> iterator = sysDepartModelList.iterator();
				while (iterator.hasNext()) {
					SysDepartModel model = iterator.next();
					boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
					if (b) {
						iterator.remove();
					}
				}

				if (CollectionUtil.isEmpty(sysDepartModelList)) {
					tipMessage = "导入失败，该文件为空。";
					return imporReturnRes(errorLines, successLines, tipMessage, false, null);
				}

				Map<String,SysDepart> departMap = new HashMap<>();
				//数据校验
				for (SysDepartModel model : sysDepartModelList) {
					if (ObjectUtil.isNotEmpty(model)) {
						StringBuilder stringBuilder = new StringBuilder();
						List<SysDepartModel> collect = sysDepartModelList.stream().filter(e -> e.getDepartName().equals(model.getDepartName())).collect(Collectors.toList());
						if (collect.size() > 1) {
							stringBuilder.append("存在重复数据！");
						}
						examine(model,stringBuilder,departMap);

						if (stringBuilder.length() > 0) {
							// 截取字符
							stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
							model.setSysDepartMistake(stringBuilder.toString());
							errorLines++;
						}
					}
				}
				if (errorLines > 0) {
					//错误报告下载
					return getErrorExcel(errorLines, sysDepartModelList, errorMessage, successLines, type, url);
				}else{
					//遍历map依次插入校验成功的数据
					for(Map.Entry<String,SysDepart> entry : departMap.entrySet()){
						String username = JwtUtil.getUserNameByToken(request);
						SysDepart sysDepart = entry.getValue();
						sysDepart.setCreateBy(username);
						//当当前部门的父部门也是要添加的数据，此时没有父id，要先查询父部门的id
						String key = entry.getKey();
						List<String> strings = StrUtil.splitTrim(key, "/");
						if (StrUtil.isBlank(sysDepart.getParentId()) && strings.size() > 1) {
							String substring = key.substring(0, key.length() - sysDepart.getDepartName().length() - 1);
							SysDepart value = departMap.get(substring);
							sysDepart.setParentId(value.getId());
						}
						this.saveDepartData(sysDepart,username);
					}
				}

			}catch (Exception e) {
				String msg = e.getMessage();
				log.error(msg, e);
				if (msg != null && msg.contains("Duplicate entry")) {
					return Result.error("文件导入失败:有重复数据！");
				} else {
					return Result.error("文件导入失败:" + e.getMessage());
				}
			}finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}

		}

		return imporReturnRes(errorLines, successLines, null, true, url);
	}

	private void examine(SysDepartModel sysDepartModel,
						 StringBuilder stringBuilder,
						 Map<String,SysDepart> departMap) {

		//机构名称数据校验
		String[] split = sysDepartModel.getDepartName().split("/");
		List<String> stringList = Arrays.asList(split);
		if (CollectionUtil.isNotEmpty(stringList)){
			List<String> collect4 = stringList.stream().distinct().collect(Collectors.toList());
			if (collect4.size()!=stringList.size()){
				stringBuilder.append("同根同枝叶之间不能重复！");
			}
			int size = stringList.size();
			String s1 = stringList.get(size - 1);
			String pid = "";
			StringBuilder stringBuilder1 = new StringBuilder();
			//遍历表格之中的拆分之后的机构名称
			for (int i = 0; i < stringList.size(); i++) {
				SysDepart sysDepart = new SysDepart();
				LambdaQueryWrapper<SysDepart> sysDepartLambdaQueryWrapper =  new LambdaQueryWrapper<>();
				sysDepartLambdaQueryWrapper.eq(SysDepart::getDelFlag,0);
				sysDepartLambdaQueryWrapper.eq(SysDepart::getDepartName,stringList.get(i));
				sysDepartLambdaQueryWrapper.eq(SysDepart::getParentId,pid);
				SysDepart sysDepart1 = sysDepartMapper.selectOne(sysDepartLambdaQueryWrapper);
				stringBuilder1.append(stringList.get(i));
				sysDepart.setDepartName(stringList.get(i));
			    if (i==stringList.size()-1){
			    	if (ObjectUtil.isNotEmpty(sysDepart1)){
						stringBuilder.append("数据库已存在该组织机构！");
					}else {
						if (stringList.get(i).equals(s1)){
							//机构全称
							if (StrUtil.isNotBlank(sysDepartModel.getDepartFullName())){
								sysDepart.setDepartFullName(sysDepartModel.getDepartFullName());
							}

							CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);

							//机构类型数据校验
							if (StrUtil.isNotBlank(sysDepartModel.getOrgCategory())){
								//获取机构类型的数据字典
								List<DictModel> orgCategory = bean.queryDictItemsByCode("org_category");
								//获取机构类型的字典文本
								List<String> collect = orgCategory.stream().map(DictModel::getText).collect(Collectors.toList());
								//判断表格中元素是否存在机构类型的字典中
								boolean contains = collect.contains(sysDepartModel.getOrgCategory());
								if (contains){
									//过滤出字典对应的表格数据
									DictModel dictModel = Optional.ofNullable(orgCategory).orElse(Collections.emptyList()).stream().filter(e -> e.getText().equals(sysDepartModel.getOrgCategory())).findFirst().orElse(null);
									sysDepart.setOrgCategory(dictModel.getValue());
								}else {
									stringBuilder.append("系统中不存在该机构类型！");
								}
							}
							//班组类型校验
							if (StrUtil.isNotBlank(sysDepartModel.getTeamType())){
								//获取班组类型的数据字典
								List<DictModel> teamType = bean.queryDictItemsByCode("team_type");
								//获取班组类型的字典文本
								List<String> collect = teamType.stream().map(DictModel::getText).collect(Collectors.toList());
								//判断表格中元素是否存在班组类型的字典中
								boolean contains = collect.contains(sysDepartModel.getTeamType());
								if (contains){
									//过滤出字典对应的表格数据
									DictModel dictModel = Optional.ofNullable(teamType).orElse(Collections.emptyList()).stream().filter(e -> e.getText().equals(sysDepartModel.getOrgCategory())).findFirst().orElse(null);
									sysDepart.setTeamType(Convert.toInt(dictModel.getValue()));
								}else {
									stringBuilder.append("系统中不存在该班组类型！");
								}
							}

							//机构电话数据校验
							if (StrUtil.isNotBlank(sysDepartModel.getDepartPhoneNum())){
								boolean mobile = iSysBaseAPI.isMobile(sysDepartModel.getDepartPhoneNum());
								boolean phone = iSysBaseAPI.isPhone(sysDepartModel.getDepartPhoneNum());
								if (mobile&&phone){
									sysDepart.setDepartPhoneNum(sysDepartModel.getDepartPhoneNum());
								}else{
									stringBuilder.append("机构电话格式错误！");
								}
							}

							//联系人数据校验
							if (StrUtil.isNotBlank(sysDepartModel.getContactId())){
								sysDepart.setContactId(sysDepartModel.getContactId());
							}

							//联系方式数据校验
							if (StrUtil.isNotBlank(sysDepartModel.getConcatWay())){
								sysDepart.setConcatWay(sysDepartModel.getConcatWay());
							}

							//管理负责人数据校验
							if(StrUtil.isNotBlank(sysDepartModel.getManagerName())){
								LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
								lambdaQueryWrapper.eq(SysUser::getDelFlag,0);
								lambdaQueryWrapper.eq(SysUser::getRealname,sysDepartModel.getManagerName());
								List<SysUser> sysUsers = sysUserMapper.selectList(lambdaQueryWrapper);
								if (CollectionUtil.isNotEmpty(sysUsers)){
									if (sysUsers.size()==1){
										List<String> collect = sysUsers.stream().map(SysUser::getId).collect(Collectors.toList());
										String join = CollectionUtil.join(collect,"");
										sysDepart.setManagerId(join);
									}else{
										stringBuilder.append("管理负责人名称有重复，请在同样的位置填写工号！");
									}
								}else {
									LambdaQueryWrapper<SysUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
									lambdaQueryWrapper1.eq(SysUser::getDelFlag,0);
									lambdaQueryWrapper1.eq(SysUser::getWorkNo,sysDepartModel.getManagerName());
									List<SysUser> sysUsers1 = sysUserMapper.selectList(lambdaQueryWrapper1);

									if (CollectionUtil.isNotEmpty(sysUsers1)){
										List<String> collect = sysUsers1.stream().map(SysUser::getId).collect(Collectors.toList());
										String join = CollectionUtil.join(collect,"");
										sysDepart.setManagerId(join);
									}else {
										stringBuilder.append("系统中不存在该管理负责人！");
									}
								}
							}
							//技术负责人数据校验
							if(StrUtil.isNotBlank(sysDepartModel.getTechnicalName())){
								LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
								lambdaQueryWrapper.eq(SysUser::getDelFlag,0);
								lambdaQueryWrapper.eq(SysUser::getRealname,sysDepartModel.getTechnicalName());
								List<SysUser> sysUsers = sysUserMapper.selectList(lambdaQueryWrapper);
								if (CollectionUtil.isNotEmpty(sysUsers)){
									if (sysUsers.size()==1){
										List<String> collect = sysUsers.stream().map(SysUser::getId).collect(Collectors.toList());
										String join = CollectionUtil.join(collect,"");
										sysDepart.setTechnicalId(join);
									}else{
										stringBuilder.append("技术负责人名称有重复，请在同样的位置填写工号！");
									}
								}else {
									LambdaQueryWrapper<SysUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
									lambdaQueryWrapper1.eq(SysUser::getDelFlag,0);
									lambdaQueryWrapper1.eq(SysUser::getWorkNo,sysDepartModel.getTechnicalName());
									List<SysUser> sysUsers1 = sysUserMapper.selectList(lambdaQueryWrapper1);

									if (CollectionUtil.isNotEmpty(sysUsers1)){
										List<String> collect = sysUsers1.stream().map(SysUser::getId).collect(Collectors.toList());
										String join = CollectionUtil.join(collect,"");
										sysDepart.setTechnicalId(join);
									}else {
										stringBuilder.append("系统中不存在该技术负责人！");
									}
								}
							}
							if (StrUtil.isNotBlank(sysDepartModel.getMemo())){
								sysDepart.setMemo(sysDepartModel.getMemo());
							}
						}
					}
				}
				if (ObjectUtil.isEmpty(sysDepart1)){
					sysDepart.setParentId(pid);
					departMap.put(stringBuilder1.toString(),sysDepart);
					pid="";
				}else {
					pid=sysDepart1.getId();
				}
				stringBuilder1.append("/");
			}
		}else {
			stringBuilder.append("机构名称不能为空！");
		}
	}




	private Result<?> getErrorExcel(int errorLines,  List<SysDepartModel> sysDepartModelList, List<String> errorMessage, int successLines, String type, String url) throws IOException {
		//创建导入失败错误报告,进行模板导出
		org.springframework.core.io.Resource resource = new ClassPathResource("/templates/departmentError.xlsx");
		InputStream resourceAsStream = resource.getInputStream();
		//2.获取临时文件
		File fileTemp = new File("/templates/departmentError.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		String path = fileTemp.getAbsolutePath();
		TemplateExportParams exportParams = new TemplateExportParams(path);
		Map<String, Object> errorMap = new HashMap<String, Object>(16);
		List<Map<String, String>> listMap = new ArrayList<>();

		for (int i = 0; i < sysDepartModelList.size(); i++) {
			SysDepartModel sysDepartModel = sysDepartModelList.get(i);
			Map<String, String> map = new HashMap<>(16);

			//错误报告获取信息
			map.put("departName", sysDepartModel.getDepartName());
			map.put("departFullName", sysDepartModel.getDepartFullName());
			map.put("orgCategory", sysDepartModel.getOrgCategory());
			map.put("teamType", sysDepartModel.getTeamType());
			map.put("contactId", sysDepartModel.getContactId());
			map.put("concatWay", sysDepartModel.getConcatWay());
			map.put("managerName", sysDepartModel.getManagerName());
			map.put("technicalName", sysDepartModel.getTechnicalName());
			map.put("memo", sysDepartModel.getMemo());
			map.put("sysDepartMistake", sysDepartModel.getSysDepartMistake());
			listMap.add(map);
		}
		errorMap.put("maplist", listMap);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
		sheetsMap.put(0, errorMap);
		Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
		try {
			String fileName = "组织机构导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
			FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
			url =fileName;
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imporReturnRes(errorLines, successLines, null, true, url);
	}
	public static Result<?> imporReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String failReportUrl) throws IOException {
		if (isType) {
			if (errorLines != 0) {
				JSONObject result = new JSONObject(5);
				result.put("isSucceed", false);
				result.put("errorCount", errorLines);
				result.put("successCount", successLines);
				int totalCount = successLines + errorLines;
				result.put("totalCount", totalCount);
				result.put("failReportUrl", failReportUrl);
				Result res = Result.ok(result);
				res.setMessage("文件失败，数据有错误。");
				res.setCode(200);
				return res;
			} else {
				//是否成功
				JSONObject result = new JSONObject(5);
				result.put("isSucceed", true);
				result.put("errorCount", errorLines);
				result.put("successCount", successLines);
				int totalCount = successLines + errorLines;
				result.put("totalCount", totalCount);
				Result res = Result.ok(result);
				res.setMessage("文件导入成功！");
				res.setCode(200);
				return res;
			}
		} else {
			JSONObject result = new JSONObject(5);
			result.put("isSucceed", false);
			result.put("errorCount", errorLines);
			result.put("successCount", successLines);
			int totalCount = successLines + errorLines;
			result.put("totalCount", totalCount);
			Result res = Result.ok(result);
			res.setMessage(tipMessage);
			res.setCode(200);
			return res;
		}

	}
	//下拉框
	private void selectList(Workbook workbook,String name,int firstCol, int lastCol,List<DictModel> modelList){
		Sheet sheet = workbook.getSheetAt(0);
		if (CollectionUtil.isNotEmpty(modelList)) {
			//将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
			int sheetTotal = workbook.getNumberOfSheets();
			String hiddenSheetName = name + "_hiddenSheet";
			List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
			Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
			if (hiddenSheet == null) {
				hiddenSheet = workbook.createSheet(hiddenSheetName);
				//写入下拉数据到新的sheet页中
				for (int i = 0; i < collect.size(); i++) {
					Row hiddenRow = hiddenSheet.createRow(i);
					Cell hiddenCell = hiddenRow.createCell(0);
					hiddenCell.setCellValue(collect.get(i));
				}
				workbook.setSheetHidden(sheetTotal, true);
			}

			// 下拉数据
			CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
			//  生成下拉框内容名称
			String strFormula = hiddenSheetName + "!$A$1:$A$65535";
			// 根据隐藏页面创建下拉列表
			XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
			DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
			//  对sheet页生效
			sheet.addValidationData(validation);
		}

	}
	/**
	 * 根据部门id删除并且删除其可能存在的子级任何部门
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean delete(String id) {
		List<String> idList = new ArrayList<>();
		idList.add(id);
		this.checkChildrenExists(id, idList);
		//清空部门树内存
		boolean ok = this.removeByIds(idList);
		//根据部门id获取部门角色id
		List<String> roleIdList = new ArrayList<>();
		LambdaQueryWrapper<SysDepartRole> query = new LambdaQueryWrapper<>();
		query.select(SysDepartRole::getId).in(SysDepartRole::getDepartId, idList);
		List<SysDepartRole> depRoleList = sysDepartRoleMapper.selectList(query);
		for(SysDepartRole deptRole : depRoleList){
			roleIdList.add(deptRole.getId());
		}
		//根据部门id删除用户与部门关系
		userDepartMapper.delete(new LambdaQueryWrapper<SysUserDepart>().in(SysUserDepart::getDepId,idList));
		//根据部门id删除部门授权
		departPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>().in(SysDepartPermission::getDepartId,idList));
		//根据部门id删除部门角色
		sysDepartRoleMapper.delete(new LambdaQueryWrapper<SysDepartRole>().in(SysDepartRole::getDepartId,idList));
		if(roleIdList != null && roleIdList.size()>0){
			//根据角色id删除部门角色授权
			departRolePermissionMapper.delete(new LambdaQueryWrapper<SysDepartRolePermission>().in(SysDepartRolePermission::getRoleId,roleIdList));
			//根据角色id删除部门角色用户信息
			departRoleUserMapper.delete(new LambdaQueryWrapper<SysDepartRoleUser>().in(SysDepartRoleUser::getDroleId,roleIdList));
		}
		return ok;
	}

	/**
	 * delete 方法调用
	 * @param id
	 * @param idList
	 */
	private void checkChildrenExists(String id, List<String> idList) {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getParentId,id);
		List<SysDepart> departList = this.list(query);
		if(departList != null && departList.size() > 0) {
			for(SysDepart depart : departList) {
				idList.add(depart.getId());
				this.checkChildrenExists(depart.getId(), idList);
			}
		}
	}

	@Override
	public List<SysDepart> queryUserDeparts(String userId) {
		return baseMapper.queryUserDeparts(userId);
	}

	@Override
	public List<SysDepart> queryDepartsByUsername(String username) {
		return baseMapper.queryDepartsByUsername(username);
	}

	/**
	 * 根据用户所负责部门ids获取父级部门编码
	 * @param departIds
	 * @return
	 */
	private String[] getMyDeptParentOrgCode(String departIds){
		//根据部门id查询所负责部门
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.in(SysDepart::getId, Arrays.asList(departIds.split(",")));
		query.orderByAsc(SysDepart::getOrgCode);
		List<SysDepart> list = this.list(query);
		//查找根部门
		if(list == null || list.size()==0){
			return null;
		}
		String orgCode = this.getMyDeptParentNode(list);
		String[] codeArr = orgCode.split(",");
		return codeArr;
	}

	/**
	 * 获取负责部门父节点
	 * @param list
	 * @return
	 */
	private String getMyDeptParentNode(List<SysDepart> list){
		Map<String,String> map = new HashMap(5);
		//1.先将同一公司归类
		for(SysDepart dept : list){
			String code = dept.getOrgCode().substring(0,3);
			if(map.containsKey(code)){
				String mapCode = map.get(code)+","+dept.getOrgCode();
				map.put(code,mapCode);
			}else{
				map.put(code,dept.getOrgCode());
			}
		}
		StringBuffer parentOrgCode = new StringBuffer();
		//2.获取同一公司的根节点
		for(String str : map.values()){
			String[] arrStr = str.split(",");
			parentOrgCode.append(",").append(this.getMinLengthNode(arrStr));
		}
		return parentOrgCode.substring(1);
	}

	/**
	 * 获取同一公司中部门编码长度最小的部门
	 * @param str
	 * @return
	 */
	private String getMinLengthNode(String[] str){
		int min =str[0].length();
		StringBuilder orgCode = new StringBuilder(str[0]);
		for(int i =1;i<str.length;i++){
			if(str[i].length()<=min){
				min = str[i].length();
				orgCode =orgCode.append(orgCode+","+str[i]) ;
			}
		}
		return String.valueOf(orgCode);
	}
    /**
     * 获取部门树信息根据关键字
     * @param keyWord
     * @return
     */
    @Override
    public List<SysDepartTreeModel> queryTreeByKeyWord(String keyWord) {
        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(SysDepart::getDepartOrder);
        List<SysDepart> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
        List<SysDepartTreeModel> treelist =new ArrayList<>();
        if(StringUtils.isNotBlank(keyWord)){
            this.getTreeByKeyWord(keyWord,listResult,treelist);
        }else{
            return listResult;
        }
        return treelist;
    }

	/**
	 * 根据parentId查询部门树
	 * @param parentId
	 * @param ids 前端回显传递
	 * @return
	 */
	@Override
	public List<SysDepartTreeModel> queryTreeListByPid(String parentId,String ids) {
		Consumer<LambdaQueryWrapper<SysDepart>> square = i -> {
			if (oConvertUtils.isNotEmpty(ids)) {
				i.in(SysDepart::getId, ids.split(","));
			} else {
				if(oConvertUtils.isEmpty(parentId)){
					i.and(q->q.isNull(true,SysDepart::getParentId).or().eq(true,SysDepart::getParentId,""));
				}else{
					i.eq(true,SysDepart::getParentId,parentId);
				}
			}
		};
		LambdaQueryWrapper<SysDepart> lqw=new LambdaQueryWrapper();
		lqw.eq(true,SysDepart::getDelFlag,CommonConstant.DEL_FLAG_0.toString());
		lqw.func(square);
		lqw.orderByDesc(SysDepart::getDepartOrder);
		List<SysDepart> list = list(lqw);
        //update-begin---author:wangshuai ---date:20220316  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
        //设置用户id,让前台显示
        this.setUserIdsByDepList(list);
        //update-end---author:wangshuai ---date:20220316  for：[JTC-119]在部门管理菜单下设置部门负责人 创建用户的时候不需要处理
		List<SysDepartTreeModel> records = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			SysDepart depart = list.get(i);
            SysDepartTreeModel treeModel = new SysDepartTreeModel(depart);
            //TODO 异步树加载key拼接__+时间戳,以便于每次展开节点会刷新数据
			treeModel.setKey(treeModel.getKey());
            Integer count=this.baseMapper.queryCountByPid(depart.getId());
            if(count>0){
                treeModel.setIsLeaf(false);
            }else{
                treeModel.setIsLeaf(true);
            }
            records.add(treeModel);
        }
		return records;
	}

	@Override
	public JSONObject queryAllParentIdByDepartId(String departId) {
		JSONObject result = new JSONObject();
		for (String id : departId.split(SymbolConstant.COMMA)) {
			JSONObject all = this.queryAllParentId("id", id);
			result.put(id, all);
		}
		return result;
	}

	@Override
	public JSONObject queryAllParentIdByOrgCode(String orgCode) {
		JSONObject result = new JSONObject();
		for (String code : orgCode.split(SymbolConstant.COMMA)) {
			JSONObject all = this.queryAllParentId("org_code", code);
			result.put(code, all);
		}
		return result;
	}

	/**
	 * 查询某个部门的所有父ID信息
	 *
	 * @param fieldName 字段名
	 * @param value     值
	 */
	private JSONObject queryAllParentId(String fieldName, String value) {
		JSONObject data = new JSONObject();
		// 父ID集合，有序
		data.put("parentIds", new JSONArray());
		// 父ID的部门数据，key是id，value是数据
		data.put("parentMap", new JSONObject());
		this.queryAllParentIdRecursion(fieldName, value, data);
		return data;
	}

	/**
	 * 递归调用查询父部门接口
	 */
	private void queryAllParentIdRecursion(String fieldName, String value, JSONObject data) {
		QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(fieldName, value);
		SysDepart depart = super.getOne(queryWrapper);
		if (depart != null) {
			data.getJSONArray("parentIds").add(0, depart.getId());
			data.getJSONObject("parentMap").put(depart.getId(), depart);
			if (oConvertUtils.isNotEmpty(depart.getParentId())) {
				this.queryAllParentIdRecursion("id", depart.getParentId(), data);
			}
		}
	}

	@Override
	public SysDepart queryCompByOrgCode(String orgCode) {
		int length = YouBianCodeUtil.ZHANWEI_LENGTH;
		String compyOrgCode = orgCode.substring(0,length);
		return this.baseMapper.queryCompByOrgCode(compyOrgCode);
	}
	/**
	 * 根据id查询下级部门
	 * @param pid
	 * @return
	 */
	@Override
	public List<SysDepart> queryDeptByPid(String pid) {
		return this.baseMapper.queryDeptByPid(pid);
	}
	/**
     * 根据关键字筛选部门信息
     * @param keyWord
     * @return
     */
    public void getTreeByKeyWord(String keyWord,List<SysDepartTreeModel> allResult,List<SysDepartTreeModel>  newResult){
        for (SysDepartTreeModel model:allResult) {
            if (model.getDepartName().contains(keyWord)){
                newResult.add(model);
                continue;
            }else if(model.getChildren()!=null){
                getTreeByKeyWord(keyWord,model.getChildren(),newResult);
            }
        }
    }

    //update-begin---author:wangshuai ---date:20200308  for：[JTC-119]在部门管理菜单下设置部门负责人，新增方法添加部门负责人、删除负责部门负责人、查询部门对应的负责人
    /**
     * 通过用户id设置负责部门
     * @param sysDepart SysDepart部门对象
     * @param userIds 多个负责用户id
     */
    public void addDepartByUserIds(SysDepart sysDepart, String userIds) {
        //获取部门id,保存到用户
        String departId = sysDepart.getId();
        //循环用户id
        String[] userIdArray = userIds.split(",");
        for (String userId:userIdArray) {
            //查询用户表增加负责部门
            SysUser sysUser = sysUserMapper.selectById(userId);
            //如果部门id不为空，那么就需要拼接
            if(oConvertUtils.isNotEmpty(sysUser.getDepartIds())){
                if(!sysUser.getDepartIds().contains(departId)) {
                    sysUser.setDepartIds(sysUser.getDepartIds() + "," + departId);
                }
            }else{
                sysUser.setDepartIds(departId);
            }
            //设置身份为上级
            sysUser.setUserIdentity(CommonConstant.USER_IDENTITY_2);
            //跟新用户表
            sysUserMapper.updateById(sysUser);
            //判断当前用户是否包含所属部门
            List<SysUserDepart> userDepartList = userDepartMapper.getUserDepartByUid(userId);
            boolean isExistDepId = userDepartList.stream().anyMatch(item -> departId.equals(item.getDepId()));
            //如果不存在需要设置所属部门
            if(!isExistDepId){
                userDepartMapper.insert(new SysUserDepart(userId,departId));
            }
        }
    }

    /**
     * 修改用户负责部门
     * @param sysDepart SysDepart对象
     */
    private void updateChargeDepart(SysDepart sysDepart) {
        //新的用户id
        String directorIds = sysDepart.getDirectorUserIds();
        //旧的用户id（数据库中存在的）
        String oldDirectorIds = sysDepart.getOldDirectorUserIds();
        String departId = sysDepart.getId();
        //如果用户id为空,那么用户的负责部门id应该去除
        if(oConvertUtils.isEmpty(directorIds)){
            this.deleteChargeDepId(departId,null);
        }else if(oConvertUtils.isNotEmpty(directorIds) && oConvertUtils.isEmpty(oldDirectorIds)){
            //如果用户id不为空但是用户原来负责部门的用户id为空
            this.addDepartByUserIds(sysDepart,directorIds);
        }else{
            //都不为空，需要比较，进行添加或删除
            //找到新的负责部门用户id与原来负责部门的用户id，进行删除
            List<String> userIdList = Arrays.stream(oldDirectorIds.split(",")).filter(item -> !directorIds.contains(item)).collect(Collectors.toList());
            for (String userId:userIdList){
                this.deleteChargeDepId(departId,userId);
            }
            //找到原来负责部门的用户id与新的负责部门用户id，进行新增
            String addUserIds = Arrays.stream(directorIds.split(",")).filter(item -> !oldDirectorIds.contains(item)).collect(Collectors.joining(","));
            if(oConvertUtils.isNotEmpty(addUserIds)){
                this.addDepartByUserIds(sysDepart,addUserIds);
            }
        }
    }

    /**
     * 删除用户负责部门
     * @param departId 部门id
     * @param userId 用户id
     */
    private void deleteChargeDepId(String departId,String userId){
        //先查询负责部门的用户id,因为负责部门的id使用逗号拼接起来的
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.like(SysUser::getDepartIds,departId);
        //删除全部的情况下用户id不存在
        if(oConvertUtils.isNotEmpty(userId)){
            query.eq(SysUser::getId,userId);
        }
        List<SysUser> userList = sysUserMapper.selectList(query);
        for (SysUser sysUser:userList) {
            //将不存在的部门id删除掉
            String departIds = sysUser.getDepartIds();
            List<String> list = new ArrayList<>(Arrays.asList(departIds.split(",")));
            list.remove(departId);
            //删除之后再将新的id用逗号拼接起来进行更新
            String newDepartIds = String.join(",",list);
            sysUser.setDepartIds(newDepartIds);
            sysUserMapper.updateById(sysUser);
        }
    }

    /**
     * 通过部门集合为部门设置用户id，用于前台展示
     * @param departList 部门集合
     */
    private void setUserIdsByDepList(List<SysDepart> departList) {
        //查询负责部门不为空的情况
        LambdaQueryWrapper<SysUser> query  = new LambdaQueryWrapper<>();
        query.isNotNull(SysUser::getDepartIds);
        List<SysUser> users = sysUserMapper.selectList(query);
        Map<String,Object> map = new HashMap(5);
        //先循环一遍找到不同的负责部门id
        for (SysUser user:users) {
            String departIds = user.getDepartIds();
            String[] departIdArray = departIds.split(",");
            for (String departId:departIdArray) {
                //mao中包含部门key，负责用户直接拼接
                if(map.containsKey(departId)){
                    String userIds = map.get(departId) + "," + user.getId();
                    map.put(departId,userIds);
                }else{
                    map.put(departId,user.getId());
                }
            }
        }
        //循环部门集合找到部门id对应的负责用户
        for (SysDepart sysDepart:departList) {
            if(map.containsKey(sysDepart.getId())){
                sysDepart.setDirectorUserIds(map.get(sysDepart.getId()).toString());
            }
        }
    }
    //update-end---author:wangshuai ---date:20200308  for：[JTC-119]在部门管理菜单下设置部门负责人，新增方法添加部门负责人、删除负责部门负责人、查询部门对应的负责人


	@Override
	public void processingTreeList(String name,List<SysDepartTreeModel> list) {
		Iterator<SysDepartTreeModel> iterator = list.iterator();
		while (iterator.hasNext()) {
			SysDepartTreeModel next = iterator.next();
			if (StrUtil.containsAnyIgnoreCase(next.getDepartName(),name)) {
				//名称匹配则赋值颜色
				next.setColor("#FF5B05");
			}
			List<SysDepartTreeModel> children = next.getChildren();
			if (CollUtil.isNotEmpty(children)) {
				processingTreeList(name, children);
			}
			//如果没有子级，并且当前不匹配，则去除
			if (CollUtil.isEmpty(next.getChildren()) && StrUtil.isEmpty(next.getColor())) {
				iterator.remove();
			}
		}
	}


}
