package de.ulikoenig.sortPictures;

import java.io.File;
import java.io.FilenameFilter;

public class ImageFilter implements FilenameFilter {

	private String fileNameWithOutExt = null;
	
	public ImageFilter(String _fileNameWithOutExt)  {
		if(_fileNameWithOutExt.contains(File.separator)) {
			throw new IllegalArgumentException(_fileNameWithOutExt+" is a path and not a filename!");
		}
		this.fileNameWithOutExt = _fileNameWithOutExt;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(fileNameWithOutExt+".");
	}

}
