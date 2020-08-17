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

import au.edu.anu.omhtk.jars.Jars;

/**
 * The jar-making class for 3Worlds.
 * 
 * @author Jacques Gignoux - 12 août 2019
 * 
 * NB: this is completely different from the previous version of this class.
 *
 */
public class ThreeWorldsJar extends Jars{

	public ThreeWorldsJar(String major, String minor, String micro) {
		super();
		version = major+"."+minor+"."+micro;
		specVendor = "Gignoux, Davies & Flint";
		specTitle = "3Worlds";
	}

	
}
