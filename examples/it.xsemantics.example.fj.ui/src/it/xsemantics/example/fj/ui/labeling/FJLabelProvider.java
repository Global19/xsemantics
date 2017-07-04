/*******************************************************************************
 * Copyright (c) 2013-2017 Lorenzo Bettini.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Lorenzo Bettini - Initial contribution and API
 *******************************************************************************/

/*
 * generated by Xtext
 */
package it.xsemantics.example.fj.ui.labeling;

import it.xsemantics.example.fj.fj.*;

import java.util.ListIterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class FJLabelProvider extends DefaultEObjectLabelProvider {
	/*
	 * 
	 * //Labels and icons can be computed like this:
	 * 
	 * String text(MyModel ele) { return "my "+ele.getName(); }
	 * 
	 * String image(MyModel ele) { return "MyModel.gif"; }
	 */
	String image(it.xsemantics.example.fj.fj.Class ele) {
		return "class_obj.gif";
	}

	String image(Field f) {
		return "field_public_obj.gif";
	}

	String text(Field f) {
		return f.getName() + " : " + getText(f.getType());
	}

	String text(BasicType type) {
		return type.getBasic();
	}
	
	String text(ClassType type) {
		return (type.getClassref() != null ? type.getClassref().getName()
				: "");
	}

	String image(Method m) {
		return "methdef_obj.gif";
	}

	String text(Method m) {
		return m.getName() + "(" + listToText(m.getParams()) + ") : "
				+ getText(m.getType());
	}
	
	String text(Parameter param) {
		return getText(param.getType());
	}

	String text(Expression e) {
		if (e.eContainer().eClass().getClassifierID() == FjPackage.PROGRAM) {
			return "Main";
		}

		return "expression";
	}
	
	String image(Expression e) {
		if (e.eContainer().eClass().getClassifierID() == FjPackage.PROGRAM) {
			return "methpub_obj.gif";
		}

		return null;
	}
	
	String listToText(EList<Parameter> list) {
		StringBuffer buffer = new StringBuffer();
		ListIterator<Parameter> it = list.listIterator();
		while (it.hasNext()) {
			buffer.append(text(it.next()));
			if (it.hasNext())
				buffer.append(", ");
		}
		return buffer.toString();
	}

}
