package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.result.OutsourcingPersonnelResult;
import com.aiurt.boot.modules.fault.entity.OutsourcingPersonnel;
import com.aiurt.boot.modules.fault.param.OutsourcingPersonnelParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date: 2021-09-18
 * @Version: V1.0
 */
public interface IOutsourcingPersonnelService extends IService<OutsourcingPersonnel> {

    /**
     *
     * 新增委外人员
     * @param personnel
     * @param req
     * @return
     */
    Result add(OutsourcingPersonnel personnel, HttpServletRequest req);

    /**
     * 查询委外人员
     *
     * @param page
     * @param param
     * @return
     */
    IPage<OutsourcingPersonnelResult> pageList(IPage<OutsourcingPersonnelResult> page, OutsourcingPersonnelParam param);

    /**
     * 委外人员导出
     *
     * @param param
     * @return
     */
    List<OutsourcingPersonnelResult> exportXls(OutsourcingPersonnelParam param);

    /**
     * excel导入
     *
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);

}
