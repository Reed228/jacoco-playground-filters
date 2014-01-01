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

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Base class for filter test provides basic infrastructure for filter testing.
 */
public abstract class FilterTestBase {

	protected TargetMethod target;

	private IFilter filter;

	private Set<Integer> filtered;

	@Before
	public void setup() throws IOException {
		filter = getFilter();
		filtered = new TreeSet<Integer>();
	}

	protected abstract IFilter getFilter();

	protected final void applyFilterTo(MethodNode method) throws IOException {
		filter.filter(method, new IFilterOutput() {

			public void ignore(InsnSubList list) {
				for (AbstractInsnNode insn : list) {
					if (insn.getOpcode() != -1) {
						ignore(insn);
					}
				}
			}

			public void ignore(AbstractInsnNode node) {
				filtered.add(target.getLabelOf(node));
			}

			public void map(AbstractInsnNode fromNode, AbstractInsnNode toNode) {
				// TODO Auto-generated method stub
			}

		});
	}

	protected void assertFiltered(int... instructions) {
		final Set<Integer> expected = new TreeSet<Integer>();
		for (int i : instructions) {
			expected.add(Integer.valueOf(i));
		}
		assertEquals(expected, filtered);
	}

}
