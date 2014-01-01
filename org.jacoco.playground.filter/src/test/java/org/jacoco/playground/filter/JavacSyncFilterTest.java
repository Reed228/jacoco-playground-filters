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
	 * public void sync() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void sync() throws IOException {
		target = new TargetMethod(0, "run", "()V") {
			{
				visitTryCatchBlock(6, 10, 11, null);
				visitTryCatchBlock(11, 14, 11, null);

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitFieldInsn(GETFIELD, "Foo", "lock",
						"Ljava/lang/Object;");
				L(3).visitInsn(DUP);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitInsn(MONITORENTER);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(8).visitVarInsn(ALOAD, 1);
				L(9).visitInsn(MONITOREXIT);
				L(10).visitJumpInsn(GOTO, 16);
				L(11).visitVarInsn(ASTORE, 2);
				L(12).visitVarInsn(ALOAD, 1);
				L(13).visitInsn(MONITOREXIT);
				L(14).visitVarInsn(ALOAD, 2);
				L(15).visitInsn(ATHROW);
				L(16).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(11, 12, 13, 14, 15);
	}

	/**
	 * <pre>
	 * public void syncWithReturn() {
	 * 	synchronized (lock) {
	 * 		doit();
	 * 		return;
	 * 	}
	 * }
	 * </pre>
	 */
	@Test
	public void syncWithReturnECJ() throws IOException {
		target = new TargetMethod(0, "run", "()V") {
			{
				visitTryCatchBlock(6, 10, 11, null);
				visitTryCatchBlock(11, 14, 11, null);

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitFieldInsn(GETFIELD, "Foo", "lock",
						"Ljava/lang/Object;");
				L(3).visitInsn(DUP);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitInsn(MONITORENTER);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(8).visitVarInsn(ALOAD, 1);
				L(9).visitInsn(MONITOREXIT);
				L(10).visitInsn(RETURN);

				L(11).visitVarInsn(ASTORE, 2);
				L(12).visitVarInsn(ALOAD, 1);
				L(13).visitInsn(MONITOREXIT);
				L(14).visitVarInsn(ALOAD, 2);
				L(15).visitInsn(ATHROW);
			}
		};

		applyFilterTo(target);

		assertFiltered(11, 12, 13, 14, 15);
	}

	/**
	 * <pre>
	 * public void syncWithReturn() {
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
				visitTryCatchBlock(6, 10, 11, null);
				visitTryCatchBlock(11, 13, 11, null);

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitFieldInsn(GETFIELD, "Foo", "lock",
						"Ljava/lang/Object;");
				L(3).visitInsn(DUP);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitInsn(MONITORENTER);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(8).visitVarInsn(ALOAD, 1);
				L(9).visitInsn(MONITOREXIT);
				L(10).visitInsn(RETURN);

				L(11).visitVarInsn(ALOAD, 1);
				L(12).visitInsn(MONITOREXIT);
				L(13).visitInsn(ATHROW);
			}
		};

		applyFilterTo(target);

		assertFiltered(11, 12, 13);
	}

	/**
	 * <pre>
	 * public void syncNested() {
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
	public void syncNested() throws IOException {
		target = new TargetMethod(0, "run", "()V") {
			{

				visitTryCatchBlock(13, 17, 18, null);
				visitTryCatchBlock(18, 20, 18, null);
				visitTryCatchBlock(6, 25, 26, null);
				visitTryCatchBlock(25, 28, 26, null);

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitFieldInsn(GETFIELD, "Foo", "lock1",
						"Ljava/lang/Object;");
				L(3).visitInsn(DUP);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitInsn(MONITORENTER);
				L(6).visitVarInsn(ALOAD, 0);
				L(7).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(8).visitVarInsn(ALOAD, 0);
				L(9).visitFieldInsn(GETFIELD, "Foo", "lock2",
						"Ljava/lang/Object;");
				L(10).visitInsn(DUP);
				L(11).visitVarInsn(ASTORE, 2);
				L(12).visitInsn(MONITORENTER);
				L(13).visitVarInsn(ALOAD, 0);
				L(14).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(15).visitVarInsn(ALOAD, 2);
				L(16).visitInsn(MONITOREXIT);
				L(17).visitJumpInsn(GOTO, 21);

				L(18).visitVarInsn(ALOAD, 2);
				L(19).visitInsn(MONITOREXIT);
				L(20).visitInsn(ATHROW);

				L(21).visitVarInsn(ALOAD, 0);
				L(22).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(23).visitVarInsn(ALOAD, 1);
				L(24).visitInsn(MONITOREXIT);
				L(25).visitJumpInsn(GOTO, 28);
				L(26).visitVarInsn(ALOAD, 1);
				L(27).visitInsn(MONITOREXIT);
				L(28).visitInsn(ATHROW);
				L(29).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered(18, 19, 20, 26, 27, 28);
	}

	/**
	 * <pre>
	 * public void negativCatch() {
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
		target = new TargetMethod(0, "run", "()V") {
			{
				visitTryCatchBlock(1, 3, 4,
						"java/lang/UnsupportedOperationException");

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(3).visitJumpInsn(GOTO, 7);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(7).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

	/**
	 * <pre>
	 * public void negativFinally() {
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
		target = new TargetMethod(0, "run", "()V") {
			{
				visitTryCatchBlock(1, 4, 4, null);

				L(1).visitVarInsn(ALOAD, 0);
				L(2).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(3).visitJumpInsn(GOTO, 9);
				L(4).visitVarInsn(ASTORE, 1);
				L(5).visitVarInsn(ALOAD, 0);
				L(6).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(7).visitVarInsn(ALOAD, 1);
				L(8).visitInsn(ATHROW);
				L(9).visitVarInsn(ALOAD, 0);
				L(10).visitMethodInsn(INVOKESPECIAL, "Foo", "doit", "()V");
				L(11).visitInsn(RETURN);
			}
		};

		applyFilterTo(target);

		assertFiltered();
	}

}
