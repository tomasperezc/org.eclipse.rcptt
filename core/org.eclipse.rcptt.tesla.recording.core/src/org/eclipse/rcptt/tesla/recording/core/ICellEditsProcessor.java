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
package org.eclipse.rcptt.tesla.recording.core;

public interface ICellEditsProcessor {

	public boolean activateCellEdit(Object editor,
			Object event);

	public boolean applyCellEdit(Object editor);

	public boolean cancelCellEdit(Object editor);

	public boolean deactivateCellEdit(Object editor);

}
