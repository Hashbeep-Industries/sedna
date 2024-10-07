package me.hashbeep.sedna.instruction.decoder;

import me.hashbeep.sedna.instruction.decoder.tree.DecoderTreeBranchNode;
import me.hashbeep.sedna.instruction.decoder.tree.DecoderTreeSwitchNode;

public interface DecoderTreeVisitor {
    DecoderTreeSwitchVisitor visitSwitch(final DecoderTreeSwitchNode node);

    DecoderTreeBranchVisitor visitBranch(final DecoderTreeBranchNode node);

    DecoderTreeLeafVisitor visitInstruction();

    void visitEnd();
}
