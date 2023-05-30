package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.param.SysFileInfoParam;
import com.aiurt.modules.sysfile.vo.TypeNameVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * @Description: 文件信息表
 * @Author: zwl
 * @Date: 2023-04-12
 * @Version: V1.0
 */
public interface ISysFileInfoService extends IService<SysFileInfo> {
    /**
     * 添加下载记录
     *
     * @param sysFileInfo 待添加的下载记录信息
     * @return 添加后的下载记录信息
     */
    SysFileInfo addDownload(SysFileInfo sysFileInfo);
    /**
     * 分页查询下载记录列表
     *
     * @param page             分页参数
     * @param sysFileInfoParam 查询条件参数
     * @return 下载记录分页列表
     */
    Page<SysFileInfo> queryPageDownloadList(Page<SysFileInfo> page, SysFileInfoParam sysFileInfoParam);
    /**
     * 导出下载报告列表
     *
     * @param fileId 文件ID，指定要导出下载的报告列表的文件的唯一标识符
     * @return ModelAndView对象，用于渲染导出下载报告列表的视图
     */
    ModelAndView reportExportDownloadList(Long fileId);


}
