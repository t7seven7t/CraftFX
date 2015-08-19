package net.t7seven7t.util;

import org.bukkit.DyeColor;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class MaterialDataUtilTest {

    @Test
    public void testMaterialData() {
        MaterialData data = new Wool(DyeColor.RED);
        assertEquals(MaterialDataUtil.getMaterialData("wool:red"), data);
    }
}
