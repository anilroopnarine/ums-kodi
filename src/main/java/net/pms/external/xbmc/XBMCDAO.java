package net.pms.external.xbmc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pms.external.XBMCConfig;
import net.pms.external.XBMCLog;

public abstract class XBMCDAO {

	public final static int DB_TYPE_MYSQL_VIDEO = 1;
	public final static int DB_TYPE_SQLITE_VIDEO = 2;
	public final static int DB_TYPE_MYSQL_MUSIC = 3;
	public final static int DB_TYPE_SQLITE_MUSIC = 4;

	private int dbType;
	private Connection connection;

	public XBMCDAO(int dbType) {
		this.dbType = dbType;
	}

	protected void connect() {
		if (dbType == DB_TYPE_MYSQL_VIDEO) {
			connectMySQL(XBMCConfig.PMS_XBMC_MYSQL_VIDEO_DB);
		} else if (dbType == DB_TYPE_MYSQL_MUSIC) {
			connectMySQL(XBMCConfig.PMS_XBMC_MYSQL_MUSIC_DB);
		} else if (dbType == DB_TYPE_SQLITE_VIDEO || dbType == DB_TYPE_SQLITE_MUSIC) {
			connectSQLite();
		}
	}

	private void connectMySQL(String db) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_HOST) + ":" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_PORT) + "/" + XBMCConfig.getSetting(db);
			XBMCLog.info("connecting to mysql with url: " + url);
			connection = DriverManager.getConnection(url, XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_USER), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_PASS));
		} catch (Exception e) {
			XBMCLog.error(e);
		}
	}

	private void connectSQLite() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + XBMCConfig.getSetting(dbType == DB_TYPE_SQLITE_VIDEO ? XBMCConfig.PMS_XBMC_SQLITE_VIDEO_DB : XBMCConfig.PMS_XBMC_SQLITE_MUSIC_DB));
		} catch (Exception e) {
			XBMCLog.error(e);
		}
	}

	protected void disconnect(Statement st, ResultSet rs) {
		try {
			if (connection != null) {
				connection.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			XBMCLog.error(e);
		}
	}

	protected Connection getConnection() {
		if (connection == null) {
			connect();
		}
		return connection;
	}

	private Pattern htmltag = Pattern.compile("<thumb\\b[^>]*preview=\"[^>]*>(.*?)</thumb>");
	private Pattern link = Pattern.compile("preview=\"[^>]*\">");

	protected List<String> extractLinks(String source) {
		List<String> links = new ArrayList<String>();
		Matcher tagmatch = htmltag.matcher(source);
		while (tagmatch.find()) {
			Matcher matcher = link.matcher(tagmatch.group());
			matcher.find();
			String link = matcher.group().replaceFirst("preview=\"", "").replaceFirst("\">", "");
			links.add(link);
		}
		return links;
	}
}
