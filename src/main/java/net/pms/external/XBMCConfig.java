package net.pms.external;

import net.pms.PMS;

public class XBMCConfig {
	public final static String PMS_XBMC_SQLITE_VIDEO_DB= "pmsxbmc.db.video";
	public final static String PMS_XBMC_SQLITE_MUSIC_DB = "pmsxbmc.db.music";
	
	public final static String PMS_XBMC_MYSQL_HOST = "pmsxbmc.mysql.host";
	public final static String PMS_XBMC_MYSQL_PORT = "pmsxbmc.mysql.port";
	public final static String PMS_XBMC_MYSQL_VIDEO_DB = "pmsxbmc.mysql.video";
	public final static String PMS_XBMC_MYSQL_MUSIC_DB = "pmsxbmc.mysql.music";
	public final static String PMS_XBMC_MYSQL_USER = "pmsxbmc.mysql.user";
	public final static String PMS_XBMC_MYSQL_PASS = "pmsxbmc.mysql.pass";

	public static final void setSetting(String key, String value) {
		PMS.getConfiguration().setCustomProperty(key, value);
	}
	
	public static final String getSetting(String key) {
		return (String) PMS.getConfiguration().getCustomProperty(key);
	}
}
