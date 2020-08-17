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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class IvyFile {

	private PrintStream output; 
	private List<IvyDependency> dependencies = new ArrayList<>();
	
	public IvyFile(PrintStream output) {
		this.output = output;
	}
	
	public void addDependency(IvyDependency dependency) {
		dependencies.add(dependency);
	}
	
	public void buildIvyFile() {
		output.println("<ivy-module version=\"2.0\">");
		output.println("  <info organisation=\"au.edu.anu.rscs\" module=\"threeWorlds\" />");
		output.println("  <dependencies>");
		for (IvyDependency dep : dependencies)
			output.println("    " + dep.dependencyXml());
		output.println("  </dependencies>");
		output.println("</ivy-module>");
		output.close();
	}
	
//	// QUICK TEST
//	
//	public static void main(String[] args) {
//		IvyFile ivyFile = new IvyFile(System.out);
//		ivyFile.addDependency(new IvyDependency("org.graphstream", "gs-core", "1.2"));
//		ivyFile.addDependency(new IvyDependency("org.graphstream", "gs-algo", "1.2"));
//		ivyFile.addDependency(new IvyDependency("org.graphstream", "gs-ui", "1.2"));
//		ivyFile.buildIvyFile();
//	}
}
