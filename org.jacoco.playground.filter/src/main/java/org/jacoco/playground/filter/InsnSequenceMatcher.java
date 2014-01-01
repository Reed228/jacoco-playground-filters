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
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Matcher for instruction sequences.
 */
public class InsnSequenceMatcher {

	private boolean ignoreLines;
	private boolean ignoreLabels;
	private final List<INodeMatcher> nodeMatchers;

	public InsnSequenceMatcher() {
		ignoreLines = false;
		ignoreLabels = false;
		nodeMatchers = new ArrayList<INodeMatcher>();
	}

	// === configuration methods ===

	/**
	 * Specifies that lines should be ignored.
	 */
	public InsnSequenceMatcher ignoreLines() {
		this.ignoreLines = true;
		return this;
	}

	/**
	 * Specifies that labels should be ignored.
	 */
	public InsnSequenceMatcher ignoreLabels() {
		this.ignoreLabels = true;
		return this;
	}

	/**
	 * Adds instructions with the given opcode to the expected sequence.
	 * 
	 * @param opcodes
	 *            opcodes of the sequence
	 */
	public InsnSequenceMatcher insn(int... opcodes) {
		for (int opcode : opcodes) {
			nodeMatchers.add(new OpcodeMatcher(opcode));
		}
		return this;
	}

	/**
	 * Adds a method instruction to the expected sequence.
	 * 
	 * @param opcodes
	 *            opcodes of the sequence
	 */
	public InsnSequenceMatcher method(int opcode, String name, String desc) {
		nodeMatchers.add(new MethodMatcher(opcode, name, desc));
		return this;
	}

	// === matcher methods ===

	/**
	 * Matches searching forward from the given start node.
	 * 
	 * @param start
	 *            first node to match
	 * @return match or <code>null</code>
	 */
	public InsnSubList matchForward(final AbstractInsnNode start) {
		AbstractInsnNode node = skipIgnoredForward(start);
		AbstractInsnNode first = node;
		AbstractInsnNode last = node;
		for (INodeMatcher m : nodeMatchers) {
			if (node == null) {
				return null;
			}
			if (!m.matches(node)) {
				return null;
			}
			last = node;
			node = getNext(node);
		}
		return new InsnSubList(first, last);
	}

	/**
	 * Matches searching backwards from the given start node.
	 * 
	 * @param start
	 *            first node to match
	 * @return match or <code>null</code>
	 */
	public InsnSubList matchBackward(AbstractInsnNode start) {
		AbstractInsnNode node = skipIgnoredBackward(start);
		AbstractInsnNode first = node;
		AbstractInsnNode last = node;
		for (int i = nodeMatchers.size(); --i >= 0;) {
			if (node == null) {
				return null;
			}
			if (!nodeMatchers.get(i).matches(node)) {
				return null;
			}
			first = node;
			node = getPrevious(node);
		}
		return new InsnSubList(first, last);
	}

	private AbstractInsnNode getNext(final AbstractInsnNode node) {
		return skipIgnoredForward(node.getNext());
	}

	private AbstractInsnNode skipIgnoredForward(final AbstractInsnNode node) {
		if (node == null) {
			return null;
		}
		if (ignoreLabels && node.getType() == AbstractInsnNode.LABEL) {
			return getNext(node);
		}
		if (ignoreLines && node.getType() == AbstractInsnNode.LINE) {
			return getNext(node);
		}
		return node;
	}

	private AbstractInsnNode getPrevious(final AbstractInsnNode node) {
		return skipIgnoredBackward(node.getPrevious());
	}

	private AbstractInsnNode skipIgnoredBackward(final AbstractInsnNode node) {
		if (node == null) {
			return null;
		}
		if (ignoreLabels && node.getType() == AbstractInsnNode.LABEL) {
			return getPrevious(node);
		}
		if (ignoreLines && node.getType() == AbstractInsnNode.LINE) {
			return getPrevious(node);
		}
		return node;
	}

	private interface INodeMatcher {
		boolean matches(AbstractInsnNode node);
	}

	private static class OpcodeMatcher implements INodeMatcher {

		private final int opcode;

		OpcodeMatcher(int opcode) {
			this.opcode = opcode;
		}

		public boolean matches(AbstractInsnNode node) {
			return opcode == node.getOpcode();
		}

	}

	private static class MethodMatcher extends OpcodeMatcher {

		private final String name;
		private final String desc;

		MethodMatcher(int opcode, String name, String desc) {
			super(opcode);
			this.name = name;
			this.desc = desc;
		}

		@Override
		public boolean matches(AbstractInsnNode node) {
			if (!super.matches(node)) {
				return false;
			}
			if (node.getType() != AbstractInsnNode.METHOD_INSN) {
				return false;
			}
			MethodInsnNode method = (MethodInsnNode) node;
			return name.equals(method.name) && desc.equals(method.desc);
		}

	}

}
