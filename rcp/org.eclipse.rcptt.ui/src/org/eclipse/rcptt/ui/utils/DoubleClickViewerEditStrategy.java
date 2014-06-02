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
package org.eclipse.rcptt.ui.utils;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MouseEvent;

/**
 * Tree/Table Viewer strategy to activate cell editing on double click 
 */
public class DoubleClickViewerEditStrategy extends
		ColumnViewerEditorActivationStrategy {

	public DoubleClickViewerEditStrategy(ColumnViewer viewer) {
		super(viewer);
	}

	/**
	 * @param event
	 *            the event triggering the action
	 * @return <code>true</code> if this event should open the editor
	 */
	protected boolean isEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		boolean singleSelect = ((IStructuredSelection) getViewer()
				.getSelection()).size() == 1;
		boolean isLeftMouseSelect = event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				&& ((MouseEvent) event.sourceEvent).button == 1;

		return singleSelect
				&& (isLeftMouseSelect
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC || event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL);
	}

}
