package net.pms.external;

import net.pms.PMS;

public class XBMCConfig {
	public final static String PMS_XBMC_VIDEO_SQLITE = "pmsxbmc.db.video";
	public final static String PMS_XBMC_VIDEO_MYSQL_HOST = "pmsxbmc.video.mysql.host";
	public final static String PMS_XBM_VIDEOC_MYSQL_PORT = "pmsxbmc.video.mysql.port";
	public final static String PMS_XBMC_VIDEO_MYSQL_DB = "pmsxbmc.video.mysql.db";
	public final static String PMS_XBMC_VIDEO_MYSQL_USER = "pmsxbmc.video.mysql.user";
	public final static String PMS_XBMC_VIDEO_MYSQL_PASS = "pmsxbmc.video.mysql.pass";

	public static final void setSetting(String key, String value) {
		PMS.getConfiguration().setCustomProperty(key, value);
	}
	
	public static final String getSetting(String key) {
		return (String) PMS.getConfiguration().getCustomProperty(key);
	}
}
