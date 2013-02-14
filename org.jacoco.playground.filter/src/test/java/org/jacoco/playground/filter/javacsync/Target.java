/*******************************************************************************
 * Copyright (c) 2009, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.playground.filter.javacsync;

public class Target {

	private final Object lock1 = new Object();
	private final Object lock2 = new Object();

	public void sync() {
		synchronized (lock1) {
			doit();
		}
	}

	public void syncWithReturn() {
		synchronized (lock1) {
			doit();
			return;
		}
	}

	public void syncNested() {
		synchronized (lock1) {
			doit();
			synchronized (lock2) {
				doit();
			}
			doit();
		}
	}

	public void negativCatch() {
		try {
			doit();
		} catch (UnsupportedOperationException ex) {
			doit();
		}
	}

	public void negativFinally() {
		try {
			doit();
		} finally {
			doit();
		}
	}

	private void doit() {
	}

}
