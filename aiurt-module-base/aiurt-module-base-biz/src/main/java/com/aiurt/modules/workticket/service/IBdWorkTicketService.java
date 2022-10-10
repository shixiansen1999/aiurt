package com.aiurt.modules.workticket.service;

import com.aiurt.modules.workticket.dto.WorkTicketReqDTO;
import com.aiurt.modules.workticket.dto.WorkTicketResDTO;
import com.aiurt.modules.workticket.entity.BdWorkTicket;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: bd_work_ticket
 * @Author: aiurt
 * @Date:   2022-10-08
 * @Version: V1.0
 */
public interface IBdWorkTicketService extends IService<BdWorkTicket> {

    /**
     * 更新或添加
     * @return
     */
    public String addOrUpdate(BdWorkTicket bdWorkTicket);

    /**
     * 历史任务
     * @param pageList
     * @param workTicketReqDTO
     * @return
     */
    Page<WorkTicketResDTO> historyGet(Page<WorkTicketResDTO> pageList, WorkTicketReqDTO workTicketReqDTO);

    /**
     * 待办任务查询
     * @param pageList
     * @param username
     * @return
     */
    Page<BdWorkTicket> queryPageList(Page<BdWorkTicket> pageList, String username);

    /**
     * 更新状态
     * @param businessKey 业务数据
     * @param states 状态值
     */
    void updateState(String businessKey, Integer states);
}
