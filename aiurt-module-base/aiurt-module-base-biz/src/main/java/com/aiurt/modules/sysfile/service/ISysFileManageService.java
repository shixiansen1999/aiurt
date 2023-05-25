package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.param.SysFileParam;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.SysFileVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @Description: 文档接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface ISysFileManageService extends IService<SysFile> {
    /**
     * 获取文档分页列表查询
     *
     * @param page    分页对象，用于返回查询结果
     * @param sysFile SysFileWebParam对象，包含查询条件
     * @return 分页查询结果，包含SysFileVO对象列表
     */
    Page<SysFileManageVO> getFilePageList(Page<SysFileManageVO> page, SysFileWebParam sysFile);

    /**
     * 添加文档
     *
     * @param files 待添加的文件信息集合
     * @return 添加结果，表示文件添加成功与否的整数值。大于0表示成功，等于0表示失败。
     */
    Result<SysFile> addFile(List<SysFileParam> files);

    /**
     * 编辑文档
     *
     * @param sysFileParam SysFileParam对象，待添加的文件信息
     * @return 编辑结果，表示文件编辑成功与否的整数值。大于0表示成功，等于0表示失败。
     */
    void editFile(SysFileParam sysFileParam);

    /**
     * 删除文件
     *
     * @param id 文件ID，待删除的文件的唯一标识符
     * @return 删除结果，表示文件删除成功与否的通用结果对象
     */
    int removeById(String id);

    /**
     * 通过id查询
     *
     * @param id 文件ID，待查询的文件的唯一标识符
     * @return 查询文件结果
     */
    SysFileDetailVO queryById(String id);



    /**
     * 增加下载次数
     *
     * @param id 文件id
     * @return 增加计数是否成功的布尔值，成功返回true，否则返回false
     */
    boolean addCount(Long id);

    /**
     * 添加下载记录
     *
     * @param sysFileInfo 待添加的下载记录信息
     * @return 添加后的下载记录信息
     */
    SysFileInfo addDownload(SysFileInfo sysFileInfo);
}
