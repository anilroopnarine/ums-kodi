package net.pms.external.xbmc.folders.tv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.ListFolder;

public class TVShowTitleFolder extends VirtualFolder {

	private VideoDAO dao;

	public TVShowTitleFolder(VideoDAO dao) {
		super(Consts.TITLE, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering tv show initials");
		List<String> initials = dao.getInitials();
		for (final String initial : initials) {
			ListFolder f = new ListFolder(initial) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading tv show titles for: " + initial);
					Map<Integer, String> map = dao.getTitlesByInitial(initial);
					List<VirtualFolder> list = new ArrayList<VirtualFolder>();
					for (Integer id : map.keySet()) {
						String title = map.get(id);
						TVShowFolder tvShowFolder = new TVShowFolder(dao, id, title);
						list.add(tvShowFolder);
					}
					return list;
				}
			};
			addChild(f);
		}
	}
}
