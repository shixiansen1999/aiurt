package com.aiurt.modules.sysfile.mapper;

import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文档表Mapper 接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface SysFileManageMapper extends BaseMapper<SysFile> {

    IPage<SysFile> selectFilePage(Page page, @Param("typeId") List<Long> typeId, @Param("fileName") String fileName);

    /**
     * 根据条件查询文件分页列表
     *
     * @param page             分页对象，用于返回分页结果
     * @param sysFileWebParam  文件查询参数对象，包含查询条件
     * @param currLoginUserId  当前登录用户ID
     * @param currLoginOrgCode 当前登录用户所属组织机构代码
     * @param userNames        用户账号集合
     * @return 文件分页列表
     */
    List<SysFileManageVO> getFilePageList(@Param("page") Page<SysFileManageVO> page, @Param("sysFileWebParam") SysFileWebParam sysFileWebParam, @Param("currLoginUserId") String currLoginUserId, @Param("currLoginOrgCode") String currLoginOrgCode, @Param("userNames") List<String> userNames);
}
