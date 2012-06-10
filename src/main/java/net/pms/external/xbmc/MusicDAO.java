package net.pms.external.xbmc;

import java.util.List;
import java.util.Map;

import net.pms.external.xbmc.info.SongInfo;

public interface MusicDAO {

	public Map<Integer, String> getArtists();

	public Map<Integer, String> getAlbums();

	public Map<Integer, String> getAlbumsByArtist(int artistId);

	public List<SongInfo> getTracksByAlbum(int albumId);

	public List<SongInfo> getTracksByArtist(int artistId);
}