package com.aiurt.modules.weeklyplan.service;

import com.aiurt.modules.weeklyplan.entity.BdSite;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 工区表，存储工区包含工作场所及对应工班信息
 * @Author: wgp
 * @Date:   2021-03-31
 * @Version: V1.0
 */
public interface IBdSiteService extends IService<BdSite> {

    /**
     * 通过lineId、deptId=1查询班组
     *
     * @return
     */
    /*
    List<BdTeam> getWithoutSite();
    // 1、先查询工区是否有关联的人员
    boolean isAssociateUser(String id);
    //根据id集合查询选择工区集合
    List<BdSite> querySiteName(List strings);
    //通过专业id查询工区
    List<BdSite> queryBydeptId(String deptId);
    //根据班组id查询班组下的用户
    List<TeamStaffDTO> queryByteamId(String teamId,String realname);
    //根据班组id查询班组下的用户（不模糊匹配姓名）
    List<TeamStaffDTO> queryByteamId(String teamId);
    //查询所有不包括账户被冻结用户
    List<TeamStaffDTO> queryAllStaff(String realname);
    //查询人员工区
    List<UserSiteDTO> queryStaffSite(String siteId, String staffName);
    //查询人员工区
    List<UserSiteDTO> querySiteStaffTeam(String siteCode, String siteId, String staffName);
    //获取当前用户管辖班组下工区
    IPage<BdSite> querySiteByTeam(Page<BdSite> page);
    //获取站点下的工区
    IPage<BdSite> querySite(Page<BdSite> page,String code);
    //添加日志中工班负责人的下拉数据
    List<TeamStaffDTO> queryByProduction(String realname);
    //查询系统所有的生产调度
    List<TeamStaffDTO> getAllPro();
     */
    //获取当前用户管辖班组下工区

    /**
     * 获取当前用户管辖班组下工区
     * @param page
     * @return
     */
    IPage<BdSite> querySiteByTeam(Page<BdSite> page);
}
