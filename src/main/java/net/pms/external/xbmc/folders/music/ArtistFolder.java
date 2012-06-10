package net.pms.external.xbmc.folders.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.MusicDAO;
import net.pms.external.xbmc.folders.ListFolder;

public class ArtistFolder extends VirtualFolder {

	private MusicDAO dao;

	public ArtistFolder(String name, MusicDAO dao) {
		super(name, null);
		this.dao = dao;
	}

	@Override
	public void discoverChildren() {
		XBMCLog.info("discovering movie categories");

		Map<Integer, String> artists = dao.getArtists();

		for (final Integer artistId : artists.keySet()) {
			addChild(new ListFolder(artists.get(artistId)) {
				@Override
				public List<VirtualFolder> getList() {
					List<VirtualFolder> albumFolders = new ArrayList<VirtualFolder>();
					Map<Integer, String> albums = dao.getAlbumsByArtist(artistId);
					for (Integer albumId : albums.keySet()) {
						albumFolders.add(new AlbumFolder(albums.get(albumId), artistId, dao));
					}
					return albumFolders;
				}
			});
		}
	}

}
