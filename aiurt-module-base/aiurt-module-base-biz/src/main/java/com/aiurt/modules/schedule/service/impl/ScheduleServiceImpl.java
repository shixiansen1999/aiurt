package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.schedule.entity.Schedule;
import com.aiurt.modules.schedule.entity.ScheduleItem;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.mapper.ScheduleMapper;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.service.IScheduleItemService;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.schedule.service.IScheduleService;
import com.aiurt.modules.train.utils.DlownTemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

    @Autowired
    private IScheduleRecordService recordService;

    @Autowired
    private IScheduleItemService scheduleItemService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public IPage<Schedule> getList(Schedule schedule, Page<Schedule> temp) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = sysUser.getOrgCode();
        IPage page = new Page();
        page = temp;
        List<Schedule> scheduleList = new ArrayList<>();

        /**
         * 1、获取查询范围及当月有多少天
         */
        Date date = schedule.getDate();
        if (date == null) {
            try {
                date = DateUtils.parseDate(DateUtils.formatDate(), "yyyy-MM");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        /**
         * 2、从record表中获取有多少人在时间范围类安排了工作,从sys_user查询本班组的成员,根据班组查询
         */
        //List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDate(DateUtils.format(date, "yyyy-MM"),schedule.getUserName());
        //List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDateAndOrgCode(DateUtils.format(date, "yyyy-MM"),schedule.getUserName(),orgCode);
        List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDateAndOrgCodeAndOrgId(DateUtil.format(date, "yyyy-MM"), schedule.getUserName(), orgCode, schedule.getOrgId(),schedule.getText());
        /**
         * 3、获取记录数据
         */
        long start=System.currentTimeMillis();
        if (scheduleUserList != null && scheduleUserList.size() > 0) {
            for (ScheduleUser scheduleUser : scheduleUserList) {
                Schedule userSchedule = new Schedule();
                userSchedule.setUserId(scheduleUser.getUserId());
                userSchedule.setUserName(scheduleUser.getUserName());
                userSchedule.setOrgName(scheduleUser.getOrgName());
                userSchedule.setWorkNo(scheduleUser.getWorkNo());
                userSchedule.setDate(date);
                List<ScheduleRecordModel> scheduleRecordList = new ArrayList<ScheduleRecordModel>(maximum);
                for (int index = 0; index < maximum; index++) {
                    scheduleRecordList.add(new ScheduleRecordModel());
                }
                /**
                 * a、查询所有记录
                 */
                List<ScheduleRecordModel> recordModelList = recordService.getRecordListByUserAndDate(scheduleUser.getUserId(), DateUtil.format(date, "yyyy-MM"));
                if (recordModelList != null && recordModelList.size() > 0) {
                    for (ScheduleRecordModel recordModel : recordModelList) {
                        calendar.setTime(recordModel.getDate());
                        int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                        scheduleRecordList.set(index, recordModel);
                    }
                }
                userSchedule.setItem(scheduleRecordList);
                scheduleList.add(userSchedule);
            }
        }
        long end=System.currentTimeMillis();
        log.info("time:{}",end-start);
        page.setRecords(scheduleList);
        return page;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importScheduleExcel(List<Map<Integer, String>> scheduleDate, HttpServletResponse response) {
        //校验
        Boolean aBoolean = importErrorExcel(response, scheduleDate);
        if (aBoolean) {
            Result.error("文件导入失败");
        }
        //从表头获取时间
        String title = scheduleDate.get(1).get(3);
        String date = StrUtil.removeSuffix(title, "排班表");
        Date startTime = DateUtil.parse(date, "yyyy年M月d日");

        for (int i = 2; i < scheduleDate.size(); i++) {
            //获取一条排班记录
            Map<Integer, String> scheduleMap = scheduleDate.get(i);
            LoginUser user = scheduleMapper.getUser(scheduleMap.get(1),scheduleMap.get(2));
            //生成排班月份中每天的排班记录
            //获取排班月份的第一天和最后一天
            Calendar start = Calendar.getInstance();
            //从表头获取时间
            //calendar的月份从0开始，1月是0
            start.setTime(startTime);

            Date end = DateUtil.endOfMonth(start.getTime());
            //遍历本月所有的天数
            while (!start.getTime().after(end)) {
                ScheduleItem scheduleItem = scheduleItemService.getOne(new LambdaQueryWrapper<ScheduleItem>()
                        .eq(ScheduleItem::getName, scheduleMap.get(DateUtil.dayOfMonth(start.getTime())+3)).eq(ScheduleItem::getDelFlag,0));
                ScheduleRecord record = ScheduleRecord.builder()
                        .scheduleId(null)
                        .userId(user.getId())
                        .date(start.getTime())
                        .itemId(scheduleItem.getId())
                        .itemName(scheduleItem.getName())
                        .startTime(scheduleItem.getStartTime())
                        .endTime(scheduleItem.getEndTime())
                        .color(scheduleItem.getColor())
                        .delFlag(0)
                        .build();
               // recordService.save(record);
                start.add(Calendar.DAY_OF_YEAR, 1);
            }

        }
        return Result.ok("文件导入成功！");
    }

    /**校验,如果导入出错怎返回错误报告*/
    public Boolean importErrorExcel(HttpServletResponse response,List<Map<Integer, String>> scheduleDate) {
        //创建导入失败错误报告,进行模板导出

        URL resource = DlownTemplateUtil.class.getResource("/templates/scheduleErrorReport.xlsx");
        String path = resource.getPath();
        log.info("path:{}", path);
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<String, Object> errorMap = new HashMap<String, Object>();

        List<Map<String, Object>> listMap = new ArrayList<>();

        //从表头获取时间
        String title = scheduleDate.get(1).get(3);
        errorMap.put("title", title);
        String date = StrUtil.removeSuffix(title, "排班表");
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月");
        Date startTime = null;
        String timeMistake = null;
        try {
            startTime = format.parse(date);
        } catch (ParseException e) {
            timeMistake = "日期格式不正确，应为yyyy年M月";
        }

        //获得所有已经排班的用户
        List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDate(DateUtil.format(startTime, "yyyy-MM"), null);
        List<String> userIds = scheduleUserList.stream().map(ScheduleUser::getUserId).collect(Collectors.toList());

        for (int i = 3; i < scheduleDate.size(); i++) {
            //获取一条排班记录
            Map<Integer, String> scheduleMap = scheduleDate.get(i);
            Map<String, Object> lm = new HashMap<String, Object>();
            //错误报告获取信息
            lm.put("depart", scheduleMap.get(0));
            lm.put("realname", scheduleMap.get(1));
            lm.put("workno", scheduleMap.get(2));
            for (int j = 0; j < 31; j++) {
                lm.put(Convert.toStr(j + 1), scheduleMap.get(3 + j));
            }
            List<String> errorList = new ArrayList<>();
            int first = 3;
            if (i == first && StrUtil.isNotEmpty(timeMistake)) {
                errorList.add(timeMistake);
            }

            //判断部门，人员，工号是否填写，并且存在在该系统中
            if (StrUtil.isNotEmpty(scheduleMap.get(0))) {
                List<String> departNames = StrUtil.splitTrim(scheduleMap.get(0), "/");
                SysDepartModel parentDepart = scheduleMapper.getDepartByName(departNames.get(0));
                SysDepartModel depart = scheduleMapper.getDepartByName(departNames.get(1));
                if (ObjectUtil.isEmpty(depart) || ObjectUtil.isEmpty(parentDepart)) {
                    errorList.add("系统中未存在该组织机构");
                }
            } else {
                errorList.add("组织结构不能为空");
            }

            if (StrUtil.isNotEmpty(scheduleMap.get(1)) && StrUtil.isNotEmpty(scheduleMap.get(2))) {
                LoginUser user = scheduleMapper.getUser(scheduleMap.get(1), scheduleMap.get(2));
                if (ObjectUtil.isEmpty(user)) {
                    errorList.add("系统中未存在该人员姓名或工号");
                }
                //判断时间内是否有人已经排班
                if (CollUtil.isNotEmpty(userIds)&&userIds.contains(user.getId())) {
                    errorList.add("用户此段时间内已排班");
                }
            } else {
                errorList.add("用户姓名或工号不能为空");
            }
            //获取排班月份的第一天和最后一天
            Calendar start = Calendar.getInstance();
            //从表头获取时间
            //calendar的月份从0开始，1月是0
            start.setTime(startTime);
            Date end = DateUtil.endOfMonth(start.getTime());
            while (!start.getTime().after(end)) {
                ScheduleItem scheduleItem = scheduleItemService.getOne(new LambdaQueryWrapper<ScheduleItem>()
                        .eq(ScheduleItem::getName, scheduleMap.get(DateUtil.dayOfMonth(start.getTime())+3)).eq(ScheduleItem::getDelFlag,0));
                if (ObjectUtil.isEmpty(scheduleItem)){
                    errorList.add(DateUtil.format(start.getTime(),"dd")+"号存在系统中未包含的班次名称");
                }

                start.add(Calendar.DAY_OF_YEAR, 1);
            }
            String error = null;
            if (CollUtil.isNotEmpty(errorList)) {
                error = StrUtil.join(";", errorList);
            }
            lm.put("mistake", error);
            listMap.add(lm);
        }
        errorMap.put("maplist", listMap);

        //如果有错误则生成错误报告
        for (Map<String, Object> map : listMap) {
            Object mistake = map.get("mistake");
            if (ObjectUtil.isNotNull(mistake)) {
                Workbook workbook = ExcelExportUtil.exportExcel(exportParams, errorMap);
                List<ScheduleItem> scheduleItems = scheduleItemService.getBaseMapper().selectList(new LambdaQueryWrapper<ScheduleItem>().eq(ScheduleItem::getDelFlag, 0));
                String[] names = scheduleItems.stream().map(ScheduleItem::getName).toArray(String[]::new);
                for (int i = 0; i <31 ; i++) {
                    ExcelSelectListUtil.selectList(workbook, i+3, i+3, names);
                }
                String fileName = "导入失败错误报告";
                try {
                    response.setHeader("Content-Disposition",
                            "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
                    response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                    BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
                    workbook.write(bufferedOutPut);
                    bufferedOutPut.flush();
                    bufferedOutPut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    /**excel 下拉框*/
    public static final class ExcelSelectListUtil {
        /**
         * firstRow 開始行號 根据此项目，默认为3(下标0开始)
         * lastRow  根据此项目，默认为最大65535
         * firstCol 区域中第一个单元格的列号 (下标0开始)
         * lastCol 区域中最后一个单元格的列号
         * strings 下拉内容
         * */
        public static void selectList(Workbook workbook,int firstCol,int lastCol,String[] strings ){
            Sheet sheet = workbook.getSheetAt(0);
            //  生成下拉列表
            //  只对(x，x)单元格有效
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
            //  生成下拉框内容
            XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(strings);
            //HSSFDataValidation dataValidation = new HSSFDataValidation(cellRangeAddressList, dvConstraint);
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
            DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
            //  对sheet页生效
            sheet.addValidationData(validation);
        }
    }

}
