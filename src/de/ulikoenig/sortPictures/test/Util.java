package de.ulikoenig.sortPictures.test;

import java.io.File;

public class Util {

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	public static String getParentName(File file) {
		if (file == null || file.isDirectory()) {
			return file.getPath();
		}
		String parent = file.getParent();
		parent = parent.substring(parent.lastIndexOf(File.separator) + 1,
				parent.length());
		return parent;
	}
	
	public static void createParentDirIfNotExisting(String string) {
		if ((string.lastIndexOf(File.separator) + 1) == string.length()) {
			// ist Verzeichnis
		} else {
			string = Util.getParentName(new File(string));
		}
		File theDir = new File(string);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			// System.out.println("creating directory: ");
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
		}

	}

	public static int getFilesCount(File file) {
		  File[] files = file.listFiles();
		  int count = 0;
		  for (File f : files)
		    if (f.isDirectory())
		      count += getFilesCount(f);
		    else
		      count++;

		  return count;
		}
	

}
