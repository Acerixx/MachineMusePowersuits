package net.machinemuse.general.gui.frame;


import net.machinemuse.api.IModularItem;
import net.machinemuse.general.gui.clickable.ClickableSlider;
import net.machinemuse.numina.general.MuseLogger;
import net.machinemuse.numina.geometry.Colour;
import net.machinemuse.numina.geometry.DrawableMuseRect;
import net.machinemuse.numina.geometry.MusePoint2D;
import net.machinemuse.numina.geometry.MuseRect;
import net.machinemuse.numina.network.PacketSender;
import net.machinemuse.powersuits.common.items.old.ItemPowerArmor;
import net.machinemuse.powersuits.network.packets.MusePacketColourInfo;
import net.machinemuse.utils.MuseItemUtils;
import net.machinemuse.utils.render.GuiIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:19 AM, 03/05/13
 *
 * Ported to Java by lehjr on 11/2/16.
 */
public class ColourPickerFrame implements IGuiFrame {
    public ItemSelectionFrame itemSelector;
    public DrawableMuseRect border;
    public ClickableSlider rslider;
    public ClickableSlider gslider;
    public ClickableSlider bslider;
    public ClickableSlider selectedSlider;
    public int selectedColour;
    public int decrAbove;

    public ColourPickerFrame(MuseRect borderRef, Colour insideColour, Colour borderColour, ItemSelectionFrame itemSelector) {
        this.itemSelector = itemSelector;
        this.border = new DrawableMuseRect(borderRef, insideColour, borderColour);

        // TODO: replace with single slider
        this.rslider = new ClickableSlider(new MusePoint2D(this.border.centerx(), this.border.top() + 8), this.border.width() - 10, I18n.format("gui.red"));
        this.gslider = new ClickableSlider(new MusePoint2D(this.border.centerx(), this.border.top() + 24), this.border.width() - 10, I18n.format("gui.green"));
        this.bslider = new ClickableSlider(new MusePoint2D(this.border.centerx(), this.border.top() + 40), this.border.width() - 10, I18n.format("gui.blue"));

        this.selectedSlider = null;
        this.selectedColour = 0;
        this.decrAbove = -1;
    }

    public int[] colours() {
        return (getOrCreateColourTag() != null) ? getOrCreateColourTag().getIntArray() : new int[0];
    }

    public NBTTagIntArray getOrCreateColourTag() {
        if (this.itemSelector.getSelectedItem() == null)
            return null;

        NBTTagCompound renderSpec = MuseItemUtils.getMuseRenderTag(this.itemSelector.getSelectedItem().getItem());
        if (renderSpec.hasKey("colours") && renderSpec.getTag("colours") instanceof NBTTagIntArray) {
            return (NBTTagIntArray) renderSpec.getTag("colours");
        } else {
            Item item = this.itemSelector.getSelectedItem().getItem().getItem();
            int[] intArray;
            if (item instanceof ItemPowerArmor) {
                ItemPowerArmor itemPowerArmor = (ItemPowerArmor)item;
                intArray = new int[]{itemPowerArmor.getColorFromItemStack(this.itemSelector.getSelectedItem().getItem()).getInt(),
                        itemPowerArmor.getGlowFromItemStack(this.itemSelector.getSelectedItem().getItem()).getInt() };
            }
            else {
                // TODO: powerfist
                intArray = new int[0];
            }
            renderSpec.setIntArray("colours", intArray);
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.world.isRemote) {
                PacketSender.sendToServer(new MusePacketColourInfo(player, this.itemSelector.getSelectedItem().inventorySlot, this.colours()));
            }
            return (NBTTagIntArray) renderSpec.getTag("colours");
        }
    }

    public NBTTagIntArray setColourTagMaybe(int[] newarray) {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        NBTTagCompound renderSpec = MuseItemUtils.getMuseRenderTag(this.itemSelector.getSelectedItem().getItem());
        renderSpec.setTag("colours", new NBTTagIntArray(newarray));
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.world.isRemote) {
            PacketSender.sendToServer(new MusePacketColourInfo(player, this.itemSelector.getSelectedItem().inventorySlot, this.colours()));
        }
        return (NBTTagIntArray) renderSpec.getTag("colours");
    }

    public ArrayList<Integer> importColours() {
        return new ArrayList<>();
    }

    @Override
    public void onMouseUp(double x, double y, int button) {
        this.selectedSlider = null;
    }


    @Override
    public void update(double mousex, double mousey) {
        if (this.selectedSlider != null) {
            this.selectedSlider.setValueByX(mousex);
            if (colours().length > selectedColour) {
                colours()[selectedColour] = Colour.getInt(rslider.value(), gslider.value(), bslider.value(), 1.0);
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                if (player.world.isRemote)
                    PacketSender.sendToServer(new MusePacketColourInfo(player, itemSelector.getSelectedItem().inventorySlot, colours()));
            }
        }
    }

    @Override
    public void draw() {
        this.border.draw();
        this.rslider.draw();
        this.gslider.draw();
        this.bslider.draw();
        for (int i = 0; i <  colours().length; i++) {
            new GuiIcons.ArmourColourPatch(border.left() + 8 + i * 8, border.bottom() - 16, new Colour(colours()[i]), null, null, null, null);
        }
        new GuiIcons.ArmourColourPatch(this.border.left() + 8 + this.colours().length * 8, this.border.bottom() - 16, Colour.WHITE, null, null, null, null);
        new GuiIcons.SelectedArmorOverlay(this.border.left() + 8 + this.selectedColour * 8, this.border.bottom() - 16, Colour.WHITE, null, null, null, null);
        new GuiIcons.MinusSign(this.border.left() + 8 + this.selectedColour * 8, this.border.bottom() - 24, Colour.RED, null, null, null, null);
        new GuiIcons.PlusSign(this.border.left() + 8 + this.colours().length * 8, this.border.bottom() - 16, Colour.GREEN, null, null, null, null);
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }

    public void onSelectColour(int i) {
        Colour c = new Colour(this.colours()[i]);
        this.rslider.setValue(c.r);
        this.gslider.setValue(c.g);
        this.bslider.setValue(c.b);
        this.selectedColour = i;
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        if (this.rslider.hitBox(x, y))
            this.selectedSlider = this.rslider;
        else if (this.gslider.hitBox(x, y))
            this.selectedSlider = this.gslider;
        else if (this.bslider.hitBox(x, y))
            this.selectedSlider = this.bslider;
        else
            this.selectedSlider = null;

        // add
        if (y > this.border.bottom() - 16 && y < this.border.bottom() - 8) {
            int colourCol = (int)(x - this.border.left() - 8.0) / 8;
            if (colourCol >= 0 && colourCol < colours().length) {
                this.onSelectColour(colourCol);
            } else if (colourCol == this.colours().length) {
                MuseLogger.logDebug("Adding");
                setColourTagMaybe(ArrayUtils.add(getIntArray(getOrCreateColourTag()), Colour.WHITE.getInt()));
            }
        }

        // remove
        if (y > border.bottom() - 24 && y < border.bottom() - 16 && x > border.left() + 8 + selectedColour * 8 && x < border.left() + 16 + selectedColour * 8) {
            NBTTagIntArray nbtTagIntArray = getOrCreateColourTag();

            int[] intArray = getIntArray(nbtTagIntArray);
            if (intArray.length > 1) {
                /* TODO - for 1.10.2 and above, simplyfy with Java 8 collections and streams. Seriously, all this to remove an element from an int array*/
                List<Integer> integerArray = new ArrayList<>();
                int intToRemove = intArray[selectedColour];
                for (int anIntArray : intArray) {
                    if (anIntArray != intToRemove)
                        integerArray.add(anIntArray);
                }
                int[] newIntArray = new int[integerArray.size()];
                int j = 0;
                for (Integer i : integerArray) {
                    newIntArray[j] = i;
                    j+=1;
                }
                setColourTagMaybe(newIntArray);

                decrAbove = selectedColour;
                if (selectedColour == getIntArray(nbtTagIntArray).length) {
                    selectedColour = selectedColour -1;
                }

                EntityPlayerSP player = Minecraft.getMinecraft().player;
                if (player.world.isRemote)
                    PacketSender.sendToServer(new MusePacketColourInfo(player, itemSelector.getSelectedItem().inventorySlot, nbtTagIntArray.getIntArray()));
            }
        }
    }

    public int[] getIntArray(NBTTagIntArray e) {
        if (e == null) // null when no armor item selected
            return new int[0];
        return e.getIntArray();
    }
}