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

/**
 * 
 */
package org.eclipse.xsemantics.runtime.validation;

import org.eclipse.xsemantics.runtime.ErrorInformation;
import org.eclipse.xsemantics.runtime.ResultWithFailure;
import org.eclipse.xsemantics.runtime.RuleFailedException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.inject.Inject;

/**
 * This class will be used by the validator generated by Xsemantics.
 * 
 * @author Lorenzo Bettini
 * 
 */
public class XsemanticsValidatorErrorGenerator {

	@Inject
	XsemanticsValidatorFilter filter;

	public void generateErrors(
			ValidationMessageAcceptor validationMessageAcceptor,
			ResultWithFailure resultWithFailure, EObject originalSource) {
		generateErrors(validationMessageAcceptor,
				resultWithFailure.getRuleFailedException(), originalSource);
	}

	protected void generateErrors(
			ValidationMessageAcceptor validationMessageAcceptor,
			RuleFailedException ruleFailedException, EObject originalSource) {
		if (ruleFailedException == null) {
			return;
		}
		Iterable<RuleFailedException> allFailures = filter
				.filterRuleFailedExceptions(ruleFailedException);
		// the last information about a model element with error
		ErrorInformation lastErrorInformationWithSource = null;
		// we will use it to print error messages which do not have
		// an associated model element
		for (RuleFailedException ruleFailedException2 : allFailures) {
			lastErrorInformationWithSource = generateErrors(
					validationMessageAcceptor,
					ruleFailedException2.getMessage(),
					ruleFailedException2.getIssue(),
					filter.filterErrorInformation(ruleFailedException2),
					lastErrorInformationWithSource, originalSource);
		}
	}

	protected ErrorInformation generateErrors(
			ValidationMessageAcceptor validationMessageAcceptor,
			String errorMessage, String issue,
			Iterable<ErrorInformation> filteredErrorInformation,
			ErrorInformation lastErrorInformationWithSource,
			EObject originalSource) {
		
		ErrorInformation errorInformationToReturn = lastErrorInformationWithSource;
		if (filteredErrorInformation.iterator().hasNext()) {
			for (ErrorInformation errorInformation : filteredErrorInformation) {
				error(validationMessageAcceptor, errorMessage,
						errorInformation.getSource(),
						errorInformation.getFeature(), issue);
				errorInformationToReturn = errorInformation;
			}
		} else {
			if (lastErrorInformationWithSource != null) {
				error(validationMessageAcceptor, errorMessage,
						lastErrorInformationWithSource.getSource(),
						lastErrorInformationWithSource.getFeature(), issue);
			} else {
				error(validationMessageAcceptor, errorMessage, originalSource,
						null, issue);
			}
		}
		return errorInformationToReturn;
	}

	protected void error(ValidationMessageAcceptor validationMessageAcceptor,
			String message, EObject source, EStructuralFeature feature,
			String code, String... issueData) {
		validationMessageAcceptor.acceptError(message, source, feature,
				ValidationMessageAcceptor.INSIGNIFICANT_INDEX, code, issueData);
	}
}
