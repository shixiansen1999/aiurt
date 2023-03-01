package com.aiurt.boot.standard.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.standard.dto.InspectionCodeContentDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.service.IInspectionCodeContentService;
import com.aiurt.boot.standard.vo.InspectionCodeVo;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class InspectionCodeContentServiceImpl extends ServiceImpl<InspectionCodeContentMapper, InspectionCodeContent> implements IInspectionCodeContentService {
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

	@Override
	public void addInspectionCodeContent(InspectionCodeContent inspectionCodeContent) {
	   //新增时设置hasChild为0
	    inspectionCodeContent.setHasChild(IInspectionCodeContentService.NOCHILD);
		if(oConvertUtils.isEmpty(inspectionCodeContent.getPid())){
			inspectionCodeContent.setPid(IInspectionCodeContentService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			InspectionCodeContent parent = baseMapper.selectById(inspectionCodeContent.getPid());
			if(parent!=null && !InspectionConstant.HAS_CHILD_1.equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(inspectionCodeContent);
	}

	@Override
	public void updateInspectionCodeContent(InspectionCodeContent inspectionCodeContent) {
		InspectionCodeContent entity = this.getById(inspectionCodeContent.getId());
		if(entity==null) {
			throw new AiurtBootException("未找到对应实体");
		}
		String oldPid = entity.getPid();
		String newPid = inspectionCodeContent.getPid();
		if(!oldPid.equals(newPid)) {
			updateOldParentNode(oldPid);
			if(oConvertUtils.isEmpty(newPid)){
				inspectionCodeContent.setPid(IInspectionCodeContentService.ROOT_PID_VALUE);
			}
			if(!IInspectionCodeContentService.ROOT_PID_VALUE.equals(inspectionCodeContent.getPid())) {
				baseMapper.updateTreeNodeStatus(inspectionCodeContent.getPid(), IInspectionCodeContentService.HASCHILD);
			}
		}
		baseMapper.updateById(inspectionCodeContent);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteInspectionCodeContent(String id) throws AiurtBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    InspectionCodeContent inspectionCodeContent = this.getById(idVal);
                    String pidVal = inspectionCodeContent.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<InspectionCodeContent> dataList = baseMapper.selectList(new QueryWrapper<InspectionCodeContent>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if(flag){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            InspectionCodeContent inspectionCodeContent = this.getById(id);
            if(inspectionCodeContent==null) {
                throw new AiurtBootException("未找到对应实体");
            }
            updateOldParentNode(inspectionCodeContent.getPid());
            baseMapper.deleteById(id);
        }
	}

	@Override
    public List<InspectionCodeContent> queryTreeListNoPage(QueryWrapper<InspectionCodeContent> queryWrapper) {
        List<InspectionCodeContent> dataList = baseMapper.selectList(queryWrapper);
        List<InspectionCodeContent> mapList = new ArrayList<>();
        for(InspectionCodeContent data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IInspectionCodeContentService.NOCHILD.equals(pidVal)){
                InspectionCodeContent rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<InspectionCodeContent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(InspectionCodeContent::getPid, parentCode);
            List<InspectionCodeContent> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new AiurtBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new AiurtBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public IPage<InspectionCodeContent> pageList(Page<InspectionCodeContent> page, InspectionCodeContent inspectionCodeContent) {
        //1.查询表中未删除的所有的数据
        List<InspectionCodeContent> allList = baseMapper.selectLists(inspectionCodeContent);
        if(inspectionCodeContent.getCode()!=null ||inspectionCodeContent.getName()!=null ||inspectionCodeContent.getStatusItem()!=null){
            return page.setRecords(allList);
        }
        //2.找到所有根节点 ParentId=0
        List<InspectionCodeContent> rooList = allList.stream().filter(r -> "0".equals(r.getPid())).collect(Collectors.toList());
        //3.找到所有非根节点
        List<InspectionCodeContent> subLists = allList.stream().filter(r -> !"0".equals(r.getPid())).collect(Collectors.toList());
        if (rooList.size()<1){
            return page.setRecords(subLists);
        }
        List<InspectionCodeContent> subList = allList.stream().filter(r -> !"0".equals(r.getPid())).collect(Collectors.toList());
        //4.循环阶段去subList找对应的字节点
        rooList = rooList.stream().map(root -> {
            //通过根节点的id和子节点的pid判断是否相等，如果相等的话，代表是根节点的子集
            List<InspectionCodeContent> list = subLists.stream().filter(r -> r.getPid().equals(root.getId())).collect(Collectors.toList());
            //如果当前没一个子级，初始化一个数组
            if (CollectionUtils.isEmpty(list)){
                list =new ArrayList<>();
            }
            root.setChildren(list);
            return root;
        }).collect(Collectors.toList());
        subList =subList.stream().map(s->{
            List<InspectionCodeContent> list = subLists.stream().filter(l-> l.getPid().equals(s.getId())).collect(Collectors.toList());
            s.setChildren(list);
            return s;
        }).collect(Collectors.toList());
        return page.setRecords(rooList);
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IInspectionCodeContentService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<InspectionCodeContent>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IInspectionCodeContentService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private InspectionCodeContent getTreeRoot(String pidVal){
        InspectionCodeContent data =  baseMapper.selectById(pidVal);
        if(data != null && !IInspectionCodeContentService.ROOT_PID_VALUE.equals(data.getPid())){
            return this.getTreeRoot(data.getPid());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<InspectionCodeContent> dataList = baseMapper.selectList(new QueryWrapper<InspectionCodeContent>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(InspectionCodeContent tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }


    /**
     * 通过检修标准id查看检修项
     *
     * @param id  检修标准id
     * @return
     */
    @Override
    public List<InspectionCodeContent> selectCodeContentList(String id) {
        if (StrUtil.isEmpty(id)) {
            return new ArrayList<>();
        }
        List<InspectionCodeContent> result = baseMapper.selectList(
                new LambdaQueryWrapper<InspectionCodeContent>()
                        .eq(InspectionCodeContent::getInspectionCodeId, id)
                        .orderByAsc(InspectionCodeContent::getSortNo));

        if (CollUtil.isNotEmpty(result)) {
            result.forEach(r -> {
                r.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
                r.setStatusItemName(sysBaseApi.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
                r.setInspectionTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_VALUE,String.valueOf(r.getInspectionType())));
            });
        }

        // 构造树形结构
        return treeFirst(result);
    }

    @Override
    public void checkCode(String code, String inspectionCodeId, String id) {
        QueryWrapper<InspectionCodeContent> queryWrapper = new QueryWrapper<InspectionCodeContent>();
               queryWrapper.lambda().eq(InspectionCodeContent::getInspectionCodeId,inspectionCodeId)
                .eq(InspectionCodeContent::getCode,code);
        if (id!="" && id!=null){
            queryWrapper.ne("id",id);
        }
        queryWrapper.lambda().eq(InspectionCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<InspectionCodeContent> inspectionCodeContents = baseMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(inspectionCodeContents)){
            throw new AiurtBootException("输入的code当前列表重复,请重新输入");
        }
    }
    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<InspectionCodeContent> treeFirst(List<InspectionCodeContent> list) {
        Map<String, InspectionCodeContent> map = new HashMap<>(50);
        for (InspectionCodeContent treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
     * @param list
     * @param map
     * @return
     */
    private static List<InspectionCodeContent> addChildren(List<InspectionCodeContent> list, Map<String, InspectionCodeContent> map) {
        List<InspectionCodeContent> rootNodes = new ArrayList<>();
        for (InspectionCodeContent treeNode : list) {
            InspectionCodeContent parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<InspectionCodeContent>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

    @Override
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, InspectionCodeContent inspectionCodeContent) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<InspectionCodeContent> inspectionCodeContents = inspectionCodeContentMapper.selectLists(inspectionCodeContent);
        for (InspectionCodeContent dto : inspectionCodeContents) {
            //检修周期类型
            List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_value");
            inspectionType= inspectionType.stream().filter(f -> (String.valueOf(dto.getType())).equals(f.getValue())).collect(Collectors.toList());
            String typeName = inspectionType.stream().map(DictModel::getText).collect(Collectors.joining());
            dto.setIsType(typeName);
            //检查值类型
            List<DictModel> appointDevice = sysBaseApi.getDictItems("patrol_input_type");
            appointDevice= appointDevice.stream().filter(f -> (String.valueOf(dto.getStatusItem())).equals(f.getValue())).collect(Collectors.toList());
            String  relatedDevice = appointDevice.stream().map(DictModel::getText).collect(Collectors.joining());
            dto.setSStatusItem(relatedDevice);
            //检查值是否必填
            List<DictModel> takeEffect = sysBaseApi.getDictItems("inspection_value");
            takeEffect = takeEffect.stream().filter(f -> (String.valueOf(dto.getInspectionType())).equals(f.getValue())).collect(Collectors.toList());
            String effectStatus = takeEffect.stream().map(DictModel::getText).collect(Collectors.joining());
            dto.setIsInspectionType(effectStatus);

            Integer sortNo = dto.getSortNo();
            dto.setIsSortNo(String.valueOf(sortNo));
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "配置项数据导出");
        //excel注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, InspectionCodeContent.class);
        //自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("配置项数据导出", "配置项数据导出"));
        //导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, inspectionCodeContents);
        return mv;
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 错误信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        String url = null;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, successLines, errorMessage,false,url);
            }
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<InspectionCodeContentDTO> csList = ExcelImportUtil.importExcel(file.getInputStream(), InspectionCodeContentDTO.class, params);
                List<InspectionCodeContentDTO> inspectionCodeContentDtoList = csList.parallelStream()
                        .filter(c->c.getName()!=null||c.getCode()!=null||c.getIsType()!=null||c.getSStatusItem()!=null||c.getHasChild() !=null)
                        .collect(Collectors.toList());

                List<InspectionCodeContent> list = new ArrayList<>();
                for (int i = 0; i < inspectionCodeContentDtoList.size(); i++) {
                    InspectionCodeContentDTO inspectionCodeContentDto = inspectionCodeContentDtoList.get(i);
                    boolean error = true;
                    StringBuffer sb = new StringBuffer();
                    if (ObjectUtil.isNull(inspectionCodeContentDto.getHasChild())) {
                        errorMessage.add("层级类型为必填项，忽略导入");
                        sb.append("层级类型为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> hasChild = sysBaseApi.getDictItems("yn");
                        List<String> collect = hasChild.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeContentDto.getSStatusItem())){
                            errorMessage.add("层级类型不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，层级类型输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }

                    if (ObjectUtil.isNull(inspectionCodeContentDto.getName())) {
                        errorMessage.add("检修项内容为必填项，忽略导入");
                        sb.append("检修项内容为必填项为必填项;");
                        if(error) {
                            errorLines++;
                            error = false;
                        }
                    }else {
                        InspectionCodeContent inspectionCodeContent = inspectionCodeContentMapper.selectOne(new QueryWrapper<InspectionCodeContent>().lambda().eq(InspectionCodeContent::getName, inspectionCodeContentDto.getName()).eq(InspectionCodeContent::getDelFlag, 0));
                        if (inspectionCodeContent != null) {
                            errorMessage.add(inspectionCodeContentDto.getName() + "检修项内容已经存在，忽略导入");
                            sb.append("检修项内容已经存在;");
                            if (error) {
                                errorLines++;
                                error =false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeContentDto.getCode())) {
                        errorMessage.add("配置项编码为必填项，忽略导入");
                        sb.append("配置项编码为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else {
                        InspectionCodeContent inspectionCodeContent = inspectionCodeContentMapper.selectOne(new QueryWrapper<InspectionCodeContent>().lambda().eq(InspectionCodeContent::getCode, inspectionCodeContentDto.getCode()).eq(InspectionCodeContent::getDelFlag, 0));
                        if (inspectionCodeContent != null) {
                            errorMessage.add(inspectionCodeContentDto.getCode() + "配置项编码已经存在，忽略导入");
                            sb.append("配置项编码已经存在;");
                            if (error) {
                                errorLines++;
                                error =false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeContentDto.getIsType())) {
                        errorMessage.add("是否为检查项为必填项，忽略导入");
                        sb.append("是否为检查项为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> isType = sysBaseApi.getDictItems("inspection_value");
                        List<String> collect = isType.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeContentDto.getIsType())){
                            errorMessage.add("是否为检查项不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，是否为检查项输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeContentDto.getSStatusItem())) {
                        errorMessage.add("检查值类型为必填项，忽略导入");
                        sb.append("检查值类型为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }else{
                        List<DictModel> isItem = sysBaseApi.getDictItems("patrol_input_type");
                        List<String> collect = isItem.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(!collect.contains(inspectionCodeContentDto.getSStatusItem())){
                            errorMessage.add("检查值类型不是下拉框内的内容，忽略导入");
                            sb.append("格式错误，检查值类型输入了额外的码值或其他的字符;");
                            if(error){
                                errorLines++;
                                error = false;
                            }
                        }
                    }
                    if (ObjectUtil.isNull(inspectionCodeContentDto.getInspectionCodeId())) {
                        errorMessage.add("检修标准Id为必填项，忽略导入");
                        sb.append("检修标准Id为必填项;");
                        if(error){
                            errorLines++;
                            error =false;
                        }
                    }

                    InspectionCodeContent inspectionCodeContent = new InspectionCodeContent();
                    BeanUtils.copyProperties(inspectionCodeContentDto, inspectionCodeContent);
                    //判断是否与下拉框值一致，一致则添加进数据库
                    if(StrUtil.isNotEmpty(inspectionCodeContentDto.getIsSortNo())){
                            String isSortNo = inspectionCodeContentDto.getIsSortNo();
                            inspectionCodeContent.setSortNo(Integer.valueOf(isSortNo));

                    }
                    if(StrUtil.isNotEmpty(inspectionCodeContentDto.getIsType())){
                        List<DictModel> isType = sysBaseApi.getDictItems("inspection_value");
                        List<String> collect = isType.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeContentDto.getIsType())){
                            inspectionCodeContent.setType(Integer.valueOf(inspectionCodeContentDto.getIsType()));
                        }
                    }
                    if(StrUtil.isNotEmpty(inspectionCodeContentDto.getSStatusItem())){
                        List<DictModel> isItem = sysBaseApi.getDictItems("patrol_input_type");
                        List<String> collect = isItem.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeContentDto.getSStatusItem())){
                            inspectionCodeContent.setStatusItem(Integer.valueOf(inspectionCodeContentDto.getSStatusItem()));
                        }
                    }
                    //检查值是否必填
                    if(StrUtil.isNotEmpty(inspectionCodeContentDto.getIsInspectionType())){
                        List<DictModel> inspectType = sysBaseApi.getDictItems("inspection_value");
                        List<String> collect = inspectType.stream().map(DictModel::getValue).collect(Collectors.toList());
                        if(collect.contains(inspectionCodeContentDto.getIsInspectionType())){
                            inspectionCodeContent.setInspectionType(Integer.valueOf(inspectionCodeContentDto.getIsInspectionType()));
                        }
                    }
                    list.add(inspectionCodeContent);
                    //判断填写的数据中是否有重复数据
                    if(list.size()>1){
                        if(ObjectUtil.isNotNull(inspectionCodeContent.getName())){
                            List<InspectionCodeContent> nameList = list.stream().filter(f -> f.getName() != null).collect(Collectors.toList());
                            Map<Object, Long> mapGroup2 = nameList.stream().collect(Collectors.groupingBy(InspectionCodeContent::getName, Collectors.counting()));
                            List<Object> collect = mapGroup2.keySet().stream().filter(key -> mapGroup2.get(key) > 1).collect(Collectors.toList());
                            if(collect.contains(inspectionCodeContent.getName())){
                                errorMessage.add("检修项内容重复，忽略导入");
                                sb.append("检修项内容重复；");
                                if(error){
                                    errorLines++;
                                    error = false;
                                }
                            }
                        }
                        if(ObjectUtil.isNotNull(inspectionCodeContent.getCode())){
                            List<InspectionCodeContent> codeList = list.stream().filter(f -> f.getCode() != null).collect(Collectors.toList());
                            Map<String, Long> collect1 = codeList.stream().collect(Collectors.groupingBy(InspectionCodeContent::getCode, Collectors.counting()));
                            List<Object> collect = collect1.keySet().stream().filter(key -> collect1.get(key) > 1).collect(Collectors.toList());
                            if(collect.contains(inspectionCodeContent.getCode())){
                                errorMessage.add("检修项内容重复，忽略导入");
                                sb.append("检修项内容重复；");
                                if(error){
                                    errorLines++;
                                    error = false;
                                }
                            }
                        }
                    }
                    inspectionCodeContentDto.setErrorCause(String.valueOf(sb));
                    successLines++;
                }
                if(errorLines==0) {
                    for (InspectionCodeContent inspectionCodeContent : list) {
                        inspectionCodeContentMapper.insert(inspectionCodeContent);
                    }
                } else {
                    successLines =0;
                    //1.获取文件流
                    org.springframework.core.io.Resource resource = new ClassPathResource("/templates/inspectionCodeContent.xlsx");
                    InputStream resourceAsStream = resource.getInputStream();

                    //2.获取临时文件
                    File fileTemp= new File("/templates/inspectionCodeContent.xlsx");
                    try {
                        //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                        FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
                    }catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    String path = fileTemp.getAbsolutePath();
                    TemplateExportParams exportParams = new TemplateExportParams(path);
                    Map<String, Object> errorMap = new HashMap<String, Object>(32);
                    errorMap.put("title", "配置检修项导入错误清单");
                    List<Map<String, Object>> listMap = new ArrayList<>();
                    for (InspectionCodeContentDTO dto : inspectionCodeContentDtoList) {
                        //获取一条排班记录
                        Map<String, Object> lm = new HashMap<String, Object>(32);
                        //是否为检查项字典值翻译
                        String insType = null;
                        if(ObjectUtil.isNotNull(dto.getIsType())){
                            List<DictModel> inspectionType = sysBaseApi.getDictItems("inspection_value");
                            List<String> collect = inspectionType.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getIsType())){
                                inspectionType= inspectionType.stream().filter(f -> (String.valueOf(dto.getIsType())).equals(f.getValue())).collect(Collectors.toList());
                                insType = inspectionType.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                insType = dto.getIsType();
                            }
                        }else{
                            insType = null;
                        }
                        //检查值类型字典值翻译
                        String statusItem = null;
                        if(ObjectUtil.isNotNull(dto.getSStatusItem())){
                            List<DictModel> appointDevice = sysBaseApi.getDictItems("patrol_input_type");
                            List<String> collect = appointDevice.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getSStatusItem())){
                                appointDevice= appointDevice.stream().filter(f -> (String.valueOf(dto.getSStatusItem())).equals(f.getValue())).collect(Collectors.toList());
                                statusItem = appointDevice.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                statusItem = dto.getSStatusItem();
                            }
                        }else{
                            statusItem = null;
                        }
                        //检查值是否必填字典值翻译
                        String inspectionCode = null;
                        if(ObjectUtil.isNotNull(dto.getIsInspectionType())){
                            List<DictModel> appointDevice = sysBaseApi.getDictItems("inspection_value");
                            List<String> collect = appointDevice.stream().map(DictModel::getValue).collect(Collectors.toList());
                            if(collect.contains(dto.getIsInspectionType())){
                                appointDevice= appointDevice.stream().filter(f -> (String.valueOf(dto.getIsInspectionType())).equals(f.getValue())).collect(Collectors.toList());
                                inspectionCode = appointDevice.stream().map(DictModel::getText).collect(Collectors.joining());
                            }else{
                                inspectionCode = dto.getIsInspectionType();
                            }
                        }else{
                            inspectionCode = null;
                        }

                        //错误报告获取信息
                        lm.put("child", dto.getHasChild());
                        lm.put("pid", dto.getPid());
                        lm.put("name", dto.getName());
                        lm.put("code", dto.getCode());
                        lm.put("sortNo", dto.getIsSortNo());
                        lm.put("isType", insType);
                        lm.put("qualityStandard",dto.getQualityStandard());
                        lm.put("statusItem", statusItem);
                        lm.put("inspectionType", inspectionCode);
                        lm.put("dictCode", dto.getDictCode());
                        lm.put("dataCheck", dto.getDataCheck());
                        lm.put("inspectionCodeId", dto.getInspectionCodeId());
                        lm.put("mistake", dto.getErrorCause());
                        listMap.add(lm);
                    }
                    errorMap.put("maplist", listMap);
                    Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
                    String filename = "配置检修项导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
                    FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+filename);
                    url =filename;
                    workbook.write(out);
                }
            } catch (Exception e) {
                errorMessage.add("发生异常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
        return imporReturnRes(errorLines, successLines, errorMessage,true,url);
    }

    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage,boolean isType,String failReportUrl) throws IOException {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl",failReportUrl);
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
            res.setMessage("导入失败，文件类型不对。");
            res.setCode(200);
            return res;
        }
    }

}
