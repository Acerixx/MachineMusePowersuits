package net.machinemuse_old.powersuits.powermodule.tool;

import net.machinemuse_old.api.IModularItem;
import net.machinemuse_old.api.ModuleManager;
import net.machinemuse_old.api.moduletrigger.IRightClickModule;
import net.machinemuse_old.general.gui.MuseIcon;
import net.machinemuse_old.powersuits.item.ItemComponent;
import net.machinemuse_old.powersuits.powermodule.PowerModuleBase;
import net.machinemuse_old.powersuits.powermodule.PropertyModifierIntLinearAdditive;
import net.machinemuse_old.utils.ElectricItemUtils;
import net.machinemuse_old.utils.MuseCommonStrings;
import net.machinemuse_old.utils.MuseItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by User: Andrew2448
 * 7:13 PM 4/21/13
 */
public class LeafBlowerModule extends PowerModuleBase implements IRightClickModule {
    private static final String MODULE_LEAF_BLOWER = "Leaf Blower";
    private static final String LEAF_BLOWER_ENERGY_CONSUMPTION = "Energy Consumption";
    private static final String RADIUS = "Radius";


//    private static final String PLANT_RADIUS = "Plant Radius";
//    private static final String LEAF_RADIUS = "Leaf Radius";
//    private static final String SNOW_RADIUS = "Snow Radius";

    public LeafBlowerModule(List<IModularItem> validItems) {
        super(validItems);
        addInstallCost(new ItemStack(Items.IRON_INGOT, 3));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.solenoid, 1));
        addBaseProperty(LEAF_BLOWER_ENERGY_CONSUMPTION, 100, "J");

        addBaseProperty(RADIUS, 1, "m");


//        addBaseProperty(PLANT_RADIUS, 1, "m");
//        addBaseProperty(LEAF_RADIUS, 1, "m");
//        addBaseProperty(SNOW_RADIUS, 1, "m");

        addIntTradeoffProperty(RADIUS, RADIUS, 8, "m", 1, 0);

//        addIntTradeoffProperty(PLANT_RADIUS, PLANT_RADIUS, 8, "m", 1, 0);
//        addIntTradeoffProperty(LEAF_RADIUS, LEAF_RADIUS, 8, "m", 1, 0);
//        addIntTradeoffProperty(SNOW_RADIUS, SNOW_RADIUS, 5, "m", 1, 0);
    }

    public PowerModuleBase addIntTradeoffProperty(String tradeoffName, String propertyName, double multiplier, String unit, int roundTo, int offset) {
        units.put(propertyName, unit);
        return addPropertyModifier(propertyName, new PropertyModifierIntLinearAdditive(tradeoffName, multiplier, roundTo, offset));
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_TOOL;
    }

    @Override
    public String getDataName() {
        return MODULE_LEAF_BLOWER;
    }

    @Override
    public String getUnlocalizedName() {
        return "leafBlower";
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        int radius = (int) ModuleManager.computeModularProperty(itemStackIn, RADIUS);
        if (useBlower(radius, itemStackIn, playerIn, worldIn, playerIn.getPosition()))
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);

        //        Block blockID = world.getBlock(x, y, z);
//        int plant = (int) ModuleManager.computeModularProperty(itemStack, PLANT_RADIUS);
//        int leaf = (int) ModuleManager.computeModularProperty(itemStack, LEAF_RADIUS);
//        int snow = (int) ModuleManager.computeModularProperty(itemStack, SNOW_RADIUS);
//        int totalEnergyDrain = 0;
//
//        // Plants
//        useBlower(plant, "plants", itemStack, player, world,  x, y, z);
//        // Leaves
//        useBlower(leaf, "leaves", itemStack, player, world,  x, y, z);
//        // Snow
//        useBlower(snow, "snow", itemStack, player, world,  x, y, z);



        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    private boolean useBlower(int radius, ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
        int totalEnergyDrain = 0;
        BlockPos newPos;
        for (int i = pos.getX() - radius; i < pos.getX() + radius ; i++) {
            for (int j = pos.getY() - radius; j < pos.getY() + radius; j++) {
                for (int k = pos.getZ() - radius; k < pos.getZ() + radius; k++) {
                    newPos = new BlockPos(i, j, k);
                    if (ToolHelpers.blockCheckAndHarvest(player, world, newPos)) {
                        totalEnergyDrain += ModuleManager.computeModularProperty(itemStack, LEAF_BLOWER_ENERGY_CONSUMPTION);
                    }
                }
            }
        }
        ElectricItemUtils.drainPlayerEnergy(player, totalEnergyDrain);
        return true;
    }


    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.leafBlower;
    }
}
