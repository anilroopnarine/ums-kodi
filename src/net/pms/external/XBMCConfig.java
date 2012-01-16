package net.pms.external;

import net.pms.PMS;

public class XBMCConfig {
	private final static String PMS_XBMC_CONF_VIDEO = "pmsxbmc.db.video";
	private final static String PMS_XBMC_CONF_ENABLED = "pmsxbmc.enabled";

	public static final boolean isEnabled() {
		String enabled = (String) PMS.getConfiguration().getCustomProperty(PMS_XBMC_CONF_ENABLED);
		if (enabled == null) {
			enabled = "0";
		}
		XBMCLog.info("is PMS XBMC Plug enabled? = " + enabled);
		return enabled.equals("1");
	}

	public static final void setEnabled(boolean enabled) {
		XBMCLog.info("setting PMS XBMC Plug enabled = " + enabled);
		PMS.getConfiguration().setCustomProperty(PMS_XBMC_CONF_ENABLED, enabled ? "1" : "0");
	}

	public static final String getVideoDBLocation() {
		return (String) PMS.getConfiguration().getCustomProperty(PMS_XBMC_CONF_VIDEO);
	}

	public static final void setVideoDBLocation(String location) {
		PMS.getConfiguration().setCustomProperty(PMS_XBMC_CONF_VIDEO, location);
	}
}
