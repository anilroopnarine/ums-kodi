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
import net.pms.external.xbmc.info.TitleInfo;

public class MovieDAO extends XBMCDAO implements VideoDAO {

	public MovieDAO(int dbType) {
		super(dbType);
	}

	@Override
	public List<String> getInitials() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			st = getConnection().prepareStatement("select distinct substr(c00,1,1) initial from movieview order by initial asc");
			rs = st.executeQuery();
			List<String> result = new ArrayList<String>();
			while (rs.next()) {
				result.add(rs.getString("initial"));
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
	public Map<Integer, String> getTitlesByInitial(String initial) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select idMovie, c00 from movieview where substr(c00,1,1) = ? order by c00 asc";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, initial);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idMovie"), rs.getString("c00"));
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
	
	public Map<Integer, String> getSets() {
		PreparedStatement st = null;
		PreparedStatement st1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		connect();
		try {
			st = getConnection().prepareStatement("select idSet, strSet from sets");
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				st1 = getConnection().prepareStatement("select count(*) from movie where idSet = ?");
				st1.setInt(1,rs.getInt("idSet"));
				rs1 = st1.executeQuery();
				if(rs1.next() && rs1.getInt(1) > 1){
					result.put(rs.getInt("idSet"), rs.getString("strSet"));
				}
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

	public Map<Integer, String> getTitlesBySet(String set) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select distinct movie.idMovie, movie.c00 from movie, sets  where movie.idSet=sets.idSet and sets.strSet=?";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, set);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idMovie"), rs.getString("c00"));
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
	public TitleInfo getTitleByID(int titleId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select *, movieview.idFile fi from movieview,streamdetails where streamdetails.idFile = movieview.idFile and movieview.idMovie = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, titleId);
			rs = st.executeQuery();
			TitleInfo mi = null;
			while (rs.next()) {
				if (mi == null) {
					mi = new TitleInfo();
					mi.setPosters(getPosterURLs(titleId));
					mi.setFanart(getFanartURLs(titleId));
					mi.setActors(getActors(titleId));
				}
				if (rs.getInt("iStreamType") == 0) { // video stream
					mi.setTitleId(titleId);
					mi.setFileId(rs.getInt("fi"));
					mi.setFile(new File(rs.getString("strPath") + rs.getString("strFileName")));
					mi.setSinopsis(rs.getString("c01"));
					mi.setName(rs.getString("c00"));
					mi.setDirector(rs.getString("c15"));
					mi.setWriter(rs.getString("c06"));
					mi.setGenre(rs.getString("c14"));
					mi.setAge(rs.getString("c12"));
					mi.setRunningTime(rs.getString("c11"));
					mi.setTagline(rs.getString("c03"));
					mi.setRating(rs.getString("c05"));
					mi.setWatched(rs.getInt("playCount"));
					mi.setCountry(rs.getString("c21"));
					mi.setStudio(rs.getString("c18"));
					mi.setVideoCodec(rs.getString("strVideoCodec"));
					mi.setVideoRes(rs.getString("iVideoWidth") + "x" + rs.getString("iVideoHeight"));
				} else { // audio stream
					mi.setAudioCodec(rs.getString("strAudioCodec"));
					mi.setAudioChannels(rs.getString("iAudioChannels"));
				}
			}
			return mi;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public List<String> getActors(int titleId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select strActor from actors, actorlinkmovie where actors.idActor = actorlinkmovie.idActor and idMovie = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, titleId);
			rs = st.executeQuery();
			List<String> actors = new ArrayList<String>();
			while (rs.next()) {
				String actor = rs.getString("strActor");
				XBMCLog.info(actor);
				actors.add(actor);
				return actors;
			}
			return actors;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}

	}

	@Override
	public List<String> getPosterURLs(int titleId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select c08 from movieview where movieview.idMovie = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, titleId);
			rs = st.executeQuery();
			if (rs.next()) {
				String tumbXML = rs.getString("c08");
				XBMCLog.info(tumbXML);
				List<String> urls = extractLinks(tumbXML);
				XBMCLog.info(urls);
				return urls;
			} else {
				return null;
			}
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public List<String> getFanartURLs(int titleId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select c20 from movieview where movieview.idMovie = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, titleId);
			rs = st.executeQuery();
			if (rs.next()) {
				String tumbXML = rs.getString("c20");
				XBMCLog.info(tumbXML);
				List<String> urls = extractLinks(tumbXML);
				XBMCLog.info(urls);
				return urls;
			} else {
				return null;
			}
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public void updateWatched(TitleInfo movie) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "update files set playCount = ? where idFile = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, movie.getWatched());
			st.setInt(2, movie.getFileId());
			st.executeUpdate();
		} catch (SQLException e) {
			XBMCLog.error(e);
		} finally {
			disconnect(st, rs);
		}
	}

	@Override
	public Map<Integer, String> getGenres() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			st = getConnection().prepareStatement("select distinct genre.idGenre, genre.strGenre from movie, genrelinkmovie, genre where movie.idMovie = genrelinkmovie.idMovie and genre.idGenre = genrelinkmovie.idGenre");
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idGenre"), rs.getString("strGenre"));
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
	public Map<Integer, String> getTitlesByGenre(String genre) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select distinct movie.idMovie, movie.c00 from movie, genrelinkmovie, genre where movie.idMovie = genrelinkmovie.idMovie and genre.idGenre = genrelinkmovie.idGenre and genre.strGenre=?";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, genre);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idMovie"), rs.getString("c00"));
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
	public Map<Integer, String> getEpisodes(int tvShowId, String season) {
		return null;
	}

	@Override
	public Map<String, String> getSeasons(int tvShowId) {
		return null;
	}

	@Override
	public Map<Integer, String> getYears() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			st = getConnection().prepareStatement("select distinct c07 from movie order by c07 desc");
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("c07"), rs.getString("c07"));
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
	public Map<Integer, String> getTitlesByYear(String year) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select * from movieview where c07 = ? order by c00 asc";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, year);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idMovie"), rs.getString("c00"));
			}
			return result;
		} catch (SQLException e) {
			XBMCLog.error(e);
			return null;
		} finally {
			disconnect(st, rs);
		}
	}
}
