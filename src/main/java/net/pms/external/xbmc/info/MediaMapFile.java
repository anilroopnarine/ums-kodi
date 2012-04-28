package net.pms.external.xbmc.info;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.pms.PMS;
import net.pms.configuration.MapFileConfiguration;
import net.pms.dlna.CueFolder;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.DVDISOFile;
import net.pms.dlna.MapFile;
import net.pms.dlna.PlaylistFolder;
import net.pms.dlna.RarredFile;
import net.pms.dlna.RealFile;
import net.pms.dlna.ZippedFile;
import net.pms.dlna.virtual.TranscodeVirtualFolder;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.network.HTTPResource;
import net.pms.util.NaturalComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaMapFile extends DLNAResource {
	private static final Logger logger = LoggerFactory.getLogger(MapFile.class);
	private List<MediaFile> discoverable;

	/**
	 * @deprecated Use standard getter and setter to access this variable.
	 */
	@Deprecated
	public File potentialCover;

	/**
	 * @deprecated Use standard getter and setter to access this variable.
	 */
	@Deprecated
	protected MapFileConfiguration conf;

	private static final Collator collator;

	static {
		collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
	}

	public MediaMapFile() {
		setConf(new MapFileConfiguration());
		setLastmodified(0);
	}

	public MediaMapFile(MapFileConfiguration conf) {
		setConf(conf);
		setLastmodified(0);
	}

	private boolean isFileRelevant(MediaFile f) {
		String fileName = f.getName().toLowerCase();
		return (PMS.getConfiguration().isArchiveBrowsing() && (fileName.endsWith(".zip") || fileName.endsWith(".cbz") || fileName.endsWith(".rar") || fileName.endsWith(".cbr"))) || fileName.endsWith(".iso") || fileName.endsWith(".img") || fileName.endsWith(".m3u") || fileName.endsWith(".m3u8") || fileName.endsWith(".pls") || fileName.endsWith(".cue");
	}

	private boolean isFolderRelevant(MediaFile f) {
		boolean excludeNonRelevantFolder = true;
		if (f.isDirectory() && PMS.getConfiguration().isHideEmptyFolders()) {
			MediaFile children[] = f.listFiles();
			for (MediaFile child : children) {
				if (child.isFile()) {
					if (PMS.get().getAssociatedExtension(child.getName()) != null || isFileRelevant(child)) {
						excludeNonRelevantFolder = false;
						break;
					}
				} else {
					if (isFolderRelevant(child)) {
						excludeNonRelevantFolder = false;
						break;
					}
				}
			}
		}

		return !excludeNonRelevantFolder;
	}

	private void manageFile(MediaFile f) {
		if ((f.isFile() || f.isDirectory()) && !f.isHidden()) {
			if (PMS.getConfiguration().isArchiveBrowsing() && (f.getName().toLowerCase().endsWith(".zip") || f.getName().toLowerCase().endsWith(".cbz"))) {
				addChild(new ZippedFile(f));
			} else if (PMS.getConfiguration().isArchiveBrowsing() && (f.getName().toLowerCase().endsWith(".rar") || f.getName().toLowerCase().endsWith(".cbr"))) {
				addChild(new RarredFile(f));
			} else if ((f.getName().toLowerCase().endsWith(".iso") || f.getName().toLowerCase().endsWith(".img")) || (f.isDirectory() && f.getName().toUpperCase().equals("VIDEO_TS"))) {
				addChild(new DVDISOFile(f));
			} else if (f.getName().toLowerCase().endsWith(".m3u") || f.getName().toLowerCase().endsWith(".m3u8") || f.getName().toLowerCase().endsWith(".pls")) {
				addChild(new PlaylistFolder(f));
			} else if (f.getName().toLowerCase().endsWith(".cue")) {
				addChild(new CueFolder(f));
			} else {
				/* Optionally ignore empty directories */
				if (f.isDirectory() && PMS.getConfiguration().isHideEmptyFolders() && !isFolderRelevant(f)) {
					logger.debug("Ignoring empty/non relevant directory: " + f.getName());
				} /* Otherwise add the file */else {
					RealMediaFile file = new RealMediaFile(f);
					addChild(file);
				}
			}
		}
		if (f.isFile()) {
			String fileName = f.getName().toLowerCase();
			if (fileName.equalsIgnoreCase("folder.jpg") || fileName.equalsIgnoreCase("folder.png") || (fileName.contains("albumart") && fileName.endsWith(".jpg"))) {
				setPotentialCover(f);
			}
		}
	}

	private List<MediaFile> getFileList() {
		List<MediaFile> out = new ArrayList<MediaFile>();
		for (MediaFile file : this.conf.getFiles()) {
			if (file != null && file.isDirectory() && file.canRead()) {
				out.addAll(Arrays.asList(file.listFiles()));
			}
		}
		return out;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean analyzeChildren(int count) {
		int currentChildrenCount = getChildren().size();
		int vfolder = 0;
		while ((getChildren().size() - currentChildrenCount) < count || count == -1) {
			if (vfolder < getConf().getChildren().size()) {
				addChild(new MapFile(getConf().getChildren().get(vfolder)));
				++vfolder;
			} else {
				if (discoverable.isEmpty()) {
					break;
				}
				manageFile(discoverable.remove(0));
			}
		}
		return discoverable.isEmpty();
	}

	@Override
	public void discoverChildren() {
		super.discoverChildren();

		if (discoverable == null) {
			discoverable = new ArrayList<MediaFile>();
		} else {
			return;
		}

		List<MediaFile> files = getFileList();

		switch (PMS.getConfiguration().getSortMethod()) {
			case 4: // Locale-sensitive natural sort
				Collections.sort(files, new Comparator<MediaFile>() {
					public int compare(MediaFile f1, MediaFile f2) {
						return NaturalComparator.compareNatural(collator, f1.getName(), f2.getName());
					}
				});
				break;
			case 3: // Case-insensitive ASCIIbetical sort
				Collections.sort(files, new Comparator<MediaFile>() {

					public int compare(MediaFile f1, MediaFile f2) {
						return f1.getName().compareToIgnoreCase(f2.getName());
					}
				});
				break;
			case 2: // Sort by modified date, oldest first
				Collections.sort(files, new Comparator<MediaFile>() {

					public int compare(MediaFile f1, MediaFile f2) {
						return new Long(f1.lastModified()).compareTo(new Long(f2.lastModified()));
					}
				});
				break;
			case 1: // Sort by modified date, newest first
				Collections.sort(files, new Comparator<MediaFile>() {

					public int compare(MediaFile f1, MediaFile f2) {
						return new Long(f2.lastModified()).compareTo(new Long(f1.lastModified()));
					}
				});
				break;
			default: // Locale-sensitive A-Z
				Collections.sort(files, new Comparator<MediaFile>() {

					public int compare(MediaFile f1, MediaFile f2) {
						return collator.compare(f1.getName(), f2.getName());
					}
				});
				break;
		}

		for (MediaFile f : files) {
			if (f.isDirectory()) {
				discoverable.add(f); // manageFile(f);
			}
		}

		for (MediaFile f : files) {
			if (f.isFile()) {
				discoverable.add(f); // manageFile(f);
			}
		}
	}

	@Override
	public boolean isRefreshNeeded() {
		long lastModif = 0;
		for (MediaFile f : this.getConf().getFiles()) {
			if (f != null) {
				lastModif = Math.max(lastModif, f.lastModified());
			}
		}
		return lastRefreshTime < lastModif;
	}

	@Override
	public void refreshChildren() {
		List<MediaFile> files = getFileList();
		List<MediaFile> addedFiles = new ArrayList<MediaFile>();
		List<DLNAResource> removedFiles = new ArrayList<DLNAResource>();
		for (DLNAResource d : getChildren()) {
			boolean isNeedMatching = !(d.getClass() == MapFile.class || (d instanceof VirtualFolder && !(d instanceof DVDISOFile)));
			if (isNeedMatching) {
				if (!foundInList(files, d)) {
					removedFiles.add(d);
				}
			}
		}
		for (MediaFile f : files) {
			if (!f.isHidden()) {
				if (f.isDirectory() || PMS.get().getAssociatedExtension(f.getName()) != null) {
					addedFiles.add(f);
				}
			}
		}

		for (DLNAResource f : removedFiles) {
			logger.debug("MediaFile automatically removed: " + f.getName());
		}

		for (MediaFile f : addedFiles) {
			logger.debug("MediaFile automatically added: " + f.getName());
		}

		TranscodeVirtualFolder vf = getTranscodeFolder(false);

		for (DLNAResource f : removedFiles) {
			getChildren().remove(f);
			if (vf != null) {
				for (int j = vf.getChildren().size() - 1; j >= 0; j--) {
					if (vf.getChildren().get(j).getName().equals(f.getName())) {
						vf.getChildren().remove(j);
					}
				}
			}
		}

		for (MediaFile f : addedFiles) {
			manageFile(f);
		}

		for (MapFileConfiguration f : this.getConf().getChildren()) {
			addChild(new MapFile(f));
		}
	}

	private boolean foundInList(List<MediaFile> files, DLNAResource d) {
		for (MediaFile f : files) {
			if (!f.isHidden()) {
				if (isNameMatch(f, d) && (isRealFolder(d) || isSameLastModified(f, d))) {
					files.remove(f);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isSameLastModified(MediaFile f, DLNAResource d) {
		return d.getLastmodified() == f.lastModified();
	}

	private boolean isRealFolder(DLNAResource d) {
		return d instanceof RealFile && d.isFolder();
	}

	private boolean isNameMatch(MediaFile file, DLNAResource resource) {
		return (resource.getName().equals(file.getName()) || isDVDIsoMatch(file, resource));
	}

	private boolean isDVDIsoMatch(MediaFile file, DLNAResource resource) {
		return (resource instanceof DVDISOFile) && resource.getName().startsWith(DVDISOFile.PREFIX) && resource.getName().substring(DVDISOFile.PREFIX.length()).equals(file.getName());
	}

	@Override
	public String getSystemName() {
		return getName();
	}

	@Override
	public String getThumbnailContentType() {
		String thumbnailIcon = this.getConf().getThumbnailIcon();
		if (thumbnailIcon != null && thumbnailIcon.toLowerCase().endsWith(".png")) {
			return HTTPResource.PNG_TYPEMIME;
		}
		return super.getThumbnailContentType();
	}

	@Override
	public InputStream getThumbnailInputStream() throws IOException {
		return this.getConf().getThumbnailIcon() != null ? getResourceInputStream(this.getConf().getThumbnailIcon()) : super.getThumbnailInputStream();
	}

	@Override
	public long length() {
		return 0;
	}

	@Override
	public String getName() {
		return this.getConf().getName();
	}

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public boolean allowScan() {
		return isFolder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MapFile [name=" + getName() + ", id=" + getResourceId() + ", ext=" + getExt() + ", children=" + getChildren() + "]";
	}

	/**
	 * @return the conf
	 * @since 1.50
	 */
	protected MapFileConfiguration getConf() {
		return conf;
	}

	/**
	 * @param conf
	 *            the conf to set
	 * @since 1.50
	 */
	protected void setConf(MapFileConfiguration conf) {
		this.conf = conf;
	}

	/**
	 * @return the potentialCover
	 * @since 1.50
	 */
	public MediaFile getPotentialCover() {
		return potentialCover;
	}

	/**
	 * @param potentialCover
	 *            the potentialCover to set
	 * @since 1.50
	 */
	public void setPotentialCover(MediaFile potentialCover) {
		this.potentialCover = potentialCover;
	}

	TranscodeVirtualFolder getTranscodeFolder(boolean create) {
		if (!isTranscodeFolderAvailable()) {
			return null;
		}
		if (PMS.getConfiguration().getHideTranscodeEnabled()) {
			return null;
		}
		// search for transcode folder
		for (DLNAResource r : getChildren()) {
			if (r instanceof TranscodeVirtualFolder) {
				return (TranscodeVirtualFolder) r;
			}
		}
		if (create) {
			TranscodeVirtualFolder vf = new TranscodeVirtualFolder(null);
			addChildInternal(vf);
			return vf;
		}
		return null;
	}
}
