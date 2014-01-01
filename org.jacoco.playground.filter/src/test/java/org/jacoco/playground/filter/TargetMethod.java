package org.jacoco.playground.filter;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class TargetMethod extends MethodNode {

	private final Map<Integer, Label> labels;
	private final Map<AbstractInsnNode, Integer> labelIds;

	public TargetMethod(final int access, final String name, final String desc,
			final String... exceptions) {
		super(Opcodes.ASM4, access, name, desc, null, exceptions);
		labels = new HashMap<Integer, Label>();
		labelIds = new HashMap<AbstractInsnNode, Integer>();
	}

	protected AbstractInsnNode insn() {
		return instructions.getLast();
	}

	public void visitTryCatchBlock(final int start, final int end,
			final int handler, final String type) {
		visitTryCatchBlock(label(start), label(end), label(handler), type);
	}

	public void visitJumpInsn(int opcode, int n) {
		visitJumpInsn(opcode, label(n));
	}

	public TargetMethod L(int n) {
		visitLabel(label(n));
		labelIds.put(instructions.getLast(), Integer.valueOf(n));
		return this;
	}

	public Integer getLabelOf(AbstractInsnNode node) {
		if (node instanceof LabelNode) {
			return labelIds.get(node);
		}
		return getLabelOf(node.getPrevious());
	}

	private Label label(int n) {
		final Integer key = Integer.valueOf(n);
		Label l = labels.get(key);
		if (l == null) {
			l = new Label();
			labels.put(key, l);
		}
		return l;
	}

}
