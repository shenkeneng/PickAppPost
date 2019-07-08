package com.frxs.PickApp.comms;

/**
 * 某某某类
 * 
 * @ClassName: GlobelDefines
 * @Description: 定义全局常量
 * @author: ewu
 * @date: 2015-3-3
 * 
 */
public class GlobelDefines
{
	
	public static final String PREFS_NAME = "MyFrefsFile";
	
	public static final String KEY_SHELF_AREAID = "key_shelf_areaid";
	
	public static final String KEY_FIRST_ENTER = "key_first_enter";
	
	public static final String KEY_ENVIRONMENT = "key_environment";

	public static final String KEY_USER_ACCOUNT = "key_user_account";

	public static final String KEY_FONT_SIZE = "key_fontsize";

	public static final String KEY_VERSION_SELECTOR = "key_version_selector";

	public static final String KEY_PRINT_SETTING = "key_print_setting"; //拣货完成是否必须打印

	public static final String KEY_ORDER_PRINT = "key_order_print"; //已打印订单

	public static final String KEY_DOWNLOAD_ID = "key_download_id";

	public interface FontSizeConstants
	{
		public int FONT_BIG = 1;
		public int FONT_MEDIUM = 2;
		public int FONT_SMALL = 3;
	}

	public interface VersionSelector
	{
		public int VERSION_CLASSIC = 1;// 经典版
		public int VERSION_SIMPLE = 2;// 精简版
	}

	/**
	 * 网络请求成功
	 */
	public static final String FLAG_SUCCESS = "SUCCESS";// "FAIL",
	
	/**
	 * 网络请求失败
	 */
	public static final String FLAG_FAIL = "FAIL";// "FAIL",
	
	

	public static final String SP_USERNAME = "sp_username";
	
	
	public static final String SP_PASSWORD = "sp_password";
	
	public static String INSTOCK = "0";// 正常（不缺货）
	
	public static String OUTSTOCK = "1";// 缺货
}
