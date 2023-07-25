package com.aiurt.modules.outsourcingpersonnel.service;

import com.aiurt.common.result.OutsourcingPersonnelResult;
import com.aiurt.modules.outsourcingpersonnel.entity.OutsourcingPersonnel;
import com.aiurt.modules.outsourcingpersonnel.param.OutsourcingPersonnelParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author : sbx
 * @Classname : IOutsourcingPersonnelService
 * @Description : 委外人员
 * @Date : 2023/7/24 8:51
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
