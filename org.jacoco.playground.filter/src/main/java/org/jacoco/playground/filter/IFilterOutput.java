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

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Interface used by filters to emit filtered items.
 */
public interface IFilterOutput {

	void ignore(AbstractInsnNode node);

	void ignore(InsnSubList list);

	void map(AbstractInsnNode fromNode, AbstractInsnNode toNode);

}
