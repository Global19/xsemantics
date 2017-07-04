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
package it.xsemantics.example.fjcached;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class FjcachedStandaloneSetup extends FjcachedStandaloneSetupGenerated{

	public static void doSetup() {
		new FjcachedStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

