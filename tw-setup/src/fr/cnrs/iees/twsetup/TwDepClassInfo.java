package fr.cnrs.iees.twsetup;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import static au.edu.anu.twcore.project.TwPaths.*;

public class TwDepClassInfo {

	public static void main(String[] args) {
//		String fn = System.getProperty("user.home") + "/.3w/tw-dep.jar";
		String fn = TW_ROOT+File.pathSeparator+TW_DEP_JAR;
		File f = new File(fn);
		long sumClassCompressed = 0;
		long sumClassUncompressed = 0;
		JarFile jf;
		try {
			// NB: Don't use jar input streams. These will not contain the info required!
			// Not much use doing the same for .java because they are not all there so
			// comparison with .class file means nothing.
			jf = new JarFile(f.getAbsolutePath());
			Enumeration e = jf.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				String name = je.getName();
				if (name.endsWith(".class")) {
					sumClassCompressed += je.getCompressedSize();
					sumClassUncompressed += je.getSize();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(fn);
		System.out.println(
				"Compressed .class file bytes:\t" + NumberFormat.getNumberInstance().format(sumClassCompressed));
		System.out.println(
				"Uncompressed .class file bytes:\t" + NumberFormat.getNumberInstance().format(sumClassUncompressed));

	}

}
