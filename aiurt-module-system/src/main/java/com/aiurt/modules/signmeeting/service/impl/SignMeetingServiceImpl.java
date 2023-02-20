package com.aiurt.modules.signmeeting.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.aiurt.modules.signmeeting.entity.SignMeeting;
import com.aiurt.modules.signmeeting.entity.Conferee;
import com.aiurt.modules.signmeeting.mapper.ConfereeMapper;
import com.aiurt.modules.signmeeting.mapper.SignMeetingMapper;
import com.aiurt.modules.signmeeting.service.ISignMeetingService;
import liquibase.pro.packaged.S;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: 会议签到
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Service
public class SignMeetingServiceImpl extends ServiceImpl<SignMeetingMapper, SignMeeting> implements ISignMeetingService {

	@Autowired
	private SignMeetingMapper signMeetingMapper;
	@Autowired
	private ConfereeMapper confereeMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(SignMeeting signMeeting, List<Conferee> confereeList) {
		signMeetingMapper.insert(signMeeting);
		if(confereeList!=null && confereeList.size()>0) {
			for(Conferee entity:confereeList) {
				//外键设置
				entity.setMeetingId(signMeeting.getId());
				confereeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(SignMeeting signMeeting,List<Conferee> confereeList) {
		signMeetingMapper.updateById(signMeeting);
		
		//1.先删除子表数据
		confereeMapper.deleteByMainId(signMeeting.getId());
		
		//2.子表数据重新插入
		if(confereeList!=null && confereeList.size()>0) {
			for(Conferee entity:confereeList) {
				//外键设置
				entity.setMeetingId(signMeeting.getId());
				confereeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		confereeMapper.deleteByMainId(id);
		signMeetingMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			confereeMapper.deleteByMainId(id.toString());
			signMeetingMapper.deleteById(id);
		}
	}

    @Override
    public void exportOneXls(String id, HttpServletResponse response) {
		// 根据签到表id获取数据
		SignMeeting signMeetingEntity = signMeetingMapper.selectById(id);
		List<Conferee> conferees = confereeMapper.selectByMainId(id);

		String path = "templates/meeting.xlsx";
		TemplateExportParams params = new TemplateExportParams(path, true);

		HashMap<String, Object> map = new HashMap<>();
		map.put("meetingTime", DateFormatUtils.format(signMeetingEntity.getMeetingTime(), "yyyy-MM-dd"));
		map.put("place", signMeetingEntity.getPlace());
		map.put("content", signMeetingEntity.getContent());
		map.put("attendance", signMeetingEntity.getAttendance());
		ArrayList<Map<String, String>> mapList = new ArrayList<>();

		if (conferees.size() > 0) {
			// 设置展示参会人员的序号和name
			for (int i = 0; i < conferees.size(); i++) {
				HashMap<String, String> map1 = new HashMap<>();
				map1.put("id", i + 1 + "");
				map1.put("name", conferees.get(i).getName());
				mapList.add(map1);
			}
		}
		map.put("mapList", mapList);

		Workbook workbook = ExcelExportUtil.exportExcel(params, map);
		// System.out.println("########"+workbook);
		String fileName = "会议签到表.xlsx";
		try {
			//response.setHeader("content-Type", "application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			workbook.write(bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }


}
