package com.roughlyunderscore.enchs;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import static com.roughlyunderscore.enchs.parsers.action.ActionUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.roughlyunderscore.enchs.util.general.Utils.*;

@SuppressWarnings({"unused","FieldCanBeLocal"})
public class TestUE {

    // I don't know why but @BeforeEach/@BeforeAll don't work??
    // XMaterial fails to initialize the server

    private static UnderscoreEnchants plugin;
    private static ServerMock server;

    @RepeatedTest(25)
    public void testParseItem1() {
        server = MockBukkit.mock();

        final ItemStack item = parseItem("stone:3");
        assertTrue(arrayContains(arrayOf(new ItemStack(Material.STONE, 3)), item));

        MockBukkit.unmock();
    }

    @RepeatedTest(25)
    public void testParseItem2() {
        server = MockBukkit.mock();

        final ItemStack item = parseItem("stone:3;cobblestone:15");
        assertTrue(arrayContains(arrayOf(new ItemStack(Material.STONE, 3), new ItemStack(Material.COBBLESTONE, 15)), item));

        MockBukkit.unmock();
    }

    @RepeatedTest(25)
    public void testParseItem3() {
        server = MockBukkit.mock();

        final ItemStack item = parseItem("STONE:3:40%;COBBLESTONE:60%:15");
        assertTrue(arrayContains(arrayOf(new ItemStack(Material.STONE, 3), new ItemStack(Material.COBBLESTONE, 15)), item));

        MockBukkit.unmock();
    }

    @RepeatedTest(25)
    public void testParseItem4() {
        server = MockBukkit.mock();

        final ItemStack item = parseItem("STONE:3:15%;COBBLESTONE:20%:30;GRANITE:10;OAK_LOG:15%");
        assertTrue(arrayContains(arrayOf(new ItemStack(Material.STONE, 3), new ItemStack(Material.COBBLESTONE, 30), new ItemStack(Material.GRANITE, 10), new ItemStack(Material.OAK_LOG)), item));

        MockBukkit.unmock();
    }

    @RepeatedTest(25)
    public void testParseItem5() {
        server = MockBukkit.mock();

        final ItemStack item = parseItem("STONE:50%;COBBLESTONE:30%");
        assertTrue(arrayContains(arrayOf(new ItemStack(Material.STONE), new ItemStack(Material.COBBLESTONE), new ItemStack(Material.AIR)), item));

        MockBukkit.unmock();
    }
}
