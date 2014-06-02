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
package org.eclipse.rcptt.tesla.internal.ui.problemview;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.rcptt.tesla.core.Q7WaitUtils;
import org.eclipse.rcptt.tesla.core.context.ContextManagement;
import org.eclipse.rcptt.tesla.core.context.ContextManagement.Context;
import org.eclipse.rcptt.tesla.core.info.AdvancedInformation;
import org.eclipse.rcptt.tesla.core.info.Q7WaitInfoRoot;
import org.eclipse.rcptt.tesla.core.protocol.ElementCommand;
import org.eclipse.rcptt.tesla.core.protocol.ElementKind;
import org.eclipse.rcptt.tesla.core.protocol.IElementProcessorMapper;
import org.eclipse.rcptt.tesla.core.protocol.SelectCommand;
import org.eclipse.rcptt.tesla.core.protocol.SelectResponse;
import org.eclipse.rcptt.tesla.core.protocol.raw.Command;
import org.eclipse.rcptt.tesla.core.protocol.raw.Element;
import org.eclipse.rcptt.tesla.core.protocol.raw.Response;
import org.eclipse.rcptt.tesla.internal.core.AbstractTeslaClient;
import org.eclipse.rcptt.tesla.internal.core.processing.ElementGenerator;
import org.eclipse.rcptt.tesla.internal.core.processing.ITeslaCommandProcessor;
import org.eclipse.rcptt.tesla.internal.ui.player.SWTUIElement;
import org.eclipse.rcptt.tesla.internal.ui.player.UIJobCollector;
import org.eclipse.rcptt.tesla.internal.ui.player.WorkbenchUIElement;
import org.eclipse.rcptt.tesla.internal.ui.processors.SWTUIProcessor;

public class ProblemViewSupportProcessor implements ITeslaCommandProcessor {

	private AbstractTeslaClient client;
	// private String id;
	private Set<String> elements = new HashSet<String>();

	public ProblemViewSupportProcessor() {
	}

	public boolean callMasterProcess(Context currentContext) {
		return false;
	}

	public boolean canProceed(Context context, Q7WaitInfoRoot info) {
		return true;
	}

	public void clean() {
		elements.clear();
	}

	public Response executeCommand(Command command,
			IElementProcessorMapper mapper) {
		return null;
	}

	public String getFeatureID() {
		return null;
	}

	public void initialize(AbstractTeslaClient client, String id) {
		this.client = client;
		// this.id = id;
	}

	public boolean isCommandSupported(Command cmd) {
		return false;
	}

	public boolean isInactivityRequired() {
		return false;
	}

	public boolean isSelectorSupported(String kind) {
		return false;
	}

	public void postSelect(Element element, IElementProcessorMapper mapper) {
		SWTUIProcessor processor = client.getProcessor(SWTUIProcessor.class);
		SWTUIElement swtuiElement = processor.getMapper().get(element);
		if (swtuiElement == null) {
			return;
		}
		List<SWTUIElement> parents = processor.getPlayer().getParentsList(
				swtuiElement);
		parents.add(swtuiElement);
		for (SWTUIElement w : parents) {
			if (w.getKind().is(ElementKind.View)
					&& w instanceof WorkbenchUIElement) {
				WorkbenchUIElement wb = (WorkbenchUIElement) w;
				IWorkbenchPart part = wb.getReference().getPart(true);
				if (part != null) {
					try { 
						if (part instanceof org.eclipse.ui.views.markers.MarkerSupportView) {
							mapper.map(element, this);
							this.elements.add(element.getId() + ":"
									+ element.getKind());
						}
					} catch (Throwable e) {
						// ignore
					}
				}
			}
		}
	}

	private class WaitForJobsStatus extends PreExecuteStatus {
		UIJobCollector collector = new UIJobCollector() {
			protected void addJob(Job job, long delay) {
				super.addJob(job, delay);
				if (isMarkersJob(job)) {
					add(job);
				}
			};

			protected boolean isAsyncSupported() {
				return false;
			};

			protected boolean isSyncSupported() {
				return false;
			};

			public void clean() {
				Job.getJobManager().removeJobChangeListener(collector);
			}

		};

		public WaitForJobsStatus(boolean canExecute) {
			super(canExecute);
		}
	};

	public PreExecuteStatus preExecute(Command command,
			PreExecuteStatus previousStatus, Q7WaitInfoRoot info) {
		if (command instanceof ElementCommand) {
			Element element = ((ElementCommand) command).getElement();
			if (!elements.contains(element.getId() + ":" + element.getKind())) {
				return null;
			}
		} else if (command instanceof SelectCommand) {
			SelectCommand selCmd = (SelectCommand) command;
			Element parent = selCmd.getData().getParent();
			if (parent == null) {
				return null;
			}
			if (parent != null
					&& !elements.contains(parent.getId() + ":"
							+ parent.getKind())) {
				return null;
			}
		} else {
			return null;
		}
		if (previousStatus instanceof WaitForJobsStatus) {
			WaitForJobsStatus s = (WaitForJobsStatus) previousStatus;
			if (!s.collector.isEmpty(ContextManagement.currentContext(), info)) {
				return s;
			}
			Job.getJobManager().removeJobChangeListener(s.collector);
		}
		// Check for marker update job is present
		Job[] find = Job.getJobManager().find(null);
		WaitForJobsStatus st = new WaitForJobsStatus(false);
		for (Job job : find) {
			if (isMarkersJob(job)) {
				st.collector.add(job);
			}
			if (job.belongsTo(ResourcesPlugin.FAMILY_AUTO_BUILD)) {
				st.collector.add(job);
			}
		}
		if (st.collector.isEmpty(ContextManagement.currentContext(), info)) {
			return null;
		}
		Job.getJobManager().addJobChangeListener(st.collector);
		Q7WaitUtils.updateInfo("problems.view", "wait", info);
		return st;
	}

	private boolean isMarkersJob(Job job) {
		String name = job.getClass().getName();
		// This is Eclipse 3.6
		if (name.equals("org.eclipse.ui.internal.views.markers.MarkerUpdateJob")) {
			return true;
		}
		if (is35job(job, "org.eclipse.ui.internal.views.markers.UIUpdateJob")) {
			return true;
		}
		if (is35job(job, "org.eclipse.ui.views.markers.internal.MarkerView",
				"org.eclipse.ui.views.markers.internal.ProblemView")) {
			return true;
		}

		if (is35job(job,
				"org.eclipse.ui.internal.views.markers.CachedMarkerBuilder",
				"org.eclipse.ui.internal.views.markers.ExtendedMarkersView",
				"org.eclipse.jdt.internal.ui.viewsupport.ProblemMarkerManager")) {
			return true;
		}
		return false;
	}

	private boolean is35job(Job job, String... list) {
		try {
			Class<? extends Job> class1 = job.getClass();
			Field this$0 = class1.getDeclaredField("this$0");
			this$0.setAccessible(true);
			Object obj = this$0.get(job);
			if (obj != null) {
				for (String name : list) {
					if (class1.getName().contains(name)) {
						return true;
					}
					if (obj.getClass().getName().equals(name)) {
						return true;
					}
				}
			}
		} catch (Throwable t) {
		}
		return false;
	}

	public SelectResponse select(SelectCommand cmd, ElementGenerator generator,
			IElementProcessorMapper mapper) {
		return null;
	}

	public void terminate() {
		clean();
		client = null;
	}

	public void checkHang() {
		// TODO Auto-generated method stub

	}

	public void collectInformation(AdvancedInformation information,
			Command lastCommand) {
		// TODO Auto-generated method stub

	}

	public void notifyUI() {
		// TODO Auto-generated method stub

	}
}
