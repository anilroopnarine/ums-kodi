package net.pms.external.xbmc.folders.video;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCConfig;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.MovieDAO;
import net.pms.external.xbmc.TVDAO;
import net.pms.external.xbmc.XBMCDAO;
import net.pms.external.xbmc.folders.video.movie.MoviesFolder;
import net.pms.external.xbmc.folders.video.tv.TVShowsFolder;

public class VideoFolder extends VirtualFolder {

	public VideoFolder(String name, String thumbnailIcon) {
		super(name, thumbnailIcon);
	}

	public void discoverChildren() {
		XBMCLog.info("discovering video folders");
		int dbType = 0;
		String mySQLHost = XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_HOST);
		XBMCLog.info("MySQL Host: *" + mySQLHost + "*");

		if (mySQLHost == null || mySQLHost.length() == 0) {
			XBMCLog.info("Using SQLite");
			dbType = XBMCDAO.DB_TYPE_SQLITE;
		} else {
			XBMCLog.info("Using MySQL");
			dbType = XBMCDAO.DB_TYPE_MYSQL;
		}

		addChild(new MoviesFolder("Movies", new MovieDAO(dbType)));
		addChild(new TVShowsFolder("TV shows", new TVDAO(dbType)));
	}
}
