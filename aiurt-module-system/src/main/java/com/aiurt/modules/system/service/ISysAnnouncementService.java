package com.aiurt.modules.system.service;

import com.aiurt.modules.system.dto.SysMessageInfoDTO;
import com.aiurt.modules.system.dto.SysMessageTypeDTO;
import com.aiurt.modules.system.entity.SysAnnouncement;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysAnnouncementService extends IService<SysAnnouncement> {

    /**
     * 保存系统通告
     * @param sysAnnouncement
     */
	public void saveAnnouncement(SysAnnouncement sysAnnouncement);

    /**
     * 修改系统通告
     * @param sysAnnouncement
     * @return
     */
	public boolean upDateAnnouncement(SysAnnouncement sysAnnouncement);

    /**
     * 保存系统通告
     * @param title 标题
     * @param msgContent 信息内容
     */
	public void saveSysAnnouncement(String title, String msgContent);

    /**
     * 分页查询系统通告
     * @param page 当前页数
     * @param userId 用户id
     * @param msgCategory 消息类型
     * @return Page<SysAnnouncement>
     */
	public Page<SysAnnouncement> querySysCementPageByUserId(Page<SysAnnouncement> page, String userId, List<String> msgCategory);

    /**
     * 消息中心类型统计
     * @return
     */
    public List<SysMessageTypeDTO> queryMessageType();

    /**
     * 消息中心详情
     * @param messageFlag
     * @param todoType
     * @param keyword
     * @param busType
     * @return
     */
    public IPage<SysMessageInfoDTO> queryMessageInfo(Page<SysMessageInfoDTO> page ,String messageFlag, String todoType, String keyword, String busType,String msgCategory);

    public IPage<String> queryPageSize(Page<SysMessageInfoDTO> page ,String messageFlag, String todoType);

}
