package net.machinemuse.powersuits.common.items.modules.tool;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IBlockBreakingModule;
import net.machinemuse.api.moduletrigger.IToggleableModule;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.common.items.ItemComponent;
import net.machinemuse.powersuits.common.items.modules.PowerModuleBase;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static net.machinemuse.powersuits.common.MPSConstants.MODULE_PICKAXE;


public class PickaxeModule extends PowerModuleBase implements IBlockBreakingModule, IToggleableModule {
    public static final String PICKAXE_HARVEST_SPEED = "Pickaxe Harvest Speed";
    public static final String PICKAXE_ENERGY_CONSUMPTION = "Pickaxe Energy Consumption";
    protected static final ItemStack emulatedTool = new ItemStack(Items.IRON_PICKAXE);


    public PickaxeModule(List<IModularItem> validItems) {
        super(validItems);
        addInstallCost(new ItemStack(Items.IRON_INGOT, 3));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.solenoid, 1));
        addBaseProperty(PICKAXE_ENERGY_CONSUMPTION, 50, "J");
        addBaseProperty(PICKAXE_HARVEST_SPEED, 8, "x");
        addTradeoffProperty("Overclock", PICKAXE_ENERGY_CONSUMPTION, 950);
        addTradeoffProperty("Overclock", PICKAXE_HARVEST_SPEED, 22);
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_TOOL;
    }

    @Override
    public String getDataName() {
        return MODULE_PICKAXE;
    }

    @Override
    public String getUnlocalizedName() {
        return "pickaxe";
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, IBlockState state, EntityPlayer player) {
        if (ToolHelpers.isEffectiveTool(state, emulatedTool)) {
            if (ElectricItemUtils.getPlayerEnergy(player) > ModuleManager.computeModularProperty(stack, PICKAXE_ENERGY_CONSUMPTION)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        // this does a check using ActualBlockstate, something we can't do without BlockPos
        if (ForgeHooks.canToolHarvestBlock(worldIn, pos, emulatedTool)) {
            ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving, ModuleManager.computeModularProperty(stack, PICKAXE_ENERGY_CONSUMPTION));
            return true;
        }
        return false;
    }

    @Override
    public void handleBreakSpeed(BreakSpeed event) {
        event.setNewSpeed((float) (event.getNewSpeed() * ModuleManager.computeModularProperty(event.getEntityPlayer().inventory.getCurrentItem(), PICKAXE_HARVEST_SPEED)));
    }

    @Override
    public ItemStack getEmulatedTool() {
        return emulatedTool;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.pickaxe;
    }
}