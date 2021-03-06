package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class Hotbar extends OverlayElement {
    private SettingPosition position;

    public Hotbar() {
        super("hotbar");

        position = new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE);
        position.setEdge(true);
        position.setPostSpacer(2);

        settings.addChild(position);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        final Minecraft mc = Minecraft.getMinecraft();

        return GuiIngameForge.renderHotbar
            && !OverlayHook.pre(context.getEvent(), ElementType.HOTBAR)
            && (
                !mc.playerController.isSpectator()
                || mc.ingameGUI.getSpectatorGui().isMenuActive()
            );
    }

    @Override
    public Rect render(OverlayContext context) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (Minecraft.getMinecraft().playerController.isSpectator()) {
            GuiSpectator spectator = mc.ingameGUI.getSpectatorGui();
            ScaledResolution res = new ScaledResolution(mc);

            spectator.renderTooltip(res, context.getPartialTicks());
            spectator.renderSelectedItem(res);

            return new Rect(182, 22).anchor(context.getLayoutManager().getScreen(), Direction.SOUTH);
        } else {
            return renderHotbar(context, mc);
        }
    }

    private Rect renderHotbar(OverlayContext context, Minecraft mc) {
        Rect barTexture = new Rect(182, 22);
        Rect bounds = position.applyTo(new Rect(barTexture));

        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
        GlUtil.drawRect(bounds, barTexture);

        Rect slot = bounds.grow(-3).withWidth(16);

        float partialTicks = context.getPartialTicks();
        for(int i = 0; i < 9; i++, slot = slot.translate(Direction.EAST.scale(20))) {
            if(i == Minecraft.getMinecraft().player.inventory.currentItem) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
                GlUtil.drawRect(slot.grow(4), new Rect(0, 22, 24, 24));
            }

            GlUtil.renderHotbarItem(slot, Minecraft.getMinecraft().player.inventory.mainInventory.get(i), partialTicks);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        OverlayHook.post(context.getEvent(), ElementType.HOTBAR);
        return bounds;
    }
}
