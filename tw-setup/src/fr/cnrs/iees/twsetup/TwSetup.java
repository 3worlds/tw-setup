/**************************************************************************
 *  TW-SETUP - tw.jar generator                                       *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  This module is specifically for generating the dependency file for    *
 *  3Worlds: tw.jar.                                                  *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-SETUP.                                        *
 *                                                                        *
 *  TW-SETUP is free software: you can redistribute it and/or modify      *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-SETUP is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-SETUP.                                                  *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package fr.cnrs.iees.twsetup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A class to properly generate the .3w repo with the necessary dependencies
 * from the 3Worlds eclipse project.
 * 
 * CAUTION: before running this class, it is strongly recommended to completely
 * clean the ivy cache by removing the local and cache subdirectories and
 * regenerating them using the 3w libraries build.xml scripts in the proper
 * order (ie omhtk > omugi > qgraph > aot > tw-core > tw-apps > tw-uifx)
 * 
 * @author Ian Davies
 * @date 10 Dec. 2017
 */
// refactored and carefully tested JG 11/3/2022
public class TwSetup implements ProjectPaths, TwPaths {

	// version management
	private static final String workDir = System.getProperty("user.dir") + File.separator + "src";
	private static final String packageDir = TwSetup.class.getPackage().getName().replace('.', File.separatorChar);
	private static final String DOT = ".";

	// jar names for the 3w applications
	public static final String MODELRUNNER_JAR = "modelRunner.jar";
	public static final String MODELMAKER_JAR = "modelMaker.jar";
	// this is supposed to return the root dir of all 3worlds libraries, e.g.
	// /home/gignoux/git
	public static final String CODEROOT = Path.of(System.getProperty("user.dir")).getParent().getParent().toString();
	// NB these two names cannot be extracted from the classes because the classes
	// are in tw-uifx.
	private static final String MODELMAKER_CLASS = "au.edu.anu.twuifx.mm.MMmain";
	@SuppressWarnings("unused")
	private static final String MODELRUNNER_CLASS = "au.edu.anu.twuifx.mr.MRmain";

	private static File buildTwApplicationIvyFile() {
		String ivyFile = "<ivy-module version=\"2.0\"\n"
				+ "		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "		xmlns:m=\"http://maven.apache.org/POM/4.0.0\"\n"
				+ "		xsi:noNamespaceSchemaLocation=\"http://ant.apache.org/ivy/schemas/ivy.xsd\">\n" + "\n"
				+ "	<info	organisation=\"fr.cnrs.iees.tw-setup\"\n" + "			module=\"tw-setup\"\n"
				+ "			revision=\"0.0.1\"\n" + "			status=\"integration\">\n"
				+ "		<license name=\"gpl3\" url=\"https://www.gnu.org/licenses/gpl-3.0.txt\"/>\n"
				+ "		<description>This module brings together all components required for 3Worlds including archetype</description>\n"
				+ "	</info>\n" + "\n" + "	<configurations>\n" + "		<conf name=\"java library\"/>\n"
				+ "	</configurations>\n" + "\n" + "	<publications>\n" + "		<artifact type=\"jar\" ext=\"jar\">\n"
				+ "			<conf name=\"java library\"/>\n" + "		</artifact>\n" + "	</publications>\n" + "\n"
				+ "	<dependencies>\n"
				+ "		<dependency org=\"au.edu.anu.tw-uifx\" name=\"tw-uifx\" rev=\"[0.3.12,)\"/>\n"
				+ "	</dependencies>\n" + "\n" + "</ivy-module>\n" + "";
		File outf = new File("bidon.xml");
		PrintWriter writer;
		try {
			writer = new PrintWriter(outf);
			writer.print(ivyFile);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return outf;
	}

	/**
	 * Create the 3worlds workspace {@code TW_ROOT = "3w"} and save the 3w jar into
	 * it.
	 * 
	 * @param filename
	 * @return
	 */
	private static File jarFile(String filename) {
		File file = new File(TW_ROOT + File.separator + filename);
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * packs all what's needed to run 3worlds in a single jar, puts it in a zip file
	 * under the 3w dir: 3w/tw.jar
	 */
	private static void pack3wAll(String major, String minor, String build) {
		ThreeWorldsJar twDepPacker = new ThreeWorldsJar(major, minor, build);
		ThreeWorldsJar fxDepPacker = new ThreeWorldsJar(major, minor, build);
		String twDepFileName = TW_DEP_JAR;
		String twFxFileName = TW_FX_DEP_JAR;
		// main class in manifest
		twDepPacker.setMainClass(MODELMAKER_CLASS);
		//fxDepPacker.setMainClass("javafx.application.Application"); // doesnt work
		// get all dependencies of all 3w libraries
		// and pack them in a single jar
		// this puts in everything since tw-uifx depends on all libraries
		System.out.println("Packing 3worlds files and dependencies into " + twDepFileName);
		List<String> other = new ArrayList<>();
		List<String> tw = new ArrayList<>();
		for (String s : new DependencySolver(buildTwApplicationIvyFile().toString()).getJars()) {
			if (s.contains("javafx-base") || 
				s.contains("javafx-swing") || 
				s.contains("javafx-controls") || 
				s.contains("javafx-web") || 
				s.contains("javafx-graphics") || 
				s.contains("javafx-media") ||
				s.contains("javafx-fxml")) {
//				System.out.println("JAVAFX: " + s);
				fxDepPacker.addJar(s);
			} else
				twDepPacker.addJar(s);
			String name = new File(s).getName();
			if (s.contains("au.") || s.contains("fr."))
				tw.add(name);
			else if (!other.contains(name))
				other.add(name);
		}
		// TODO update manifest in dep with fxDep
		twDepPacker.addDependencyOnJar("./"+twFxFileName);
//		twDepPacker.addDependencyOnJar(twFxFileName); // doesnt change anything
		System.out.println("packing jar...");
		// write jar
		File depJarFile = jarFile(twDepFileName);
		twDepPacker.saveJar(depJarFile);
		// set executable
		depJarFile.setExecutable(true, false);
		File fxDepJarFile = jarFile(twFxFileName);
		fxDepPacker.saveJar(fxDepJarFile);
		
		
		// output to console
		Collections.sort(other);
		Collections.sort(tw);
		System.out.println("------------- THIRD PARTY ---------------");
		int count = 0;
		for (String s : other)
			System.out.println(++count + "\t" + s);
		System.out.println("------------- 3WORLDS LIBS --------------");
		count = 0;
		for (String s : tw)
			System.out.println(++count + "\t" + s);
		System.out.println("\n" + depJarFile.getName() + " ["
				+ new DecimalFormat("#.##").format(depJarFile.length() / 1048576.0) + " Mb.]");
		System.out.println(fxDepJarFile.getName() + " ["
				+ new DecimalFormat("#.##").format(fxDepJarFile.length() / 1048576.0) + " Mb.]\n");
	}

	// copied from https://www.baeldung.com/java-compress-and-uncompress
	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				// added to only store tw.jar
				if (childFile.getName().equals(TW_DEP_JAR))
					// end
					zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	// copied & adapted from https://www.baeldung.com/java-compress-and-uncompress
	private static void zipDir(String directory, String zipFile) {
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			File fileToZip = new File(directory);
			zipFile(fileToZip, fileToZip.getName(), zipOut);
			zipOut.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void confirmVersionUpgrade(String oldv, String newv) {
		System.out.print(
				"Upgrading '" + TW_DEP_JAR + "' from version " + oldv + " to version " + newv.toString() + " (Y/n)? ");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();
			if (s.startsWith("N") || s.startsWith("n")) {
				System.out.println("OK, OK. Why bother me if you don't want to do it? Aborting.");
				System.exit(0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Creates the 3w directory and the 3Worlds jar if they do not exist. Can also
	 * package them in a zip for distribution.
	 * 
	 * Arguments: 0, 1 or 2 args. no arg: just regenerate tw.jar into 3w using
	 * stored version data -build: regenerate tw.jar, increasing build number only
	 * -minor: regenerate tw.jar, increasing minor number and setting build to zero
	 * -major: regenerate tw.jar, increasing major number and setting build and
	 * minor to zero -zip: also make a zip file for distribution
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Logging.setDefaultLogLevel(Level.OFF);

		// version data
		StringBuilder version = new StringBuilder();
		int major = 0, minor = 0, build = 0;
		boolean pack = false;

		// read current version
		File vfile = Paths.get(workDir, packageDir, "tw-jar-version.txt").toFile();
		if (vfile.exists())
			try {
				BufferedReader fr = new BufferedReader(new FileReader(vfile));
				String line = fr.readLine();
				while (line != null) {
					if (line.startsWith("MAJOR"))
						major = Integer.valueOf(line.split("=")[1]);
					if (line.startsWith("MINOR"))
						minor = Integer.valueOf(line.split("=")[1]);
					if (line.startsWith("BUILD"))
						build = Integer.valueOf(line.split("=")[1]);
					line = fr.readLine();
				}
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		// else all version numbers are zero.
		String oldv = major + DOT + minor + DOT + build;

		// process command line arguments
		boolean argError = false;
		if (args.length == 0)
			; // do nothing
		else if (args.length == 1) {
			if (args[0].equals("-zip"))
				pack = true;
			else if (args[0].equals("-major")) {
				major++;
				minor = 0;
				build = 0;
			} else if (args[0].equals("-minor")) {
				minor++;
				build = 0;
			} else if (args[0].equals("-build")) {
				build++;
			} else
				argError = true;
		} else if (args.length == 2) {
			int vindex = 0;
			if (args[0].equals("-zip")) {
				pack = true;
				vindex = 1;
			} else if (args[1].equals("-zip")) {
				pack = true;
				vindex = 0;
			} else
				argError = true;
			if (!argError) {
				if (args[vindex].equals("-major")) {
					major++;
					minor = 0;
					build = 0;
				} else if (args[vindex].equals("-minor")) {
					minor++;
					build = 0;
				} else if (args[vindex].equals("-build")) {
					build++;
				} else
					argError = true;
			}
		} else
			argError = true;
		// exit if any error in arguments
		if (argError) {
			System.out.println("Wrong arguments. Usage ('[...]' means 'optional'):\n" + "Setup [version] [option]\n"
					+ "  no arguments: regenerate " + TW_DEP_JAR + " using last version information\n"
					+ "  1 or 2 arguments:\n" + "    option = \"-zip\": regenerate " + TW_DEP_JAR
					+ " and zip it for distribution\n" + "    version = \"-build\": regenerate " + TW_DEP_JAR
					+ " increasing 'build' version number\n" + "    version = \"-minor\": regenerate " + TW_DEP_JAR
					+ " increasing 'minor' version number\n" + "    version = \"-major\": regenerate " + TW_DEP_JAR
					+ " increasing 'major' version number\n" + "Aborting.");
			System.exit(1);
		}
		version.append(major).append(DOT).append(minor).append(DOT).append(build);
		String os = System.getProperty("os.name").toLowerCase();
		// both manifests will have os - tw.jar doesnt need this but leave for now for debugging.
		String zipFileName = null;
		if (pack)
			zipFileName = TW_ROOT + "-" + os + DOT + version.toString() + DOT + "zip";

		// last chance to exit without harm
		if (args.length == 0)
			System.out.println("Regenerating '" + TW_DEP_JAR + "' version " + os + DOT + version.toString());
		else if (args.length == 1) {
			if (pack == true) {
				System.out.println("Regenerating '" + TW_DEP_JAR + "' version " + os + DOT + version.toString()
						+ " and packing it into '" + zipFileName + "'");
			} else {
				confirmVersionUpgrade(oldv, version.toString());
				System.out.println("Generating '" + TW_DEP_JAR + "' version " + os + DOT + version.toString());
			}
		} else if (args.length == 2) {
			confirmVersionUpgrade(oldv, version.toString());
			System.out.println("Generating '" + TW_DEP_JAR + "' version " + os + DOT + version.toString()
					+ " and packing it into '" + zipFileName + "'");
		}

		// save new version into version file
		vfile = Paths.get(workDir, packageDir, "tw-jar-version.txt").toFile();
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(vfile));
			fw.write("//GENERATED - DO NOT EDIT THIS FILE");
			fw.newLine();
			fw.write("MAJOR=" + major);
			fw.newLine();
			fw.write("MINOR=" + minor);
			fw.newLine();
			fw.write("BUILD=" + build);
			fw.newLine();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// now do the real work
		System.out.println("Setting up local 3Worlds environment:");
		System.out.println("Creating the '" + TW_ROOT + "' directory");
		pack3wAll(os + DOT + Integer.toString(major), Integer.toString(minor), Integer.toString(build));
		FileUtilities.deleteFileTree(new File(DependencySolver.destPath));
		if (pack) {
			System.out.println("Writing zip file '" + zipFileName + "' for distribution");
			zipDir(TW_ROOT, zipFileName);
		}
		System.out.println("FINISHED");
	}
}
