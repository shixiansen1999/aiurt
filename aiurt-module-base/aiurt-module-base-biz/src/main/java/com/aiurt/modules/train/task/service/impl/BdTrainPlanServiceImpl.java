package com.aiurt.modules.train.task.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamPaper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.exam.entity.BdExamRecordDetail;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.task.constans.TainPlanConstans;
import com.aiurt.modules.train.task.enmu.QuarterEnmu;
import com.aiurt.modules.train.task.entity.*;
import com.aiurt.modules.train.task.listener.NoModelDataListener;
import com.aiurt.modules.train.task.mapper.*;
import com.aiurt.modules.train.task.service.IBdTrainPlanService;
import com.aiurt.modules.train.task.vo.*;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 年计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Service
public class BdTrainPlanServiceImpl extends ServiceImpl<BdTrainPlanMapper, BdTrainPlan> implements IBdTrainPlanService {

    @Autowired
    private BdTrainTaskSignMapper bdTrainTaskSignMapper;
    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;

    @Autowired
    private BdTrainPlanMapper bdTrainPlanMapper;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private BdExamPaperMapper bdExamPaperMapper;
    @Autowired
    private BdTrainPlanSubMapper bdTrainPlanSubMapper;

    @Autowired
    private BdExamRecordDetailMapper bdExamRecordDetailMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;
    @Autowired
    private BdTrainMakeupExamRecordMapper bdTrainMakeupExamRecordMapper;
    @Autowired
    private BdTrainTaskUserMapper bdTrainTaskUserMapper;

    /**
     * 发布
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void publish(String id) {
        //查询年计划
        BdTrainPlan bdTrainPlan = getBaseMapper().selectById(id);
        if (ObjectUtil.isNull(bdTrainPlan)) {
            throw new JeecgBootException("此年计划不存在，请重新选择！");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 发消息
        BusMessageDTO messageDTO = new BusMessageDTO();
        //设置消息属性
        messageDTO.setStartTime(new Date());
        messageDTO.setEndTime(new Date());
        messageDTO.setPriority("H");
        messageDTO.setCategory("1");
        messageDTO.setTitle("年计划发布");
        messageDTO.setContent("年计划已经发布，请相关人员进行培训计划的制定。");
        messageDTO.setFromUser(sysUser.getUsername());
        messageDTO.setBusType(SysAnnmentTypeEnum.TRAINPLAN.getType());
        //设置接收人（查询部门下面的人员）
        String deptName = bdTrainPlan.getDeptName();
        StringBuilder stringBuilder = new StringBuilder();
        String users = "";
        //根据部门名查询部门id
        String departId = baseMapper.getDepartIdByDeptName(deptName);
        String teamIds = null;
        if (departId == null) {
            //推送给系统管理员
            users = "admin";
        } else {
           /* List<String> list = new ArrayList<>();
            List<SysDepartModel> departsById= baseMapper.getDepartIdsByTeamId(departId);
            getAllDepart(departsById, list);
            //根据组织机构id查询用户
            for (String s : list) {
                //根据组织机构id查询用户
                List<String> userNames = baseMapper.getUserByTeamId(s);
                for (String userName : userNames) {
                    stringBuilder.append(userName).append(",");
                }
            }*/
            //根据组织机构id查询用户
            List<String> userNames = baseMapper.getUserByTeamId(departId);
            for (String userName : userNames) {
                stringBuilder.append(userName).append(",");
            }
            users = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        }
        messageDTO.setToUser(users);
        messageDTO.setToAll(false);
        iSysBaseAPI.sendBusAnnouncement(messageDTO);
        //更改为已发布的状态
        bdTrainPlan.setState(1);
        bdTrainPlan.setPblishTime(new Date());
        baseMapper.updateById(bdTrainPlan);
    }

     void getAllDepart(List<SysDepartModel> departsById, List<String> list) {
        List<String> ids = departsById.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ids)) {
            list.addAll(ids);
            for (String id: ids) {
                List<SysDepartModel> departs= baseMapper.getDepartIdsByTeamId(id);
                getAllDepart(departs, list);
            }
        }
    }

    /**
     * 报表管理
     *
     * @param page
     * @param reportReqVO
     * @return
     */
    @Override
    public IPage<ReportVO> report(Page<ReportVO> page, ReportReqVO reportReqVO) {
        if (reportReqVO.getYear() != null && reportReqVO.getSeason() != null) {
            //季度转换时间区间处理
            String dateString = dateHandler(reportReqVO.getYear(), reportReqVO.getSeason());
            if (StrUtil.isNotBlank(dateString)) {
                String[] split = dateString.split(",");
                if (split.length > 1) {
                    reportReqVO.setTrainStart(split[0]);
                    reportReqVO.setTrainEnd(split[1]);
                }
            }
        }
        List<ReportVO> list = baseMapper.report(page, reportReqVO);
        for (ReportVO reportVO : list) {
            //处理培训部门
//            SysDepartModel sysDepartModel = sysBaseAPI.selectAllById(reportVO.getTaskTeamId());
//            if (ObjectUtil.isNotNull(sysDepartModel)) {
//                reportVO.setSysOrgCode(sysDepartModel.getDepartName());
//            }
            //根据培训任务id查询培训人员
            int trainNum = bdTrainTaskSignMapper.getByTaskId(reportVO.getTrainTaskId());
            //应到人数
            reportVO.setInComeNum(trainNum);
            //根据培训任务id查询已签到的培训人员
            int signNum = bdTrainTaskSignMapper.getSignByTaskId(reportVO.getTrainTaskId());
            //实到人数
            reportVO.setReallyComNum(signNum);
            //培训出勤率
            if (trainNum > 0 && signNum > 0) {
                String accuracy = accuracy(Integer.valueOf(signNum).doubleValue(), Integer.valueOf(trainNum).doubleValue(), 2);
                reportVO.setTrainRate(accuracy);
            }
            //根据train_task_id查询成绩合格且类别为正考的考试记录
            List<BdExamRecord> examRecords = baseMapper.getByTrainTaskId(reportVO.getTrainTaskId());
            //根据train_task_id查询总的考试记录
            List<BdExamRecord> allExamRecords = baseMapper.getAllExamRecord(reportVO.getTrainTaskId());
            //通过人数
            int passNum = examRecords.size();
            //总人数
            int allNum = allExamRecords.size();
            //考试通过率
            if (passNum > 0 && allNum > 0 && passNum < allNum) {
                String accuracy = accuracy(Integer.valueOf(passNum).doubleValue(), Integer.valueOf(allNum).doubleValue(), 2);
                reportVO.setExamPassRate(accuracy);
            }
            //根据子计划id查询年计划
            BdTrainPlan bdTrainPlan = bdTrainPlanMapper.getPlanByPlanSubId(reportVO.getPlanSubId());
            //是否为计划内
            if (ObjectUtil.isNotNull(bdTrainPlan)) {
                if (StrUtil.isNotBlank(bdTrainPlan.getId())) {
                    reportVO.setIsPlan("是");
                }
            }
        }
        return page.setRecords(list);
    }

    /**
     * 培训报表标题获取
     *
     * @param reportReqVO
     * @return
     */
    @Override
    public TitleReportVo reportTitle(ReportReqVO reportReqVO) {
        TitleReportVo vo = new TitleReportVo();
        if (reportReqVO.getSeason() != null) {
            String quarter = QuarterEnmu.getByCode(reportReqVO.getSeason());
            String title = reportReqVO.getYear() + "年" + quarter + TainPlanConstans.TITLE;
            vo.setTitle(title);
        }
        else {
            vo.setTitle(TainPlanConstans.TITLE);
        }
        return vo;
    }

    /**
     * 培训报表导出
     *
     * @param request
     * @return
     */
    @Override
    public ModelAndView reportExport(HttpServletRequest request, ReportReqVO reportReqVO) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //获取标题
        TitleReportVo vo = this.reportTitle(reportReqVO);
        //获取数据
        Page<ReportVO> page = new Page<>(reportReqVO.getPageNo(), reportReqVO.getPageSize());
        IPage<ReportVO> report = this.report(page, reportReqVO);
        List<ReportVO> reportData = report.getRecords();
        HSSFWorkbook workbook = null;
        if (ObjectUtil.isNotNull(vo) && CollUtil.isNotEmpty(reportData)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "培训报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, ReportVO.class);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(vo.getTitle(), "培训报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, reportData);
        }
        return mv;
    }


    /**
     * 培训年计划导入
     *
     * @param request
     * @return
     */
    @Override
    public BdTrainPlan yearPlanImport(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
        MultipartFile file = mr.getFile("file");
        // 判断是否是以。xls 或者.xlsx 结尾
        String type = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
            throw new JeecgBootException("格式错误，仅支持excel文件");
        }
        //读取数据并处理
        NoModelDataListener noModelDataListener = new NoModelDataListener();
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, DemoData.class, noModelDataListener)
                    .extraRead(CellExtraTypeEnum.MERGE)
                    .ignoreEmptyRow(false).autoTrim(true)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        //获取封装对象
        BdTrainPlan bdTrainPlan = noModelDataListener.getBdTrainPlan();
        List<BdTrainPlanSub> list = noModelDataListener.getList();
        Map<String, Integer> collect = list.stream().filter(l -> l.getCourseHours() != null)
                .collect(Collectors.groupingBy(BdTrainPlanSub::getClassifyName, Collectors.summingInt(BdTrainPlanSub::getCourseHours)));
        if (CollUtil.isNotEmpty(collect)) {
            Set<String> strings = collect.keySet();
            for (String string : strings) {
                if ("安全类".equals(string)) {
                    bdTrainPlan.setSafeHours(collect.get(string));
                } else if ("制度类".equals(string)) {
                    bdTrainPlan.setInstitutionHours(collect.get(string));
                } else {
                    bdTrainPlan.setSkillHours(collect.get(string));
                }
            }
        }
        //放入子计划
        bdTrainPlan.setSubList(list);
        return bdTrainPlan;
    }

    /**
     * 培训年计划保存
     *
     * @param bdTrainPlan
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void yearPlanSave(BdTrainPlan bdTrainPlan) {
        //保存年计划
        bdTrainPlan.setState(0);
        //通过部门名称获取部门id
        String departId = baseMapper.getDepartIdByDeptName(bdTrainPlan.getDeptName());
        bdTrainPlan.setDeptId(departId);
        baseMapper.insert(bdTrainPlan);
        String id = bdTrainPlan.getId();
        //保存子计划
        List<BdTrainPlanSub> subList = bdTrainPlan.getSubList();
        if (CollUtil.isNotEmpty(subList)) {
            //查询字典
            List<DictModel> dictModels = sysBaseAPI.queryDictItemsByCode(TainPlanConstans.CLASSIFY_STATE);
            Map<String, String> collect = dictModels.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue));
            for (BdTrainPlanSub bdTrainPlanSub : subList) {
                if ("安全类".equals(bdTrainPlanSub.getClassifyName())) {
                    bdTrainPlanSub.setClassify(Integer.parseInt(collect.get("安全类")));
                } else if ("制度类".equals(bdTrainPlanSub.getClassifyName())) {
                    bdTrainPlanSub.setClassify(Integer.parseInt(collect.get("制度类")));
                } else {
                    bdTrainPlanSub.setClassify(Integer.parseInt(collect.get("技能类")));
                }
                bdTrainPlanSub.setPlanId(id);
                bdTrainPlanSubMapper.insert(bdTrainPlanSub);
            }
        }
    }

    /**
     * 培训复核管理-列表查询
     *
     * @param page
     * @param reCheckReqVo
     * @return
     */
    @Override
    public IPage<ReCheckVO> getReCheckList(Page<ReCheckVO> page, ReCheckReqVo reCheckReqVo) {
        List<ReCheckVO> list = baseMapper.getReCheckList(page, reCheckReqVo);
        list.forEach(l -> {
            //对待复核的记录的是否及格做处理
            //2为待复核
            if (l.getIsRelease() == 2) {
                //将返回的是否及格字段置为空，不显示
                l.setIsPass("");
            }
            if(l.getTaskState().equals("5"))
            {
                l.setReviewDisplayStatus(1);
            }
            else {
                l.setReviewDisplayStatus(0);
            }
            if(l.getIsRelease()==4)
            {
                l.setReviewDisplayStatus(0);
            }
            if(l.getExamClassify().equals("0")&&l.getIsRelease()==2)
            {
                l.setReviewDisplayStatus(1);
            }
        });
        return page.setRecords(list);
    }

    /**
     * 培训复核管理-复核
     *
     * @param id
     * @return
     */
    @Override
    public List<QuestionReCheckVO> reCheck(String id) {
        List<QuestionReCheckVO> voList = new ArrayList<>();
        //查询试卷的简答题详情
        BdExamRecord bdExamRecord = bdExamRecordMapper.selectById(id);
        BdExamPaper bdExamPaper =bdExamPaperMapper.selectById(bdExamRecord.getExamPaperId());
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ShortAnswerVo> list = baseMapper.getShortAnswerQuestion(id,df.format(date),bdExamPaper.getDanumber());
        Collections.reverse(list);
        if (CollUtil.isNotEmpty(list)) {
            for (ShortAnswerVo shortAnswerVo : list) {
                QuestionReCheckVO questionReCheckVO = new QuestionReCheckVO();
                questionReCheckVO.setShortAnswerVos(shortAnswerVo);
                //根据题目id查询题目关联的多媒体
                List<BdQuestionOptionsAtt> mideas = baseMapper.getMidea(shortAnswerVo.getId());
                if (CollUtil.isNotEmpty(mideas)) {
                    questionReCheckVO.setMideas(mideas);
                }
                voList.add(questionReCheckVO);
            }
        }
        return voList;
    }

    /**
     * 培训复核管理-提交审核结果
     *
     * @param reqList
     */
    @Override
    public void submitReCheck(List<ShortQuesReqVo> reqList) {
        for (ShortQuesReqVo shortQuesReqVo : reqList) {
            //获取学生答题详情根据习题表id、考试记录id
            Date date =new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BdExamRecordDetail detail = bdExamRecordDetailMapper.getByQuTypeAndExamRecordId(shortQuesReqVo,df.format(date));
            //查询考试记录
            BdExamRecord bdExamRecord = bdExamRecordMapper.selectById(shortQuesReqVo.getExamRecordId());
            //查询考试试卷
            BdExamPaper bdExamPaper = bdExamPaperMapper.selectById(bdExamRecord.getExamPaperId());
            if (ObjectUtil.isNull(detail)) {
                throw new JeecgBootException("参数错误或当前考试详情不存在");
            }
            if (StrUtil.isBlank(shortQuesReqVo.getScore()) || Integer.parseInt(shortQuesReqVo.getScore()) < 0) {
                throw new JeecgBootException("主观评分不能为空或输入的分数小于0");
            }
            //更新复核后的简答题选项跟分数
            detail.setIsTrue(shortQuesReqVo.getOption());
            if (shortQuesReqVo.getOption() == 1) {
                detail.setScore(bdExamPaper.getDascore());
            } else if (shortQuesReqVo.getOption() == 2) {
                //错误
                detail.setScore(0);
            } else {
                //主观评分，不完全正确，酌情给分
                detail.setScore(Integer.parseInt(shortQuesReqVo.getScore()));
            }
            bdExamRecordDetailMapper.updateById(detail);
            //试卷及格分
           int passScore = bdExamRecordMapper.getPassSorce(bdExamRecord.getId());
            //查询该学生考试关联的所有答题详情
            List<BdExamRecordDetail> list = bdExamRecordDetailMapper.getByExamRecordId(bdExamRecord.getId(),df.format(date),bdExamPaper.getNumber());
              int sum = 0;
            if (CollUtil.isNotEmpty(list)) {
                //统计考生的分数，评定是否及格
                List<BdExamRecordDetail> collect = list.stream().filter(l -> l.getScore() != null).collect(Collectors.toList());
                sum = collect.stream().mapToInt(BdExamRecordDetail::getScore).sum();
            }
            if (sum < passScore) {
                bdExamRecord.setIsPass(0);
            }
            else
            {
                bdExamRecord.setIsPass(1);
            }
            //复核过后不管是否及格，考试记录都将改为待发布
            bdExamRecord.setScore(sum);
            bdExamRecord.setIsRelease("4");
            bdExamRecordMapper.updateById(bdExamRecord);
        }
    }

    /**
     * 培训复核管理-发布
     * @param reCheckVOList
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reCheckPublish(List<ReCheckVO> reCheckVOList) {
        Date date =new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ReCheckVO checkVO : reCheckVOList) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            // 发消息
            BusMessageDTO messageDTO = new BusMessageDTO();
            //设置消息属性

            messageDTO.setStartTime(new Date());
            messageDTO.setEndTime(new Date());
            messageDTO.setPriority("H");
            messageDTO.setCategory("1");
            messageDTO.setTitle("考试结果发布");
            messageDTO.setBusType(SysAnnmentTypeEnum.TRAINRECHECK.getType());
            messageDTO.setToAll(false);
            if (StrUtil.isBlank(checkVO.getUserName()) || StrUtil.isBlank(checkVO.getExamResult())) {
                throw new JeecgBootException("考生姓名不存在或考生考试结果为空");
            }
            messageDTO.setContent(String.format("%s你好，你的考试结果为%s分", checkVO.getExamPersonName(), checkVO.getExamResult()));

            //设置接收人
            messageDTO.setToUser(checkVO.getUserName());
            iSysBaseAPI.sendBusAnnouncement(messageDTO);

            BdExamRecord bdExamRecord = bdExamRecordMapper.selectById(checkVO.getId());
            BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(bdExamRecord.getTrainTaskId());
            //对考试记录是否及格做更新，复核没有及格的放入补考管理当中
            //试卷及格分
            BdExamPaper bdExamPaper = bdExamPaperMapper.selectById(bdExamRecord.getExamPaperId());
            int passScore = bdExamRecordMapper.getPassSorce(bdExamRecord.getId());
            //查询该学生考试关联的所有答题详情
            List<BdExamRecordDetail> list = bdExamRecordDetailMapper.getByExamRecordId(bdExamRecord.getId(),df.format(date),bdExamPaper.getNumber());
            int sum = 0;
            if (CollUtil.isNotEmpty(list)) {
                //统计考生的分数，评定是否及格
                List<BdExamRecordDetail> collect = list.stream().filter(l -> l.getScore() != null).collect(Collectors.toList());
                sum = collect.stream().mapToInt(BdExamRecordDetail::getScore).sum();
            }
            if (sum < passScore && bdTrainTask.getMakeUpState().equals(1))  {
                bdExamRecord.setIsPass(0);
                //复核之后还是不及格，将次考试记录放入补考记录，等待补考
                BdTrainMakeupExamRecord bdTrainMakeupExamRecord = new BdTrainMakeupExamRecord();
                bdTrainMakeupExamRecord.setIsMakeup(0);
                bdTrainMakeupExamRecord.setTrainTaskId(bdExamRecord.getTrainTaskId());
                bdTrainMakeupExamRecord.setExamPaperId(bdExamRecord.getExamPaperId());
                bdTrainMakeupExamRecord.setUserId(bdExamRecord.getUserId());
                bdTrainMakeupExamRecord.setExamClassify(bdExamRecord.getExamClassify());
                bdTrainMakeupExamRecord.setExamId(bdExamRecord.getId());
                //根据考试记录查询培训任务。查询对应的班组id
                String teamId = bdExamRecordMapper.getTeamIdByExamRecordId(bdExamRecord.getId());
                if (StrUtil.isBlank(teamId)) {
                    throw new JeecgBootException("当前任务计划的培训部门不存在！");
                }
                bdTrainMakeupExamRecord.setSysOrgCode(teamId);
                bdTrainMakeupExamRecordMapper.insert(bdTrainMakeupExamRecord);
            }
            //更新考试记录状态为已结束
            bdExamRecord.setIsRelease("3");
            bdExamRecordMapper.updateById(bdExamRecord);

            //判断该任务的学员是否全部发布
            if (bdExamRecord.getExamClassify() == 1) {
                List<BdExamRecord> records = bdExamRecordMapper.getNum(checkVO.getTaskId());
                List<BdExamRecord> collect = records.stream().filter(b -> ExamConstans.RECORD_OVER.equals(b.getIsRelease())).collect(Collectors.toList());
                int num1 = records.size();
                int num2 = collect.size();
                if (num1 == num2) {
                    bdTrainTask.setTaskState(6);
                    bdTrainTaskMapper.updateById(bdTrainTask);
                }
            }
        }
    }
    /**
     * 培训报表管理-部门下拉数据
     *
     * @return
     */
    @Override
    public List<String> getDept() {
        return baseMapper.getDept();
    }

    public static String accuracy(Double num, Double total, Integer scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = (num / total) * 100;
        return df.format(accuracy_num) + "%";
    }

    public static String dateHandler(String year, Integer quarter) {
        String desc = QuarterEnmu.getByCode(quarter);
        //判断是否为闰年
        // boolean isLeapYear = DateUtil.isLeapYear(Integer.parseInt(year));
        switch (Objects.requireNonNull(desc)) {
            case "第一季度":
                return year + "-01-01 00:00:00," + year + "-03-31 00:00:00";
            case "第二季度":
                return year + "-04-01 00:00:00," + year + "-06-30 00:00:00";
            case "第三季度":
                return year + "-07-01 00:00:00," + year + "-09-30 00:00:00";
            case "第四季度":
                return year + "-10-01 00:00:00," + year + "-12-31 00:00:00";
            default:
                return null;
        }
    }
}
