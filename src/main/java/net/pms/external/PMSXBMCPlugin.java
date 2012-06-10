package net.pms.external;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.xbmc.folders.music.MusicFolder;
import net.pms.external.xbmc.folders.video.VideoFolder;

/**
 * 
 * @author Frans
 * 
 */
public class PMSXBMCPlugin implements AdditionalFolderAtRoot {
	private final static int TYPE_VIDEO = 1;

	private VirtualFolder plugRoot;

	private JButton browseSQLiteFileButt;
	private JButton configMySQLButt;
	private JLabel sqLiteLocationLab = new JLabel(XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE));
	private JLabel mySqlLab = new JLabel("Configure MySQL");
	private JLabel sqLiteLab = new JLabel("Configure SQLite");
	private JFileChooser sqLiteFileChooser;
	private String[] mySqlDetails;
	private MySqlOptionPane mySqlDialog = new MySqlOptionPane();

	public PMSXBMCPlugin() {
		XBMCLog.info("creating root folder");
		plugRoot = new VirtualFolder(name(), null);

		XBMCLog.info("adding video folder");
		plugRoot.addChild(new VideoFolder("Videos", null));

		XBMCLog.info("adding music folder");
		plugRoot.addChild(new MusicFolder("Music", null));
	}

	@Override
	public String name() {
		return "PMS XBMC Plug";
	}

	@Override
	public void shutdown() {
	}

	@Override
	public DLNAResource getChild() {
		return plugRoot;
	}

	@Override
	public JComponent config() {
		JPanel confPanel = new JPanel();
		confPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;

		c.gridx = 0;
		c.gridy = 0;
		confPanel.add(mySqlLab, c);

		c.gridx = 1;
		c.gridy = 0;
		confPanel.add(getConfigMySQLButt(), c);

		c.gridx = 0;
		c.gridy = 1;
		confPanel.add(sqLiteLab, c);

		c.gridx = 1;
		c.gridy = 1;
		confPanel.add(getBrowseSQLiteFileButt(), c);

		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;
		confPanel.add(sqLiteLocationLab, c);

		return confPanel;
	}

	class ConfigActionListener implements ActionListener {

		private int type;

		public ConfigActionListener(int type) {
			this.type = type;
		}

		public ConfigActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == getConfigMySQLButt()) {
				XBMCLog.info("configuring mysql");
				String[] details = mySqlDialog.showInputDialog();
				if (details != null) {
					for (String string : details) {
						XBMCLog.info(string);
					}
					setMySqlDetails(details);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_HOST, details[0]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_PORT, details[1]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_DB, details[2]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_USER, details[3]);
					XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_PASS, details[4]);
				}

			} else if (ae.getSource() == getBrowseSQLiteFileButt()) {

				int option = getSqLiteFileChooser().showOpenDialog(null);

				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						XBMCLog.info("Choosen: " + getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString());
						if (getSqLiteFileChooser().getSelectedFile().exists()) {
							String location = getSqLiteFileChooser().getSelectedFile().getCanonicalPath().toString();
							if (type == TYPE_VIDEO) {
								XBMCConfig.setSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE, location);
								sqLiteLocationLab.setText(location);
								sqLiteLocationLab.repaint();
							}
							XBMCLog.info("Using following DB file: " + location);
						} else {
							XBMCLog.info("Choosen file does not exist");
						}
					} catch (IOException e) {
						XBMCLog.error("Error while getting path of choosen file");
					}
				}
			}
		}
	}

	public JButton getBrowseSQLiteFileButt() {
		if (browseSQLiteFileButt == null) {
			browseSQLiteFileButt = new JButton("Browse");
			browseSQLiteFileButt.addActionListener(new ConfigActionListener(TYPE_VIDEO));
		}
		return browseSQLiteFileButt;
	}

	public JButton getConfigMySQLButt() {
		if (configMySQLButt == null) {
			configMySQLButt = new JButton("Configure");
			configMySQLButt.addActionListener(new ConfigActionListener());
		}
		return configMySQLButt;
	}

	public JFileChooser getSqLiteFileChooser() {
		if (sqLiteFileChooser == null) {
			String path = XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_SQLITE);
			if (path == null) {
				sqLiteFileChooser = new JFileChooser();
			}
			else {
				sqLiteFileChooser = new JFileChooser(new File(path));
			}
			sqLiteFileChooser.setDialogTitle("Locate XBMC Database file");
			sqLiteFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XBMC SQLite Database", "db", "DB"));
		}
		return sqLiteFileChooser;
	}

	public String[] getMySqlDetails() {
		if (mySqlDetails == null) {
			mySqlDetails = new String[] { XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_HOST), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_PORT), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_DB), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_USER), XBMCConfig.getSetting(XBMCConfig.PMS_XBMC_VIDEO_MYSQL_PASS) };
		}
		return mySqlDetails;
	}

	public void setMySqlDetails(String[] mySqlDetails) {
		this.mySqlDetails = mySqlDetails;
	}

	@SuppressWarnings("serial")
	class MySqlOptionPane extends JOptionPane {
		public String[] showInputDialog() {
			String[] data = null;
			class GetData extends JDialog implements ActionListener {
				JTextField host = new JTextField(getMySqlDetails()[0]);
				JTextField port = new JTextField(getMySqlDetails()[1]);
				JTextField db = new JTextField(getMySqlDetails()[2]);
				JTextField user = new JTextField(getMySqlDetails()[3]);
				JTextField pass = new JTextField(getMySqlDetails()[4]);
				JButton btnOK = new JButton("   OK   ");
				JButton btnCancel = new JButton("Cancel");
				String[] str = null;

				public GetData() {
					setModal(true);
					setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					setLocation(400, 300);

					getContentPane().setLayout(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;

					c.gridx = 0;
					c.gridy = 0;
					getContentPane().add(new JLabel("Host"), c);
					c.gridx = 1;
					getContentPane().add(host, c);

					c.gridx = 0;
					c.gridy = 1;
					getContentPane().add(new JLabel("Port"), c);
					c.gridx = 1;
					getContentPane().add(port, c);

					c.gridx = 0;
					c.gridy = 2;
					getContentPane().add(new JLabel("Database"), c);
					c.gridx = 1;
					getContentPane().add(db, c);

					c.gridx = 0;
					c.gridy = 3;
					getContentPane().add(new JLabel("Username"), c);
					c.gridx = 1;
					getContentPane().add(user, c);

					c.gridx = 0;
					c.gridy = 4;
					getContentPane().add(new JLabel("Password"), c);
					c.gridx = 1;
					getContentPane().add(pass, c);

					JPanel jp = new JPanel();
					btnOK.addActionListener(this);
					btnCancel.addActionListener(this);
					jp.add(btnOK);
					jp.add(btnCancel);

					c.weightx = 0.0;
					c.gridwidth = 3;
					c.gridx = 0;
					c.gridy = 5;
					getContentPane().add(jp, c);
					pack();
					setVisible(true);
				}

				public void actionPerformed(ActionEvent ae) {
					if (ae.getSource() == btnOK) {
						str = new String[] { host.getText(), port.getText(), db.getText(), user.getText(), pass.getText() };
					}
					dispose();
				}

				public String[] getData() {
					return str;
				}
			}
			data = new GetData().getData();
			return data;
		}
	}
}
