package net.machinemuse.powersuits.common.powermodule.energy;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IPlayerTickModule;
import net.machinemuse.powersuits.client.events.MuseIcon;
import net.machinemuse.powersuits.common.items.ItemComponent;
import net.machinemuse.powersuits.common.powermodule.PowerModuleBase;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseHeatUtils;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

import static net.machinemuse.powersuits.common.MPSConstants.MODULE_THERMAL_GENERATOR;

/**
 * Created by User: Andrew2448
 * 6:43 PM 4/23/13
 */
public class ThermalGeneratorModule extends PowerModuleBase implements IPlayerTickModule {
    public static final String THERMAL_ENERGY_GENERATION = "Energy Generation";

    public ThermalGeneratorModule(List<IModularItem> validItems) {
        super(validItems);
        addBaseProperty(THERMAL_ENERGY_GENERATION, 25);
        addBaseProperty(MuseCommonStrings.WEIGHT, 1000);
        addTradeoffProperty("Energy Generated", THERMAL_ENERGY_GENERATION, 25, " Joules");
        addTradeoffProperty("Energy Generated", MuseCommonStrings.WEIGHT, 1000, "g");
// Fixme: and maybe add options for a Magmatic Dynamo and maybe a stirling generator
//        if (ModCompatibility.isIndustrialCraftLoaded()) {
//            addInstallCost(ModCompatibility.getIC2Item("geothermalGenerator"));
//            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
//        } else {
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 2));
            addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.basicPlating, 1));
//        }
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_ENERGY;
    }

    @Override
    public String getDataName() {
        return MODULE_THERMAL_GENERATOR;
    }

    @Override
    public String getUnlocalizedName() {
        return "thermalGenerator";
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        double currentHeat = MuseHeatUtils.getPlayerHeat(player);
        double maxHeat = MuseHeatUtils.getMaxHeat(player);
        if (player.world.getTotalWorldTime() % 20 == 0) {
            if (player.isBurning()) {
                ElectricItemUtils.givePlayerEnergy(player, 4 * ModuleManager.computeModularProperty(item, THERMAL_ENERGY_GENERATION));
            } else if (currentHeat >= 200) {
                ElectricItemUtils.givePlayerEnergy(player, 2 * ModuleManager.computeModularProperty(item, THERMAL_ENERGY_GENERATION));
            } else if ((currentHeat / maxHeat) >= 0.5) {
                ElectricItemUtils.givePlayerEnergy(player, ModuleManager.computeModularProperty(item, THERMAL_ENERGY_GENERATION));
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.thermalGenerator;
    }
}