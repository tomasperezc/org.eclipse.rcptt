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
package org.eclipse.rcptt.tesla.ecl.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.rcptt.ecl.core.Command;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Return From Os Dialog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.rcptt.tesla.ecl.model.ReturnFromOsDialog#getKind <em>Kind</em>}</li>
 *   <li>{@link org.eclipse.rcptt.tesla.ecl.model.ReturnFromOsDialog#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.rcptt.tesla.ecl.model.TeslaPackage#getReturnFromOsDialog()
 * @model annotation="http://www.eclipse.org/ecl/docs description='Emulates result returning from native dialog.' returns='nothing'"
 * @generated
 */
public interface ReturnFromOsDialog extends Command {
	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see #setKind(String)
	 * @see org.eclipse.rcptt.tesla.ecl.model.TeslaPackage#getReturnFromOsDialog_Kind()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/ecl/docs description='Must be one of followings: FILE_SELECTOR, FOLDER_SELECTOR, MESSAGE_BOX, FONT_DIALOG, COLOR'"
	 * @generated
	 */
	String getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.rcptt.tesla.ecl.model.ReturnFromOsDialog#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see #getKind()
	 * @generated
	 */
	void setKind(String value);

	/**
	 * Returns the value of the '<em><b>Result</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result</em>' attribute list.
	 * @see org.eclipse.rcptt.tesla.ecl.model.TeslaPackage#getReturnFromOsDialog_Result()
	 * @model annotation="http://www.eclipse.org/ecl/docs description='String presentation of returned value (platform specific).'"
	 * @generated
	 */
	EList<String> getResult();

} // ReturnFromOsDialog
