package org.ae_pattern_improve.ae_pattern_improve;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.*;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientComparatorConfig;
import org.ae_pattern_improve.ae_pattern_improve.config.ClientConfig;
import org.ae_pattern_improve.ae_pattern_improve.config.CommonConfig;
import org.ae_pattern_improve.ae_pattern_improve.setup.AEPartsRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.DataComponentRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.ItemsAndBlocksRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.MenuRegistry;
import org.ae_pattern_improve.ae_pattern_improve.setup.client.CreativeTab;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.HostWirelessBatchTerm;
import org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.WirelessBatchTermMenu;
import org.slf4j.Logger;

import static org.ae_pattern_improve.ae_pattern_improve.xmodcompat.wt.HostWirelessBatchTerm.ID4WT;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AePatternImprove.MODID)
public class AePatternImprove {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "ae_pattern_improve";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "ae_pattern_improve" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Creates a new Block with the id "ae_pattern_improve:example_block", combining the namespace and path
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AePatternImprove(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemsAndBlocksRegistry.ITEMS.register(modEventBus);
        DataComponentRegistry.REGISTRAR.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        AEPartsRegistry.init();
        modEventBus.addListener(MenuRegistry::init);

        modEventBus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.ITEM) {
                // deferred initialization after AE2WtLib initialization has been completed
                Icon.Texture TX = new Icon.Texture(getRL("textures/guis/batch_encoder_icon.png"), 16, 16);
                AddTerminalEvent.register(event -> {
                    Preconditions.checkState(AE2wtlibAPI.getWUT() instanceof ItemWUT,
                            "AE2WtLib API is not initialized or WUT is not set. ");
                    event.builder(ID4WT,
                            HostWirelessBatchTerm::new,
                            WirelessBatchTermMenu.TYPE,
                            ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.get(), // the item to be used as the terminal
                            new Icon(0, 0, 16, 16, TX)// the terminal icon
                    ).addTerminal();
                });
            }});
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Ae_test) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientComparatorConfig.CONFIG_SPEC, "ae_pattern_improve_client_comparator.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        GridLinkables.register(
                ItemsAndBlocksRegistry.WIRELESS_BATCH_ENCODER.get(), WirelessTerminalItem.LINKABLE_HANDLER);

//        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
    public static ResourceLocation getRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
