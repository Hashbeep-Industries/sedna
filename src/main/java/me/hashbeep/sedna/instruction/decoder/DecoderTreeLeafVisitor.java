package me.hashbeep.sedna.instruction.decoder;

import me.hashbeep.sedna.instruction.InstructionDeclaration;

public interface DecoderTreeLeafVisitor {
    void visitInstruction(final InstructionDeclaration declaration);

    void visitEnd();
}
