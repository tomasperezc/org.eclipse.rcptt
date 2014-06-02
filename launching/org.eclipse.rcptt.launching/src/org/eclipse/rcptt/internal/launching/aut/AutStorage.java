/*******************************************************************************
 * Copyright (c) 2009, 2014 Xored Software Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Xored Software Inc - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.rcptt.internal.launching.aut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import org.eclipse.rcptt.launching.Aut;

/**
 * Thread-safe AUT storage with quick access by name and launch configuration
 */
public class AutStorage {

	public synchronized List<Aut> getAll() {
		return new ArrayList<Aut>(auts);
	}

	public synchronized void add(BaseAut aut) {
		auts.add(aut);
		byName.put(aut.getName(), aut);
		byConfig.put(aut.getConfig(), aut);
	}

	public synchronized void remove(Aut aut) {
		auts.remove(aut);
		byName.remove(aut.getName());
		byConfig.remove(aut.getConfig());
	}

	public synchronized BaseAut removeByLaunch(ILaunchConfiguration config) {
		BaseAut aut = getByLaunch(config);
		if (aut != null) {
			remove(aut);
		}
		return aut;
	}

	public synchronized BaseAut getByName(String name) {
		return byName.get(name);
	}

	public BaseAut getByLaunch(ILaunch launch) {
		return getByLaunch(launch.getLaunchConfiguration());
	}

	public synchronized BaseAut getByLaunch(ILaunchConfiguration config) {
		return byConfig.get(shell(config));
	}

	private ILaunchConfiguration shell(ILaunchConfiguration config) {
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			config = ((ILaunchConfigurationWorkingCopy) config).getOriginal();
		}
		return config;
	}

	private List<BaseAut> auts = new ArrayList<BaseAut>();
	private Map<String, BaseAut> byName = new HashMap<String, BaseAut>();
	private Map<ILaunchConfiguration, BaseAut> byConfig = new HashMap<ILaunchConfiguration, BaseAut>();
}
