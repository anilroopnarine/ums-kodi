package net.pms.external.xbmc.folders.video;

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
import net.pms.configuration.RendererConfiguration;

public class TitleVirtualFolder extends VirtualFolder {

	private VideoDAO dao;
	private int titleId;

	public TitleVirtualFolder(int titleId, String title, VideoDAO dao) {
		super(title, null);
		this.titleId = titleId;
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.logTimeStart("loading movie info from DB");
		final TitleInfo info = dao.getTitleByID(titleId);
		XBMCLog.logTimeStop();

		if (info == null) {
			addChild(new VirtualFolder("NOT FOUND CHECK LOG", null));
			return;
		}

		if (!info.getFile().exists()) {
			addChild(new VirtualFolder("<FILE IS MISSING>", null));
			return;
		}
		addChild(new VirtualFolder("Title: " + info.getName(), null));

		XBMCLog.logTimeStart("adding title file");
		addChild(new RealFile(info.getFile()) {
			@Override
			public String getName() {
				return "Play Movie";
			}

			@Override
			public void startPlaying(String rendererId, RendererConfiguration render) {
				super.startPlaying(rendererId, null);
				//info.setWatched(info.getWatched() + 1);
				//dao.updateWatched(info);
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
		if (info.getWriter() != null && info.getWriter().length() > 0) {
			addChild(new VirtualFolder("Writer: " + info.getWriter(), null));
		}
		if (info.getRating() != null && info.getRating().length() > 0) {
			addChild(new VirtualFolder("IMDB Rating: " + info.getRating(), null));
		}
		if (info.getCountry() != null && info.getCountry().length() > 0) {
			addChild(new VirtualFolder("Country: " + info.getCountry(), null));
		}
		if (info.getStudio() != null && info.getStudio().length() > 0) {
			addChild(new VirtualFolder("Studio: " + info.getStudio(), null));
		}
		if (info.getVideoCodec() != null && info.getVideoCodec().length() > 0) {
			addChild(new VirtualFolder("Video CODEC: " + info.getVideoCodec(), null));
		}
		if (info.getVideoRes() != null && info.getVideoRes().length() > 0) {
			addChild(new VirtualFolder("Video Size: " + info.getVideoRes(), null));
		}
		if (info.getAudioCodec() != null && info.getAudioCodec().length() > 0) {
			addChild(new VirtualFolder("Audio CODEC: " + info.getAudioCodec(), null));
		}
		if (info.getAudioChannels() != null && info.getAudioChannels().length() > 0) {
			addChild(new VirtualFolder("Audio Channels: " + info.getAudioChannels(), null));
		}
		if (info.getEpisode() != null && info.getEpisode().length() > 0) {
			addChild(new VirtualFolder("Season/Episode: " + info.getEpisode(), null));
		}
/*
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
*/

		addThumbnails(info.getPosters(), Consts.POSTER);
		addThumbnails(info.getFanart(), Consts.FANART);
		
	}

	private void addThumbnails(List<String> thumbUrls, String title) {
		if (thumbUrls != null) {
			for (final String thumbUrl : thumbUrls) {
				addChild(new WebStream(title, thumbUrl, thumbUrl, Format.VIDEO) {
					// @Override
					// public InputStream getInputStream() {
					// try {
					// XBMCLog.info("getInputStream: " + thumbUrl);
					// URL url = new URL(thumbUrl);
					// return url.openStream();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// return super.getInputStream();
					// }
				});
			}
		}
	}
}
