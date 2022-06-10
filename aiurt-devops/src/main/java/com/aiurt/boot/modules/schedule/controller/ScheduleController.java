package com.aiurt.boot.modules.schedule.controller;

import cn.hutool.core.util.StrUtil;

import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.schedule.entity.*;
import com.aiurt.boot.modules.schedule.model.ScheduleUser;
import com.aiurt.boot.modules.schedule.service.*;
import com.aiurt.boot.modules.schedule.vo.RecordParam;
import com.aiurt.boot.modules.schedule.vo.ScheduleRecordVo;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * @Description: schedule
 * @Author: qian
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "人员排班")
@RestController
@RequestMapping("/schedule/schedule")
public class ScheduleController {
    @Autowired
    private IScheduleService scheduleService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IScheduleRecordService recordService;
    @Autowired
    private IScheduleRuleService ruleService;
    @Autowired
    private IScheduleRuleItemService ruleItemService;
    @Autowired
    private IScheduleItemService ItemService;
    @Autowired
    private IScheduleHolidaysService scheduleHolidaysService;

    @Value("${support.downFilePath.holidayExcelPath}")
    private String excelPath;

    /**
     * 分页列表查询
     *
     * @param schedule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "人员排班-分页列表查询")
    @ApiOperation(value = "人员排班-分页列表查询", notes = "人员排班-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Schedule>> queryPageList(Schedule schedule,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Result<IPage<Schedule>> result = new Result<IPage<Schedule>>();
        Page<Schedule> page = new Page<Schedule>(pageNo, pageSize);
        IPage<Schedule> pageList = scheduleService.getList(schedule, page);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param schedule
     * @return
     */
    @AutoLog(value = "人员排班-添加")
    @ApiOperation(value = "人员排班-添加", notes = "人员排班-添加")
    @PostMapping(value = "/add")
    public Result<Schedule> add(@RequestBody Schedule schedule) {
        Result<Schedule> result = new Result<Schedule>();
        try {
            Calendar start = Calendar.getInstance();
            start.setTime(schedule.getStartDate());
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("rule_id", schedule.getRuleId());
            List<ScheduleRuleItem> itemList = ruleItemService.list(wrapper);
            int itemSize = itemList.size();
            Map<Integer, Integer> scheduleRuleItemMap = new HashMap<>(itemSize);
            for (ScheduleRuleItem item : itemList) {
                scheduleRuleItemMap.put(item.getSort(), item.getItemId());
            }
            int i = 0;
            while (!start.getTime().after(schedule.getEndDate())) {
                i++;
                int index = (i % itemSize == 0 ? itemSize : i % itemSize);
                Integer ruleItemId = scheduleRuleItemMap.get(index);
                ScheduleItem scheduleItem = ItemService.getById(ruleItemId);
                for (String userId : schedule.getUserIds()) {
                    ScheduleRecord record = ScheduleRecord.builder()
                            .scheduleId(schedule.getId())
                            .userId(userId)
                            .date(start.getTime())
                            .itemId(scheduleItem.getId())
                            .itemName(scheduleItem.getName())
                            .startTime(scheduleItem.getStartTime())
                            .endTime(scheduleItem.getEndTime())
                            .color(scheduleItem.getColor())
                            .delFlag(0)
                            .build();
                    recordService.save(record);
                }
                start.add(Calendar.DAY_OF_YEAR, 1);
            }
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
     * @param schedule
     * @return
     */
    @AutoLog(value = "人员排班-编辑")
    @ApiOperation(value = "人员排班-编辑", notes = "人员排班-编辑")
    @PutMapping(value = "/edit")
    public Result<Schedule> edit(@RequestBody Schedule schedule) {
        Result<Schedule> result = new Result<Schedule>();
        Schedule scheduleEntity = scheduleService.getById(schedule.getId());
        if (scheduleEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = scheduleService.updateById(schedule);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @return
     */
    @AutoLog(value = "人员排班-通过id删除")
    @ApiOperation(value = "人员排班-通过id删除", notes = "人员排班-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "userId", required = true) String userId,
                            @RequestParam(name = "date", required = true) String date) {
        try {
            Date temp = DateUtils.parseDate(date, "yyyy-MM");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(temp);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            Date startDate = DateUtils.parseDate(year + "-" + month + "-01", "yyyy-MM-dd");
            Date endDate = DateUtils.parseDate(year + "-" + (month + 1) + "-01", "yyyy-MM-dd");
            UpdateWrapper wrapper = new UpdateWrapper();
            wrapper.eq("user_id", userId);
            wrapper.isNotNull("item_id");
            wrapper.set("del_flag", 1);
            wrapper.ge("date", startDate);
            wrapper.lt("date", endDate);
            recordService.update(wrapper);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
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
    @AutoLog(value = "人员排班-批量删除")
    @ApiOperation(value = "人员排班-批量删除", notes = "人员排班-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Schedule> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Schedule> result = new Result<Schedule>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.scheduleService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "人员排班-通过id查询")
    @ApiOperation(value = "人员排班-通过id查询", notes = "人员排班-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Schedule> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Schedule> result = new Result<Schedule>();
        Schedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(schedule);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<Schedule> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Schedule schedule = JSON.parseObject(deString, Schedule.class);
                queryWrapper = QueryGenerator.initQueryWrapper(schedule, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Schedule> pageList = scheduleService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "schedule列表");
        mv.addObject(NormalExcelConstants.CLASS, Schedule.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    @RequestMapping(value = "/exportXls1")
    public void exportXls2(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("username");
        String date = request.getParameter("date");
        if (StrUtil.isEmptyOrUndefined(date)) {
            date = DateUtils.format(new Date(), "yyyy-MM");
        }else{
            Date date1 = new Date(Long.valueOf(date));
            date = DateUtils.formatDate(date1,"yyyy-MM");
        }
        if (StrUtil.isEmptyOrUndefined(userName)){
            userName=null;
        }
        List<Date> dateList = DateUtils.getDateList(date);

         //从record表中获取有多少人在时间范围类安排了工作
        List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDate(date,userName);


        List<Map> resultList = new ArrayList<>();
        for (ScheduleUser user : scheduleUserList) {
            Map map = new HashMap();
            map.put(0, user.getUserName());
            List<ScheduleRecord> recordList = recordService.getRecordListInDays(user.getUserId(), DateUtils.format(dateList.get(0), "yyyy-MM-dd"), DateUtils.format(dateList.get(dateList.size() - 1), "yyyy-MM-dd"));
            for (ScheduleRecord record : recordList) {
                map.put(record.getExcelIndex(), record.getItemName());
            }
            resultList.add(map);
        }
       // ExportUtil.export2(dateList, resultList, request, response, date + "排班表.xlsx");
    }


/*    *//**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     *//*
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Schedule> listSchedules = ExcelImportUtil.importExcel(file.getInputStream(), Schedule.class, params);
                scheduleService.saveBatch(listSchedules);
                return Result.ok("文件导入成功！数据行数:" + listSchedules.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }*/

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        try {
            MultipartFile file = multipartRequest.getFile("file");
            InputStream inputStream = file.getInputStream();//获取后缀名
            String nameAndType[] = file.getOriginalFilename().split("\\.");
            String type = nameAndType[1];
            List<Map<Integer, String>> scheduleDate = ImportExcelUtil.readExcelContentByList(inputStream, type, 0, 0);
            scheduleService.importScheduleExcel(scheduleDate, request);
            return Result.ok("文件导入成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }

    @RequestMapping(value = "selectScheduleUser", method = RequestMethod.GET)
    public Result<List<SysUser>> selectScheduleUser(@RequestParam(name = "startDate", required = true) String startDate,
                                                    @RequestParam(name = "endDate", required = true) String endDate) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<SysUser> userList = userService.getUsersByOrgCode(orgCode);
        userList.forEach(user -> {
            List list = recordService.getRecordListInDays(user.getId(), startDate, endDate);
            if (list != null && list.size() > 0) {
                user.setStatus(2);
            }
        });
        result.setSuccess(true);
        result.setResult(userList);
        return result;
    }

    @RequestMapping(value = "selectScheduleRule", method = RequestMethod.GET)
    public Result<List<ScheduleRule>> selectScheduleRule() {

        Result<List<ScheduleRule>> result = new Result<List<ScheduleRule>>();
        List<ScheduleRule> list = ruleService.getAllDetailRules();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 下载模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "下载模板")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        String filePath = excelPath;
        InputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }
    /**
     * 下载排班表生成导入模板模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "下载模板")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @RequestMapping(value = "/downloadScheduleExcel", method = RequestMethod.GET)
    public void downloadScheduleExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        ClassPathResource classPathResource =  new ClassPathResource("template/排班表导入模板.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    @RequestMapping(value = "/importHolidayExcel", method = RequestMethod.POST)
    public Result<?> importHolidayExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        try {
            MultipartFile file = multipartRequest.getFile("file");
            InputStream inputStream = file.getInputStream();//获取后缀名
            String nameAndType[] = file.getOriginalFilename().split("\\.");
            String type = nameAndType[1];
            List<Map<Integer, String>> data = ImportExcelUtil.readExcelContentByList(inputStream, type, 0, 0);
            scheduleHolidaysService.importHolidayExcel(data, request);
            return Result.ok("文件导入成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }


    @AutoLog(value = "夜班人员统计")
    @ApiOperation(value = "夜班人员统计", notes = "夜班人员统计板")
    @RequestMapping("count")
    public Result<IPage<ScheduleRecordVo>> count(RecordParam param) {
        Date startDate = DateUtils.getStartDate(param.getStartDate());
        Date endDate = DateUtils.getEndDate(param.getEndDate());
        Result<IPage<ScheduleRecordVo>> result = new Result<>();
        IPage<ScheduleRecordVo> page = new Page<ScheduleRecordVo>();
        List<SysUser> userList = userService.selectUserByRoleAndDepartment("jishuyuan", param.getOrgId(), param.getUserName());

        if (userList != null && userList.size() > 0) {
            List<ScheduleRecordVo> list = new ArrayList<>(userList.size());
            userList.forEach(sysUser -> {
                ScheduleRecordVo vo = new ScheduleRecordVo();
                vo.setUserId(sysUser.getId());
                vo.setDepartment(sysUser.getOrgName());
                vo.setUsername(sysUser.getRealname());
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("user_id", sysUser.getId());
                wrapper.ge("date", startDate);
                wrapper.le("date", endDate);
                wrapper.like("item_name", "夜");
                vo.setCount(recordService.count(wrapper));
                if (new Date().before(endDate)) {
                    wrapper = new QueryWrapper();
                    wrapper.eq("user_id", sysUser.getId());
                    wrapper.ge("date", startDate);
                    wrapper.le("date", new Date());
                    wrapper.like("item_name", "夜");
                    vo.setAct(recordService.count(wrapper));
                } else {
                    vo.setAct(vo.getCount());
                }
                list.add(vo);
            });
            page.setRecords(list);
            page.setCurrent(1);
            page.setTotal(list.size());
            page.setSize(list.size());
        }

        result.setResult(page);
        return result;
    }

}
