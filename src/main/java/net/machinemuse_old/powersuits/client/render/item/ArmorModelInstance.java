package net.machinemuse_old.powersuits.client.render.item;

import net.minecraft.client.model.ModelBiped;
/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:01 PM, 11/07/13
 */
public class ArmorModelInstance {
    private static ModelBiped instance = null;
    public static ModelBiped getInstance() {
//        // TODO: Actual (skinned) vanilla model as low bandwitdth alternative
//        if (instance == null) {
//            if ( ModCompatibility.isRenderPlayerAPILoaded()) {
//                try {
//                    MuseLogger.logInfo("Attempting to load SmartMoving armor model.");
//                    instance = Class.forName("net.machinemuse_old.powersuits.client.render.item.SMovingArmorModel").asSubclass(ModelBiped.class).newInstance();
//                    MuseLogger.logInfo("SmartMoving armor model loaded successfully!");
//                } catch (Exception e) {
//                    MuseLogger.logInfo("Smart Moving armor model did not load successfully. Either Smart Moving is not installed, or there was another problem.");
//                    instance = HighPolyArmor.getInstance();
//                }
//            } else {
                instance = HighPolyArmor.getInstance();
//            }
//        }
        return instance;
    }
}