package com.aiurt.common.constant;

/**
 * @author: huangxutao
 * @date: 2019-06-14
 * @description: 缓存常量
 */
public interface CacheConstant {

	/**
	 * 字典信息缓存（含禁用的字典项）
	 */
    public static final String SYS_DICT_CACHE = "platform:sys:cache:dict";

	/**
	 * 字典信息缓存 status为有效的
	 */
	public static final String SYS_ENABLE_DICT_CACHE = "platform:sys:cache:dictEnable";
	/**
	 * 表字典信息缓存
	 */
    public static final String SYS_DICT_TABLE_CACHE = "sys:cache:dictTable";
	public static final String SYS_DICT_TABLE_BY_KEYS_CACHE = SYS_DICT_TABLE_CACHE + "ByKeys";

	/**
	 * 数据权限配置缓存
	 */
    public static final String SYS_DATA_PERMISSIONS_CACHE = "platform:sys:cache:permission:datarules";

	/**
	 * 缓存用户信息
	 */
	public static final String SYS_USERS_CACHE = "platform:sys:cache:user";

	/**
	 * 全部部门信息缓存
	 */
	public static final String SYS_DEPARTS_CACHE = "platform:sys:cache:depart:alldata";


	/**
	 * 全部部门ids缓存
	 */
	public static final String SYS_DEPART_IDS_CACHE = "platform:sys:cache:depart:allids";


	/**
	 * 测试缓存key
	 */
	public static final String TEST_DEMO_CACHE = "platform:test:demo";

	/**
	 * 字典信息缓存
	 */
	public static final String SYS_DYNAMICDB_CACHE = "platform:sys:cache:dbconnect:dynamic:";

	/**
	 * gateway路由缓存
	 */
	public static final String GATEWAY_ROUTES = "platform:sys:cache:cloud:gateway_routes";


	/**
	 * gateway路由 reload key
	 */
	public static final String ROUTE_JVM_RELOAD_TOPIC = "gateway_jvm_route_reload_topic";

	/**
	 * TODO 冗余代码 待删除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_RANKING = "platform:pluginMall::rankingList";
	/**
	 * TODO 冗余代码 待删除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_PAGE_LIST = "platform:pluginMall::queryPageList";


	/**
	 * online列表页配置信息缓存key
	 */
	public static final String ONLINE_LIST = "platform:sys:cache:online:list";

	/**
	 * online表单页配置信息缓存key
	 */
	public static final String ONLINE_FORM = "platform:sys:cache:online:form";

	/**
	 * online报表
	 */
	public static final String ONLINE_RP = "platform:sys:cache:online:rp";

	/**
	 * online图表
	 */
	public static final String ONLINE_GRAPH = "platform:sys:cache:online:graph";
	/**
	 * 拖拽页面信息缓存
	 */
	public static final String DRAG_PAGE_CACHE = "platform:drag:cache:page";
}
