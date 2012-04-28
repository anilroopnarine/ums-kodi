package net.pms.external.xbmc;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jcifs.smb.SmbFile;

import net.pms.external.Consts;
import net.pms.external.XBMCLog;
import net.pms.external.xbmc.info.MediaFile;
import net.pms.external.xbmc.info.TitleInfo;

public class TVDAO extends XBMCDAO implements VideoDAO {

	public TVDAO(int dbType) {
		super(dbType);
	}

	@Override
	public List<String> getInitials() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			st = getConnection().prepareStatement("select distinct substr(c00, 1, 1) initial from tvshow order by initial asc");
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
			String stStr = "select idShow, c00 from tvshow where substr(c00, 1, 1) = ? order by c00 asc";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, initial);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idShow"), rs.getString("c00"));
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
	public TitleInfo getTitleByID(int episodeId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select * from episodeview where idEpisode = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, episodeId);
			rs = st.executeQuery();
			TitleInfo mi = null;
			if (rs.next()) {
				mi = new TitleInfo();
				mi.setPosters(getPosterURLs(episodeId));
				mi.setFanart(getFanartURLs(episodeId));
				mi.setActors(getActors(episodeId));
				mi.setTitleId(episodeId);
				mi.setFileId(rs.getInt("idFile"));
				mi.setFile(new MediaFile(rs.getString("strPath") + rs.getString("strFileName")));
				mi.setSinopsis(rs.getString("c01"));
				mi.setName(rs.getString("c00"));
				mi.setDirector(rs.getString("c06"));
				mi.setGenre(rs.getString("c14"));
				mi.setAge(rs.getString("c12"));
				mi.setRunningTime(rs.getString("c11"));
				mi.setTagline(rs.getString("c03"));
				mi.setRating(rs.getString("c05"));
				mi.setWatched(rs.getInt("playCount"));
				mi.setEpisode(rs.getString("c12") + "x" + rs.getString("c13"));
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
			st = getConnection().prepareStatement("select distinct genre.idGenre, genre.strGenre from tvshow, genrelinktvshow, genre where tvshow.idShow = genrelinktvshow.idShow and genre.idGenre = genrelinktvshow.idGenre");
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
			String stStr = "select distinct tvshow.idShow, tvshow.c00 from tvshow, genrelinktvshow, genre where tvshow.idShow = genrelinktvshow.idShow and genre.idGenre = genrelinktvshow.idGenre and genre.strGenre=?";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, genre);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idShow"), rs.getString("c00"));
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
	public Map<Integer, String> getEpisodes(int showId, String season) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStrSeason = "select * from episodeview where idShow=? and c12=?";
			String stStr = "select * from episodeview where idShow=?";
			st = getConnection().prepareStatement(Consts.ALL.equals(season) ? stStr : stStrSeason);
			st.setInt(1, showId);
			if (!Consts.ALL.equals(season)) {
				st.setString(2, season);
			}
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idEpisode"), rs.getString("c12") + "x" + rs.getString("c13") + ". " + rs.getString("c00"));
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
	public Map<String, String> getSeasons(int tvShowId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select distinct c12 from episodeview where idShow=?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, tvShowId);
			rs = st.executeQuery();
			Map<String, String> result = new TreeMap<String, String>();
			result.put(Consts.ALL, "All Seasons");
			while (rs.next()) {
				result.put(rs.getString("c12"), "Season " + rs.getString("c12"));
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
	public Map<Integer, String> getYears() {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			st = getConnection().prepareStatement("select distinct premiered, idShow from episodeview order by premiered desc");
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idShow"), rs.getString("premiered"));
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
			String stStr = "select * from episodeview where premiered=? order by premiered desc";
			st = getConnection().prepareStatement(stStr);
			st.setString(1, year);
			rs = st.executeQuery();
			Map<Integer, String> result = new TreeMap<Integer, String>();
			while (rs.next()) {
				result.put(rs.getInt("idShow"), rs.getString("strTitle"));
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
	public List<String> getActors(int titleId) {
		PreparedStatement st = null;
		ResultSet rs = null;
		connect();
		try {
			String stStr = "select strActor from actors, actorlinktvshow where actors.idActor = actorlinktvshow.idActor and idShow = ?";
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
			String stStr = "select c06 from episodeview where idEpisode = ?";
			st = getConnection().prepareStatement(stStr);
			st.setInt(1, titleId);
			rs = st.executeQuery();
			if (rs.next()) {
				String tumbXML = rs.getString("c06");
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
		return null;
	}

	@Override
	protected List<String> extractLinks(String source) {
		XBMCLog.info(source);
		List<String> links = new ArrayList<String>();
		if (source != null && source.length() > 0) {
			if (source.startsWith("<thumb>")) {
				source = source.substring(source.indexOf("<thumb>") + 7, source.indexOf("</thumb>"));
				XBMCLog.info(source);
				links.add(source);
			}
		}
		return links;
	}
}
