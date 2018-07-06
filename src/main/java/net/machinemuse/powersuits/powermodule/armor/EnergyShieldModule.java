package net.machinemuse.powersuits.powermodule.armor;

import net.machinemuse.numina.api.item.IModularItem;
import net.machinemuse.numina.utils.item.MuseItemUtils;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.powersuits.utils.MuseCommonStrings;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnergyShieldModule extends PowerModuleBase {
    public static final String MODULE_ENERGY_SHIELD = "Energy Shield";

    public EnergyShieldModule(List<IModularItem> validItems) {
        super(validItems);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));
        addTradeoffProperty("Field Strength", MuseCommonStrings.ARMOR_VALUE_ENERGY, 6, " Points");
        addTradeoffProperty("Field Strength", MuseCommonStrings.ARMOR_ENERGY_CONSUMPTION, 500, "J");
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_ARMOR;
    }

    @Override
    public String getDataName() {
        return MODULE_ENERGY_SHIELD;
    }

    @Override
    public String getUnlocalizedName() { return "energyShield";
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.energyShield;
    }
}