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

public class IvyDependency {
	
	private String org;
	private String name;
	private String rev;
	
	public IvyDependency(String org, String name, String rev) {
		this.org = org;
		this.name = name;
		this.rev = rev;
	}
	
	public String getOrg() {
		return org;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRev() {
		return rev;
	}
	
	public String dependencyXml() {
		return "<dependency org=\"" + org + "\" name=\"" + name + "\" rev=\"" + rev + "\" />";
	}
	
}
