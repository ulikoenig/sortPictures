package de.ulikoenig.sortPictures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;

public class Utils {
	private static int movedFilesCounter = 0;
	private static int abeFound = 0;
	private static int renamedFilesCounter = 0;

	public static void moveImageToDir(File source, String destinationDir) {
		String fileWithoutExtension = Utils.getFilenameWithoutExtension(source);

		File dir = source.getParentFile();
		File[] files = dir.listFiles(new ImageFilter(fileWithoutExtension));

		for (File file : files) {
			if (!Main.TESTING) {
				file.renameTo(new File(destinationDir + File.separator
						+ file.getName()));
				movedFilesCounter++;
				if ((movedFilesCounter % 100) == 1) {
					System.out.print("*");
				}
			} else {
				System.out.println(" child.renameTo(new File(" + destinationDir
						+ File.separator + file.getName() + "));");
			}
		}
	}

	/**
	 * Move Image File. If a JPG is moved, a RAW will also be moved.
	 * 
	 * @param source
	 * @param destinationFileName
	 */
	public static void renameImage(File source, File destinationFileName) {
		String fileWithoutExtension = Utils.getFilenameWithoutExtension(source);

		File dir = source.getParentFile();
		File[] files = dir.listFiles(new ImageFilter(fileWithoutExtension));

		for (File file : files) {
			String extension = getExtension(file);
			String destFile = destinationFileName.getParent()+File.separator+
					Utils
					.getFilenameWithoutExtension(destinationFileName)
					+ "."
					+ extension;
			File destFileF = new File(destFile);

			if (Main.VERBOSE) {
				if (destFileF.isFile()) {
					System.out.println("Warnung: Ziel existiert.");
				}
				System.out.println("Moving: " + file.getName() + " --> "
						+ destFile);
			}

			if (!Main.TESTING) {
				if (!destFileF.isFile()) {
					file.renameTo(destFileF);
					renamedFilesCounter++;
					if ((movedFilesCounter % 100) == 1) {
						System.out.print("*");
					}
				}
			} else {
				System.out.println("child.renameTo(new File(" + destFile
						+ "));");
			}
		}
	}

	public static int getMovedFilesCounter() {
		return movedFilesCounter;
	}

	public static int getAbeFound() {
		return abeFound;
	}

	public static void detectAutobracketing(File dir)
			throws ImageProcessingException, IOException, MetadataException {
		File[] directoryListing = dir.listFiles();
		TreeMap<String, File> listOfJpegFilesByDate = new TreeMap<String, File>();
		if (directoryListing != null) {
			// SortFiles
			TreeSet<File> directoryListingSet = new TreeSet<File>();
			for (int i = 0; i < directoryListing.length; i++) {
				directoryListingSet.add(directoryListing[i]);
			}

			for (File child : directoryListingSet) {
				if (isJPEG(child) || isPNG(child)) {
					Metadata metadata = ImageMetadataReader.readMetadata(child);
					if (isAutobracketingByExif(metadata)) {
						// System.out.println("Belichtungsreihe: " +
						// child+" EV"+getExifExposureBiasValue(child)+" Time"+getExifDate(metadata));
						listOfJpegFilesByDate.put(child.getPath(), child);

					}
				}
			}
			// System.out.println("ABE Elemente: "+listOfJpegFilesByDate.size());
			// Detect Belichtungsreihe
			// Eigenschaften: Erstes Bild ist ausgangspunkt
			// Folgende Bilder haben andere Belichtung
			// Folgende Bilder haben höchstens Belichtungszeit des
			// Vorgängers+1Sek als Abstand.

			Map<Integer, File> abe = new TreeMap<Integer, File>();
			if (listOfJpegFilesByDate.size() > 0) {
				Date lastDate = getImageDateAsDate(listOfJpegFilesByDate
						.values().iterator().next()); // init
				Calendar nextDate = Calendar.getInstance();

				for (Iterator<File> iterator = listOfJpegFilesByDate.values()
						.iterator(); iterator.hasNext();) {
					File file = iterator.next();
					double exposureTime = getExifExposureTime(file);
					int bias = getExifExposureBiasValue(file);
					nextDate.setTime(lastDate);
					nextDate.add(Calendar.SECOND,
							(int) Math.ceil(exposureTime) + 1);

					// System.out.println("Date: "+getImageDateAsDate(file)+" nextDate "+nextDate.getTime());

					// New Bias
					if ((!abe.containsKey(bias)) &&
					// Not 2 times the same Bias in one ABE
							getImageDateAsDate(file).before(nextDate.getTime())
					// No Timegaps in ABE - Only automatic ABEs
					)

					{
						abe.put(bias, file);
					} else {
						// Ende of ABE
						sortAbe(abe);
						// Start with new ABE
						abe = new TreeMap<Integer, File>();
						abe.put(bias, file);
					}
					lastDate = getImageDateAsDate(file);
				}
				// Ende of ABE
				sortAbe(abe);
			}
		}
	}

	private static double getExifExposureTime(File file)
			throws ImageProcessingException, IOException, MetadataException {
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		return getExifExposureTime(metadata);
	}

	/**
	 * get exposureTime from Image
	 * 
	 * @param metadata
	 *            exifMetaData
	 * @return
	 */
	private static double getExifExposureTime(Metadata metadata) {
		double expTime = -1;
		for (Iterator<Directory> exifDirectories = metadata.getDirectories()
				.iterator(); exifDirectories.hasNext();) {
			Directory exifDir = exifDirectories.next();
			try {
				expTime = exifDir
						.getDouble(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
			} catch (MetadataException e) {
				// Not found
			}
			if (expTime != -1)
				return expTime;
		}
		return -1;
	}

	static Integer getExifExposureBiasValue(File child)
			throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(child);
		return getExifExposureBiasValue(metadata);
	}

	private static Integer getExifExposureBiasValue(Metadata metadata) {
		Integer Bias = null;
		for (Iterator<Directory> exifDirectories = metadata.getDirectories()
				.iterator(); exifDirectories.hasNext();) {
			Directory exifDir = exifDirectories.next();
			Bias = exifDir.getInteger(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS);
			if (Bias != null)
				return Bias;
		}
		return null;
	}

	/**
	 * Get filename without file extension.
	 * 
	 * @param source
	 *            Input Filename
	 * @return filename without extension
	 */
	static String getFilenameWithoutExtension(File source) {
		return Paths.get(source.getPath().replaceFirst("[.][^.]+$", ""))
				.getFileName().toString();
	}

	static boolean isJPEG(File child) {
		return child.isFile()
				&& (getExtension(child.getName()).compareToIgnoreCase("jpg") == 0);
	}

	private static boolean isPNG(File child) {
		return child.isFile()
				&& (getExtension(child.getName()).compareToIgnoreCase("png") == 0);
	}

	private static String getExtension(File fileName) {
		return getExtension(fileName.getName());
	}

	private static String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1);
		}
		return "";
	}

	static String getImageDate(File imagePath) throws ImageProcessingException,
			IOException {
		Date date = getImageDateAsDate(imagePath);

		TimeZone tz = TimeZone.getTimeZone("CET");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(tz);

		if (date == null) {
			System.err.println("ERROR: getImageDate" + imagePath
					+ ": date == null!");
			return null;
		}
		return df.format(date);
	}

	static Date getImageDateAsDate(File imagePath)
			throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(imagePath);
		Date date = null;
		date = getExifDate(metadata);
		if (date == null) {
			date = getFilesystemDate(metadata); // Using filesystem info as
												// fallback.
		}
		return date;
	}

	static Date getFilesystemDate(Metadata metadata) {
		Date date;
		FileMetadataDirectory fileInfo = metadata
				.getFirstDirectoryOfType(FileMetadataDirectory.class);
		date = fileInfo.getDate(3);
		return date;
	}

	private static Date getExifDate(Metadata metadata) {
		Date date = null;
		for (Iterator<Directory> exifDirectories = metadata.getDirectories()
				.iterator(); exifDirectories.hasNext();) {
			Directory exifDir = exifDirectories.next();
			date = exifDir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (date != null)
				return date;
		}
		return null;
	}

	static boolean isAutobracketingByExif(Metadata metadata)
			throws MetadataException {
		Integer exposureMode;
		for (Iterator<Directory> exifDirectories = metadata.getDirectories()
				.iterator(); exifDirectories.hasNext();) {
			Directory exifDir = (Directory) exifDirectories.next();
			exposureMode = exifDir.getInteger(41986);
			if (exposureMode != null)
				if (exposureMode == 2)
					return true;
		}
		return false;
	}

	public static void sortAbe(Map<Integer, File> abe)
			throws ImageProcessingException, IOException {
		abeFound++;
		if (Main.VERBOSE)
			System.out.println("ABE: length " + abe.size());

		File firstAbeFile = abe.values().iterator().next();
		String isoDatum = getImageDate(firstAbeFile);
		File dir = firstAbeFile.getParentFile();
		String destinationDir = dir.getPath() + File.separator + isoDatum
				+ File.separator + "ABE_"
				+ getFilenameWithoutExtension(firstAbeFile);
		(new File(destinationDir)).mkdirs();

		for (Iterator<File> iterator2 = abe.values().iterator(); iterator2
				.hasNext();) {
			File abefile = iterator2.next();
			if (Main.VERBOSE)
				System.out.println("* " + abefile.getPath() + " EV "
						+ getExifExposureBiasValue(abefile) + " Time"
						+ getImageDateAsDate(abefile));
			moveImageToDir(abefile, destinationDir);
		}
		if (Main.VERBOSE)
			System.out.println("***");
	}

	public static void sortDirectoryJPEGs(File dir) {
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (isJPEG(child) || isPNG(child)) {
					moveFileByDate(dir, child);
				}
			}
		}
	}

	public static void sortDirectoryRAWs(File dir) {
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (isRAW(child)) {
					moveFileByDate(dir, child);
				}
			}
		}
	}

	private static void moveFileByDate(File dir, File child) {
		String isoDatum;
		try {
			isoDatum = getImageDate(child);
			if (isoDatum != null) {
				String destinationDir = dir.getPath() + File.separator
						+ isoDatum;
				(new File(destinationDir)).mkdirs();

				// move JPEG to new Location
				moveImageToDir(child, destinationDir);
			}

		} catch (ImageProcessingException e) {
		} catch (IOException e) {
		}
	}

	private static boolean isRAW(File child) {
		// CRW/CR2/NEF/RW2/ORF
		return child.isFile()
				&& ((getExtension(child.getName()).compareToIgnoreCase("CRW") == 0)
						| (getExtension(child.getName()).compareToIgnoreCase(
								"CR2") == 0)
						| (getExtension(child.getName()).compareToIgnoreCase(
								"NEF") == 0)
						| (getExtension(child.getName()).compareToIgnoreCase(
								"RW2") == 0) | (getExtension(child.getName())
						.compareToIgnoreCase("ORF") == 0));
	}

	public static void renameFiles(File dir) {
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (isJPEG(child) | isPNG(child) | isRAW(child)) {
					try {
						Date date = getImageDateAsDate(child);
						TimeZone tz = TimeZone.getTimeZone("CET");
						DateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd_kk-mm-ss");
						df.setTimeZone(tz);
						if (date == null) {
							System.err.println("ERROR: renameFiles"
									+ child.getPath() + ": date == null!");
							return;
						}
						String dateS = df.format(date);

						String oldFileName = Paths.get(child.getPath())
								.getFileName().toString();
						String destFileName = dateS + "_" + oldFileName;
						File destination = new File(child.getParent()
								.toString() + File.separator + destFileName);
						if (!oldFileName.startsWith(dateS)) {
							renameImage(child, destination);
						}

					} catch (ImageProcessingException | IOException e) {
					}
				}
			}
		}
	}

	public static int getRenamedFilesCounter() {
		return renamedFilesCounter;
	}
}
