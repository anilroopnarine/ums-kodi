package net.pms.external.xbmc.info;

import java.util.List;

public class TitleInfo {

	private int titleId;
	private int fileId;
	private MediaFile file;
	private List<String> posters;
	private List<String> fanart;
	private String name;
	private String sinopsis;
	private String tagline;
	private String rating;
	private String runningTime;
	private String genre;
	private String director;
	private String writer;
	private List<String> actors;
	private String age;
	private int watched;
	private String episode;
	private String country;
	private String studio;
	private String videoCodec;
	private String videoRes;
	private String audioCodec;
	private String audioChannels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSinopsis() {
		return sinopsis;
	}

	public void setSinopsis(String sinopsis) {
		this.sinopsis = sinopsis;
	}

	public MediaFile getFile() {
		return file;
	}

	public void setFile(MediaFile file) {
		this.file = file;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(String runningTime) {
		this.runningTime = runningTime;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public int getWatched() {
		return watched;
	}

	public void setWatched(int watched) {
		this.watched = watched;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public String getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	public String getVideoRes() {
		return videoRes;
	}

	public void setVideoRes(String videoRes) {
		this.videoRes = videoRes;
	}

	public String getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public String getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(String audioChannels) {
		this.audioChannels = audioChannels;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public List<String> getPosters() {
		return posters;
	}

	public void setPosters(List<String> posters) {
		this.posters = posters;
	}

	public List<String> getFanart() {
		return fanart;
	}

	public void setFanart(List<String> fanart) {
		this.fanart = fanart;
	}
}
