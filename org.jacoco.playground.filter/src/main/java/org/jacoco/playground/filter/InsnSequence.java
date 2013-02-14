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

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Sequence of instructions.
 */
public class InsnSequence {

	private final List<AbstractInsnNode> instructions;

	public InsnSequence(List<AbstractInsnNode> instructions) {
		this.instructions = instructions;
	}

	public List<AbstractInsnNode> getInstructions() {
		return instructions;
	}

}
