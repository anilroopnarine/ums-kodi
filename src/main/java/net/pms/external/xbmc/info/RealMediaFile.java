package net.pms.external.xbmc.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.pms.PMS;
import net.pms.dlna.DLNAMediaDatabase;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAMediaLang;
import net.pms.dlna.InputFile;
import net.pms.dlna.RealFile;
import net.pms.formats.Format;
import net.pms.util.FileUtil;
import net.pms.util.ProcessUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealMediaFile extends MediaMapFile {
	private static final Logger logger = LoggerFactory.getLogger(RealMediaFile.class);

	public RealMediaFile(MediaFile file) {
		getConf().getFiles().add(file);
		setLastmodified(file.lastModified());
	}

	public RealMediaFile(MediaFile file, String name) {
		getConf().getFiles().add(file);
		getConf().setName(name);
		setLastmodified(file.lastModified());
	}

	@Override
	// FIXME: this is called repeatedly for invalid files e.g. files MediaInfo
	// can't parse
	public boolean isValid() {
		File file = this.getFile();
		checktype();
		if (getType() == Format.VIDEO && file.exists() && PMS.getConfiguration().getUseSubtitles() && file.getName().length() > 4) {
			setSrtFile(FileUtil.doesSubtitlesExists(file, null));
		}
		boolean valid = file.exists() && (getExt() != null || file.isDirectory());

		if (valid && getParent().getDefaultRenderer() != null && getParent().getDefaultRenderer().isMediaParserV2()) {
			// we need to resolve the dlna resource now
			run();
			if (getMedia() != null && getMedia().getThumb() == null && getType() != Format.AUDIO) // MediaInfo
																								  // retrieves
																								  // cover
																								  // art
																								  // now
			{
				getMedia().setThumbready(false);
			}
			if (getMedia() != null && (getMedia().isEncrypted() || getMedia().getContainer() == null || getMedia().getContainer().equals(DLNAMediaLang.UND))) {
				// fine tuning: bad parsing = no file !
				valid = false;
				if (getMedia().isEncrypted()) {
					logger.info("The file " + file.getAbsolutePath() + " is encrypted. It will be hidden");
				} else {
					logger.info("The file " + file.getAbsolutePath() + " was badly parsed. It will be hidden");
				}
			}
			if (getParent().getDefaultRenderer().isMediaParserV2ThumbnailGeneration()) {
				checkThumbnail();
			}
		}
		return valid;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(getFile());
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	@Override
	public long length() {
		if (getPlayer() != null && getPlayer().type() != Format.IMAGE) {
			return DLNAMediaInfo.TRANS_SIZE;
		} else if (getMedia() != null && getMedia().isMediaparsed()) {
			return getMedia().getSize();
		}
		return getFile().length();
	}

	public boolean isFolder() {
		return getFile().isDirectory();
	}

	public File getFile() {
		return getConf().getFiles().get(0);
	}

	@Override
	public String getName() {
		if (this.getConf().getName() == null) {
			String name = null;
			File file = getFile();
			if (file.getName().trim().equals("")) {
				if (PMS.get().isWindows()) {
					name = PMS.get().getRegistry().getDiskLabel(file);
				}
				if (name != null && name.length() > 0) {
					name = file.getAbsolutePath().substring(0, 1) + ":\\ [" + name + "]";
				} else {
					name = file.getAbsolutePath().substring(0, 1);
				}
			} else {
				name = file.getName();
			}
			this.getConf().setName(name);
		}
		return this.getConf().getName();
	}

	@Override
	protected void checktype() {
		if (getExt() == null) {
			setExt(PMS.get().getAssociatedExtension(getFile().getAbsolutePath()));
		}

		super.checktype();
	}

	@Override
	public String getSystemName() {
		return ProcessUtil.getShortFileNameIfWideChars(getFile().getAbsolutePath());
	}

	@Override
	public void resolve() {
		File file = getFile();
		if (file.isFile() && file.exists() && (getMedia() == null || !getMedia().isMediaparsed())) {
			boolean found = false;
			InputFile input = new InputFile();
			input.setFile(file);
			String fileName = file.getAbsolutePath();
			if (getSplitTrack() > 0) {
				fileName += "#SplitTrack" + getSplitTrack();
			}

			if (PMS.getConfiguration().getUseCache()) {
				DLNAMediaDatabase database = PMS.get().getDatabase();

				if (database != null) {
					ArrayList<DLNAMediaInfo> medias = database.getData(fileName, file.lastModified());

					if (medias != null && medias.size() == 1) {
						setMedia(medias.get(0));
						getMedia().finalize(getType(), input);
						found = true;
					}
				}
			}

			if (!found) {
				if (getMedia() == null) {
					setMedia(new DLNAMediaInfo());
				}
				found = !getMedia().isMediaparsed() && !getMedia().isParsing();
				if (getExt() != null) {
					getExt().parse(getMedia(), input, getType(), getParent().getDefaultRenderer());
				} else // don't think that will ever happen
				{
					getMedia().parse(input, getExt(), getType(), false);
				}
				if (found && PMS.getConfiguration().getUseCache()) {
					DLNAMediaDatabase database = PMS.get().getDatabase();

					if (database != null) {
						database.insertData(fileName, file.lastModified(), getType(), getMedia());
					}
				}
			}
		}
		super.resolve();
	}

	@Override
	public String getThumbnailContentType() {
		return super.getThumbnailContentType();
	}

	@Override
	public InputStream getThumbnailInputStream() throws IOException {
		File file = getFile();
		File cachedThumbnail = null;
		if (getParent() != null && getParent() instanceof RealFile) {
			cachedThumbnail = ((RealFile) getParent()).getPotentialCover();
			File thumbFolder = null;
			boolean alternativeCheck = false;
			while (cachedThumbnail == null) {
				if (thumbFolder == null && getType() != Format.IMAGE) {
					thumbFolder = file.getParentFile();
				}
				cachedThumbnail = FileUtil.getFileNameWitNewExtension(thumbFolder, file, "jpg");
				if (cachedThumbnail == null) {
					cachedThumbnail = FileUtil.getFileNameWitNewExtension(thumbFolder, file, "png");
				}
				if (cachedThumbnail == null) {
					cachedThumbnail = FileUtil.getFileNameWitAddedExtension(thumbFolder, file, ".cover.jpg");
				}
				if (cachedThumbnail == null) {
					cachedThumbnail = FileUtil.getFileNameWitAddedExtension(thumbFolder, file, ".cover.png");
				}
				if (alternativeCheck) {
					break;
				}
				if (StringUtils.isNotBlank(PMS.getConfiguration().getAlternateThumbFolder())) {
					thumbFolder = new File(PMS.getConfiguration().getAlternateThumbFolder());
					if (!thumbFolder.exists() || !thumbFolder.isDirectory()) {
						thumbFolder = null;
						break;
					}
				}
				alternativeCheck = true;
			}
			if (file.isDirectory()) {
				cachedThumbnail = FileUtil.getFileNameWitNewExtension(file.getParentFile(), file, "/folder.jpg");
				if (cachedThumbnail == null) {
					cachedThumbnail = FileUtil.getFileNameWitNewExtension(file.getParentFile(), file, "/folder.png");
				}
			}

		}
		boolean hasAlreadyEmbeddedCoverArt = getType() == Format.AUDIO && getMedia() != null && getMedia().getThumb() != null;
		if (cachedThumbnail != null && (!hasAlreadyEmbeddedCoverArt || file.isDirectory())) {
			return new FileInputStream(cachedThumbnail);
		} else if (getMedia() != null && getMedia().getThumb() != null) {
			return getMedia().getThumbnailInputStream();
		} else {
			return super.getThumbnailInputStream();
		}
	}

	@Override
	public void checkThumbnail() {
		InputFile input = new InputFile();
		input.setFile(getFile());
		checkThumbnail(input);
	}

	@Override
	protected String getThumbnailURL() {
		if (getType() == Format.IMAGE && !PMS.getConfiguration().getImageThumbnailsEnabled()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(PMS.get().getServer().getURL());
		sb.append("/");
		if (getMedia() != null && getMedia().getThumb() != null) {
			return super.getThumbnailURL();
		} else if (getType() == Format.AUDIO) {
			if (getParent() != null && getParent() instanceof RealFile && ((RealFile) getParent()).getPotentialCover() != null) {
				return super.getThumbnailURL();
			}
			return null;
		}
		return super.getThumbnailURL();
	}
}
