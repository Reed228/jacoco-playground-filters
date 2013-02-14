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
package org.jacoco.playground.filter.designprivdefctor;

import java.io.IOException;

import org.jacoco.playground.filter.DesignPrivDefCtor;
import org.jacoco.playground.filter.FilterTestBase;
import org.jacoco.playground.filter.IFilter;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

public class DesignPrivDefCtorTest extends FilterTestBase {

	@Override
	protected IFilter getFilter() {
		return new DesignPrivDefCtor();
	}

	@Test
	public void emptyPrivateConstructor() throws IOException {
		applyFilterTo(Target1.class, "<init>", "()V");

		assertFilteredInsn(Opcodes.ALOAD);
		assertFilteredMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
				"<init>", "()V");
		assertFilteredInsn(Opcodes.RETURN);
		assertNoMoreFilteredInsn();
	}

	@Test
	public void noConstructor() throws IOException {
		applyFilterTo(Target1.class, "doit", "()V");

		assertNoMoreFilteredInsn();
	}

	@Test
	public void emptyPrivateConstructorWithArguments() throws IOException {
		applyFilterTo(Target1.class, "<init>", "(Ljava/lang/Object;)V");

		assertNoMoreFilteredInsn();
	}

	@Test
	public void nonEmptyConstructor() throws IOException {
		applyFilterTo(Target2.class, "<init>", "()V");

		assertNoMoreFilteredInsn();
	}

	@Test
	public void nonPrivateConstructor() throws IOException {
		applyFilterTo(Target3.class, "<init>", "()V");

		assertNoMoreFilteredInsn();
	}

}
