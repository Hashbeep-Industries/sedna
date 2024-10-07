package me.hashbeep.sedna.instruction.decoder.tree;

import me.hashbeep.sedna.instruction.InstructionDeclaration;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeSwitchVisitor;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeVisitor;

import javax.annotation.Nullable;

public final class DecoderTreeSwitchNode extends AbstractDecoderTreeInnerNode {
    public DecoderTreeSwitchNode(final AbstractDecoderTreeNode[] children) {
        super(children);
    }

    @Override
    public void accept(final DecoderTreeVisitor visitor) {
        final DecoderTreeSwitchVisitor switchVisitor = visitor.visitSwitch(this);
        if (switchVisitor != null) {
            final int[] patterns = new int[children.length];
            for (int i = 0; i < children.length; i++) {
                patterns[i] = children[i].getPattern();
            }
            switchVisitor.visit(getMask(), patterns, getArguments());
            for (int i = 0; i < children.length; i++) {
                final DecoderTreeVisitor switchCaseVisitor = switchVisitor.visitSwitchCase(i, children[i].getPattern() & getMask());
                if (switchCaseVisitor != null) {
                    children[i].accept(switchCaseVisitor);
                }
            }

            switchVisitor.visitEnd();
        }

        visitor.visitEnd();
    }

    @Nullable
    @Override
    public InstructionDeclaration query(final int instruction) {
        for (final AbstractDecoderTreeNode child : children) {
            if ((instruction & getMask()) == (child.getPattern() & getMask())) {
                return child.query(instruction);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "[SWITCH]";
    }
}
