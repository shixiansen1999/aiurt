package org.jeecg.common.system.api;

import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.api.dto.message.*;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.entity.DeviceTypeTable;
import com.aiurt.modules.common.entity.SelectDeviceType;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.position.entity.CsStation;
import com.alibaba.fastjson.JSONObject;
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

    //update-begin---author:taoyan ---date:20220705  for：支持自定义推送类型，邮件、钉钉、企业微信、系统消息-----------
    /**
     * 发送模板消息【新，支持自定义推送类型】
     * @param message
     */
    void sendTemplateMessage(MessageDTO message);

    /**
     * 根据模板编码获取模板内容【新，支持自定义推送类型】
     * @param templateCode
     * @return
     */
    String getTemplateContent(String templateCode);
    //update-begin---author:taoyan ---date:20220705  for：支持自定义推送类型，邮件、钉钉、企业微信、系统消息-----------

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
     * @param busType SysAnnmentTypeEnum
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
     * 20weeklyplan
     *
     * @return
     */
    public List<ComboModel> queryAllRole();

    /**
     * 21weeklyplan 带参
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
     * 23通过部门编号查询部门名称
     *
     * @param orgCode
     * @return
     */
    public String getDepartNameByOrgCode(String orgCode);

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
     * 手机号验证（验证成功返回true,验证失败返回false）
     * @param string
     * @return
     */
    boolean isMobile(String string);


    /**
     * 电话号码(座机)验证（验证成功返回true,验证失败返回false）
     * @param string
     * @return
     */
    boolean isPhone(String string);

    /**
     * 38根据多个部门编码(逗号分隔)，查询返回多个部门信息
     *
     * @param orgCodes
     * @return
     */
    List<JSONObject> queryDepartsByOrgcodes(String orgCodes);

    /**
     * 根据多个部门编码(集合)，查询返回多个部门名称
     * @param orgCodes
     * @return
     */
    List<String> queryOrgNamesByOrgCodes(List<String> orgCodes);

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
    List<DeviceTypeTable> selectList(String majorCode, String systemCode, String deviceCode,String name);

    /**
     * 数据过大 物资分类列表结构查询（无分页。用于左侧树）
     *
     * @param value
     * @return
     */
    List<SelectDeviceType> selectDeviceTypeList(String value);

    /**
     * 根据用户账号 查询用户信息
     *
     * @param username
     * @return
     */
    LoginUser queryUser(String username);

    /**
     * 定时任务
     * @param quartzJobDTO
     */
    void saveAndScheduleJob(QuartzJobDTO quartzJobDTO);

    /**
     * 定时任务
     * @param quartzJobDTO
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
     * @param lineCodes
     * @return
     */
    List<String> getStationCodeByLineCode(List<String> lineCodes);

    /**
     * 根据站点编号查询对应的位置信息(没有位置就返回站点)
     * @param stationCode
     * @return
     */
    CsStation getPositionCodeByStationCode(String stationCode);

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
     * 根据多个编码获取专业名称
     * @param majorCodes
     * @return
     */
    List<String> getCsMajorNamesByCodes(List<String> majorCodes);
    /**
     * 根据专业名称获取专业
     * @param majorName
     * @return
     */
    JSONObject getCsMajorByName(String majorName);
    /**
     * 根据专业code、子系统名称，获取子系统
     * @param systemName
     * @param majorCode
     * @return
     */
    JSONObject getSystemName(String majorCode,String systemName);
    /**
     * 根据子系统code，获取子系统名称
     * @param systemCodes
     * @return
     */
    List<String> getSystemNames(List<String> systemCodes);

    /**
     * 根据专业code、设备类型名称 ，查询设备类型信息
     * @param majorCode
     * @param deviceTypeName
     * @return
     */
    DeviceType getCsMajorByCodeTypeName(String majorCode, String deviceTypeName);

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
     * 根据用户姓名查询用户账号
     * @param realName
     * @return
     */
    String getUserName(String realName);


    /**
     * 根据用户姓名模糊查询用户账号
     * @param realName
     * @return
     */
    List<String> getUserLikeName(String realName);

    /**
     * 根据站点id获取站点信息
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
     * 根据设备code获取设备信息
     * @param code
     * @return
     */
    JSONObject getDeviceByCode(String code);

    /**
     * 根据线路Id获取线路编号
     *
     * @param lineId
     * @return
     */
    String getLineCodeById(String lineId);
    /**
     * 通过链接将图片保存到本地
     *
     * @param bizPath       自定义路径
     * @param remoteFileUrl 图片链接
     * @return
     */
    String remoteUploadLocal(String remoteFileUrl, String bizPath);

    /**
     * 获取对应角色编码的用户
     *
     * @param roleId
     */
    List<SysUserRoleModel> getUserByRoleId(String roleId);

    /**
     * 根据角色编码获取角色id
     * @param roleCode
     * @return
     */
    String getRoleIdByCode(String roleCode);

    /**
     * 获取所有工区信息
     * @return
     */
    List<CsWorkAreaModel> getWorkAreaInfo();

    /**
     * 根据站点名称和线路id获取站点信息
     * 施工计划导入模块使用，其余慎用！
     * @param cellText
     * @param line
     */
    List<CsStation> getStationInfoByNameAndLineId(String cellText, String line);

    /**
     * 根据工区编码获取工区名称
     *
     * @param workAreaCode
     * @return
     */
    String getWorkAreaNameByCode(String workAreaCode);

    /**
     * 获取用户的所属部门及所属部门子部门编码
     *
     * @param
     * @return
     */
    List<SysDepartModel> getUserDepartCodes();

    /**
     * 组织机构和用户联动
     */
    List<SysDeptUserModel> getDeptUserGanged();

    /**
     * 根据组织机构编码获取所有用户，如果编码为空则获取全部
     *
     * @param deptCode
     * @return
     */
    List<LoginUser> getUserByDeptCode(String deptCode);



    /**
     * 查询配置巡检项树
     *
     * @param id
     * @return
     */
    List<PatrolStandardItemsModel> patrolStandardList(String id);

    /**
     * 根据专业、子系统，查询是否有安全事项
     *
     * @param majorCode
     * @param systemCode
     * @return
     */
    boolean isNullSafetyPrecautions(String majorCode, String systemCode, String code, Integer status);

    /**
     * 根据线路编码获取线路id
     *
     * @param lineCode
     * @return
     */
    String getLineIdByCode(String lineCode);

    /**
     * 根据部门名称获取部门信息
     *
     * @param departName
     * @return
     */
    JSONObject getDepartByName(String departName);

    /**
     * 根据部门名称和父id获取部门信息
     *
     * @param departName
     * @return
     */
    JSONObject getDepartByNameAndParentId(String departName, String parentId);

    /**
     * 根据父编码获取所有子部门信息
     *
     * @return
     */
    List<SysDepartModel> getDepartByParentId(String parentId);

    /**
     * 根据部门code,获取该部门下所有子级的code和自己
     *
     * @param orgCode
     * @return
     */
    List<String> sysDepartList(String orgCode);

    /**
     * 根据线路名称获取线路信息
     *
     * @param lineName
     * @return
     */
    JSONObject getLineByName(String lineName);

    /**
     * 根据站点名称获取站点信息
     *
     * @param stationName
     * @return
     */
    JSONObject getStationByName(String stationName);

    /**
     * 根据位置名称，线路站点code获取位置信息（位置名存在重复可能性）
     *
     * @param positionName
     * @param lineCode
     * @param stationCode
     * @return
     */
    JSONObject getPositionByName(String positionName, String lineCode, String stationCode);

    /**
     * 根据用户姓名,工号查询用户信息
     *
     * @param realName
     * @param workNo
     * @return
     */
    List<LoginUser> getUserByRealName(String realName, String workNo);


    /**
     * 获取用户岗位为post的人员
     *
     * @param post
     * @return
     */
    List<LoginUser> getUserByPost(int post);

    /**
     * 根据业务类型及业务id查询消息
     *
     * @param busType 业务类型 SysAnnmentTypeEnum
     * @param busId   业务id
     * @return 消息的id
     */
    public String getSysAnnounByBusTypeAndBusId(String busType, String busId);

    /**
     * 获取SysAttachment的文件地址
     *
     * @param filePath
     * @return
     */
    SysAttachment getFilePath(String filePath);

    /**
     * 根据部门，角色编码查询人员账号
     *
     * @param orgCode  组织机构编码
     * @param roleCode 角色编码
     * @return 人员账号用逗号隔开
     */
    public String getUserNameByOrgCodeAndRoleCode(List<String> orgCode, List<String> roleCode);

    /**
     * 根据站点获取工区信息
     *
     * @param stationCode
     * @return
     */
    List<CsWorkAreaModel> getWorkAreaByCode(String stationCode);

    /**
     * 通过code查询地点信息（这三个中的一个：线路、站点、位置）
     *
     * @param code
     * @return
     */
    JSONObject getPositionMessage(String code);

    /**
     * 通过权限查询用户
     *
     * @return
     */
    List<LoginUser> getAllUsers();

    /**
     * 根据位置编码获取线路站点和位置全名，格式如线路/站点/位置
     *
     * @param positionCode
     * @return
     */
    String getFullNameByPositionCode(String positionCode);

    /**
     * 根据用户的部门权限编码和角色编码获取用户账号
     *
     * @param orgCodes
     * @param roleCodes
     * @return
     */
    String getUserNameByDeptAuthCodeAndRoleCode(List<String> orgCodes, List<String> roleCodes);

    /**
     * 根据站点id获取站点信息
     *
     * @param stationCode
     * @return
     */
    JSONObject getCsStationByCode(String stationCode);

    /**
     * 根据组织机构编码获取本级以及本级下所有子级的组织机构编码
     *
     * @param orgCode
     * @return
     */
    List<String> getSublevelOrgCodes(String orgCode);

    /**
     * 返回当前dictCode对应的字典项在数据库中最后更新的时间
     * @param dictCode
     * @return
     */
    String getCurrentNewModified(String dictCode);


    /**
     * 树形搜索匹配
     *
     * @param name
     * @param list
     * @return
     */
    void processingTreeList(String name, List<SelectTable> list);

    /**
     * 根据数据库名和表名验证该数据库中是存在这张表
     * @param dbName
     * @param tableName
     * @return
     */
    boolean selectTableName(String dbName, String tableName);
}

