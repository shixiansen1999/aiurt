package com.aiurt.modules.schedule.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.schedule.entity.Schedule;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.entity.ScheduleRule;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.service.IScheduleHolidaysService;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.schedule.service.IScheduleRuleService;
import com.aiurt.modules.schedule.service.IScheduleService;
import com.aiurt.modules.schedule.util.ImportExcelUtil;
import com.aiurt.modules.schedule.vo.RecordParam;
import com.aiurt.modules.schedule.vo.ScheduleRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: schedule
 * @Author: HQY
 * @Date: 2022-07-20
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
    private ISysBaseAPI userService;
    @Autowired
    private IScheduleRecordService recordService;
    @Autowired
    private IScheduleRuleService ruleService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IScheduleHolidaysService scheduleHolidaysService;
    @Autowired
    private ScheduleRecordMapper scheduleRecordMapper;

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
    @PermissionData(pageComponent = "schedule/ScheduleList")
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
        return scheduleService.add(schedule);
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
   public void exportXls(HttpServletRequest request, HttpServletResponse response, Schedule schedule) {
        Page<Schedule> page = new Page<Schedule>(1, 10000);
        IPage<Schedule> pageList = scheduleService.getList(schedule, page);
        List<Schedule> records = pageList.getRecords();
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        if (CollUtil.isNotEmpty(records)) {
            int i = 1;
            for (Schedule record : records) {
                Map<String,Object> map = new HashMap<>();
                map.put("sort", i);
                map.put("orgName", record.getOrgName());
                map.put("userName",record.getUserName());
                map.put("workNo", record.getWorkNo());
                List<ScheduleRecordModel> item = record.getItem();
                for (int j = 0; j < item.size(); j++) {
                    ScheduleRecordModel scheduleRecordModel = item.get(j);
                    map.put("day" + j + 1, scheduleRecordModel.getItemName());
                }
                dataList.add(map);
                i++;
            }

        }
        Calendar calendar = Calendar.getInstance();
       SysDepartModel sysDepartModel = sysBaseAPI.getDepartByOrgCode(schedule.getOrgCode());
       String title = null;
       if (schedule.getDate() == null) {
           try {
               calendar.setTime(DateUtils.parseDate(DateUtils.formatDate(), "yyyy年MM"));
               title = DateUtils.formatDate() + "排班表 — " + sysDepartModel.getDepartName();
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }else {
           calendar.setTime(schedule.getDate());
           title = DateUtil.format(schedule.getDate(), "yyyy年MM月") + "排班表 — " + sysDepartModel.getDepartName();}
        int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //配置ExcelExportEntity集合如下：
        List<ExcelExportEntity> entityList = new ArrayList<>();
        //一般表头使用这种两个参数的构造器
        ExcelExportEntity e1 = new ExcelExportEntity("序号","sort");
        ExcelExportEntity e2 = new ExcelExportEntity("班组","orgName");
        ExcelExportEntity e3 = new ExcelExportEntity("姓名","userName");
        ExcelExportEntity e4 = new ExcelExportEntity("工号","workNo");

        entityList.add(e1);
        entityList.add(e2);
        entityList.add(e3);
        entityList.add(e4);

        for (int i = 0; i < maximum; i++) {
            String format = String.format("%02d", i + 1);
            ExcelExportEntity e = new ExcelExportEntity(format,"day" + i + 1);
            entityList.add(e);
        }
       //调用ExcelExportUtil.exportExcel方法生成workbook
       Workbook wb = ExcelExportUtil.exportExcel(new ExportParams(title, "sheetName"),entityList,dataList);
       String fileName = "排班表";
       try {
           response.setHeader("Content-Disposition",
                   "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
           response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
           BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
           wb.write(bufferedOutPut);
           bufferedOutPut.flush();
           bufferedOutPut.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    @RequestMapping(value = "/exportXls1")
    public void exportXls2(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("username");
        String date = request.getParameter("date");
        if (StrUtil.isEmptyOrUndefined(date)) {
            date=  DateUtil.format(new Date(), "yyyy-MM");
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
            List<ScheduleRecord> recordList = recordService.getRecordListInDays(user.getUserId(), DateUtil.format(dateList.get(0), "yyyy-MM-dd"), DateUtil.format(dateList.get(dateList.size() - 1), "yyyy-MM-dd"));
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
            // todo 后期修改
            //List<Map<Integer, String>> scheduleDate = new ArrayList<>();
            List<Map<Integer, String>> scheduleDate = ImportExcelUtil.readExcelContentByList(inputStream, type, 0, 0);
            return scheduleService.importScheduleExcel(scheduleDate, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }
    @AutoLog(value = "人员排班-查询人员下拉")
    @ApiOperation(value = "人员排班-查询人员下拉", notes = "人员排班-查询人员下拉")
    @RequestMapping(value = "selectScheduleUser", method = RequestMethod.GET)
    @PermissionData(pageComponent = "schedule/ScheduleList")
    public Result<List<LoginUser>> selectScheduleUser(@RequestParam(name = "startDate", required = true) String startDate,
                                                    @RequestParam(name = "endDate", required = true) String endDate) {
        //根据数据规则查出所属权限的人员
        List<LoginUser> userList = sysBaseAPI.getAllUsers();
        Result<List<LoginUser>> result = new Result<List<LoginUser>>();

       //如果已经被安排过排班则把状态设置为冻结
        if (CollUtil.isNotEmpty(userList)) {
            userList.forEach(user -> {
                List list = recordService.getRecordListInDays(user.getId(), startDate, endDate);
                if (list != null && list.size() > 0) {
                    user.setStatus(2);
                }
            });
        }
        result.setSuccess(true);
        result.setResult(userList);
        return result;
    }

    @AutoLog(value = "获取本人创建的所有规则")
    @ApiOperation(value = "获取本人创建的所有规则", notes = "获取本人创建的所有规则")
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
        ClassPathResource classPathResource =  new ClassPathResource("templates/schedule.xlsx");
        InputStream bis = classPathResource.getInputStream();
        //设置发送到客户端的响应的内容类型
        response.setContentType("tapplication/vnd.ms-excel;charset=utf-8");
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
            InputStream inputStream = file.getInputStream();
            //获取后缀名
            String nameAndType[] = file.getOriginalFilename().split("\\.");
            String type = nameAndType[1];
            // todo 后期修改
            List<Map<Integer, String>> scheduleDate = new ArrayList<>();
//            List<Map<Integer, String>> data = ImportExcelUtil.readExcelContentByList(inputStream, type, 0, 0);
            scheduleHolidaysService.importHolidayExcel(scheduleDate, request);
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
        // todo 后期修改
        List<LoginUser> userList = new ArrayList<>();
//        List<LoginUser> userList = userService.selectUserByRoleAndDepartment("jishuyuan", param.getOrgId(), param.getUserName());

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
                vo.setCount((long) recordService.count(wrapper));
                if (new Date().before(endDate)) {
                    wrapper = new QueryWrapper();
                    wrapper.eq("user_id", sysUser.getId());
                    wrapper.ge("date", startDate);
                    wrapper.le("date", new Date());
                    wrapper.like("item_name", "夜");
                    vo.setAct((long) recordService.count(wrapper));
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

    @AutoLog(value = "校验本年度是否有存在节假日")
    @ApiOperation(value = "校验本年度是否有存在节假日", notes = "校验本年度是否有存在节假日")
    @RequestMapping(value = "checkHolidays", method = RequestMethod.GET)
    public Boolean checkHolidays() {
        List<String> allHolidays = sysBaseAPI.getAllHolidays();
        if (CollUtil.isNotEmpty(allHolidays)) {
            int i = DateUtil.thisYear();
            List<String> list = allHolidays.stream().filter(h -> h.contains(Convert.toStr(i))).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
