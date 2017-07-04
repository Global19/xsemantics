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
package it.xsemantics.example.expressions.typing;

import it.xsemantics.runtime.StringRepresentation;

/**
 * @author Lorenzo Bettini
 * 
 */
public class ExpressionsStringRepresentation extends StringRepresentation {

	protected String stringRep(String s) {
		return "'" + s + "'";
	}
}
