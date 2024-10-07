package me.hashbeep.sedna.riscv;

import me.hashbeep.sedna.instruction.InstructionDeclaration;
import me.hashbeep.sedna.instruction.InstructionDeclarationLoader;
import me.hashbeep.sedna.instruction.InstructionDefinition;
import me.hashbeep.sedna.instruction.InstructionDefinitionLoader;
import me.hashbeep.sedna.instruction.decoder.DecoderTree;
import me.hashbeep.sedna.instruction.decoder.PrintStreamDecoderTreeVisitor;
import me.hashbeep.sedna.instruction.decoder.tree.AbstractDecoderTreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public final class R5Instructions {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Spec RV32 = new Spec("/riscv/instructions32.txt");
    public static final Spec RV64 = new Spec("/riscv/instructions64.txt");

    @Nullable
    public static InstructionDefinition getDefinition(final InstructionDeclaration declaration) {
        return RV64.getDefinition(declaration);
    }

    public static AbstractDecoderTreeNode getDecoderTree() {
        return RV64.getDecoderTree();
    }

    public static final class Spec {
        private final ArrayList<InstructionDeclaration> DECLARATIONS = new ArrayList<>();
        private final HashMap<InstructionDeclaration, InstructionDefinition> DEFINITIONS = new HashMap<>();
        private final AbstractDecoderTreeNode DECODER_TREE;

        public Spec(final String instructionsFile) {
            try (final InputStream stream = R5Instructions.class.getResourceAsStream(instructionsFile)) {
                if (stream == null) {
                    throw new IOException("File not found.");
                }
                DECLARATIONS.addAll(InstructionDeclarationLoader.load(stream));
            } catch (final Throwable e) {
                LOGGER.error("Failed loading RISC-V instruction declarations.", e);
            }

            try {
                DEFINITIONS.putAll(InstructionDefinitionLoader.load(R5CPUTemplate.class, DECLARATIONS));
            } catch (final Throwable e) {
                LOGGER.error("Failed loading RISC-V instruction definitions.", e);
            }

            DECODER_TREE = DecoderTree.create(DECLARATIONS);
        }

        public ArrayList<InstructionDeclaration> getDeclarations() {
            return DECLARATIONS;
        }

        @Nullable
        public InstructionDefinition getDefinition(final InstructionDeclaration declaration) {
            return DEFINITIONS.get(declaration);
        }

        public AbstractDecoderTreeNode getDecoderTree() {
            return DECODER_TREE;
        }
    }

    public static void main(final String[] args) {
        final AbstractDecoderTreeNode tree = RV64.getDecoderTree();
        tree.accept(new PrintStreamDecoderTreeVisitor(tree.getMaxDepth()));
    }
}
