package net.pms.external.xbmc;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.pms.external.XBMCLog;
import net.pms.external.xbmc.info.SongInfo;

public class SongDAO extends XBMCDAO implements MusicDAO {

	public SongDAO(int dbType) {
		super(dbType);
	}

	@Override
	public Map<Integer, String> getArtists() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select idArtist, strArtist from artist order by strArtist asc";
			st = getConnection().prepareStatement(stStr);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idArtist"), rs.getString("strArtist"));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public Map<Integer, String> getAlbums() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select idAlbum, strAlbum from album order by strAlbum asc";
			st = getConnection().prepareStatement(stStr);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idAlbum"), rs.getString("strAlbum"));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public Map<Integer, String> getAlbumsByArtist(int artistId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select idAlbum, strAlbum from album where idArtist = ? order by strAlbum asc";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, artistId);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idAlbum"), rs.getString("strAlbum"));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public List<SongInfo> getTracksByAlbum(int albumId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select * from songview where idAlbum = ? order by iTrack asc";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, albumId);
			rs = st.executeQuery();
			List<SongInfo> result = new ArrayList<SongInfo>();
			while (rs.next()) {
				result.add(populateSongFromSongViewRS(rs));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public List<SongInfo> getTracksByArtist(int artistId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select * from songview where idArtist = ? order by iTrack asc";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, artistId);
			rs = st.executeQuery();
			List<SongInfo> result = new ArrayList<SongInfo>();
			while (rs.next()) {
				result.add(populateSongFromSongViewRS(rs));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}
	
	private SongInfo populateSongFromSongViewRS(ResultSet rs) throws SQLException {
		SongInfo info = new SongInfo();
		info.setSongId(rs.getInt("idSong"));
		info.setFile(new File(rs.getString("strPath") + rs.getString("strFileName")));
		info.setGenre(rs.getString("strGenre"));
		info.setTitle(rs.getString("strTitle"));
		return info;
	}
}
