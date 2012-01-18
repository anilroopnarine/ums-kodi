package net.pms.external.xbmc;

import java.util.List;
import java.util.Map;

import net.pms.external.xbmc.info.TitleInfo;

public interface VideoDAO {

	public List<String> getInitials();

	public Map<Integer, String> getTitlesByInitial(String initial);

	public Map<Integer, String> getGenres();

	public Map<Integer, String> getTitlesByGenre(String genre);

	public TitleInfo getTitleByID(int titleId);

	public void updateWatched(TitleInfo title);

	public Map<Integer, String> getEpisodes(int tvShowId, String season);

	public Map<Integer, String> getYears();

	public Map<Integer, String> getTitlesByYear(String year);

	public Map<String, String> getSeasons(int tvShowId);

	public List<String> getPosterURLs(int titleId);

	public List<String> getFanartURLs(int titleId);

	public List<String> getActors(int titleId);

}