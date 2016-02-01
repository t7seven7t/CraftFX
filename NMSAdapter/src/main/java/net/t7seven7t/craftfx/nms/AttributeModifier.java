package net.t7seven7t.craftfx.nms;

import java.util.UUID;

/**
 *
 */
public class AttributeModifier {

    private final double amount;
    private final String attributeName;
    private final String name;
    private final int operation;
    private final UUID uuid;

    public AttributeModifier(double amount, String attributeName, String name, int operation,
                             UUID uuid) {
        this.amount = amount;
        this.attributeName = attributeName;
        this.name = name;
        this.operation = operation;
        this.uuid = uuid;
    }

    public double getAmount() {
        return amount;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getName() {
        return name;
    }

    public int getOperation() {
        return operation;
    }

    public UUID getUuid() {
        return uuid;
    }
}
