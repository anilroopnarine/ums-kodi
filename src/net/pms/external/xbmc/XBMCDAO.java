package net.pms.external.xbmc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pms.external.XBMCLog;

public abstract class XBMCDAO {

	private String dbLocation;
	private Properties dbProperties;
	private Connection connection;

	public XBMCDAO(String dbLocation) {
		this.dbLocation = dbLocation;
	}

	public XBMCDAO(Properties dbProperties) {
		this.dbProperties = dbProperties;
	}

	protected void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
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
