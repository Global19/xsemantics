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
package org.eclipse.xsemantics.example.expressions.scoping;

import org.eclipse.xsemantics.example.expressions.expressions.Model;
import org.eclipse.xsemantics.example.expressions.expressions.Variable;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 * 
 */
public class ExpressionsScopeProvider extends AbstractDeclarativeScopeProvider {

	public IScope scope_Variable(Variable variable, EReference ref) {
		// only the variables declared before this one
		Model model = EcoreUtil2.getContainerOfType(variable, Model.class);
		if (model == null)
			return IScope.NULLSCOPE;
		EList<Variable> variables = model.getVariables();
		return Scopes
				.scopeFor(variables.subList(0, variables.indexOf(variable)));
	}
}
