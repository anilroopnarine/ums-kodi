package net.pms.external.xbmc.folders;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import net.pms.dlna.RealFile;
import net.pms.dlna.WebStream;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.dlna.virtual.VirtualVideoAction;
import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.VideoDAO;
import net.pms.external.xbmc.info.TitleInfo;
import net.pms.formats.Format;

public class TitleVirtualFolder extends VirtualFolder {

	private VideoDAO dao;
	private int titleId;
	private List<String> posters;
	private List<String> fanart;

	public TitleVirtualFolder(int titleId, String title, VideoDAO dao) {
		super(title, null);
		this.titleId = titleId;
		this.dao = dao;
		this.posters = dao.getPosterURLs(titleId);
		this.fanart = dao.getFanartURLs(titleId);
	}

	@Override
	public InputStream getThumbnailInputStream() {
		XBMCLog.info("getThumbnailInputStream: " + posters);
		try {
			if (posters != null) {
				URL url = new URL(posters.get(0));
				return url.openStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.getThumbnailInputStream();
	}

	@Override
	public void discoverChildren() {
		XBMCLog.logTimeStart("loading movie info from DB");
		final TitleInfo info = dao.getTitleByID(titleId);
		XBMCLog.logTimeStop();

		if (!info.getFile().exists()) {
			addChild(new VirtualFolder("<FILE IS MISSING>", null));
			return;
		}
		addChild(new VirtualFolder("Title: " + info.getName(), null));

		XBMCLog.logTimeStart("adding title file");
		addChild(new RealFile(info.getFile()) {
			@Override
			public String getName() {
				return "Play";
			}

			@Override
			public void startPlaying(String rendererId) {
				super.startPlaying(rendererId);
				info.setWatched(info.getWatched() + 1);
				dao.updateWatched(info);
			}
		});
		XBMCLog.logTimeStop();

		if (info.getTagline() != null && info.getTagline().length() > 0) {
			addChild(new VirtualFolder("Tagline: " + info.getTagline(), null));
		}
		if (info.getRunningTime() != null && info.getRunningTime().length() > 0) {
			addChild(new VirtualFolder("Running Time: " + info.getRunningTime() + "min", null));
		}
		if (info.getAge() != null && info.getAge().length() > 0) {
			addChild(new VirtualFolder("Age: " + info.getAge(), null));
		}
		if (info.getGenre() != null && info.getGenre().length() > 0) {
			addChild(new VirtualFolder("Genre: " + info.getGenre(), null));
		}
		if (info.getSinopsis() != null && info.getSinopsis().length() > 0) {
			addChild(new VirtualFolder("Sinopsis: " + info.getSinopsis(), null));
		}
		if (info.getDirector() != null && info.getDirector().length() > 0) {
			addChild(new VirtualFolder("Director: " + info.getDirector(), null));
		}
		if (info.getRating() != null && info.getRating().length() > 0) {
			addChild(new VirtualFolder("IMDB Rating: " + info.getRating(), null));
		}
		if (info.getEpisode() != null && info.getEpisode().length() > 0) {
			addChild(new VirtualFolder("Season/Episode: " + info.getEpisode(), null));
		}
		addChild(new VirtualFolder("Watched: ", info.getWatched() + " times") {
			@Override
			public String getName() {
				return "Watched: " + info.getWatched() + " times";
			}
		});
		addChild(new VirtualVideoAction("Reset watched count: ", true) {
			@Override
			public boolean enable() {
				info.setWatched(0);
				dao.updateWatched(info);
				XBMCLog.info("watched reset");
				return true;
			}
		});

		if (posters != null) {
			for (final String posterUrl : posters) {
				addChild(new WebStream(Consts.POSTER, posterUrl, posterUrl, Format.VIDEO) {
					@Override
					public InputStream getInputStream() {
						try {
							XBMCLog.info("getInputStream: " + posterUrl);
							URL url = new URL(posterUrl);
							return url.openStream();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return super.getInputStream();
					}
				});
			}
		}

		if (fanart != null) {
			for (final String fanartUrl : fanart) {
				addChild(new WebStream(Consts.FANART, fanartUrl, fanartUrl, Format.VIDEO) {
					@Override
					public InputStream getInputStream() {
						try {
							XBMCLog.info("getInputStream: " + fanartUrl);
							URL url = new URL(fanartUrl);
							return url.openStream();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return super.getInputStream();
					}
				});
			}
		}
	}
}
