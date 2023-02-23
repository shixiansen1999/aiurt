package com.aiurt.modules.signmeeting.service;

import com.aiurt.modules.signmeeting.entity.Conferee;
import com.aiurt.modules.signmeeting.entity.SignMeeting;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 会议签到
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface ISignMeetingService extends IService<SignMeeting> {

	/**
	 * 添加一对多
	 *
	 * @param signMeeting
	 * @param confereeList
	 */
	public void saveMain(SignMeeting signMeeting,List<Conferee> confereeList) ;
	
	/**
	 * 修改一对多
	 *
   * @param signMeeting
   * @param confereeList
	 */
	public void updateMain(SignMeeting signMeeting,List<Conferee> confereeList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 导出一个内容
	 *
	 * @param id
	 * @param response
	 */
    public void exportOneXls(String id, HttpServletResponse response);
}
