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
package org.eclipse.rcptt.reporting.internal;

import static org.eclipse.rcptt.reporting.internal.ReportUtils.getFailMessage;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;

import org.eclipse.rcptt.reporting.Q7Info;
import org.eclipse.rcptt.reporting.Q7Statistics;
import org.eclipse.rcptt.reporting.ResultStatus;
import org.eclipse.rcptt.reporting.core.IQ7ReportConstants;
import org.eclipse.rcptt.sherlock.core.model.sherlock.report.Node;
import org.eclipse.rcptt.sherlock.core.model.sherlock.report.Report;

public class JUnitXMLReportGenerator {

	private static final String formatTime(Node nde) {
		return ReportUtils.formatTime(nde.getEndTime() - nde.getStartTime());
	}

	public void writeContent(Reports reports, OutputStream stream)
			throws CoreException {
		XMLStreamWriter writer = XMLUtils.createWriter(stream);
		try {
			writer.writeStartDocument();
			writer.writeStartElement("testsuites");

			for (String suite : reports.getSuiteIds()) {
				Q7Statistics statistics = ReportUtils
						.calculateStatistics(reports.getReports(suite));
				writeSuite(writer, suite, reports.getReports(suite), statistics);
			}

			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
		} catch (Exception ex) {
			throw new CoreException(Q7ReportingPlugin.createStatus(ex));
		} finally {
			XMLUtils.closeWriter(writer);
		}
	}

	public void writeSuite(XMLStreamWriter writer, String name,
			Iterator<Report> reportList, Q7Statistics stats)
			throws XMLStreamException {

		writer.writeStartElement("testsuite");

		writer.writeAttribute("name", name);
		writer.writeAttribute("time", ReportUtils.formatTime(stats.getTime()));

		writer.writeAttribute("failures", Integer.toString(stats.getFailed()));
		writer.writeAttribute("tests", Integer.toString(stats.getTotal()));
		writer.writeAttribute("skipped", Integer.toString(stats.getSkipped()));
		writer.writeAttribute("errors", "0");

		while (reportList.hasNext()) {
			writeTestcase(writer, reportList.next());
		}

		writer.writeEndElement();
	}

	public void writeTestcase(XMLStreamWriter writer, Report report)
			throws XMLStreamException {
		Node item = report.getRoot();
		Q7Info info = (Q7Info) item.getProperties()
				.get(IQ7ReportConstants.ROOT);
		String name;

		if (info != null && info.getVariant() != null && info.getVariant().size() != 0) {
			name = item.getName() + "_" + ReportUtils.combineNames(info.getVariant(), "_");
		} else {
			name = item.getName();
		}

		writer.writeStartElement("testcase");
		writer.writeAttribute("name", name);
		writer.writeAttribute("time", formatTime(item));

		if (info != null && !info.getResult().equals(ResultStatus.PASS)) {
			if (info.getResult().equals(ResultStatus.SKIPPED)) {
				writer.writeAttribute("incomplete", "true");
			} else {
				writer.writeStartElement("failure");

				writer.writeAttribute("type", "testcase");
				writer.writeAttribute("message", getFailMessage(item));

				String data = ReportUtils.getDetails(item).trim();
				if (data != null && !data.trim().isEmpty()) {
					writer.writeCData(data);
				}

				writer.writeEndElement();
			}
		}

		writer.writeEndElement();
	}

	public static void collectFailures(Node item, StringBuilder result) {
		Q7Info info = (Q7Info) item.getProperties()
				.get(IQ7ReportConstants.ROOT);
		if (info != null && info.getResult().equals(ResultStatus.FAIL)) {
			String msg = info.getMessage();
			if (msg.contains("]]>")) {
				msg = msg.replace("]]>", "]] >");
			}
			if (msg != null) {
				result.append(msg).append(" [" + item.getName() + "]")
						.append("\n");
			}
		}
		EList<Node> children = item.getChildren();
		for (Node node : children) {
			collectFailures(node, result);
		}
	}
}
