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

public class MovieGenreFolder extends VirtualFolder {

	private VideoDAO dao;

	public MovieGenreFolder(VideoDAO dao) {
		super(Consts.GENRE, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering movie genres");
		// Added Sort to Genre List
		Map<Integer, String> genres = MapUtil.sortByValue(dao.getGenres());
		for (final String genre : genres.values()) {
			ListFolder f = new ListFolder(genre) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading movie titles for: " + genre);
					// Added Sort to Movie List
					Map<Integer, String> map = MapUtil.sortByValue(dao.getTitlesByGenre(genre));
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
