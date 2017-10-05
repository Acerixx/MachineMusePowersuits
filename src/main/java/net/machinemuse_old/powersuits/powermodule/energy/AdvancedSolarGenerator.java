package net.machinemuse_old.powersuits.powermodule.energy;

import net.machinemuse_old.api.IModularItem;
import net.machinemuse_old.api.ModuleManager;
import net.machinemuse_old.api.moduletrigger.IPlayerTickModule;
import net.machinemuse_old.general.gui.MuseIcon;
import net.machinemuse_old.powersuits.item.ItemComponent;
import net.machinemuse_old.powersuits.powermodule.PowerModuleBase;
import net.machinemuse_old.utils.ElectricItemUtils;
import net.machinemuse_old.utils.MuseCommonStrings;
import net.machinemuse_old.utils.MuseHeatUtils;
import net.machinemuse_old.utils.MuseItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Eximius88 on 1/12/14.
 */
public class AdvancedSolarGenerator extends PowerModuleBase implements IPlayerTickModule {
    public static final String MODULE_ADVANCED_SOLAR_GENERATOR = "Advanced Solar Generator";
    public static final String A_SOLAR_ENERGY_GENERATION_DAY = "Daytime Energy Generation";
    public static final String A_SOLAR_ENERGY_GENERATION_NIGHT = "Nighttime Energy Generation";
    public static final String SOLAR_HEAT_GENERATION_DAY = "Daytime Heat Generation";
    public static final String SOLAR_HEAT_GENERATION_NIGHT = "Nighttime Heat Generation";

    public AdvancedSolarGenerator(List<IModularItem> validItems) {
        super(validItems);
        addBaseProperty(SOLAR_HEAT_GENERATION_DAY, 15);
        addBaseProperty(SOLAR_HEAT_GENERATION_NIGHT, 5);
        addBaseProperty(A_SOLAR_ENERGY_GENERATION_DAY, 4500);
        addBaseProperty(A_SOLAR_ENERGY_GENERATION_NIGHT, 150);
        addBaseProperty(MuseCommonStrings.WEIGHT, 300);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.solarPanel, 3));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 2));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.computerChip, 1));
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_ENERGY;
    }

    @Override
    public String getDataName() {
        return MODULE_ADVANCED_SOLAR_GENERATOR;
    }

    @Override
    public String getUnlocalizedName() {
        return "advSolarGenerator";
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet != null && helmet.equals(item)) {
            World world = player.world;
            int xCoord = MathHelper.floor_double(player.posX);
            int zCoord = MathHelper.floor_double(player.posZ);
            boolean isRaining, canRain = true;
            if (world.getTotalWorldTime() % 20 == 0) {
                canRain = world.getBiome(player.getPosition()).canRain();
            }
            isRaining = canRain && (world.isRaining() || world.isThundering());
            boolean sunVisible = world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().add(0,1,0));
            boolean moonVisible = !world.isDaytime() && !isRaining && world.canBlockSeeSky(player.getPosition().add(0,1,0));
            if (!world.isRemote && !world.provider.getHasNoSky() && (world.getTotalWorldTime() % 80) == 0) {
                if (sunVisible) {
                    ElectricItemUtils.givePlayerEnergy(player, ModuleManager.computeModularProperty(item, A_SOLAR_ENERGY_GENERATION_DAY));
                    MuseHeatUtils.heatPlayer(player, ModuleManager.computeModularProperty(item, SOLAR_HEAT_GENERATION_DAY) / 2);
                } else if (moonVisible) {
                    ElectricItemUtils.givePlayerEnergy(player, ModuleManager.computeModularProperty(item, A_SOLAR_ENERGY_GENERATION_NIGHT));
                    MuseHeatUtils.heatPlayer(player, ModuleManager.computeModularProperty(item, SOLAR_HEAT_GENERATION_NIGHT) / 2);
                }
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.advSolarGenerator;
    }
}