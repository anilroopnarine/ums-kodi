package net.pms.external.xbmc.folders.tv;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;

public class TVShowsFolder extends VirtualFolder {

	private VideoDAO dao;

	public TVShowsFolder(String name, VideoDAO dao) {
		super(name, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering tv show categories");
		addChild(new TVShowTitleFolder(dao));
		addChild(new TVShowYearFolder(dao));
		addChild(new TVShowGenreFolder(dao));
	}

}
