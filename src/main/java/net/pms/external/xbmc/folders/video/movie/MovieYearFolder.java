package net.pms.external.xbmc.folders.video.movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.Consts;
import net.pms.external.MapUtil;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.ListFolder;
import net.pms.external.xbmc.folders.video.TitleVirtualFolder;

public class MovieYearFolder extends VirtualFolder {

	private VideoDAO dao;

	public MovieYearFolder(VideoDAO dao) {
		super(Consts.YEAR, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering movie years");
		Map<Integer, String> years = dao.getYears();
		for (final String year : years.values()) {
			ListFolder f = new ListFolder(year) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading movie titles for: " + year);
					// Added Sort to Movie List
					Map<Integer, String> map = MapUtil.sortByValue(dao.getTitlesByYear(year));
					List<VirtualFolder> list = new ArrayList<VirtualFolder>();
					for (Integer id : map.keySet()) {
						String name = map.get(id);
						TitleVirtualFolder title = new TitleVirtualFolder(id, name, dao);
						list.add(title);
					}
					return list;
				}
			};
			addChild(f);
		}
	}
}
