package com.aiurt.modules.faultexternallinestarel.service;

import com.aiurt.modules.faultexternallinestarel.entity.FaultExternalLineStaRel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: fault_external_line_sta_rel
 * @Author: aiurt
 * @Date:   2023-06-13
 * @Version: V1.0
 */
public interface IFaultExternalLineStaRelService extends IService<FaultExternalLineStaRel> {
    /**
     * 调度子系统-添加
     * @param faultExternalLineStaRel 添加参数
     */
    void add(FaultExternalLineStaRel faultExternalLineStaRel);

    /**
     * 调度子系统-编辑
     * @param faultExternalLineStaRel 编辑参数
     */
    void edit(FaultExternalLineStaRel faultExternalLineStaRel);

    /**
     * 调度子系统-列表查询
     * @param page 分页参数
     * @param faultExternalLineStaRel 查询参数
     * @return IPage<FaultExternalLineStaRel> pageList
     */
    IPage<FaultExternalLineStaRel> pageList(Page<FaultExternalLineStaRel> page, FaultExternalLineStaRel faultExternalLineStaRel);

    /**
     * 调度子系统-导出
     * @param faultExternalLineStaRel 查询参数
     * @return 导出列表
     */
    List<FaultExternalLineStaRel> getList(FaultExternalLineStaRel faultExternalLineStaRel);

    /**
     * 调度子系统-导入
     * @param request 请求参数
     * @param response 响应参数
     * @return 结果集
     * @throws Exception 异常
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
