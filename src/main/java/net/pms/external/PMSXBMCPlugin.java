package net.pms.external;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.xbmc.folders.music.MusicFolder;
import net.pms.external.xbmc.folders.video.VideoFolder;

/**
 * 
 * @author Frans
 * 
 */
@SuppressWarnings("serial")
public class PMSXBMCPlugin extends JPanel implements AdditionalFolderAtRoot {

	private PMSXBMCConfig configWindow;
	private VirtualFolder plugRoot;

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
		return "UMS KODI PLUGIN";
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
		if (configWindow == null) {
			configWindow = new PMSXBMCConfig();
		}
		return configWindow;
	}
}
