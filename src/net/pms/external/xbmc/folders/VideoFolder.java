package net.pms.external.xbmc.folders;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.XBMCConfig;
import net.pms.external.xbmc.MovieDAO;
import net.pms.external.xbmc.TVDAO;
import net.pms.external.xbmc.folders.movie.MoviesFolder;
import net.pms.external.xbmc.folders.tv.TVShowsFolder;

public class VideoFolder extends VirtualFolder {

	public VideoFolder(String name, String thumbnailIcon) {
		super(name, thumbnailIcon);
	}

	public void discoverChildren() {
		XBMCLog.info("discovering video folders");
		addChild(new MoviesFolder("Movies", new MovieDAO(XBMCConfig.getVideoDBLocation())));
		addChild(new TVShowsFolder("TV shows", new TVDAO(XBMCConfig.getVideoDBLocation())));
	}
}
