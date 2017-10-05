package net.machinemuse_old.powersuits.client.render.modelspec;

import net.machinemuse_old.numina.scala.MuseRegistry;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.Vec3d;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:44 AM, 4/28/13
 *
 * Ported to Java by lehjr on 11/8/16.
 */
public class ModelSpec extends MuseRegistry<ModelPartSpec> {
    private IBakedModel model;
    public Vec3d offset;
    public Vec3d rotation;
    public static String filename;

    public ModelSpec(IBakedModel model, Vec3d offset, Vec3d rotation, String filename) {
        this.model = model;
        this.offset = offset;
        this.rotation = rotation;
        this.filename = filename;
    }

    public IBakedModel getModel() {
        return model;
    }

    public void applyOffsetAndRotation() {
    }

    public String getOwnName() {
        String name = ModelRegistry.getInstance().getName(this);
        return (name != null) ? name : "";
    }
}