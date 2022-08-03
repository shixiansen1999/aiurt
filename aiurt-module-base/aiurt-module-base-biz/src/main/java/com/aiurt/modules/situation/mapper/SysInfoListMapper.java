package com.aiurt.modules.situation.mapper;

import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.aiurt.modules.situation.entity.SysAnnouncementSend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: bd_info_list
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
public interface SysInfoListMapper extends BaseMapper<SysAnnouncement> {
    /**
     * 通过id查看该通告的人是否已读
     *
     * @param id
     * @param page
     * @param readFlag
     * @return
     */
    List<SysAnnouncementSend> getByAnntId(@Param("page") Page<SysAnnouncementSend> page ,@Param("id")String id,@Param("readFlag")String readFlag);

    /**
     * 我的通知分页列表查询
     *
     * @param id
     * @param page
     * @return
     */
    List<SysAnnouncement> getMyInfo(@Param("page") Page<SysAnnouncement> page,@Param("id")String id);

    void updateReadFlag(@Param("id")String idm, @Param("readTime") Date readTime);
}
