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

package it.xsemantics.dsl.typing

import com.google.inject.Singleton
import it.xsemantics.dsl.xsemantics.EnvironmentAccess
import it.xsemantics.dsl.xsemantics.Fail
import it.xsemantics.dsl.xsemantics.OrExpression
import it.xsemantics.dsl.xsemantics.RuleInvocation
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.typesystem.legacy.XbaseBatchTypeProvider

/**
 * Custom version of XbaseTypeProvider for Custom XExpressions
 * introduced by Xsemantics
 */
@Singleton
class XsemanticsTypeProvider extends XbaseBatchTypeProvider {
	
	override getType(XExpression e) {
		type(e)
	}
	
	def dispatch type(XExpression e) {
		super.getType(e)
	}
	
	def dispatch type(RuleInvocation exp) {
		typeReferences.createAnyTypeReference(exp)
	}
	
	def dispatch type(OrExpression exp) {
		typeReferences.createAnyTypeReference(exp)
	}

	def dispatch type(Fail exp) {
		typeReferences.createAnyTypeReference(exp)
	}

	def dispatch type(EnvironmentAccess exp) {
		exp.getType
	}
}