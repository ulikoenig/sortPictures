package de.ulikoenig.sortPictures;

import java.io.File;
import java.io.IOException;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

public class Main {

	public static final boolean TESTING = false;
	public static final boolean VERBOSE = false;
	public static final String version = "0.1";
	public static boolean createDateFolders = true;
	public static boolean sortABEs = true;
	public static boolean renamePicturesByDate = true;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ImageProcessingException
	 * @throws MetadataException
	 */
	public static void main(String[] args) throws ImageProcessingException,
			IOException, MetadataException {
		if (args.length == 0) {
			long startTime = System.currentTimeMillis();

			System.out
					.println("Starting sortPictures by Uli König, 2015 Version "
							+ version + ".");

			// do the Magic
			File dir = new java.io.File(".");
			if (renamePicturesByDate)
				Utils.renameFiles(dir);
			if (sortABEs)
				Utils.detectAutobracketing(dir);
			if (createDateFolders)
				Utils.sortDirectoryJPEGs(dir);
			if (createDateFolders)
				Utils.sortDirectoryRAWs(dir);

			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;

			System.out.println("\nMoved " + Utils.getMovedFilesCounter()
					+ " File(s).");
			System.out.println("Renamed " + Utils.getRenamedFilesCounter()
					+ " File(s).");
			if (Utils.getAbeFound() > 0)
				System.out.println("Found " + Utils.getAbeFound()
						+ " Auto Exposure Bracketings (AEB).");

			System.out.println("All Done in " + (elapsedTime / 1000)
					+ " seconds.");
		} else {
			System.out.println("sortPictures by Uli König, 2015 Version "
					+ version + ".");
			System.out.println("\nUsage: sortPictures\n");
			System.out
					.println("What it does:\nsortPictures will analyse pictures in the current Folder by Exif data or filesystem data if exif is missing.");
			System.out
					.println("sortPictures will try to detect Auto Exposure Bracketings an sort each in one folder.");
			System.out
					.println("sortPictures will try to detect the date of all pictures when they where taken an sort them into folders by date.");
			System.out
					.println("All sorted pictures will just be moved or renamed but not be changed.");
			System.out
					.println("All Files with the same Name, but different extension, will be moved with the pictures. e.g. RAW Files like '.CR2' Files.");
			System.out.println("\nNo files where processed.");
		}
	}
}
