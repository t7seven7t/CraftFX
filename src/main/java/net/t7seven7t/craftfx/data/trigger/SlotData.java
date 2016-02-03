package net.t7seven7t.craftfx.data.trigger;

import net.t7seven7t.craftfx.data.AbstractData;
import net.t7seven7t.craftfx.data.ConfigDataHolder;
import net.t7seven7t.craftfx.data.Data;
import net.t7seven7t.craftfx.data.DataHolder;

import org.bukkit.util.NumberConversions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Data about which slots a trigger item can fire from
 */
public class SlotData extends AbstractData {

    private static final Set<Integer> ARMOR_SLOTS
            = IntStream.rangeClosed(36, 39).boxed().collect(Collectors.toSet());
    private static final Set<Integer> HOTBAR_SLOTS
            = IntStream.rangeClosed(0, 8).boxed().collect(Collectors.toSet());
    private static final Set<Integer> ALL_SLOTS
            = IntStream.rangeClosed(0, 35).boxed().collect(Collectors.toSet());

    static {
        ALL_SLOTS.addAll(ARMOR_SLOTS);
    }

    private int[] slots;
    private boolean handSlot;

    public SlotData(String slots) {
        this.slots = fromCollection(parseSlots(slots));
    }

    public SlotData(boolean handSlot, int... slots) {
        this.handSlot = handSlot;
        this.slots = slots;
    }

    public SlotData(boolean handSlot, List<Integer> slots) {
        this.handSlot = handSlot;
        this.slots = fromCollection(slots);
    }

    public boolean isHandSlot() {
        return handSlot;
    }

    public int[] getSlots() {
        return slots;
    }

    private Set<Integer> parseSlots(String str) {
        final String[] parts = str.split(", ?");
        final Set<Integer> ints = new HashSet<>();
        handSlot = false;
        loop:
        for (String s : parts) {
            s = s.toLowerCase();
            switch (s) {
                case "all":
                    ints.addAll(ALL_SLOTS);
                    break loop;
                case "armor":
                    ints.addAll(ARMOR_SLOTS);
                    break;
                case "hotbar":
                    ints.addAll(HOTBAR_SLOTS);
                    break;
                case "hand":
                    handSlot = true;
                    break;
                default:
                    int i = NumberConversions.toInt(s);
                    if (i < 0 || i > 36) break;
                    ints.add(i);
                    break;
            }
        }
        return ints;
    }

    private int[] fromCollection(Collection<Integer> ints) {
        final int[] slots = new int[ints.size()];
        final Iterator<Integer> it = ints.iterator();
        for (int i = 0; i < ints.size(); i++) slots[i] = it.next();
        return slots;
    }

    @Override
    public void setHolder(DataHolder holder) {
        super.setHolder(holder);
        if (holder == null) return;
        if (holder instanceof ConfigDataHolder) {
            final ConfigDataHolder h = (ConfigDataHolder) holder;
            if (h.getConfig().isList("slots")) {
                final List<?> list = h.getConfig().getList("slots");
                final Set<Integer> ints = new HashSet<>();
                for (Object o : list) {
                    if (o instanceof String) {
                        ints.addAll(parseSlots((String) o));
                    } else {
                        int i = NumberConversions.toInt(o);
                        if (i < 0 || i > 36) continue;
                        ints.add(i);
                    }
                }
                slots = fromCollection(ints);
                return;
            }
        }

        final Optional<String> opt = holder.get("slots", String.class);
        opt.ifPresent(s -> slots = fromCollection(parseSlots(s)));
    }

    @Override
    public Data getCopy() {
        return new SlotData(handSlot, slots);
    }
}
