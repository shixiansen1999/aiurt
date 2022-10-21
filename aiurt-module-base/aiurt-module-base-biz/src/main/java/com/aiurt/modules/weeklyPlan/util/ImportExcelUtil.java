package com.aiurt.modules.weeklyplan.util;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.weeklyplan.entity.BdLine;
import com.aiurt.modules.weeklyplan.entity.BdOperatePlanDeclarationForm;
import com.aiurt.modules.weeklyplan.entity.BdSite;
import com.aiurt.modules.weeklyplan.entity.BdStation;
import com.aiurt.modules.weeklyplan.mapper.BdLineMapper;
import com.aiurt.modules.weeklyplan.mapper.BdOperatePlanDeclarationFormMapper;
import com.aiurt.modules.weeklyplan.mapper.BdSiteMapper;
import com.aiurt.modules.weeklyplan.mapper.BdStationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Lai W.
 *
 * @version 1.0
 */
@Component
public class ImportExcelUtil {

    @Autowired
    private BdStationMapper bdStationMapper;
    @Autowired
    private BdOperatePlanDeclarationFormMapper bdOperatePlanDeclarationFormMapper;
    @Autowired
    private BdLineMapper bdLineMapper;
    @Autowired
    private BdSiteMapper bdSiteMapper;
    @Autowired
    private ISysBaseAPI baseApi;


    SimpleDateFormat fday=new SimpleDateFormat("yyyy-MM-dd");
    private static XSSFWorkbook workbook = null;
    public String engineerId="驻班工程师";
    public String lineId="线路负责人";
    public String dispatchId="生产调度";

    private String[] titleRow = {"序号","作业性质","作业类别","作业单位","作业时间","线路作业范围","供电要求","作业内容","防护措施","施工负责人","配合部门","请点车站","销点车站","辅站","作业人数","大中型器具"};

    public List<BdOperatePlanDeclarationForm> importToExcelOperate(MultipartFile[] importExcel) throws Exception {
        try {
            //查询工区
            List<BdSite> siteList = bdSiteMapper.selectList(new LambdaQueryWrapper<BdSite>());

            int line = -1;
            List<BdLine> lineList = bdLineMapper.selectList(new LambdaQueryWrapper<BdLine>());
            int lineId = 0;

            String lineName = "";
            String newDate = "";
            int startI = 0;
            List<BdOperatePlanDeclarationForm> resultFinal = new ArrayList<>();
            InputStream ips = importExcel[0].getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(ips);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
                XSSFRow row = (XSSFRow) ite.next();
                startI=0;
                BdOperatePlanDeclarationForm newOp = new BdOperatePlanDeclarationForm();
                for (Iterator itet = row.cellIterator(); itet.hasNext(); ) {
                    XSSFCell cell = (XSSFCell) itet.next();
                    String cellText = getValue(cell);
                    System.out.println("当前cell的内容为："+cellText);
                    if(startI==0) {
                        //是否是主标题
                        if(cellText.contains("运营施工及行车计划申报表")) {
                            line = Integer.parseInt(cellText.replace("号线运营施工及行车计划申报表", ""));
                            lineName = cellText.replace("运营施工及行车计划申报表", "");
                            for(int i = 0; i < lineList.size(); i++){
                                if(!ObjectUtil.isEmpty(lineName) && lineName.equals(lineList.get(i).getName())){
                                    lineId = lineList.get(i).getId();
                                }
                            }
                            System.out.println("线路为："+line);
                            break;
                        }
                        //是否是主时间
                        String pattern = "[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日";
                        Pattern r = Pattern.compile(pattern);
                        System.out.println(r.pattern());
                        Matcher m = r.matcher(cellText.trim());
                        //取出日期
                        if(m.lookingAt()) {
//	    	                System.out.println("时间匹配到了。。。。"+cellText);
                            int yearb = cellText.indexOf("年");
                            int monthb = cellText.indexOf("月");
                            int dayb = cellText.indexOf("日");

                            if((monthb-yearb)==2) {
                                newDate = cellText.substring(0, 4)+"-0"+cellText.substring(yearb+1, monthb);
                            }else {
                                newDate = cellText.substring(0, 4)+"-"+cellText.substring(yearb+1, monthb);
                            }
                            if((dayb-monthb)==2) {
                                newDate = newDate+"-0"+cellText.substring(monthb+1, dayb);
                            }else {
                                newDate = newDate+"-"+cellText.substring(monthb+1, dayb);
                            }
                            break;
                        }
                        //序号行去掉
                        if(("序号").equals(cellText)) {
                            break;
                        }
                        //空行去掉
                        if(("").equals(cellText)) {
                            break;
                        }
                    }
                    newOp.setTaskDate(new Timestamp(fday.parse(newDate).getTime()));
                    newOp.setWeekday(this.dateToWeek(newDate));
                    switch (startI) {
                        case 0:
                            break;
                        case 1:
                            newOp.setNature(cellText);
                            break;
                        case 2:
                            newOp.setType(cellText);
                            break;
                        case 3:
                            newOp.setDepartmentId(cellText);
                            break;
                        case 4:
                            //newOp.setTaskTime(formatTaskTime(cellText));
                            newOp.setTaskTime(cellText);
                            break;
                        case 5:
                            newOp.setTaskRange(cellText);
                            break;
                        case 6:
                            newOp.setPowerSupplyRequirement(cellText);
                            break;
                        case 7:
                            newOp.setTaskContent(cellText);
                            break;
                        case 8:
                            newOp.setProtectiveMeasure(cellText);
                            break;
                        case 9:
                            newOp.setChargeStaffId(getChargeStaffId(cellText));
                            break;
                        case 10:
                            newOp.setCoordinationDepartmentId(cellText);
                            break;
                        case 11:
                            newOp.setFirstStationId(getStationIdByLineId(cellText,lineId));
                            break;
                        case 12:
                            newOp.setSecondStationId(getStationIdByLineId(cellText,lineId));
                            break;
                        case 13:
                            //辅站
                            cellText = removeBlank(cellText);
                            if(!("").equals(cellText)&&!("无").equals(cellText)) {
                                newOp.setAssistStationIds(getStationIds(cellText,line));
                                newOp.setAssistStationManagerIds(getStaffIds(cellText));
                            }
                            break;
                        case 14:
                            if(!ObjectUtil.isEmpty(cellText)){
                                newOp.setTaskStaffNum((int)Double.parseDouble(cellText));
                            }
                            break;
                        case 15:
                            newOp.setLargeAppliances(cellText);
                            break;
                        case 16:
                            if(!ObjectUtil.isEmpty(cellText)){
                                String finalCellText = cellText;
                                Optional<BdSite> siteOptional = siteList.stream().filter(s -> finalCellText.equals(s.getName())).findFirst();
                                if(siteOptional.isPresent()){
                                    BdSite bdSite = siteOptional.get();
                                    newOp.setSiteId(bdSite.getId());
                                }
                            }

                            resultFinal.add(newOp);
                            break;
                        default:
                    }
                    startI++;
                }
//                if(row.getCell(0).getCellType()== CellType.STRING)
//                    resultFinal.add(newOp);
            }
            return resultFinal;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * taskTime 字段特殊处理
     * @param taskTime
     * @return
     */
    public String formatTaskTime(String taskTime){
        try {
            //字符串截取
            List<String> timeList = Arrays.asList(taskTime.split(","));
            Date datea = DateUtil.parse(timeList.get(0), "yyyy-MM-dd HH:mm");
            Date dateb = DateUtil.parse(timeList.get(1), "yyyy-MM-dd HH:mm");
            //判断是否是次日
            if(DateUtil.year(datea) == DateUtil.year(dateb) &&
                    DateUtil.month(datea) == DateUtil.month(dateb) &&
                    DateUtil.dayOfMonth(datea) == DateUtil.dayOfMonth(dateb)){
                //当日
                return DateUtil.format(datea, "HH:mm") + " - " + DateUtil.format(dateb, "HH:mm");
            }else{
                //次日
                return DateUtil.format(datea, "HH:mm") + " -次日 " + DateUtil.format(dateb, "HH:mm");
            }
        }catch (Exception e){
            return "";
        }
    }

    /**
     * 获取星期
     * @param datetime
     * @return
     */
    public int dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = f.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return w;
    }

    /**
     * 根据站点名字、线路 查站点id
     * @param cellText
     * @return
     */
    private String getStationIdByLineId(String cellText, int line) {
        BdStation b = new BdStation();
        QueryWrapper<BdStation> queryWrapper = QueryGenerator.initQueryWrapper(b, new HashMap<>(32));
        queryWrapper.eq("name",cellText);
        queryWrapper.eq("line_id", line);
        List<BdStation> bdStations = bdStationMapper.selectList(queryWrapper);
        if(bdStations != null && bdStations.size()>0){
            return bdStations.get(0).getId();
        } else {
            return  "";
        }
    }

    /**
     * 根据站点名字查id
     * @param cellText
     * @return
     */
    private String getStationId(String cellText, int line) {
        BdStation b = new BdStation();
        QueryWrapper<BdStation> queryWrapper = QueryGenerator.initQueryWrapper(b, new HashMap<>(32));
        queryWrapper.eq("name",cellText);
        List<BdStation> bdStations = bdStationMapper.selectList(queryWrapper);
        if(bdStations != null && bdStations.size()>0){
            return bdStations.get(0).getId();
        } else {
            return  "";
        }
    }

    /**
     * 根据人员名字查id
     * @param cellText
     * @return
     */
    private String getChargeStaffId(String cellText) {
        return bdOperatePlanDeclarationFormMapper.queryStaffIdByName(cellText);
    }


    public String getWrongMessage(String engineerStaffId,String lineStaffId,String dispatchStaffId,MultipartFile[] importExcel) throws Exception {
        try {
            int line = 0;
            String newDate = "";
            int startI = 0;
            InputStream ips = importExcel[0].getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(ips);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
                XSSFRow row = (XSSFRow) ite.next();
                startI=0;
                String num = "";
                BdOperatePlanDeclarationForm newOp = new BdOperatePlanDeclarationForm();
                for (Iterator itet = row.cellIterator(); itet.hasNext(); ) {
                    XSSFCell cell = (XSSFCell) itet.next();
                    String cellText = getValue(cell);
                    System.out.println("当前cell的内容为："+cellText);
                    if(startI==0) {
                        //是否是主标题
                        if(cellText.contains("运营施工及行车计划申报表")) {
                            line = Integer.parseInt(cellText.replace("号线运营施工及行车计划申报表", ""));
                            System.out.println("线路为为："+line);
                            break;
                        }
                        //是否是主时间
                        String pattern = "[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日";
                        Pattern r = Pattern.compile(pattern);
                        System.out.println(r.pattern());
                        Matcher m = r.matcher(cellText);
                        //取出日期
                        if(m.lookingAt()) {
//	    	                System.out.println("时间匹配到了。。。。"+cellText);
                            int yearb = cellText.indexOf("年");
                            int monthb = cellText.indexOf("月");
                            int dayb = cellText.indexOf("日");

                            if((monthb-yearb)==2) {
                                newDate = cellText.substring(0, 4)+"-0"+cellText.substring(yearb+1, monthb);
                            }else {
                                newDate = cellText.substring(0, 4)+"-"+cellText.substring(yearb+1, monthb);
                            }
                            if((dayb-monthb)==2) {
                                newDate = newDate+"-0"+cellText.substring(monthb+1, dayb);
                            }else {
                                newDate = newDate+"-"+cellText.substring(monthb+1, dayb);
                            }
                            break;
                        }
                        //序号行去掉
                        if(("序号").equals(cellText)) {
                            break;
                        }
                        //空行去掉
                        if(("").equals(cellText)) {
                            break;
                        }
                    }
                    //
                    System.out.println(newDate);
                    newOp.setTaskDate(new Timestamp(fday.parse(newDate).getTime()));
                    switch (startI) {
                        case 0:
                            num = cellText;
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            break;
                        case 9:
                            newOp.setChargeStaffId(getChargeStaffId(cellText));
                            if(getChargeStaffId(cellText) == null || ("0").equals(getChargeStaffId(cellText))){
                                return newDate+"的序号"+num+"的施工负责人解析出错";
                            }
                            break;
                        case 10:
                            newOp.setCoordinationDepartmentId(cellText);
                            break;
                        case 11:
                            newOp.setFirstStationId(getStationId(cellText,line));
                            if(getStationId(cellText,line) == null || ("-1").equals(getStationId(cellText,line))){
                                return newDate+"的序号"+num+"的请点车站解析出错";
                            }
                            break;
                        case 12:
                            newOp.setSecondStationId(getStationId(cellText,line));
                            if(getStationId(cellText,line) == null || ("-1").equals(getStationId(cellText,line))){
                                return newDate+"的序号"+num+"的销点车站解析出错";
                            }
                            break;
                        case 13:
                            //辅站
                            cellText = removeBlank(cellText);
                            if(!("").equals(cellText)&&!("无").equals(cellText)) {
                                newOp.setAssistStationIds(getStationIds(cellText,line));
                                if(("解析辅站失败").equals(getStationIds(cellText,line))) {
                                    return newDate+"的序号"+num+"的辅站解析出错";
                                }
                                newOp.setAssistStationManagerIds(getStaffIds(cellText));
                                if(("解析辅站负责人失败").equals(getStaffIds(cellText))) {
                                    return newDate+"的序号"+num+"的辅站负责人解析出错";
                                }
                            }
                            break;
                        case 14:
                            try{
                                int taskNum = (int)Double.parseDouble(cellText);
                            }catch(Exception e) {
                                return newDate+"的序号"+num+"的作业人数解析出错";
                            }
                            break;
                        case 15:
                            break;
                        //匹配字目
                        default:
                    }
                    startI++;
                }
            }
            return "";
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public String getStationIds(String cellText,Integer line) {
        if(cellText.equals("")||("无").equals(cellText)){
            return "";
        }
        String result = "";
        String[] sourceStrArray = cellText.split(",");
        for (int i = 0; i < sourceStrArray.length; i++) {
            int separate  = sourceStrArray[i].indexOf(":");
            if(separate<0) {
                separate  = sourceStrArray[i].indexOf("：");
            }
            String station = getStationId(sourceStrArray[i].substring(0, separate),line);
            if(station == null || ("-1").equals(station)) {
                return "解析辅站失败";
            }
            if(i==0) {
                result = station+"";
            }else {
                result = result+","+station;
            }
        }
        return result;
    }

    public String getStaffIds(String cellText) {
        String result = "";
        if(("").equals(cellText)||("无").equals(cellText)){
            return "";
        }
        String[] sourceStrArray;
        int spt = cellText.indexOf(",");
        if(spt<0) {
            sourceStrArray = cellText.split("，");
        }else {
            sourceStrArray = cellText.split(",");
        }
        for (int i = 0; i < sourceStrArray.length; i++) {
            int separate  = sourceStrArray[i].indexOf(":");
            if(separate<0) {
                separate  = sourceStrArray[i].indexOf("：");
            }
            String staff = getChargeStaffId(sourceStrArray[i].substring(separate+1,sourceStrArray[i].length()));
            if(staff == null) {
                return "解析辅站负责人失败";
            }
            if(i==0) {
                result = staff+"";
            }else {
                result = result+","+staff;
            }
        }
        return result;
    }





    public String removeBlank(String textIn) {
        textIn = textIn.replace(" ","");
        textIn = textIn.replace("\\n","");
        return textIn;
    }

//    public String getChargeStaffAndTelephone(int chargeStaffId) {
//        String result = "";
//        HtStaffEntity staff = staffDao.getStaff(chargeStaffId);
//        result = staff.getName()+"("+staff.getPhone()+")";
//        return result;
//    }
//
//    public String getChargeStaffId(String staffNameAndTelephone) {
//        int result = 0;
//        staffNameAndTelephone = staffNameAndTelephone.replace(" ","");
//        staffNameAndTelephone = staffNameAndTelephone.replace("\\n","");
//        int first = staffNameAndTelephone.indexOf("(");
//        if(first<0) {
//            first = staffNameAndTelephone.indexOf("（");
//        }
//        int second = staffNameAndTelephone.indexOf(")");
//        if(second<0) {
//            second = staffNameAndTelephone.indexOf("）");
//        }
//        String staffName = staffNameAndTelephone.substring(0, first);
//        String telephone = staffNameAndTelephone.substring(first+1,second);
//        if(telephone.equals("")) {
//            String hqlstaff = "select s from HtStaffEntity s where s.name=? and s.phone is null";
//            Query querystaff= sessionFactory.getCurrentSession().createQuery(hqlstaff);
//            querystaff.setParameter(0, staffName);
//            List<HtStaffEntity> staff = querystaff.list();
//            if(staff.size()==1) {
//                result = staff.get(0).getId();
//            }
//        }else {
//            String hqlstaff = "select s from HtStaffEntity s where s.name=? and s.phone=?";
//            Query querystaff= sessionFactory.getCurrentSession().createQuery(hqlstaff);
//            querystaff.setParameter(0, staffName);
//            querystaff.setParameter(1, telephone);
//            List<HtStaffEntity> staff = querystaff.list();
//            if(staff.size()==1) {
//                result = staff.get(0).getId();
//            }
//        }
//        return result;
//    }
//
//
//    private String getStationName(int stationId) {
//        String hqls = "select s from HtStationEntity s where s.id=?";
//        Query query = sessionFactory.getCurrentSession().createQuery(hqls);
//        query.setParameter(0,stationId);
//        HtStationEntity result = (HtStationEntity)query.uniqueResult();
//        return result.getName();
//    }
//
//    private String getStationId(String stationName, Integer line) {
//        String hqls = "select s from HtStationEntity s where s.name=? and s.lineId=?";
//        Query query = sessionFactory.getCurrentSession().createQuery(hqls);
//        query.setParameter(0,stationName);
//        query.setParameter(1,line);
//        HtStationEntity result = (HtStationEntity)query.uniqueResult();
//        if(result!=null) {
//            return result.getId();
//        }else {
//            return 0;
//        }
//    }


    private String getValue(XSSFCell cell){
//        if(cell.getCellType()==CellType.BOOLEAN){
//            return String.valueOf(cell.getBooleanCellValue());
//        }else if(cell.getCellType()==CellType.NUMERIC){
//            return String.valueOf(cell.getNumericCellValue());
//        }
        return String.valueOf(cell.getStringCellValue());
    }


}
