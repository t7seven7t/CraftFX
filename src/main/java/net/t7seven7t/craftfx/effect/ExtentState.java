package net.t7seven7t.craftfx.effect;

/**
 *
 */
public enum ExtentState {
    START, END;

    public ExtentState other() {
        return this == START ? END : START;
    }
}
