package me.hashbeep.sedna.instruction.decoder;

public interface DecoderTreeSwitchVisitor {
    void visit(final int mask, final int[] patterns, final DecoderTreeNodeArguments arguments);

    DecoderTreeVisitor visitSwitchCase(final int index, final int pattern);

    void visitEnd();
}
