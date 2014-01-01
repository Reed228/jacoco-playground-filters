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
package org.jacoco.playground.filter;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import java.io.IOException;

import org.jacoco.playground.filter.DesignPrivDefCtorFilter;
import org.jacoco.playground.filter.IFilter;
import org.junit.Test;

public class DesignPrivDefCtorFilterTest extends FilterTestBase {

	@Override
	protected IFilter getFilter() {
		return new DesignPrivDefCtorFilter();
	}

	/**
	 * <pre>
	 * private Target() {
	 * }
	 * </pre>
	 */
	@Test
	public void emptyPrivateConstructor() throws IOException {
		target = new TargetMethod(ACC_PRIVATE, "<init>", "()V") {
			{
				L(1).visitInsn(ALOAD);
				L(2).visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
						"<init>", "()V");
				L(3).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(1, 2, 3);
	}

	/**
	 * <pre>
	 * private void doit() {
	 * }
	 * </pre>
	 */
	@Test
	public void noConstructor() throws IOException {
		target = new TargetMethod(ACC_PRIVATE, "doit", "()V") {
			{
				L(1).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

	/**
	 * <pre>
	 * private Target(int i) {
	 * }
	 * </pre>
	 */
	@Test
	public void emptyPrivateConstructorWithArguments() throws IOException {
		target = new TargetMethod(ACC_PRIVATE, "<init>", "(I)V") {
			{
				L(1).visitInsn(ALOAD);
				L(2).visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
						"<init>", "()V");
				L(3).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

	/**
	 * <pre>
	 * private Target() {
	 * 	doit();
	 * }
	 * </pre>
	 */
	@Test
	public void nonEmptyConstructor() throws IOException {
		target = new TargetMethod(ACC_PRIVATE, "<init>", "()V") {
			{
				L(1).visitInsn(ALOAD);
				L(2).visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
						"<init>", "()V");
				L(1).visitInsn(ALOAD);
				L(2).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(3).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

	/**
	 * <pre>
	 * public Target() {
	 * }
	 * </pre>
	 */
	@Test
	public void nonPrivateConstructor() throws IOException {
		target = new TargetMethod(ACC_PUBLIC, "<init>", "()V") {
			{
				L(1).visitInsn(ALOAD);
				L(2).visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
						"<init>", "()V");
				L(3).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

}
