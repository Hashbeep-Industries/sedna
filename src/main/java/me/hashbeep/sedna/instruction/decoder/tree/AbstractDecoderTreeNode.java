package me.hashbeep.sedna.instruction.decoder.tree;

import me.hashbeep.sedna.instruction.InstructionDeclaration;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeNodeArguments;
import me.hashbeep.sedna.instruction.decoder.DecoderTreeVisitor;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public abstract class AbstractDecoderTreeNode {
    AbstractDecoderTreeNode() {
    }

    public abstract int getMaxDepth();

    public abstract int getMask();

    public abstract int getPattern();

    public abstract DecoderTreeNodeArguments getArguments();

    public abstract Stream<InstructionDeclaration> getInstructions();

    @Nullable
    public abstract InstructionDeclaration query(final int instruction);

    public abstract void accept(final DecoderTreeVisitor visitor);
}
