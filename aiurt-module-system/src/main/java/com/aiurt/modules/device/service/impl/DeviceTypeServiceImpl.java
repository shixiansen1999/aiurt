package com.aiurt.modules.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.device.controller.DeviceTypeController;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceComposeMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.mapper.CsMajorMapper;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.subsystem.mapper.CsSubsystemMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {
    @Autowired
    @Lazy
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceComposeMapper deviceComposeMapper;
    @Autowired
    private CsSubsystemMapper subsystemMapper;
    @Autowired
    private CsMajorMapper majorMapper;
    @Autowired
    private MaterialBaseMapper materialBaseMapper;
    @Autowired
    private DeviceComposeServiceImpl deviceComposeService;
    @Autowired
    @Lazy
    private DeviceTypeController deviceTypeController;
    @Autowired
    @Lazy
    private ISysBaseAPI iSysBaseAPI;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;


    /**
     * 列表
     * @return
     */
    @Override
    public List<DeviceType> selectList(){
        return deviceTypeMapper.readAll();
    }
    /**
     * 添加
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(DeviceType deviceType) {
        if(null == deviceType.getPid()){
            deviceType.setPid("0");
        }
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getName, deviceType.getName());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> nameList = deviceTypeMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("分类名称重复，请重新填写！");
        }
        String typeCodeCc = getCcStr(deviceType);
        deviceType.setCodeCc(typeCodeCc);
        deviceTypeService.save(deviceType);
        //添加设备组成
        if(null!=deviceType.getDeviceComposeList()){
            deviceType.getDeviceComposeList().forEach(compose ->{
                compose.setDeviceTypeCode(deviceType.getCode());
                deviceComposeMapper.insert(compose);
            });
        }
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(DeviceType deviceType) {
        if(null == deviceType.getPid()){
            deviceType.setPid("0");
        }
        //删除设备组成
        QueryWrapper<DeviceCompose> composeWrapper = new QueryWrapper<DeviceCompose>();
        composeWrapper.eq("device_type_code", deviceType.getCode());
        deviceComposeMapper.delete(composeWrapper);
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(deviceType.getId())) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getName, deviceType.getName());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> nameList = deviceTypeMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(deviceType.getId())) {
            return Result.error("分类名称重复，请重新填写！");
        }
        String typeCodeCc = getCcStr(deviceType);
        deviceType.setCodeCc(typeCodeCc);
        deviceTypeMapper.updateById(deviceType);
        //添加设备组成
        if(null!=deviceType.getDeviceComposeList()){
            deviceType.getDeviceComposeList().forEach(compose ->{
                compose.setDeviceTypeCode(deviceType.getCode());
                deviceComposeMapper.insert(compose);
            });
        }
        return Result.OK("编辑成功！");
    }
    /**
     * DeviceType树
     * @param typeList
     * @param pid
     * @return
     */
    @Override
    public List<DeviceType> treeList(List<DeviceType> typeList, String pid){

        List<DeviceType> childList = typeList.stream().filter(deviceType -> pid.equals(deviceType.getPid())).collect(Collectors.toList());
        if(childList != null && childList.size()>0){
            for (DeviceType deviceType : childList) {
                deviceType.setTreeType("sblx");
                String pUrl = "";
                Integer pIsSpecialDevice = null;
                if(pid.equals("0")){
                    //如果systemCode不是null，查询systemCode的名称
                    if(null!=deviceType.getSystemCode()){
                        pUrl = deviceType.getSystemName();
                    }
                    //如果systemCode是null，查询majorCode的名称
                    if(null==deviceType.getSystemCode() && null!= deviceType.getMajorCode()){
                        pUrl = deviceType.getMajorName();
                    }
                }else{
                    //如果pid不是0，查询设备类型名称
                    LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
                    DeviceType type = deviceTypeMapper.selectOne(wrapper.eq(DeviceType::getId,pid));
                    pUrl = type.getName();
                    pIsSpecialDevice = type.getIsSpecialDevice();
                }
                deviceType.setPUrl(pUrl);
                deviceType.setPIsSpecialDevice(pIsSpecialDevice);
                deviceType.setChildren(treeList(typeList,deviceType.getId().toString()));
            }
        }
        return childList;
    }
    /**
     * 拼接cc字段
     * @param deviceType
     * @return
     */
    @Override
    public String getCcStr(DeviceType deviceType) {
        String res = "";
        String str = Ccstr(deviceType, "");
        if( !"" .equals(str) ){
            if(str.contains(CommonConstant.SYSTEM_SPLIT_STR)){
                List<String> strings = Arrays.asList(str.split(CommonConstant.SYSTEM_SPLIT_STR));
                Collections.reverse(strings);
                for(String s : strings){
                    res += s + CommonConstant.SYSTEM_SPLIT_STR;
                }
                res = res.substring(0,res.length()-1);
            }else{
                res = str;
            }
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcelMaterial(MultipartFile file, ImportParams params) throws Exception {
        List<DeviceType> listMaterial = ExcelImportUtil.importExcel(file.getInputStream(), DeviceType.class, params);
        List<String> errorStrs = new ArrayList<>();
        // 去掉 sql 中的重复数据
        Integer errorLines = 0;
        Integer successLines = 0;
        List<DeviceType> list = new ArrayList<>();
        for (int i = 0; i < listMaterial.size(); i++) {
            try {
                DeviceType deviceType = listMaterial.get(i);
                //专业
                String majorCodeName = deviceType.getMajorName() == null ? "" : deviceType.getMajorName();
                if ("".equals(majorCodeName)) {
                    errorStrs.add("第 " + i + " 行：专业名称为空，忽略导入。");
                    list.add(deviceType.setText("专业名称为空，忽略导入"));
                    continue;
                }
                CsUserMajorModel csMajor = deviceTypeMapper.selectCsMajor(majorCodeName);
                if (ObjectUtil.isEmpty(csMajor)) {
                    errorStrs.add("第 " + i + " 行：无法根据专业名称找到对应数据，忽略导入。");
                    list.add(deviceType.setText("无法根据专业名称找到对应数据，忽略导入"));
                    continue;
                } else {
                    deviceType.setMajorCode(csMajor.getMajorCode());
                    //安全事项分类
                    if (StrUtil.isNotEmpty(deviceType.getSystemName())) {
                        String systemCode = deviceTypeMapper.selectSystemCode(deviceType.getSystemName(),csMajor.getMajorCode());
                        if (StrUtil.isNotEmpty(systemCode)){
                            deviceType.setSystemCode(systemCode);
                        }else {
                            errorStrs.add("第 " + i + " 行：输入的子系统找不到！请核对后输出，忽略导入。");
                            list.add(deviceType.setText("输入的子系统找不到！请核对后输出，忽略导入"));
                            continue;
                        }
                    }
                    if(StrUtil.isNotEmpty(deviceType.getPUrl())){
                       // AtomicReference<String> pid =new AtomicReference<>();
                        String pid =null;
                        String id =null;
                        List<String> strings = Arrays.asList(deviceType.getPUrl().split("-"));
                        for (int j = 0; j < strings.size() ; j++) {
                            String s = strings.get(j);
                            LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
                            if (ObjectUtil.isNotEmpty(pid)){
                               wrapper.eq(DeviceType::getPid,pid);
                            }
                             wrapper.eq(DeviceType::getName,s)
                                    .eq(DeviceType::getDelFlag,0)
                                    .eq(DeviceType::getMajorCode,deviceType.getMajorCode());
                            DeviceType deviceType2 = deviceTypeService.getOne(wrapper);
                            if (ObjectUtil.isNotEmpty(deviceType2)){
                                pid=deviceType2.getId();
                            }
                            if (strings.size()== (j+1)){
                                id = deviceType2.getId();
                            }
                        }
                        if (StrUtil.isNotEmpty(id)){
                            deviceType.setPid(id);
                        }else {
                            JSONObject systemCode = iSysBaseAPI.getSystemName(deviceType.getMajorCode(), deviceType.getPUrl());
                            if (ObjectUtil.isNotEmpty(systemCode)) {
                                deviceType.setPid("0");
                            } else {
                                errorStrs.add("第 " + i + " 行：输入的上级节点找不到！请核对后输出，忽略导入。");
                                list.add(deviceType.setText("输入的上级节点找不到！请核对后输出，忽略导入"));
                                continue;
                            }
                        }
                    }else {
                        deviceType.setPid("0");
                    }
                    if(StrUtil.isNotEmpty(deviceType.getCode())){
                        DeviceType deviceType1 = deviceTypeService.getOne(new LambdaQueryWrapper<DeviceType>()
                                .eq(DeviceType::getDelFlag,0).eq(DeviceType::getCode,deviceType.getCode()));
                        if (ObjectUtil.isNotEmpty(deviceType1)){
                            errorStrs.add("第 " + i + " 行：输入的分类编号已经存在！请重新输入，忽略导入。");
                            list.add(deviceType.setText("输入的分类编号已经存在！请重新输入，忽略导入"));
                            continue;
                        }
                    }else {
                        errorStrs.add("第 " + i + " 行：分类编号未输入！忽略导入。");
                        list.add(deviceType.setText("分类编号未输入！忽略导入"));
                        continue;
                    }
                    if(StrUtil.isNotEmpty(deviceType.getName())){
                        DeviceType deviceType1 = deviceTypeService.getOne(new LambdaQueryWrapper<DeviceType>()
                                .eq(DeviceType::getDelFlag,0).eq(DeviceType::getName,deviceType.getName())
                                .eq(DeviceType::getMajorCode,deviceType.getMajorCode())
                                .eq(DeviceType::getSystemCode,deviceType.getSystemCode())
                                .eq(DeviceType::getPid,0));
                        if (ObjectUtil.isNotEmpty(deviceType1)){
                            errorStrs.add("第 " + i + " 行：输入的分类名称已经存在！请重新输入，忽略导入。");
                            list.add(deviceType.setText("输入的分类名称已经存在！请重新输入，忽略导入"));
                            continue;
                        }
                        List<DeviceType> deviceTypes = deviceTypeService.list(new LambdaQueryWrapper<DeviceType>()
                                .eq(DeviceType::getDelFlag,0).eq(DeviceType::getName,deviceType.getName())
                                .eq(DeviceType::getMajorCode,deviceType.getMajorCode())
                                .eq(DeviceType::getSystemCode,deviceType.getSystemCode())
                                .not(wrapper->wrapper.eq(DeviceType::getPid,"0")));
                        if (CollectionUtil.isNotEmpty(deviceTypes)){
                            List<DeviceType> collect = deviceTypes.stream().filter(d -> d.getPid().equals(deviceType.getPid())).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(collect)){
                               errorStrs.add("第 " + i + " 行：输入的分类名称已经存在！请重新输入，忽略导入。");
                               list.add(deviceType.setText("输入的分类名称已经存在！请重新输入，忽略导入"));
                               continue;
                           }
                            List<DeviceType> collect1 = deviceTypes.stream().filter(d -> d.getId().equals(deviceType.getPid())).collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(collect1)){
                                errorStrs.add("第 " + i + " 行：输入的分类名称已经存在！请重新输入，忽略导入。");
                                list.add(deviceType.setText("输入的分类名称已经存在！请重新输入，忽略导入"));
                                continue;
                            }
                        }
                    }else {
                        errorStrs.add("第 " + i + " 行：分类名称未输入！忽略导入。");
                        list.add(deviceType.setText("分类名称未输入！忽略导入"));
                        continue;
                    }
                    //状态
                    String stateName = deviceType.getStatusName()==null?"": deviceType.getStatusName();
                    if ("".equals(stateName)){
                        errorStrs.add("第 " + i + " 行：分类状态为空，忽略导入。");
                        list.add(deviceType.setText("分类状态为空，忽略导入"));
                        continue;
                    }else {
                        List<DictModel> dictItems = iSysBaseAPI.getDictItems("device_type_status");
                        dictItems.forEach(s -> {
                            if (s.getText().equals(stateName)) {
                                deviceType.setStatus(Integer.valueOf(s.getValue()));
                            }
                        });
                        if (ObjectUtil.isEmpty(deviceType.getStatus())){
                        errorStrs.add("第 " + i + " 行：分类状态识别不出，忽略导入。");
                        list.add(deviceType.setText("分类状态识别不出，忽略导入"));
                        continue;
                        }
                    }
                    if (StrUtil.isNotEmpty(deviceType.getIsSpecialDeviceName())){
                        List<DictModel> dictItems = iSysBaseAPI.getDictItems("is_special_device");
                        dictItems.forEach(s -> {
                            if (s.getText().equals(deviceType.getIsSpecialDeviceName())) {
                                deviceType.setIsSpecialDevice(Integer.valueOf(s.getValue()));
                            }
                        });
                        if (ObjectUtil.isEmpty(deviceType.getIsSpecialDevice())){
                            errorStrs.add("第 " + i + " 行：是否为特种设备识别不出，忽略导入。");
                            list.add(deviceType.setText("是否为特种设备识别不出，忽略导入"));
                            continue;
                    }
                    }else {
                        errorStrs.add("第 " + i + " 行：是否为特种设备为空，忽略导入。");
                        list.add(deviceType.setText("是否为特种设备为空，忽略导入"));
                        continue;
                    }
                    if(StrUtil.isNotEmpty(deviceType.getIsEndName())){
                        List<DictModel> dictItems = iSysBaseAPI.getDictItems("is_end");
                        dictItems.forEach(s -> {
                            if (s.getText().equals(deviceType.getIsEndName())) {
                                deviceType.setIsEnd(Integer.valueOf(s.getValue()));
                            }
                        });
                        if (ObjectUtil.isEmpty(deviceType.getIsEnd())){
                            errorStrs.add("第 " + i + " 行：是否为尾节点识别不出，忽略导入。");
                            list.add(deviceType.setText("是否为尾节点识别不出，忽略导入"));
                            continue;
                        }
                    }else {
                        errorStrs.add("第 " + i + " 行：是否为尾节点为空，忽略导入。");
                        list.add(deviceType.setText("是否为尾节点为空，忽略导入"));
                        continue;
                    }
                    if (CollectionUtil.isNotEmpty(deviceType.getDeviceComposeList())&& "是".equals(deviceType.getIsEndName())){
                        for (DeviceCompose d : deviceType.getDeviceComposeList()) {
                            deviceType.setDeviceComposeCode(d.getMaterialCode());
                            MaterialBase m = materialBaseMapper.selectOne(new LambdaQueryWrapper<MaterialBase>()
                                    .eq(MaterialBase::getCode, d.getMaterialCode()).eq(MaterialBase::getDelFlag, 0));
                            if (ObjectUtil.isNotEmpty(m)) {
                                d.setBaseTypeCode(m.getBaseTypeCode()).setMaterialId(m.getId()).setMaterialName(m.getName())
                                        .setUnit(m.getUnit()).setPrice(new BigDecimal(m.getPrice()));
                            } else {
                                errorStrs.add("第 " + i + " 行："+d.getMaterialCode()+"找不到此数据，忽略导入。");
                                list.add(deviceType.setText(d.getMaterialCode()+"找不到此数据，忽略导入"));
                                continue;
                            }
                            deviceComposeService.saveBatch(deviceType.getDeviceComposeList());
                        }
                    }
                    String codecc =  getCcStr(deviceType);
                    deviceType.setCodeCc(codecc);
                    int save = deviceTypeMapper.insert(deviceType);
                    if (save <= 0) {
                        throw new Exception(CommonConstant.SQL_INDEX_UNIQ_MATERIAL_BASE_CODE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (list.size()>0){
            //创建导入失败错误报告,进行模板导出
            Resource resource = new ClassPathResource("templates/deviceTypeError.xlsx");
            InputStream resourceAsStream = resource.getInputStream();
            //2.获取临时文件
            File fileTemp= new File("templates/deviceTypeError.xlsx");
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);
            List<Map<String, Object>> mapList = new ArrayList<>();
            list.forEach(l->{
                Map<String, Object> lm = new HashMap<String, Object>();
                lm.put("majorName",l.getMajorName());
                lm.put("systemName",l.getSystemName());
                lm.put("pUrl",l.getPUrl());
                lm.put("code",l.getCode());
                lm.put("name",l.getName());
                lm.put("statusName",l.getStatusName());
                lm.put("isSpecialDeviceName",l.getIsSpecialDeviceName());
                lm.put("isEndName",l.getIsEndName());
                lm.put("deviceComposeCode",l.getDeviceComposeCode());
                lm.put("text",l.getText());
                mapList.add(lm);
            });
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("maplist", mapList);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams,errorMap);
            String fileName = "设备分类导入错误模板"+"_" + System.currentTimeMillis()+".xlsx";
            FileOutputStream out = new FileOutputStream(upLoadPath+ File.separator+fileName);
            String  url = fileName;
            workbook.write(out);
            errorLines+=errorStrs.size();
            successLines+=(listMaterial.size()-errorLines);
            return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorStrs,url);
        }
        errorLines += errorStrs.size();
        successLines += (listMaterial.size() - errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorStrs,null);
    }

    @Override
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, DeviceType deviceType) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Result<IPage<DeviceType>> result = deviceTypeController.queryPageList(deviceType,1,9999);
        List<DeviceType> deviceTypes = result.getResult().getRecords();
        if (CollectionUtil.isNotEmpty(deviceTypes)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "设备分类");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, DeviceType.class);
            //自定义导出字段
            // String exportField = "majorCode,systemCode";
            // mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备分类", "设备分类"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, deviceTypes);
        }
        return mv;
    }

    String Ccstr(DeviceType deviceType, String str){
        DeviceType deviceTypeRes = new DeviceType();
        if("0".equals(deviceType.getPid())){
            str += deviceType.getCode();
        }else{
            str += deviceType.getCode() + "/";
            deviceTypeRes = deviceTypeMapper.selectById(deviceType.getPid());
            str = Ccstr(deviceTypeRes, str);
        }
        return str;
    }


}
