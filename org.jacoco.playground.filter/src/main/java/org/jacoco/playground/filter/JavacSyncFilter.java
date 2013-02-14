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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Filter for filtering the exception path created by the Java compiler for the
 * Java synchronized statement.
 */
public class JavacSyncFilter implements IFilter {

	private static final InsnSequenceMatcher NORMAL_PATH = new InsnSequenceMatcher()
			.ignoreLabels() //
			.ignoreLines() //
			.insn(Opcodes.ALOAD, Opcodes.MONITOREXIT);

	// ECJ keeps the exception on the stack
	private static final InsnSequenceMatcher EXCEPTION_PATH_ECJ = new InsnSequenceMatcher()
			.ignoreLabels() //
			.ignoreLines() //
			.insn(Opcodes.ALOAD, Opcodes.MONITOREXIT, Opcodes.ATHROW);

	// JDK store the exception in a local
	private static final InsnSequenceMatcher EXCEPTION_PATH_JDK = new InsnSequenceMatcher()
			.ignoreLabels() //
			.ignoreLines() //
			.insn(Opcodes.ASTORE, Opcodes.ALOAD, Opcodes.MONITOREXIT,
					Opcodes.ALOAD, Opcodes.ATHROW);

	public void filter(MethodNode method, IFilterOutput output) {
		for (Object n : method.tryCatchBlocks) {
			TryCatchBlockNode tryCatch = (TryCatchBlockNode) n;

			// Only catch blocks of type any:
			if (tryCatch.type != null) {
				continue;
			}
			// Skip "safety handler" which protects the exit statement itself:
			if (tryCatch.start == tryCatch.handler) {
				continue;
			}

			final InsnSequence normalPath = NORMAL_PATH
					.matchBackward(tryCatch.end);
			if (normalPath == null) {
				continue;
			}

			InsnSequence exceptionPath = EXCEPTION_PATH_JDK
					.matchForward(tryCatch.handler);
			if (exceptionPath == null) {
				exceptionPath = EXCEPTION_PATH_ECJ
						.matchForward(tryCatch.handler);
			}

			if (exceptionPath == null) {
				continue;
			}

			output.ignore(exceptionPath);
		}
	}

}
