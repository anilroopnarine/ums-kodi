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

public class MovieTitleFolder extends VirtualFolder {

	private VideoDAO dao;

	public MovieTitleFolder(VideoDAO dao) {
		super(Consts.TITLE, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering movie initials");
		List<String> initials = dao.getInitials();
		ListFolder all = new ListFolder("All") {
			@Override
			public List<VirtualFolder> getList() {
				XBMCLog.info("loading all movie titles");
				// Added Sort to Movie List
				Map<Integer, String> map = MapUtil.sortByValue(dao.getTitlesByInitial(null));
				List<VirtualFolder> list = new ArrayList<VirtualFolder>();
				for (Integer id : map.keySet()) {
					String name = map.get(id);
					TitleVirtualFolder title = new TitleVirtualFolder(id, name, dao);
					list.add(title);
				}
				return list;
			}
		};
		addChild(all);
		
		ListFolder recent = new ListFolder("Recent") {
			@Override
			public List<VirtualFolder> getList() {
				XBMCLog.info("loading recent movie titles");
				// Added Sort to Movie List
				Map<Integer, String> map = MapUtil.sortByValue(dao.getRecent());
				List<VirtualFolder> list = new ArrayList<VirtualFolder>();
				for (Integer id : map.keySet()) {
					String name = map.get(id).split("__SEP__")[1];
					TitleVirtualFolder title = new TitleVirtualFolder(id, name, dao);
					list.add(title);
				}
				return list;
			}
		};
		addChild(recent);
		
		ListFolder rated = new ListFolder("By Rating") {
			@Override
			public List<VirtualFolder> getList() {
				XBMCLog.info("loading movies by rating");
				// Added Sort to Movie List
				Map<Integer, String> map = MapUtil.sortByValue(dao.getByRatings());
				List<VirtualFolder> list = new ArrayList<VirtualFolder>();
				for (Integer id : map.keySet()) {
					String [] name = map.get(id).split("__SEP__");
					float rating = 10f - Float.parseFloat(name[0]);
					TitleVirtualFolder title = new TitleVirtualFolder(id, name[1] + " -------- " + Float.toString(rating), dao);
					list.add(title);
				}
				return list;
			}
		};
		addChild(rated);
		
		ListFolder random = new ListFolder("Random") {
			@Override
			public List<VirtualFolder> getList() {
				XBMCLog.info("loading 5 randomly selected movies ");
				// Added Sort to Movie List
				Map<Integer, String> map = MapUtil.sortByValue(dao.getRandom());
				List<VirtualFolder> list = new ArrayList<VirtualFolder>();
				for (Integer id : map.keySet()) {
					String [] name = map.get(id).split("__SEP__");
					float rating = 10f - Float.parseFloat(name[0]);
					TitleVirtualFolder title = new TitleVirtualFolder(id, name[1] + " -------- " + Float.toString(rating), dao);
					list.add(title);
				}
				return list;
			}
		};
		addChild(random);
		
		for (final String initial : initials) {
			ListFolder f = new ListFolder(initial) {
				@Override
				public List<VirtualFolder> getList() {
					XBMCLog.info("loading movie titles for: " + initial);
					// Added Sort to Movie List
					Map<Integer, String> map = MapUtil.sortByValue(dao.getTitlesByInitial(initial));
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
