package net.pms.external.xbmc.folders.tv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.ListFolder;

public class TVShowGenreFolder extends VirtualFolder {

	private VideoDAO dao;

	public TVShowGenreFolder(VideoDAO dao) {
		super(Consts.GENRE, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering tv show genres");
		Map<Integer, String> genres = dao.getGenres();
		for (final String genre : genres.values()) {
			ListFolder f = new ListFolder(genre) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading tv show titles for: " + genre);
					Map<Integer, String> map = dao.getTitlesByGenre(genre);
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
