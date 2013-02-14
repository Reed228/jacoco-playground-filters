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

/**
 * Filter for private default constructors.
 */
public class DesignPrivDefCtorFilter implements IFilter {

	private static final InsnSequenceMatcher SUPER_CALL = new InsnSequenceMatcher()
			.ignoreLabels() //
			.ignoreLines() //
			.insn(Opcodes.ALOAD) //
			.method(Opcodes.INVOKESPECIAL, "<init>", "()V") //
			.insn(Opcodes.RETURN);

	public void filter(MethodNode method, IFilterOutput output) {
		if (isConstructor(method) && isPrivate(method)
				&& hasNoArguments(method)) {

			InsnSequence superCall = SUPER_CALL
					.matchForward(method.instructions.getFirst());

			if (superCall != null) {
				output.ignore(superCall);
			}
		}
	}

	private boolean isConstructor(MethodNode method) {
		return "<init>".equals(method.name);
	}

	private boolean isPrivate(MethodNode method) {
		return (method.access & Opcodes.ACC_PRIVATE) != 0;
	}

	private boolean hasNoArguments(MethodNode method) {
		return "()V".equals(method.desc);
	}
}
