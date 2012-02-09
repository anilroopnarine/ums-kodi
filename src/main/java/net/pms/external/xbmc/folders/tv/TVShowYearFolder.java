package net.pms.external.xbmc.folders.tv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.ListFolder;

public class TVShowYearFolder extends VirtualFolder {

	private VideoDAO dao;

	public TVShowYearFolder(VideoDAO dao) {
		super(Consts.PREMIERED, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering tv show years");
		Map<Integer, String> years = dao.getYears();
		for (final String year : years.values()) {
			ListFolder f = new ListFolder(year) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading movie titles for: " + year);
					Map<Integer, String> map = dao.getTitlesByYear(year);
					List<VirtualFolder> list = new ArrayList<VirtualFolder>();
					for (Integer id : map.keySet()) {
						String name = map.get(id);
						TVShowFolder title = new TVShowFolder(dao, id, name);
						list.add(title);
					}
					return list;
				}
			};
			addChild(f);
		}
	}
}
