package com.frxs.PickApp.model;

/**
 * @description 在线升级
 * @author Endoon
 * @date 2015-12-11 10:26:50
 */
public class AppVersionInfo
{
	private String CurVersion;// 当前版本
	
	private int UpdateFlag;// 更新标识
	
	private String UpdateRemark;// 更新内容
	
	private String DownUrl;// 下载地址

	
    public String getCurVersion()
    {
    	return CurVersion;
    }

	
    public void setCurVersion(String curVersion)
    {
    	CurVersion = curVersion;
    }

	
    public int getUpdateFlag()
    {
    	return UpdateFlag;
    }

	
    public void setUpdateFlag(int updateFlag)
    {
    	UpdateFlag = updateFlag;
    }

	
    public String getUpdateRemark()
    {
    	return UpdateRemark;
    }

	
    public void setUpdateRemark(String updateRemark)
    {
    	UpdateRemark = updateRemark;
    }

	
    public String getDownUrl()
    {
    	return DownUrl;
    }

	
    public void setDownUrl(String downUrl)
    {
    	DownUrl = downUrl;
    }
}
