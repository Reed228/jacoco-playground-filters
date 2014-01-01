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

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import java.io.IOException;

import org.jacoco.playground.filter.IFilter;
import org.jacoco.playground.filter.JavacFinallyFilter;
import org.junit.Test;

public class JavacFinallyFilterTest extends FilterTestBase {

	@Override
	protected IFilter getFilter() {
		return new JavacFinallyFilter();
	}

	/**
	 * <pre>
	 * void simple() {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		b();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void simpleECJ() throws IOException {
		target = new TargetMethod(0, "simple", "()V") {
			{
				visitTryCatchBlock(0, 3, 3, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitJumpInsn(GOTO, 8);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(6).visitVarInsn(ALOAD, 1);
				L(7).visitInsn(ATHROW);
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(10).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(3, 4, 5, 6, 7);
	}

	/**
	 * <pre>
	 * void simple() {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		b();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void simpleJDK() throws IOException {
		target = new TargetMethod(0, "simple", "()V") {
			{
				visitTryCatchBlock(0, 2, 5, null);
				visitTryCatchBlock(5, 6, 5, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitVarInsn(ALOAD, 0);
				L(3).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(4).visitJumpInsn(GOTO, 10);
				L(5).visitVarInsn(ASTORE, 1);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(8).visitVarInsn(ALOAD, 1);
				L(9).visitInsn(ATHROW);
				L(10).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(5, 6, 7, 8, 9);
	}

	/**
	 * <pre>
	 * void withCatch() {
	 * 	try {
	 * 		a();
	 * 	} catch (Exception e) {
	 * 		b();
	 * 	} finally {
	 * 		c();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void withCatchECJ() throws IOException {
		target = new TargetMethod(0, "withCatch", "()V") {
			{
				visitTryCatchBlock(0, 2, 3, "java/lang/Exception");
				visitTryCatchBlock(0, 6, 9, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitJumpInsn(GOTO, 14);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(8).visitJumpInsn(GOTO, 16);
				L(9).visitVarInsn(ASTORE, 2);
				L(10).visitVarInsn(ALOAD, 0);
				L(11).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(12).visitVarInsn(ALOAD, 2);
				L(13).visitInsn(ATHROW);
				L(14).visitVarInsn(ALOAD, 0);
				L(15).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(16).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(9, 10, 11, 12, 13); // TODO catch clause
	}

	/**
	 * <pre>
	 * void withCatch() {
	 * 	try {
	 * 		a();
	 * 	} catch (Exception e) {
	 * 		b();
	 * 	} finally {
	 * 		c();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void withCatchJDK() throws IOException {
		target = new TargetMethod(0, "withCatch", "()V") {
			{
				visitTryCatchBlock(0, 2, 5, "java/lang/Exception");
				visitTryCatchBlock(0, 2, 11, null);
				visitTryCatchBlock(5, 8, 11, null);
				visitTryCatchBlock(11, 12, 11, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitVarInsn(ALOAD, 0);
				L(3).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(4).visitJumpInsn(GOTO, 16);
				L(5).visitVarInsn(ASTORE, 1);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(10).visitJumpInsn(GOTO, 16);
				L(11).visitVarInsn(ASTORE, 2);
				L(12).visitVarInsn(ALOAD, 0);
				L(13).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(14).visitVarInsn(ALOAD, 2);
				L(15).visitInsn(ATHROW);
				L(16).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(11, 12, 13, 14, 15); // TODO catch clause
	}

	/**
	 * <pre>
	 * void controlStructure(boolean flag) {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		if (flag) {
	 * 			b();
	 * 		} else {
	 * 			c();
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void controlStructureECJ() throws IOException {
		target = new TargetMethod(0, "controlStructure", "(Z)V") {
			{
				visitTryCatchBlock(0, 3, 3, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitJumpInsn(GOTO, 13);
				L(3).visitVarInsn(ASTORE, 2);
				L(4).visitVarInsn(ILOAD, 1);
				L(5).visitJumpInsn(IFEQ, 9);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(8).visitJumpInsn(GOTO, 11);
				L(9).visitVarInsn(ALOAD, 0);
				L(10).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(11).visitVarInsn(ALOAD, 2);
				L(12).visitInsn(ATHROW);
				L(13).visitVarInsn(ILOAD, 1);
				L(14).visitJumpInsn(IFEQ, 18);
				L(15).visitVarInsn(ALOAD, 0);
				L(16).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(17).visitJumpInsn(GOTO, 20);
				L(18).visitVarInsn(ALOAD, 0);
				L(19).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(20).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
	}

	/**
	 * <pre>
	 * void controlStructure(boolean flag) {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		if (flag) {
	 * 			b();
	 * 		} else {
	 * 			c();
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void controlStructureJDK() throws IOException {
		target = new TargetMethod(0, "controlStructure", "(Z)V") {
			{
				visitTryCatchBlock(0, 2, 10, null);
				visitTryCatchBlock(10, 11, 10, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitVarInsn(ILOAD, 1);
				L(3).visitJumpInsn(IFEQ, 7);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(6).visitJumpInsn(GOTO, 20);
				L(7).visitVarInsn(ALOAD, 0);
				L(8).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(9).visitJumpInsn(GOTO, 20);
				L(10).visitVarInsn(ASTORE, 2);
				L(11).visitVarInsn(ILOAD, 1);
				L(12).visitJumpInsn(IFEQ, 16);
				L(13).visitVarInsn(ALOAD, 0);
				L(14).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(15).visitJumpInsn(GOTO, 18);
				L(16).visitVarInsn(ALOAD, 0);
				L(17).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(18).visitVarInsn(ALOAD, 2);
				L(19).visitInsn(ATHROW);
				L(20).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
	}

	/**
	 * <pre>
	 * void nested() {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		try {
	 * 			b();
	 * 		} finally {
	 * 			c();
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void nestedECJ() throws IOException {
		target = new TargetMethod(0, "nested", "()V") {
			{
				visitTryCatchBlock(0, 3, 3, null);
				visitTryCatchBlock(4, 7, 7, null);
				visitTryCatchBlock(16, 19, 19, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitJumpInsn(GOTO, 16);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(6).visitJumpInsn(GOTO, 12);
				L(7).visitVarInsn(ASTORE, 2);
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(10).visitVarInsn(ALOAD, 2);
				L(11).visitInsn(ATHROW);
				L(12).visitVarInsn(ALOAD, 0);
				L(13).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(14).visitVarInsn(ALOAD, 1);
				L(15).visitInsn(ATHROW);
				L(16).visitVarInsn(ALOAD, 0);
				L(17).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(18).visitJumpInsn(GOTO, 24);
				L(19).visitVarInsn(ASTORE, 2);
				L(20).visitVarInsn(ALOAD, 0);
				L(21).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(22).visitVarInsn(ALOAD, 2);
				L(23).visitInsn(ATHROW);
				L(24).visitVarInsn(ALOAD, 0);
				L(25).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(26).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 19, 20, 21,
				22, 23);
	}

	/**
	 * <pre>
	 * void nested() {
	 * 	try {
	 * 		a();
	 * 	} finally {
	 * 		try {
	 * 			b();
	 * 		} finally {
	 * 			c();
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void nestedJDK() throws IOException {
		target = new TargetMethod(0, "nested", "()V") {
			{
				visitTryCatchBlock(2, 4, 7, null);
				visitTryCatchBlock(7, 8, 7, null);
				visitTryCatchBlock(0, 2, 13, null);
				visitTryCatchBlock(14, 16, 19, null);
				visitTryCatchBlock(19, 20, 19, null);
				visitTryCatchBlock(13, 14, 13, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "a", "()V");
				L(2).visitVarInsn(ALOAD, 0);
				L(3).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(6).visitJumpInsn(GOTO, 12);
				L(7).visitVarInsn(ASTORE, 1);
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(10).visitVarInsn(ALOAD, 1);
				L(11).visitInsn(ATHROW);
				L(12).visitJumpInsn(GOTO, 26);
				L(13).visitVarInsn(ASTORE, 2);
				L(14).visitVarInsn(ALOAD, 0);
				L(15).visitMethodInsn(INVOKESPECIAL, "Target", "b", "()V");
				L(16).visitVarInsn(ALOAD, 0);
				L(17).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(18).visitJumpInsn(GOTO, 24);
				L(19).visitVarInsn(ASTORE, 3);
				L(20).visitVarInsn(ALOAD, 0);
				L(21).visitMethodInsn(INVOKESPECIAL, "Target", "c", "()V");
				L(22).visitVarInsn(ALOAD, 3);
				L(23).visitInsn(ATHROW);
				L(24).visitVarInsn(ALOAD, 2);
				L(25).visitInsn(ATHROW);
				L(26).visitInsn(RETURN);
			}
		};
		applyFilterTo(target);

		assertFiltered(7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23, 24, 25);
	}
}
