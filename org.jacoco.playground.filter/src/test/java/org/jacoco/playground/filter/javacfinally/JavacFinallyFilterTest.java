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

import java.io.IOException;

import org.jacoco.playground.filter.FilterTestBase;
import org.jacoco.playground.filter.IFilter;
import org.jacoco.playground.filter.JavacFinallyFilter;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

public class JavacFinallyFilterTest extends FilterTestBase {

	@Override
	protected IFilter getFilter() {
		return new JavacFinallyFilter();
	}

	@Test
	public void simple() throws IOException {
		applyFilterTo(Target.class, "simple", "()V");

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "b");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void withCatch() throws IOException {
		applyFilterTo(Target.class, "withCatch", "()V");

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void controlStructure() throws IOException {
		applyFilterTo(Target.class, "controlStructure", "(Z)V");

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ILOAD);
		assertFilteredInsn(Opcodes.IFEQ);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "b");
		assertFilteredInsn(Opcodes.GOTO);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}

	@Test
	public void nested() throws IOException {
		applyFilterTo(Target.class, "nested", "()V");

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "b");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.GOTO);
		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertFilteredInsn(Opcodes.ASTORE);
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "c");
		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredInsn(Opcodes.ATHROW);

		assertNoMoreFilteredInsn();
	}
}
