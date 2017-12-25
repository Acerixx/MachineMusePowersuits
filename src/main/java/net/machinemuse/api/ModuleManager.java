package net.machinemuse.api;

import net.machinemuse.api.moduletrigger.IBlockBreakingModule;
import net.machinemuse.api.moduletrigger.IPlayerTickModule;
import net.machinemuse.api.moduletrigger.IRightClickModule;
import net.machinemuse.api.moduletrigger.IToggleableModule;
import net.machinemuse.numina.api.capabilties.item.IModeChangingItem;
import net.machinemuse.numina.api.item.IModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    public static final String ONLINE = "Active";

    protected static final Map<String, List<ItemStack>> customInstallCosts = new HashMap<>();
    protected static final Map<String, IModule> moduleMap = new HashMap<>();
    protected static final List<IModule> moduleList = new ArrayList<>();
    protected static final List<IPlayerTickModule> playerTickModules = new ArrayList<>();
    protected static final List<IRightClickModule> rightClickModules = new ArrayList<>();
    protected static final List<IToggleableModule> toggleableModules = new ArrayList<>();
    protected static final List<IBlockBreakingModule> blockBreakingModules = new ArrayList<>();

    public static List<IModule> getAllModules() {
        return moduleList;
    }

    public static List<IPlayerTickModule> getPlayerTickModules() {
        return playerTickModules;
    }

    @Nullable
    public static IModule getModule(String key) {
        return moduleMap.get(key);
    }

    public static void addModule(IModule module) {

        moduleMap.put(module.getDataName(), module);
        moduleList.add(module);
        if (module instanceof IPlayerTickModule) {
            playerTickModules.add((IPlayerTickModule) module);
        }
        if (module instanceof IRightClickModule) {
            rightClickModules.add((IRightClickModule) module);
        }
        if (module instanceof IToggleableModule) {
            toggleableModules.add((IToggleableModule) module);
        }
        if (module instanceof IBlockBreakingModule) {
            blockBreakingModules.add((IBlockBreakingModule) module);
        }
    }

    public static double computeModularProperty(ItemStack stack, String propertyName) {
        double propertyValue = 0;
        NBTTagCompound itemTag = MuseItemTag.getMuseItemTag(stack);
        for (IModule module : moduleList) {
            if (itemHasActiveModule(stack, module.getDataName())) {
                propertyValue = module.applyPropertyModifiers(itemTag, propertyName, propertyValue);
            }
        }
        return propertyValue;
    }

    public static List<IRightClickModule> getRightClickModules() {
        return rightClickModules;
    }

    public static List<IToggleableModule> getToggleableModules() {
        return toggleableModules;
    }

    public static List<IBlockBreakingModule> getBlockBreakingModules() {
        return blockBreakingModules;
    }

    public static List<IModule> getValidModulesForItem(ItemStack stack) {
        List<IModule> validModules = new ArrayList();
        for (IModule module : getAllModules()) {
            if (module.isValidForItem(stack)) {
                validModules.add(module);
            }
        }
        return validModules;
    }

    public static boolean tagHasModule(NBTTagCompound tag, String moduleName) {
        return tag.hasKey(moduleName);
    }

    public static boolean isModuleOnline(NBTTagCompound itemTag, String moduleName) {
        if (tagHasModule(itemTag, moduleName) && !itemTag.getCompoundTag(moduleName).hasKey(ONLINE)) {
            return true;
        } else if (tagHasModule(itemTag, moduleName) && itemTag.getCompoundTag(moduleName).getBoolean(ONLINE)) {
            return true;
        }
        return false;
    }

    public static void toggleModule(NBTTagCompound itemTag, String name, boolean toggleval) {
        if (tagHasModule(itemTag, name)) {
            NBTTagCompound moduleTag = itemTag.getCompoundTag(name);
            moduleTag.setBoolean(ONLINE, toggleval);
        }
    }

    public static boolean itemHasModule(ItemStack stack, String moduleName) {
        return tagHasModule(MuseItemTag.getMuseItemTag(stack), moduleName);
    }

    public static void tagAddModule(NBTTagCompound tag, IModule module) {
        tag.setTag(module.getDataName(), module.getNewTag());
    }

    public static void itemAddModule(ItemStack stack, IModule moduleType) {
        tagAddModule(MuseItemTag.getMuseItemTag(stack), moduleType);
    }

    public static boolean removeModule(NBTTagCompound tag, String moduleName) {
        if (tag.hasKey(moduleName)) {
            tag.removeTag(moduleName);
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeModule(ItemStack stack, String moduleName) {
        return removeModule(MuseItemTag.getMuseItemTag(stack), moduleName);
    }

    public static boolean itemHasActiveModule(ItemStack itemStack, String moduleName) {
        IModule module = getModule(moduleName);
        if (module == null || itemStack == null || !module.isAllowed() || !(itemStack.getItem() instanceof IModularItem)) {
            // playerEntity.sendChatToPlayer("Server has disallowed this module. Sorry!");
            return false;
        }
        if (module instanceof IRightClickModule && itemStack.getItem() instanceof IModeChangingItem) {
            // MuseLogger.logDebug("Module: " + moduleName + " vs Mode: " +
            // MuseItemUtils.getActiveMode(itemStack));
            IModeChangingItem item = (IModeChangingItem) itemStack.getItem();

            return moduleName.equals(item.getActiveMode(itemStack));
        } else {
            return isModuleOnline(MuseItemTag.getMuseItemTag(itemStack), moduleName);
        }
    }

    public static boolean hasCustomInstallCost(String dataName) {
        return customInstallCosts.containsKey(dataName);
    }

    public static List<ItemStack> getCustomInstallCost(String dataName) {
        return customInstallCosts.get(dataName);
    }

    public static void addCustomInstallCost(String moduleName, ItemStack stack) {
        if(customInstallCosts.containsKey(moduleName)) {
            customInstallCosts.get(moduleName).add(stack);
        } else {
            customInstallCosts.put(moduleName, new ArrayList<ItemStack>());
            customInstallCosts.get(moduleName).add(stack);
        }
    }
}
