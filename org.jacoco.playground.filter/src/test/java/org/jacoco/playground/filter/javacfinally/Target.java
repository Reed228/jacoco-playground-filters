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
package org.jacoco.playground.filter.javacfinally;

public class Target {

	public void simple() {
		try {
			a();
		} finally {
			b();
		}
	}

	public void withCatch() {
		try {
			a();
		} catch (Exception e) {
			b();
		} finally {
			c();
		}
	}

	public void controlStructure(boolean flag) {
		try {
			a();
		} finally {
			if (flag) {
				b();
			} else {
				c();
			}
		}
	}

	public void nested() {
		try {
			a();
		} finally {
			try {
				b();
			} finally {
				c();
			}
		}
	}

	private void a() {
	}

	private void b() {
	}

	private void c() {
	}

}
