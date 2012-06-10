package net.pms.external.xbmc.folders.video.movie;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;

public class MoviesFolder extends VirtualFolder {

	private VideoDAO dao;

	public MoviesFolder(String name, VideoDAO dao) {
		super(name, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering movie categories");
		addChild(new MovieTitleFolder(dao));
		addChild(new MovieYearFolder(dao));
		addChild(new MovieGenreFolder(dao));
	}

}
