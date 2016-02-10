package net.t7seven7t.craftfx.data;

import net.t7seven7t.craftfx.data.trigger.HoldData;
import net.t7seven7t.craftfx.data.trigger.MoveData;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ConfigDataHolderTest {

    @Test
    public void testGetData() throws Exception {
        ConfigDataHolder dataHolder = new ConfigDataHolder(null) {
            @Override
            public <T> Optional<T> get(String propertyName, Class<T> clazz) {
                return Optional.empty();
            }
        };
        dataHolder.offer(new HoldData(10, 14));
        dataHolder.offer(new MoveData(2, maxMoveDistDef));

        Optional<HoldData> holdData = dataHolder.getData(HoldData.class);
        assertTrue(holdData.isPresent());
        assertEquals(holdData.get().getMinimumStackSize(), 10);
        assertEquals(holdData.get().getMaximumStackSize(), 14);

        Optional<MoveData> moveData = dataHolder.getData(MoveData.class);
        assertTrue(moveData.isPresent());
        assertEquals(moveData.get().getMinMoveDist(), 2, 0);

        dataHolder.offer(new MoveData(40, maxMoveDistDef));
        dataHolder.offer(new HoldData(41, 32));

        holdData = dataHolder.getData(HoldData.class);
        assertTrue(holdData.isPresent());
        assertEquals(holdData.get().getMinimumStackSize(), 41);
        assertEquals(holdData.get().getMaximumStackSize(), 32);

        moveData = dataHolder.getData(MoveData.class);
        assertTrue(moveData.isPresent());
        assertEquals(moveData.get().getMinMoveDist(), 40, 0);
    }
}