package net.pms.external;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.xbmc.folders.VideoFolder;

/**
 * 
 * @author Frans
 * 
 */
public class PMSXBMCPlugin implements AdditionalFolderAtRoot {
	private final static int TYPE_VIDEO = 1;

	private VirtualFolder plugRoot;

	public PMSXBMCPlugin() {
		XBMCLog.info("creating root folder");
		plugRoot = new VirtualFolder(name(), null);

		XBMCLog.info("adding video folder");
		plugRoot.addChild(new VideoFolder("Videos", null));

		XBMCLog.info("adding music folder");
		plugRoot.addChild(new VirtualFolder("Music", null));
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
		JCheckBox enableBox = new JCheckBox("Enable plugin");
		enableBox.setSelected(XBMCConfig.isEnabled());
		enableBox.addActionListener(new ConfigActionListener());
		JButton videoDB = new JButton("Locate video database (" + XBMCConfig.getVideoDBLocation() + ")");
		videoDB.addActionListener(new ConfigActionListener(TYPE_VIDEO));

		JPanel confPanel = new JPanel();
		confPanel.add(enableBox);
		confPanel.add(videoDB);
		return confPanel;
	}

	/**
	 * FileHandler is used in the configuration screen. Its purpouse is to react
	 * to a mouse click and then update the MM_DBLOCATION parameter.
	 * 
	 * @author el.botijo
	 * 
	 */
	class ConfigActionListener implements ActionListener {

		private int type;

		public ConfigActionListener(int type) {
			this.type = type;
		}

		public ConfigActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) ae.getSource();
				XBMCConfig.setEnabled(box.isSelected());
			} else if (ae.getSource() instanceof JButton) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Locate MM.DB");
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("MediaMonkey 3 SQlite database (mm.db)", "db", "DB"));
				int option = chooser.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						XBMCLog.info("Choosen: " + chooser.getSelectedFile().getCanonicalPath().toString());
						if (chooser.getSelectedFile().exists()) {
							String location = chooser.getSelectedFile().getCanonicalPath().toString();
							if (type == TYPE_VIDEO) {
								XBMCConfig.setVideoDBLocation(location);
								JButton butt = (JButton) ae.getSource();
								butt.setText(XBMCConfig.getVideoDBLocation());
								butt.repaint();
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

}
