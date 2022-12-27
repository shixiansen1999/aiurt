package com.aiurt.modules.workticket.mapper;

import com.aiurt.modules.workticket.dto.WorkTicketReqDTO;
import com.aiurt.modules.workticket.dto.WorkTicketResDTO;
import com.aiurt.modules.workticket.entity.BdWorkTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: bd_work_ticket
 * @Author: aiurt
 * @Date:   2022-10-08
 * @Version: V1.0
 */
public interface BdWorkTicketMapper extends BaseMapper<BdWorkTicket> {

    /**
     * 分页查询工作票任务列表
     * @param pageList
     * @param userName
     * @param bdWorkTicket
     * @return
     */
    List<BdWorkTicket> queryPageList(@Param("pageList") Page<BdWorkTicket> pageList, @Param("userName") String userName, @Param("bdWorkTicket") BdWorkTicket bdWorkTicket);

    /**
     * 历史任务
     * @param pageList
     * @param workTicketReqDTO
     * @return
     */
    List<WorkTicketResDTO> historyGet(@Param("pageList") Page<WorkTicketResDTO> pageList, @Param("workTicketReqDTO") WorkTicketReqDTO workTicketReqDTO);

    @Select("select org_id from sys_user where realname =#{realName}")
    List<String> getTeamIdList(String ticketFiller);
}
