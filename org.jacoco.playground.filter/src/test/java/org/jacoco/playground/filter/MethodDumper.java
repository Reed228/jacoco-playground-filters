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

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/**
 * Dumps methods in the format used by filter tests.
 */
public class MethodDumper {

	protected static final MethodNode getMethod(String classfile, String name,
			String desc) throws IOException {
		ClassNode node = new ClassNode();
		InputStream in = new FileInputStream(classfile);
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

	private static void dump(MethodNode method) {
		final TestCaseDumper printer = new TestCaseDumper();
		insertLabels(method.instructions, printer);

		final TraceMethodVisitor mv = new TraceMethodVisitor(printer);

		method.accept(mv);
		final PrintWriter writer = new PrintWriter(System.out);
		printer.print(writer);
		writer.flush();
	}

	private static void insertLabels(InsnList list, TestCaseDumper printer) {
		boolean hasLabel = false;
		for (AbstractInsnNode node = list.getFirst(); node != null; node = node
				.getNext()) {
			switch (node.getType()) {
			case AbstractInsnNode.LINE:
				break;
			case AbstractInsnNode.LABEL:
				printer.declareLabel(((LabelNode) node).getLabel());
				hasLabel = true;
				break;
			default:
				if (hasLabel) {
					hasLabel = false;
				} else {
					final Label label = new Label();
					printer.declareLabel(label);
					list.insertBefore(node, new LabelNode(label));
				}
				break;
			}
		}
	}

	private static class TestCaseDumper extends ASMifier {

		private Label currentLabel;

		TestCaseDumper() {
			labelNames = new HashMap<Label, String>();
		}

		@Override
		public void visitCode() {
		}

		@Override
		public void visitMethodEnd() {
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
		}

		@Override
		public void visitLineNumber(int line, Label start) {
		}

		@Override
		public void visitLocalVariable(String name, String desc,
				String signature, Label start, Label end, int index) {
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler,
				String type) {
			super.visitTryCatchBlock(start, end, handler, type);
			setLinePrefix("");
		}

		@Override
		public void declareLabel(final Label l) {
			@SuppressWarnings("unchecked")
			final Map<Label, String> names = labelNames;
			String name = names.get(l);
			if (name == null) {
				name = String.valueOf(names.size());
				names.put(l, name);
			}
		}

		@Override
		public void visitLabel(Label label) {
			declareLabel(label);
			currentLabel = label;
		}

		@Override
		public void visitInsn(int opcode) {
			super.visitInsn(opcode);
			prependLabel();
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			prependLabel();
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			super.visitJumpInsn(opcode, label);
			prependLabel();
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc) {
			super.visitMethodInsn(opcode, owner, name, desc);
			prependLabel();
		}

		private void prependLabel() {
			final String l = (String) labelNames.get(currentLabel);
			setLinePrefix("L(" + l + ").");
		}

		private void setLinePrefix(String prefix) {
			@SuppressWarnings("unchecked")
			final List<String> textString = text;
			final int idx = textString.size() - 1;
			// remove "cv." prefix:
			final String line = textString.get(idx).substring(3);
			textString.set(idx, prefix + line);
		}

	}

	public static void main(String[] args) throws IOException {
		MethodNode method = getMethod(
				"./target/test-classes/org/jacoco/playground/filter/javacfinally/Target.class",
				"nested", "()V");
		dump(method);
	}

}
