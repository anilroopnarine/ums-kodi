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

	public final static int DB_TYPE_MYSQL = 1;
	public final static int DB_TYPE_SQLITE = 2;

	private int dbType;
	private Connection connection;

	public XBMCDAO(int dbType) {
		this.dbType = dbType;
	}

	protected void connect() {
		if (dbType == DB_TYPE_MYSQL) {
			connectMySQL();
		} else if (dbType == DB_TYPE_SQLITE) {
			connectSQLite();
		}
	}

	private void connectMySQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_HOST) + ":" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEOC_MYSQL_PORT) + "/" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_DB);
			XBMCLog.info("connecting to mysql with url: " + url);
			connection = DriverManager.getConnection(url, XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_USER), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_PASS));
		} catch (Exception e) {
			XBMCLog.error(e);
		}
	}

	private void connectSQLite() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE));
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
