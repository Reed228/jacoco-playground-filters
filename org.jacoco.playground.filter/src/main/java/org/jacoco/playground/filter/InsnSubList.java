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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Instruction sequence which is a sublist of a ASM instruction list.
 */
public class InsnSubList implements Iterable<AbstractInsnNode> {

	private final AbstractInsnNode first;
	private final AbstractInsnNode last;

	/**
	 * Creates a new starting with the node first, ending with the node last.
	 * Both nodes are inclusive. The elements in this list must be part of a ASM
	 * instruction list and must be properly linked.
	 * 
	 * @param first
	 *            first node
	 * @param last
	 *            last node
	 */
	public InsnSubList(AbstractInsnNode first, AbstractInsnNode last) {
		this.first = first;
		this.last = last;
	}

	public AbstractInsnNode getFirst() {
		return first;
	}

	public AbstractInsnNode getLast() {
		return last;
	}

	public Iterator<AbstractInsnNode> iterator() {
		return new Iterator<AbstractInsnNode>() {

			private AbstractInsnNode next = first;

			public boolean hasNext() {
				return next != null;
			}

			public AbstractInsnNode next() {
				final AbstractInsnNode n = next;
				if (n == null) {
					throw new NoSuchElementException();
				}
				next = n == last ? null : n.getNext();
				return n;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
