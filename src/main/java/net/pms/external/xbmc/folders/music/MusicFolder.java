package net.pms.external.xbmc.folders.music;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCConfig;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.SongDAO;
import net.pms.external.xbmc.XBMCDAO;

public class MusicFolder extends VirtualFolder {

	public MusicFolder(String name, String thumbnailIcon) {
		super(name, thumbnailIcon);
	}

	public void discoverChildren() {
		XBMCLog.info("discovering music folders");
		int dbType = 0;
		String mySQLHost = XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_HOST);
		XBMCLog.info("MySQL Host: *" + mySQLHost + "*");

		if (mySQLHost == null || mySQLHost.length() == 0) {
			XBMCLog.info("Using SQLite");
			dbType = XBMCDAO.DB_TYPE_SQLITE_MUSIC;
		} else {
			XBMCLog.info("Using MySQL");
			dbType = XBMCDAO.DB_TYPE_MYSQL_MUSIC;
		}

		addChild(new ArtistFolder("Artists", new SongDAO(dbType)));
		addChild(new AlbumFolder("Albums", new SongDAO(dbType)));
	}
}
