package net.pms.external;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Frans
 * 
 */
@SuppressWarnings("serial")
public class PMSXBMCConfig extends JPanel {
	private final static int TYPE_VIDEO = 1;
	private final static int TYPE_MUSIC = 2;

	private JLabel sqLiteVideoLocationLab = new JLabel(XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_SQLITE_VIDEO_DB));
	private JLabel sqLiteMusicLocationLab = new JLabel(XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_SQLITE_MUSIC_DB));

	private JButton browseSQLiteVideoFileButt;
	private JButton configMySQLButt;

	private JFileChooser sqLiteFileChooser;

	private String[] mySqlDetails;

	private MySqlOptionPane mySqlDialog = new MySqlOptionPane(this);
	private JButton browseSQLiteMusicFileButt;

	public PMSXBMCConfig() {
		initConfig();
	}

	public void initConfig() {

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "MySQL Config", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "SQLite Config", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		browseSQLiteVideoFileButt = new JButton("Browse");
		browseSQLiteVideoFileButt.addActionListener(new ConfigActionListener(TYPE_VIDEO));
		browseSQLiteMusicFileButt = new JButton("Browse");
		browseSQLiteMusicFileButt.addActionListener(new ConfigActionListener(TYPE_MUSIC));
		configMySQLButt = new JButton("Configure");
		configMySQLButt.addActionListener(new ConfigActionListener());

		JLabel sqLiteMusicLab = new JLabel("SQLite Music DB");
		JLabel sqLiteVideoLab = new JLabel("SQLite Video DB");

		JLabel mySqlLab = new JLabel("Configure MySQL");

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1.createSequentialGroup().addContainerGap().addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addComponent(sqLiteVideoLocationLab, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE).addGroup(gl_panel_1.createSequentialGroup().addComponent(sqLiteVideoLab).addGap(18).addComponent(browseSQLiteVideoFileButt)).addGroup(gl_panel_1.createSequentialGroup().addComponent(sqLiteMusicLab).addGap(18).addComponent(browseSQLiteMusicFileButt)).addComponent(sqLiteMusicLocationLab, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)).addContainerGap()));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1.createSequentialGroup().addContainerGap().addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(sqLiteVideoLab).addComponent(browseSQLiteVideoFileButt)).addPreferredGap(ComponentPlacement.RELATED).addComponent(sqLiteVideoLocationLab).addGap(18).addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(sqLiteMusicLab).addComponent(browseSQLiteMusicFileButt)).addPreferredGap(ComponentPlacement.RELATED).addComponent(sqLiteMusicLocationLab, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE).addContainerGap(13, Short.MAX_VALUE)));
		panel_1.setLayout(gl_panel_1);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(mySqlLab).addGap(18).addComponent(configMySQLButt).addContainerGap(182, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(mySqlLab).addComponent(configMySQLButt)).addContainerGap(46, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);

		JPanel panel_2 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE).addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE).addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(panel, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE).addGap(19)));

		JLabel lblOr = new JLabel("OR");
		panel_2.add(lblOr);
		setLayout(groupLayout);

	}

	class ConfigActionListener implements ActionListener {

		private int type;

		public ConfigActionListener(int type) {
			this.type = type;
		}

		public ConfigActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == configMySQLButt) {
				XBMCLog.info("configuring mysql");
				String[] details = mySqlDialog.showInputDialog();
				if (details != null) {
					for (String string : details) {
						XBMCLog.info(string);
					}
					setMySqlDetails(details);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_MYSQL_HOST, details[0]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_MYSQL_PORT, details[1]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_MYSQL_VIDEO_DB, details[2]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_MYSQL_USER, details[3]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_MYSQL_PASS, details[4]);
				}

			} else if (ae.getSource() == browseSQLiteVideoFileButt) {

				int option = getSqLiteFileChooser("Locate XBMC Video Database file", XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_SQLITE_VIDEO_DB)).showOpenDialog(PMSXBMCConfig.this);

				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						XBMCLog.info("Choosen: " + getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString());
						if (getSqLiteFileChooser().getSelectedFile().exists()) {
							String location = getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString();
							if (type == TYPE_VIDEO) {
								XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_SQLITE_VIDEO_DB, location);
								sqLiteVideoLocationLab.setText(location);
								sqLiteVideoLocationLab.repaint();
							}
							XBMCLog.info("Using following DB file (Video): " + location);
						} else {
							XBMCLog.info("Choosen Video db file does not exist");
						}
					} catch (IOException e) {
						XBMCLog.error("Error while getting path of choosen file");
					}
				}
			} else if (ae.getSource() == browseSQLiteMusicFileButt) {

				int option = getSqLiteFileChooser("Locate XBMC Music Database file", XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_SQLITE_MUSIC_DB)).showOpenDialog(PMSXBMCConfig.this);

				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						XBMCLog.info("Choosen: " + getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString());
						if (getSqLiteFileChooser().getSelectedFile().exists()) {
							String location = getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString();
							if (type == TYPE_MUSIC) {
								XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_SQLITE_MUSIC_DB, location);
								sqLiteMusicLocationLab.setText(location);
								sqLiteMusicLocationLab.repaint();
							}
							XBMCLog.info("Using following DB file (Music): " + location);
						} else {
							XBMCLog.info("Choosen Music db file does not exist");
						}
					} catch (IOException e) {
						XBMCLog.error("Error while getting path of choosen file");
					}
				}
			}
		}
	}

	public JFileChooser getSqLiteFileChooser() {
		return getSqLiteFileChooser(null, null);
	}

	public JFileChooser getSqLiteFileChooser(String title, String path) {
		if (sqLiteFileChooser == null) {
			
			sqLiteFileChooser = new JFileChooser();
			sqLiteFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XBMC SQLite Database", "db", "DB"));
		}
		if (title != null) {
			sqLiteFileChooser.setDialogTitle(title);
		}
		if (path != null) {
			sqLiteFileChooser.setSelectedFile(new File(path));
		}
		return sqLiteFileChooser;
	}

	public String[] getMySqlDetails() {
		if (mySqlDetails == null) {
			mySqlDetails = new String[] { XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_HOST), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_PORT), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_VIDEO_DB), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_USER), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_MYSQL_PASS) };
		}
		return mySqlDetails;
	}

	public void setMySqlDetails(String[] mySqlDetails) {
		this.mySqlDetails = mySqlDetails;
	}
}
