/**************************************************************************
 *  TW-SETUP - tw-dep.jar generator                                       *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  This module is specifically for generating the dependency file for    *
 *  3Worlds: tw-dep.jar.                                                  *
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

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import au.edu.anu.twcore.project.Project;

import static au.edu.anu.twcore.project.Project.*;

public class TwDepClassInfo {

	public static void main(String[] args) {
//		String fn = System.getProperty("user.home") + "/.3w/tw-dep.jar";
		String fn = Project.USER_ROOT_TW_ROOT+File.pathSeparator+TW_DEP_JAR;
		File f = new File(fn);
		long sumClassCompressed = 0;
		long sumClassUncompressed = 0;
		JarFile jf;
		try {
			// NB: Don't use jar input streams. These will not contain the info required!
			// Not much use doing the same for .java because they are not all there so
			// comparison with .class file means nothing.
			jf = new JarFile(f.getAbsolutePath());
			Enumeration<JarEntry> e = jf.entries();
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
