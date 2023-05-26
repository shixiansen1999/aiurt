package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.vo.SysFolderDetailVO;
import com.aiurt.modules.sysfile.vo.SysFolderFilePermissionVO;
import com.aiurt.modules.sysfile.vo.SysFolderTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 文件夹接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface ISysFolderService extends IService<SysFileType> {

    /**
     * 添加文件夹
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要添加的系统文件夹参数信息
     */
    void addFolder(HttpServletRequest req, SysFolderParam param);

    /**
     * 查询文件夹树形结构列表
     *
     * @param name     文件夹名称，用于筛选符合名称的文件夹
     * @param parentId 文件夹父级id，用于筛选符合id的文件夹
     * @return 查询结果，包含SysFolderTreeVO对象列表
     */
    List<SysFolderTreeVO> queryFolderTree(String name, Long parentId);

    /**
     * 编辑文件夹
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要编辑的系统文件夹参数信息
     * @return Result对象，表示编辑操作的结果
     */
    void edit(HttpServletRequest req, SysFolderParam param);

    /**
     * 获取系统文件夹详情
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param id  系统文件夹的ID
     * @return SysFolderDetailVO对象，表示系统文件夹的详情信息
     */
    SysFolderDetailVO detail(HttpServletRequest req, Long id);

    /**
     * 删除系统文件夹
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param ids 系统文件夹ID列表
     */
    void deleteFolder(HttpServletRequest req, List<Long> ids);

    /**
     * 获取文件夹的权限详情列表
     *
     * @param sysFolderFilePermissions 文件夹文件权限列表
     * @return 权限详情列表
     */
    List<SysFolderFilePermissionVO> getPermissionDetails(List<SysFolderFilePermission> sysFolderFilePermissions);

    /**
     * 构建数据
     */
    void buildData();

}

