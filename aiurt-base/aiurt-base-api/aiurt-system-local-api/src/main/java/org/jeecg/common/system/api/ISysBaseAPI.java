package org.jeecg.common.system.api;

import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.api.dto.message.*;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.entity.DeviceTypeTable;
import com.aiurt.modules.position.entity.CsStation;
import com.alibaba.fastjson.JSONObject;
import io.swagger.util.Json;
import org.jeecg.common.api.dto.OnlineAuthDTO;
import org.jeecg.common.system.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description 底层共通业务API，提供其他独立模块调用
 * @Author scott
 * @Date 2019-4-20
 * @Version V1.0
 */
public interface ISysBaseAPI extends CommonAPI {


    /**
     * 1发送系统消息
     *
     * @param message 使用构造器赋值参数 如果不设置category(消息类型)则默认为2 发送系统消息
     */
    void sendSysAnnouncement(MessageDTO message);

    /**
     * 2发送消息 附带业务参数
     *
     * @param message 使用构造器赋值参数
     */
    void sendBusAnnouncement(BusMessageDTO message);

    /**
     * 3通过模板发送消息
     *
     * @param message 使用构造器赋值参数
     */
    void sendTemplateAnnouncement(TemplateMessageDTO message);

    /**
     * 4通过模板发送消息 附带业务参数
     *
     * @param message 使用构造器赋值参数
     */
    void sendBusTemplateAnnouncement(BusTemplateMessageDTO message);

    /**
     * 5通过消息中心模板，生成推送内容
     *
     * @param templateDTO 使用构造器赋值参数
     * @return
     */
    String parseTemplateByCode(TemplateDTO templateDTO);


    /**
     * 6根据用户id查询用户信息
     *
     * @param id
     * @return
     */
    LoginUser getUserById(String id);

    /**
     * 7通过用户账号查询角色集合
     *
     * @param username
     * @return
     */
    List<String> getRolesByUsername(String username);

    /**
     * 8通过用户账号查询部门集合
     *
     * @param username
     * @return 部门 id
     */
    List<String> getDepartIdsByUsername(String username);

    /**
     * 9通过用户账号查询部门 name
     *
     * @param username
     * @return 部门 name
     */
    List<String> getDepartNamesByUsername(String username);


    /**
     * 11查询所有的父级字典，按照create_time排序
     *
     * @return List<DictModel> 字典集合
     */
    public List<DictModel> queryAllDict();

    /**
     * 12查询所有分类字典
     *
     * @return
     */
    public List<SysCategoryModel> queryAllSysCategory();


    /**
     * 14查询所有部门 作为字典信息 id -->value,departName -->text
     *
     * @return
     */
    public List<DictModel> queryAllDepartBackDictModel();

    /**
     * 15根据业务类型及业务id修改消息已读
     *
     * @param busType
     * @param busId
     */
    public void updateSysAnnounReadFlag(String busType, String busId);

    /**
     * 16查询表字典 支持过滤数据
     *
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql);

    /**
     * 17查询指定table的 text code 获取字典，包含text和value
     *
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Deprecated
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray);

    /**
     * 18查询所有用户 返回ComboModel
     *
     * @return
     */
    public List<ComboModel> queryAllUserBackCombo();

    /**
     * 19分页查询用户 返回JSONObject
     *
     * @param userIds  多个用户id
     * @param pageNo   当前页数
     * @param pageSize 每页显示条数
     * @return
     */
    public JSONObject queryAllUser(String userIds, Integer pageNo, Integer pageSize);

    /**
     * 20获取所有角色
     *
     * @return
     */
    public List<ComboModel> queryAllRole();

    /**
     * 21获取所有角色 带参
     *
     * @param roleIds 默认选中角色
     * @return
     */
    public List<ComboModel> queryAllRole(String[] roleIds);

    /**
     * 22通过用户账号查询角色Id集合
     *
     * @param username
     * @return
     */
    public List<String> getRoleIdsByUsername(String username);

    /**
     * 23通过部门编号查询部门id
     *
     * @param orgCode
     * @return
     */
    public String getDepartIdsByOrgCode(String orgCode);

    /**
     * 24查询所有部门
     *
     * @return
     */
    public List<SysDepartModel> getAllSysDepart();

    /**
     * 25查找父级部门
     *
     * @param departId
     * @return
     */
    DictModel getParentDepartId(String departId);

    /**
     * 26根据部门Id获取部门负责人
     *
     * @param deptId
     * @return
     */
    public List<String> getDeptHeadByDepId(String deptId);

    /**
     * 27给指定用户发消息
     *
     * @param userIds
     * @param cmd
     */
    public void sendWebSocketMsg(String[] userIds, String cmd);

    /**
     * 28根据id获取所有参与用户
     *
     * @param userIds 多个用户id
     * @return
     */
    public List<LoginUser> queryAllUserByIds(String[] userIds);

    /**
     * 29将会议签到信息推动到预览
     * userIds
     *
     * @param userId
     * @return
     */
    void meetingSignWebsocket(String userId);

    /**
     * 30根据name获取所有参与用户
     *
     * @param userNames 多个用户账户
     * @return
     */
    List<LoginUser> queryUserByNames(String[] userNames);


    /**
     * 31获取用户的角色集合
     *
     * @param username
     * @return
     */
    Set<String> getUserRoleSet(String username);

    /**
     * 32获取用户的权限集合
     *
     * @param username
     * @return
     */
    Set<String> getUserPermissionSet(String username);

    /**
     * 33判断是否有online访问的权限
     *
     * @param onlineAuthDTO
     * @return
     */
    boolean hasOnlineAuth(OnlineAuthDTO onlineAuthDTO);

    /**
     * 34通过部门id获取部门全部信息
     *
     * @param id 部门id
     * @return SysDepartModel对象
     */
    SysDepartModel selectAllById(String id);

    /**
     * 35根据用户id查询用户所属公司下所有用户ids
     *
     * @param userId
     * @return
     */
    List<String> queryDeptUsersByUserId(String userId);

    /**
     * 36根据多个用户账号(逗号分隔)，查询返回多个用户信息
     *
     * @param usernames
     * @return
     */
    List<JSONObject> queryUsersByUsernames(String usernames);

    /**
     * 37根据多个用户ID(逗号分隔)，查询返回多个用户信息
     *
     * @param ids
     * @return
     */
    List<JSONObject> queryUsersByIds(String ids);

    /**
     * 38根据多个部门编码(逗号分隔)，查询返回多个部门信息
     *
     * @param orgCodes
     * @return
     */
    List<JSONObject> queryDepartsByOrgcodes(String orgCodes);

    /**
     * 39根据多个部门id(逗号分隔)，查询返回多个部门信息
     *
     * @param ids
     * @return
     */
    List<JSONObject> queryDepartsByIds(String ids);

    /**
     * 40发送邮件消息
     *
     * @param email
     * @param title
     * @param content
     */
    void sendEmailMsg(String email, String title, String content);

    /**
     * 41 获取公司下级部门和公司下所有用户信息
     *
     * @param orgCode
     * @return List<Map>
     */
    List<Map> getDeptUserByOrgCode(String orgCode);

    /**
     * 查询分类字典翻译
     *
     * @param ids 多个分类字典id
     * @return List<String>
     */
    List<String> loadCategoryDictItem(String ids);

    /**
     * 根据字典code加载字典text
     *
     * @param dictCode 顺序：tableName,text,code
     * @param keys     要查询的key
     * @return
     */
    List<String> loadDictItem(String dictCode, String keys);

    /**
     * 根据字典code查询字典项
     *
     * @param dictCode 顺序：tableName,text,code
     * @param dictCode 要查询的key
     * @return
     */
    List<DictModel> getDictItems(String dictCode);

    /**
     * 根据多个字典code查询多个字典项
     *
     * @param dictCodeList
     * @return key = dictCode ； value=对应的字典项
     */
    Map<String, List<DictModel>> getManyDictItems(List<String> dictCodeList);

    /**
     * 【JSearchSelectTag下拉搜索组件专用接口】
     * 大数据量的字典表 走异步加载  即前端输入内容过滤数据
     *
     * @param dictCode 字典code格式：table,text,code
     * @param keyword  过滤关键字
     * @param pageSize 分页条数
     * @return
     */
    List<DictModel> loadDictItemByKeyword(String dictCode, String keyword, Integer pageSize);

    /**
     * 根据部门编号集合查询对应的人员信息
     *
     * @param deptCodes 部门编码集合
     * @return
     */
    List<LoginUser> getUserByDepIds(List<String> deptCodes);

    /**
     * 根据部门id,查询部门下的人员信息
     *
     * @param deptId 部门编码集合
     * @return
     */
    List<LoginUser> getUserPersonnel(String deptId);

    /**
     * 根据部门list，查询部门list下的人员信息
     * @param deptIds
     * @return
     */
    List<LoginUser> getUseList(List<String> deptIds);


    /**
     * 更新文件业务数据
     *
     * @param id                文件主键即文件路径
     * @param businessId        业务数据
     * @param businessTableName 业务模块
     */
    void updateSysAttachmentBiz(String id, String businessId, String businessTableName);


    /**
     * 批量更新文件业务数据
     *
     * @param idList            文件主键即文件路径
     * @param businessId        业务数据
     * @param businessTableName 业务模块
     */
    void updateSysAttachmentBiz(List<String> idList, String businessId, String businessTableName);

    /**
     * 根据业务id查询附件信息
     *
     * @param businessIdList
     * @return
     */
    List<SysAttachment> querySysAttachmentByBizIdList(List<String> businessIdList);


    /**
     * 根据id查询附件信
     *
     * @param idList
     * @return
     */
    List<SysAttachment> querySysAttachmentByIdList(List<String> idList);

    /**
     * 查询所有站点
     *
     * @return
     */
    public List<CsStation> queryAllStation();

    /**
     * 物资分类列表结构查询（无分页。用于左侧树）
     *
     * @param majorCode
     * @param systemCode
     * @param deviceCode
     * @return
     */
    List<DeviceTypeTable> selectList(String majorCode, String systemCode, String deviceCode);

    /**
     * 根据用户账号 查询用户信息
     *
     * @param username
     * @return
     */
    LoginUser queryUser(String username);

    /**
     * 定时任务
     */
    void saveAndScheduleJob(QuartzJobDTO quartzJobDTO);

    /**
     * 定时任务
     */
    void deleteAndStopJob(QuartzJobDTO quartzJobDTO);

    /**
     * 通过部门编号查询部门信息
     *
     * @param orgCode
     * @return
     */
    SysDepartModel getDepartByOrgCode(String orgCode);

    /**
     * 通过code查询地点名称（这三个中的一个：线路、站点、位置）
     *
     * @param code
     * @return
     */
    public String getPosition(String code);

    /**
     * 通过用户id查询角色名称
     *
     * @param userId
     * @return
     */
    List<String> getRoleNamesById(String userId);

    /**
     * 通过部门编码查询工区信息
     *
     * @param orgCode
     * @return
     */
    List<SiteModel> getSiteByOrgCode(String orgCode);

    /**
     * 通过线路查询对应的站点
     *
     * @param lineCode
     * @return
     */
    List<String> getStationCodeByLineCode(String lineCode);

    /**
     * 根据线路编号集合查询对应的站点编号集合
     */
    List<String> getStationCodeByLineCode(List<String> lineCodes);

    /**
     * 通过线路和专业过滤出班组
     *
     * @param lineCode
     * @return
     */
    List<String> getTeamBylineAndMajor(String lineCode);

    /**
     * 通过线路和专业过滤出班组详细信息
     *
     * @param lineCode
     * @return
     */
    List<SysDepartModel> getTeamBylineAndMajors(String lineCode);

    /**
     * 根据用户id，作为管理负责人条件,获取部门信息
     *
     * @param userId
     * @return
     */
    List<SysDepartModel> getUserSysDepart(String userId);


    /**
     * 根据编码获取专业
     * @param majorCode
     * @return
     */
    JSONObject getCsMajorByCode(String majorCode);

    /**
     * 根据线路编号获取线路名称,线路编号:线路名称
     *
     * @param lineCodes
     * @return
     */
    Map<String, String> getLineNameByCode(List<String> lineCodes);

    /**
     * 根据站点编号获取线路名称,站点编号:站点名称
     *
     * @param stationCodes
     * @return
     */
    Map<String, String> getStationNameByCode(List<String> stationCodes);


    /**
     * 根据用户名或者用户账号查询用户信息
     * @param userNameList
     * @return
     */
    List<LoginUser> getLoginUserList(List<String> userNameList);

    /**
     * 根据专业获取id
     * @param station
     * @return
     */
    JSONObject getCsStationById(String station);

    /**
     * 根据设备编号获取设备名称,设备编号:设备名称
     *
     * @param deviceCodes
     * @return
     */
    Map<String, String> getDeviceNameByCode(List<String> deviceCodes);

    /**
     * 根据线路Id获取线路编号
     *
     * @param lineId
     * @return
     */
    String getLineCodeById(String lineId);
}
