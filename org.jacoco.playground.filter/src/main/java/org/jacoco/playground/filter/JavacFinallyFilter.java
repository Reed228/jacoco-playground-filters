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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Filter for the exception path created by the Java compiler for the finally
 * statement.
 */
public class JavacFinallyFilter implements IFilter {

	private static final InsnSequenceMatcher ANY_START = new InsnSequenceMatcher()
			.ignoreLabels() //
			.ignoreLines() //
			.insn(Opcodes.ASTORE);

	public void filter(MethodNode method, IFilterOutput output) {
		final Set<LabelNode> done = new HashSet<LabelNode>();
		for (Object n : method.tryCatchBlocks) {
			final TryCatchBlockNode tryCatch = (TryCatchBlockNode) n;
			// Only catch blocks of type any:
			if (tryCatch.type == null && done.add(tryCatch.handler)) {
				filter(tryCatch.handler, output);
			}
		}
	}

	private void filter(LabelNode handler, IFilterOutput output) {

		final InsnSubList anyStart = ANY_START.matchForward(handler);

		if (anyStart != null) {
			VarInsnNode varInsn = (VarInsnNode) anyStart.getFirst();
			final int var = varInsn.var;
			final List<AbstractInsnNode> block = getFinallyBlock(
					getNext(varInsn), var);
			if (block != null) {
				output.ignore(varInsn);
				for (AbstractInsnNode node : block) {
					output.ignore(node);
				}
				AbstractInsnNode aload = getNext(block.get(block.size() - 1));
				output.ignore(aload);
				output.ignore(getNext(aload));
			}
		}
	}

	private List<AbstractInsnNode> getFinallyBlock(AbstractInsnNode first,
			int var) {
		final List<AbstractInsnNode> block = new ArrayList<AbstractInsnNode>();
		for (AbstractInsnNode node = first; node != null; node = getNext(node)) {
			if (isBlockEnd(node, var)) {
				return block;
			}
			block.add(node);
		}
		return null;
	}

	private boolean isBlockEnd(AbstractInsnNode node, int var) {
		if (node.getOpcode() != Opcodes.ALOAD) {
			return false;
		}
		if (((VarInsnNode) node).var != var) {
			return false;
		}
		final AbstractInsnNode next = getNext(node);
		return next != null && next.getOpcode() == Opcodes.ATHROW;
	}

	private AbstractInsnNode getNext(final AbstractInsnNode node) {
		return skipIgnoredForward(node.getNext());
	}

	private AbstractInsnNode skipIgnoredForward(final AbstractInsnNode node) {
		if (node == null) {
			return null;
		}
		if (node.getType() == AbstractInsnNode.LABEL) {
			return getNext(node);
		}
		if (node.getType() == AbstractInsnNode.LINE) {
			return getNext(node);
		}
		return node;
	}

}
