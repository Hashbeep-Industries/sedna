package me.hashbeep.sedna.instruction.decoder.tree;

import me.hashbeep.sedna.instruction.InstructionDeclaration;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeBranchVisitor;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeVisitor;

import javax.annotation.Nullable;

public final class DecoderTreeBranchNode extends AbstractDecoderTreeInnerNode {
    public DecoderTreeBranchNode(final AbstractDecoderTreeNode[] children) {
        super(children);
    }

    @Override
    public void accept(final DecoderTreeVisitor visitor) {
        final DecoderTreeBranchVisitor branchVisitor = visitor.visitBranch(this);
        if (branchVisitor != null) {
            branchVisitor.visit(children.length, getArguments());
            for (int i = 0; i < children.length; i++) {
                final DecoderTreeVisitor branchCaseVisitor = branchVisitor.visitBranchCase(i, children[i].getMask(), children[i].getPattern());
                if (branchCaseVisitor != null) {
                    children[i].accept(branchCaseVisitor);
                }
            }

            branchVisitor.visitEnd();
        }

        visitor.visitEnd();
    }

    @Nullable
    @Override
    public InstructionDeclaration query(final int instruction) {
        for (final AbstractDecoderTreeNode child : children) {
            if ((instruction & child.getMask()) == child.getPattern()) {
                return child.query(instruction);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "[BRANCH]";
    }
}
