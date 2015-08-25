package net.t7seven7t.craftfx.effect;

import com.google.common.collect.MapMaker;

import net.t7seven7t.util.EnumUtil;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an effect which has a start and an end. If the effect hasn't been started yet the
 * first run call will go to start(), otherwise it will go to end()
 */
public abstract class ExtentEffect extends Effect {

    // The extent state to next execute for a particular player
    private final Map<Player, ExtentState> extentStateMap;

    public ExtentEffect() {
        super();
        extentStateMap = new MapMaker().weakKeys().makeMap();
        addData(new ExtentData());
    }

    /**
     * Gets the extent state to next execute for a particular player
     */
    public ExtentState getState(Player player) {
        return Optional.of(extentStateMap.get(player)).orElseGet(() -> {
            // If inverted then first state starts at end instead
            ExtentState state = getData(ExtentData.class)
                    .isInverted() ? ExtentState.END : ExtentState.START;
            setState(player, state);
            return state;
        });
    }

    /**
     * Updates the ExtentState to next execute for a particular player
     */
    public void setState(Player player, ExtentState state) {
        extentStateMap.put(player, state);
    }

    @Override
    public final boolean run(RunSpecification spec) {
        ExtentState state = getState(spec.getPlayer());
        if (getData(ExtentData.class).isExtentDisabled(state)) {
            state = state.other();
        }

        setState(spec.getPlayer(), state.other());
        if (state == ExtentState.START) {
            return start(spec);
        } else if (state == ExtentState.END) {
            return end(spec);
        }

        // unreachable unless we add new extent states
        return false;
    }

    /**
     * Starts the ExtentEffect with specified RunSpecification parameters
     */
    public abstract boolean start(RunSpecification spec);

    /**
     * Ends the ExtentEffect with specified RunSpecification parameters
     */
    public abstract boolean end(RunSpecification spec);

    public enum ExtentState {
        START, END;

        public ExtentState other() {
            return this == START ? END : START;
        }
    }

    public class ExtentData extends EffectData {
        public boolean isInverted() {
            return getConfig().getBoolean("invert-extents", false);
        }

        public boolean isExtentDisabled(ExtentState state) {
            return EnumUtil.matchEnumValue(ExtentState.class,
                    getConfig().getString("disable-extent", "")) == state;
        }
    }
}
