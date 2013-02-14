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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Base class for filter test provides basic infrastructure for filter testing.
 */
public abstract class FilterTestBase {

	private Map<String, MethodNode> methods;

	private IFilter filter;

	private LinkedList<AbstractInsnNode> filtered;

	@Before
	public void setup() throws IOException {
		final Class<?> target = getTarget();
		final String resource = "/" + target.getName().replace('.', '/')
				+ ".class";
		ClassNode node = new ClassNode();
		InputStream in = target.getResourceAsStream(resource);
		new ClassReader(in).accept(node, ClassReader.EXPAND_FRAMES);
		in.close();

		methods = new HashMap<String, MethodNode>();
		for (Object m : node.methods) {
			MethodNode method = (MethodNode) m;
			methods.put(method.name + '#' + method.desc, method);
		}

		filter = getFilter();
		filtered = new LinkedList<AbstractInsnNode>();
	}

	protected abstract Class<?> getTarget();

	protected abstract IFilter getFilter();

	protected final MethodNode getMethod(String name, String desc) {
		MethodNode method = methods.get(name + '#' + desc);
		assertNotNull(method);
		return method;
	}

	protected final void applyFilterTo(String name, String desc) {
		final MethodNode method = getMethod(name, desc);
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

	protected void assertNoMoreFilteredInsn() {
		assertEquals(Collections.emptyList(), filtered);
	}

}
