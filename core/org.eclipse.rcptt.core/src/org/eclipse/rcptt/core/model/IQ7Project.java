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
package org.eclipse.rcptt.core.model;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface IQ7Project extends IQ7Element, IParent {
	final String METADATA_NAME = "rcptt.properties";
	final String LEGACY_METADATA_NAME = "q7.properties";

	<T> List<T> find(Class<T> class_, String id) throws ModelException;

	IProject getProject();

	IQ7Folder getFolder(IPath path);

	Object[] getForeignResources() throws ModelException;

	public IQ7Project[] getReferences();

	IQ7NamedElement[] findNamedElement(String id) throws ModelException;

	IQ7Folder[] getFolders() throws ModelException;

	IQ7Folder getRootFolder();

	IQ7Folder createFolder(Path path);

	IQ7ProjectMetadata getMetadata() throws ModelException;
}
