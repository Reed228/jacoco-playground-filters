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

import java.io.IOException;

import org.jacoco.playground.filter.FilterTestBase;
import org.jacoco.playground.filter.IFilter;
import org.jacoco.playground.filter.JavacSyncFilter;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

public class JavacSyncFilterTest extends FilterTestBase {

	@Override
	protected Class<?> getTarget() {
		return Target.class;
	}

	@Override
	protected IFilter getFilter() {
		return new JavacSyncFilter();
	}

	@Test
	public void sync() throws IOException {
		applyFilterTo("sync", "()V");

		assertOptionalFilteredInsn(Opcodes.ASTORE); // JDK Only
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.MONITOREXIT);
		assertOptionalFilteredInsn(Opcodes.ALOAD); // JDK Only
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void syncWithReturn() throws IOException {
		applyFilterTo("syncWithReturn", "()V");

		assertOptionalFilteredInsn(Opcodes.ASTORE); // JDK Only
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.MONITOREXIT);
		assertOptionalFilteredInsn(Opcodes.ALOAD); // JDK Only
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void syncNested() throws IOException {
		applyFilterTo("syncNested", "()V");

		assertOptionalFilteredInsn(Opcodes.ASTORE); // JDK Only
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.MONITOREXIT);
		assertOptionalFilteredInsn(Opcodes.ALOAD); // JDK Only
		assertFilteredInsn(Opcodes.ATHROW);

		assertOptionalFilteredInsn(Opcodes.ASTORE); // JDK Only
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.MONITOREXIT);
		assertOptionalFilteredInsn(Opcodes.ALOAD); // JDK Only
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void negativCatch() throws IOException {
		applyFilterTo("negativCatch", "()V");

		assertNoMoreFilteredInsn();
	}

	@Test
	public void negativFinally() throws IOException {
		applyFilterTo("negativFinally", "()V");

		assertNoMoreFilteredInsn();
	}

}
