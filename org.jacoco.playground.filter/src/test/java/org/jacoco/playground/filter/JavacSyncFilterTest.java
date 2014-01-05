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
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.MONITORENTER;
import static org.objectweb.asm.Opcodes.MONITOREXIT;
import static org.objectweb.asm.Opcodes.RETURN;

import java.io.IOException;

import org.junit.Test;

public class JavacSyncFilterTest extends FilterTestBase {

	@Override
	protected IFilter getFilter() {
		return new JavacSyncFilter();
	}

	/**
	 * <pre>
	 * void sync() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncECJ() throws IOException {
		target = new TargetMethod(0, "sync", "()V") {
			{
				visitTryCatchBlock(5, 9, 10, null);
				visitTryCatchBlock(10, 12, 10, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitFieldInsn(GETFIELD, "Target", "lock",
						"Ljava/lang/Object;");
				L(2).visitInsn(DUP);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitInsn(MONITORENTER);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 1);
				L(8).visitInsn(MONITOREXIT);
				L(9).visitJumpInsn(GOTO, 13);
				L(10).visitVarInsn(ALOAD, 1);
				L(11).visitInsn(MONITOREXIT);
				L(12).visitInsn(ATHROW);
				L(13).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(10, 11, 12);
	}

	/**
	 * <pre>
	 * void sync() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncJDK() throws IOException {
		target = new TargetMethod(0, "sync", "()V") {
			{
				visitTryCatchBlock(5, 9, 10, null);
				visitTryCatchBlock(10, 13, 10, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitFieldInsn(GETFIELD, "Target", "lock",
						"Ljava/lang/Object;");
				L(2).visitInsn(DUP);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitInsn(MONITORENTER);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 1);
				L(8).visitInsn(MONITOREXIT);
				L(9).visitJumpInsn(GOTO, 15);
				L(10).visitVarInsn(ASTORE, 2);
				L(11).visitVarInsn(ALOAD, 1);
				L(12).visitInsn(MONITOREXIT);
				L(13).visitVarInsn(ALOAD, 2);
				L(14).visitInsn(ATHROW);
				L(15).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(10, 11, 12, 13, 14);
	}

	/**
	 * <pre>
	 * void syncWithReturn() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 		return;
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncWithReturnECJ() throws IOException {
		target = new TargetMethod(0, "syncWithReturn", "()V") {
			{
				visitTryCatchBlock(5, 9, 10, null);
				visitTryCatchBlock(10, 12, 10, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitFieldInsn(GETFIELD, "Target", "lock",
						"Ljava/lang/Object;");
				L(2).visitInsn(DUP);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitInsn(MONITORENTER);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 1);
				L(8).visitInsn(MONITOREXIT);
				L(9).visitInsn(RETURN);
				L(10).visitVarInsn(ALOAD, 1);
				L(11).visitInsn(MONITOREXIT);
				L(12).visitInsn(ATHROW);
			}
		};

		applyFilterTo(target);

		assertFiltered(10, 11, 12);
	}

	/**
	 * <pre>
	 * void syncWithReturn() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 		return;
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncWithReturnJDK() throws IOException {
		target = new TargetMethod(0, "run", "()V") {
			{
				visitTryCatchBlock(5, 9, 10, null);
				visitTryCatchBlock(10, 13, 10, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitFieldInsn(GETFIELD,
						"org/jacoco/playground/filter/Target", "lock",
						"Ljava/lang/Object;");
				L(2).visitInsn(DUP);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitInsn(MONITORENTER);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL,
						"org/jacoco/playground/filter/Target", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 1);
				L(8).visitInsn(MONITOREXIT);
				L(9).visitInsn(RETURN);
				L(10).visitVarInsn(ASTORE, 2);
				L(11).visitVarInsn(ALOAD, 1);
				L(12).visitInsn(MONITOREXIT);
				L(13).visitVarInsn(ALOAD, 2);
				L(14).visitInsn(ATHROW);
			}
		};

		applyFilterTo(target);

		assertFiltered(10, 11, 12, 13, 14);
	}

	/**
	 * <pre>
	 * void syncNested() {
	 * 	synchronized (lock1) {
	 * 		doit();
	 * 		synchronized (lock2) {
	 * 			doit();
	 * 		}
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncNestedECJ() throws IOException {
		target = new TargetMethod(0, "syncNested", "()V") {
			{
				visitTryCatchBlock(12, 16, 17, null);
				visitTryCatchBlock(17, 19, 17, null);
				visitTryCatchBlock(5, 24, 25, null);
				visitTryCatchBlock(25, 27, 25, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitFieldInsn(GETFIELD,
						"org/jacoco/playground/filter/Target", "lock1",
						"Ljava/lang/Object;");
				L(2).visitInsn(DUP);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitInsn(MONITORENTER);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL,
						"org/jacoco/playground/filter/Target", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 0);
				L(8).visitFieldInsn(GETFIELD,
						"org/jacoco/playground/filter/Target", "lock2",
						"Ljava/lang/Object;");
				L(9).visitInsn(DUP);
				L(10).visitVarInsn(ASTORE, 2);
				L(11).visitInsn(MONITORENTER);
				L(12).visitVarInsn(ALOAD, 0);
				L(13).visitMethodInsn(INVOKESPECIAL,
						"org/jacoco/playground/filter/Target", "doit", "()V");
				L(14).visitVarInsn(ALOAD, 2);
				L(15).visitInsn(MONITOREXIT);
				L(16).visitJumpInsn(GOTO, 20);
				L(17).visitVarInsn(ALOAD, 2);
				L(18).visitInsn(MONITOREXIT);
				L(19).visitInsn(ATHROW);
				L(20).visitVarInsn(ALOAD, 0);
				L(21).visitMethodInsn(INVOKESPECIAL,
						"org/jacoco/playground/filter/Target", "doit", "()V");
				L(22).visitVarInsn(ALOAD, 1);
				L(23).visitInsn(MONITOREXIT);
				L(24).visitJumpInsn(GOTO, 28);
				L(25).visitVarInsn(ALOAD, 1);
				L(26).visitInsn(MONITOREXIT);
				L(27).visitInsn(ATHROW);
				L(28).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(17, 18, 19, 25, 26, 27);
	}

	/**
	 * <pre>
	 * void negativCatch() {
	 * 	try {
	 * 		doit();
	 * 	} catch (UnsupportedOperationException ex) {
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void negativCatch() throws IOException {
		target = new TargetMethod(0, "negativCatch", "()V") {
			{
				visitTryCatchBlock(0, 2, 3, "java/lang/Exception");
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(2).visitJumpInsn(GOTO, 6);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(6).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

	/**
	 * <pre>
	 * void negativFinally() {
	 * 	try {
	 * 		doit();
	 * 	} finally {
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void negativFinally() throws IOException {
		target = new TargetMethod(0, "negativFinally", "()V") {
			{
				visitTryCatchBlock(0, 3, 3, null);
				L(0).visitVarInsn(ALOAD, 0);
				L(1).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(2).visitJumpInsn(GOTO, 8);
				L(3).visitVarInsn(ASTORE, 1);
				L(4).visitVarInsn(ALOAD, 0);
				L(5).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(6).visitVarInsn(ALOAD, 1);
				L(7).visitInsn(ATHROW);
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitMethodInsn(INVOKESPECIAL, "Target", "doit", "()V");
				L(10).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

}
