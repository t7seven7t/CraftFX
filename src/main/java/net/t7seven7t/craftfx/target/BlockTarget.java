package net.t7seven7t.craftfx.target;

import org.bukkit.block.Block;

/**
 *
 */
public class BlockTarget implements Target<Block> {

    private final Block block;

    public BlockTarget(Block block) {
        this.block = block;
    }

    @Override
    public Block getTarget() {
        return block;
    }
}
