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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Before;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Base class for filter test provides basic infrastructure for filter testing.
 */
public abstract class FilterTestBase {

	private IFilter filter;

	private LinkedList<AbstractInsnNode> filtered;

	@Before
	public void setup() throws IOException {
		filter = getFilter();
		filtered = new LinkedList<AbstractInsnNode>();
	}

	protected abstract IFilter getFilter();

	protected final MethodNode getMethod(Class<?> target, String name,
			String desc) throws IOException {
		final String resource = "/" + target.getName().replace('.', '/')
				+ ".class";
		ClassNode node = new ClassNode();
		InputStream in = target.getResourceAsStream(resource);
		new ClassReader(in).accept(node, ClassReader.EXPAND_FRAMES);
		in.close();

		for (Object m : node.methods) {
			MethodNode method = (MethodNode) m;
			if (name.equals(method.name) && desc.equals(method.desc)) {
				return method;
			}
		}

		fail(String.format("Method %s%s not found.", name, desc));
		return null;
	}

	protected final void applyFilterTo(Class<?> target, String name, String desc)
			throws IOException {
		final MethodNode method = getMethod(target, name, desc);
		filter.filter(method, new IFilterOutput() {

			public void ignore(InsnSequence sequence) {
				for (AbstractInsnNode insn : sequence.getInstructions()) {
					ignore(insn);
				}
			}

			public void ignore(AbstractInsnNode node) {
				filtered.add(node);
			}

			public void map(AbstractInsnNode fromNode, AbstractInsnNode toNode) {
				// TODO Auto-generated method stub
			}

		});
	}

	protected void assertFilteredInsn(int opcode) {
		final AbstractInsnNode node = filtered.removeFirst();
		assertEquals(opcode, node.getOpcode());
	}

	protected void assertOptionalFilteredInsn(int opcode) {
		if (!filtered.isEmpty() && filtered.get(0).getOpcode() == opcode) {
			filtered.removeFirst();
		}
	}

	protected void assertFilteredMethodInsn(int opcode, String owner,
			String name, String desc) {
		final MethodInsnNode node = (MethodInsnNode) filtered.removeFirst();
		assertEquals(opcode, node.getOpcode());
		assertEquals(owner, node.owner);
		assertEquals(name, node.name);
		assertEquals(desc, node.desc);
	}

	protected void assertNoMoreFilteredInsn() {
		assertEquals(Collections.emptyList(), filtered);
	}

}
