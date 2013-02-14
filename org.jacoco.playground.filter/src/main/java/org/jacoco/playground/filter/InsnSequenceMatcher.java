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
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Matcher for instruction sequences.
 */
public class InsnSequenceMatcher {

	private boolean ignoreLines;
	private boolean ignoreLabels;
	private int[] opcodes;

	public InsnSequenceMatcher() {
		ignoreLines = false;
		ignoreLabels = false;
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
		this.opcodes = opcodes;
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
	public InsnSequence matchForward(final AbstractInsnNode start) {
		AbstractInsnNode node = skipIgnoredForward(start);
		List<AbstractInsnNode> match = new ArrayList<AbstractInsnNode>();
		for (int opcode : opcodes) {
			if (node == null) {
				return null;
			}
			if (node.getOpcode() != opcode) {
				return null;
			}
			match.add(node);
			node = getNext(node);
		}
		return new InsnSequence(match);
	}

	/**
	 * Matches searching backwards from the given start node.
	 * 
	 * @param start
	 *            first node to match
	 * @return match or <code>null</code>
	 */
	public InsnSequence matchBackward(AbstractInsnNode start) {
		AbstractInsnNode node = skipIgnoredBackward(start);
		List<AbstractInsnNode> match = new ArrayList<AbstractInsnNode>();
		for (int i = opcodes.length; --i >= 0;) {
			if (node == null) {
				return null;
			}
			if (node.getOpcode() != opcodes[i]) {
				return null;
			}
			match.add(node);
			node = getPrevious(node);
		}
		Collections.reverse(match);
		return new InsnSequence(match);
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

}
