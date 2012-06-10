package net.pms.external.xbmc.folders.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.RealFile;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.MusicDAO;
import net.pms.external.xbmc.folders.ListFiles;
import net.pms.external.xbmc.info.SongInfo;

public class AlbumFolder extends VirtualFolder {

	private MusicDAO dao;
	private int artistId = -1;

	public AlbumFolder(String name, MusicDAO dao) {
		this(name, -1, dao);
	}

	public AlbumFolder(String name, int artistId, MusicDAO dao) {
		super(name, null);
		this.artistId = artistId;
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering albums");
		Map<Integer, String> albums = null;
		if (artistId != -1) {
			albums = dao.getAlbumsByArtist(artistId);
		} else {
			albums = dao.getAlbums();
		}

		for (final Integer albumId : albums.keySet()) {
			addChild(new ListFiles(albums.get(albumId)) {
				@Override
				public List<RealFile> getList() {
					List<SongInfo> songs = dao.getTracksByAlbum(albumId);
					List<RealFile> songFiles = new ArrayList<RealFile>();
					for (SongInfo songInfo : songs) {
						songFiles.add(new RealFile(songInfo.getFile()));
					}
					return songFiles;
				}
			});
		}
	}

}
