package de.ulikoenig.sortPictures.test;

import static org.junit.Assert.*;
import de.ulikoenig.sortPictures.test.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

import de.ulikoenig.sortPictures.Utils;

public class TestSorting {

	static final String targetDir = "/tmp/";
	public int fileCount = 0;

	@Before
	public void setUp() {
		// Aufräumen
		Util.deleteDirectory(new File(targetDir + "test"));

		fileCount = 0;
		System.out.println("@Before - setUp");
		String tarFile = "./test.tar";
		String destFolder = targetDir;

		// Create a TarInputStream
		TarInputStream tis;
		try {
			tis = new TarInputStream(new BufferedInputStream(
					new FileInputStream(tarFile)));
			TarEntry entry;

			while ((entry = tis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[2048];
				Util.createParentDirIfNotExisting(destFolder + "/"
						+ entry.getName());
				if ((new File(destFolder + "/" + entry.getName()))
						.isDirectory()) {
					// Tu Was.

				} else {
					fileCount++;
					FileOutputStream fos = new FileOutputStream(destFolder
							+ "/" + entry.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos);

					while ((count = tis.read(data)) != -1) {
						dest.write(data, 0, count);
					}

					dest.flush();
					dest.close();
				}
			}

			tis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		System.out.println("@After - tearDown");
		Util.deleteDirectory(new File(targetDir + "test"));

	}

	@Test
	public void testTestdaten() {
		// Test, if testsourcedata is there
		File f = new File("./test.tar");
		assertTrue(f.exists() && !f.isDirectory());

		// Test, if testdata is there
		f = new File(targetDir + "test");
		assertTrue(f.exists() && f.isDirectory());

		assertFalse(de.ulikoenig.sortPictures.Main.TESTING);

	}

	@Test
	public void testRenameFiles() {
		File dir = new File(targetDir + "test");
		assertTrue((new File(targetDir
				+ "test/2015-05-10_15-36-30_IMG_20150510_133629.jpg")).isFile());
		assertTrue((new File(targetDir + "test/IMG_20150510_133629.jpg"))
				.isFile());

		Utils.renameFiles(dir);
		System.out.println("");
		assertEquals(fileCount, Util.getFilesCount(dir));

		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-09-51_IMG_0979.JPG")).isFile());
		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-09-51_IMG_0979.CR2")).isFile());
		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-08-02_IMG_0954.CR2")).isFile());
		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-08-02_IMG_0954.JPG")).isFile());
		assertTrue((new File(targetDir
				+ "test/2016-04-01_12-57-25_IMG_0453.CR2")).isFile());
		assertTrue((new File(targetDir
				+ "test/2016-04-01_12-57-25_IMG_0453.JPG")).isFile());
		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-18-29_IMG_1059.CR2")).isFile());
		assertTrue((new File(targetDir
				+ "test/2015-10-27_19-18-29_IMG_1059.JPG")).isFile());

		assertTrue((new File(targetDir
				+ "test/2015-05-10_15-36-30_IMG_20150510_133629.jpg")).isFile());
		assertTrue((new File(targetDir + "test/IMG_20150510_133629.jpg"))
				.isFile());
	}

	@Test
	public void testDetectAutobracketing() {
		File dir = new File(targetDir + "test");
		try {
			Utils.detectAutobracketing(dir);
		} catch (ImageProcessingException | MetadataException | IOException e) {
			fail(e.toString());
		}
		assertTrue((new File(targetDir + "test" + "/2015-10-27").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0946")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946")
						.isDirectory()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0945.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0945.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0945.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0945.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0946.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0946.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0946.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0946.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0947.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0947.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0947.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0947.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0948.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0948.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0948.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0948.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0949.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0949.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0949.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-09_IMG_0949.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0950.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-10_IMG_0950.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0950.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-10_IMG_0950.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0951.CR2").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-10_IMG_0951.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/ABE_IMG_0946/IMG_0951.JPG").isFile())
				|| (new File(
						targetDir
								+ "test"
								+ "/2015-10-27/ABE_2015-10-27_19-05-09_IMG_0946/2015-10-27_19-05-10_IMG_0951.JPG")
						.isFile()));

		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0958")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-08-43_IMG_0958")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0965")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-08-58_IMG_0965")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0972")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-09-19_IMG_0972")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0979")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-09-51_IMG_0979")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0986")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-10-53_IMG_0986")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_0993")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-11-13_IMG_0993")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_1051")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-17-30_IMG_1051")
						.isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/ABE_IMG_1065")
				.isDirectory())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/ABE_2015-10-27_19-18-43_IMG_1065")
						.isDirectory()));
		System.out.println("");
		assertEquals(fileCount, Util.getFilesCount(dir));
	}

	@Test
	public void testSortDirectoryJPEGs() {
		File dir = new File(targetDir + "test");
		Utils.sortDirectoryJPEGs(dir);
		assertEquals(fileCount, Util.getFilesCount(dir));
		// assertTrue((new File(targetDir +
		// "test"+"/IMG_1059 (Kopie).CR2").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2012-02-20").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2012-03-02").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2013-07-11").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-02").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-03").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-04").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-05").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-06").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-07").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-08").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-10").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-11").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-12").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-13").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-14").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-15").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-24").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-05-27").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2016-04-01").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2016-04-03").isDirectory()));
		System.out.println("");
	}

	@Test
	public void testSortDirectoryRAWs() {
		File dir = new File(targetDir + "test");
		Utils.sortDirectoryRAWs(dir);
		assertEquals(fileCount, Util.getFilesCount(dir));

		assertTrue((new File(targetDir + "test" + "/2015-10-26").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2016-04-01").isDirectory()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0929.CR2")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-26_IMG_0929.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0929.JPG")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-26_IMG_0929.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0930.CR2")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-31_IMG_0930.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0930.JPG")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-31_IMG_0930.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0931.CR2")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-38_IMG_0931.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-26/IMG_0931.JPG")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-26/2015-10-26_20-58-38_IMG_0931.JPG")
						.isFile()));

		
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0932.CR2")
				.isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/2015-10-27_18-52-19_IMG_0932.CR2")
						.isFile()));
		
		assertTrue((new File(targetDir + "test"
				+ "/2015-10-27/IMG_0932.JPG").isFile())
				|| (new File(targetDir + "test"
						+ "/2015-10-27/2015-10-27_18-52-19_IMG_0932.JPG")
						.isFile()));
		
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0933.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-08_IMG_0933.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0933.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-08_IMG_0933.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0934.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-16_IMG_0934.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0934.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-16_IMG_0934.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0935.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-36_IMG_0935.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0935.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-54-36_IMG_0935.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0936.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-58-16_IMG_0936.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0936.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-58-16_IMG_0936.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0937.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-58-24_IMG_0937.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0937.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_18-58-24_IMG_0937.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0938.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-00-51_IMG_0938.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0938.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-00-51_IMG_0938.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0939.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-01-00_IMG_0939.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0939.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-01-00_IMG_0939.JPG")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0940.CR2")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-02-19_IMG_0940.CR2")
						.isFile()));
		assertTrue((new File(targetDir + "test" + "/2015-10-27/IMG_0940.JPG")
				.isFile())
				|| (new File(targetDir + "test" + "/2015-10-27/2015-10-27_19-02-19_IMG_0940.JPG")
						.isFile()));

		System.out.println("");

	}

	@Test
	public void testAlles() {
		testTestdaten();
		testRenameFiles();
		testDetectAutobracketing();
		testSortDirectoryJPEGs();
		testSortDirectoryRAWs();
	}

}
