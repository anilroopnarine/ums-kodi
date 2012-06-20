package net.pms.external.xbmc.folders.video.tv;

import java.util.Iterator;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.folders.video.TitleVirtualFolder;

public class TVShowFolder extends VirtualFolder {

	private VideoDAO dao;
	private int tvShowId;

	public TVShowFolder(VideoDAO dao, int tvShowId, String title) {
		super(title, null);
		this.dao = dao;
		this.tvShowId = tvShowId;
	}

	@Override
	public void discoverChildren() {

		XBMCLog.logTimeStart("discovering seasons for " + getName());
		final Map<String, String> seasons = dao.getSeasons(tvShowId);
		XBMCLog.logTimeStop();

		Iterator<String> seasonsIter = seasons.keySet().iterator();
		while (seasonsIter.hasNext()) {
			final String seasonId = seasonsIter.next();
/*	A limitation on ps3 only allows 8 folders deep, so I'm removing the season selection path. So all episodes will be displayed from all seasons always		
 * ListFolder seasonFolder = new ListFolder(seasons.get(seasonId)) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.logTimeStart("discovering files for " + seasons.get(seasonId));
					Map<Integer, String> episodes = dao.getEpisodes(tvShowId, seasonId);
					XBMCLog.logTimeStop();
					List<VirtualFolder> episodesList = new ArrayList<VirtualFolder>();
					for (Integer id : episodes.keySet()) {
						String title = episodes.get(id);
						TitleVirtualFolder titleFolder = new TitleVirtualFolder(id, title, dao);
						episodesList.add(titleFolder);
					}
					return episodesList;
				}
			};
			addChild(seasonFolder);*/
			
			if (!seasonId.equals(Consts.ALL)) {
				XBMCLog.logTimeStart("discovering files for " + seasons.get(seasonId));
				Map<Integer, String> episodes = dao.getEpisodes(tvShowId, seasonId);
				XBMCLog.logTimeStop();
				for (Integer id : episodes.keySet()) {
					String title = episodes.get(id);
					TitleVirtualFolder titleFolder = new TitleVirtualFolder(id, title, dao);
					addChild(titleFolder);
				}
			}
		}
	}
}
