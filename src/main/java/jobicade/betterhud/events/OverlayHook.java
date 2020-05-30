package jobicade.betterhud.events;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.registry.SortField;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = { Side.CLIENT }, modid = BetterHud.MODID)
public final class OverlayHook {
    // No instance
    private OverlayHook() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preOverlayEarly(RenderGameOverlayEvent.Pre event) {
        // Only side effect is GuiIngameForge.left_height and right_height
        // If the event is canceled these variables don't have much meaning
        if (shouldRun(event)) {
            // Pre-rendering hotbar so no left or right height yet
            GuiIngameForge.left_height = 0;
            GuiIngameForge.right_height = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preOverlayLate(RenderGameOverlayEvent.Pre event) {
        if (!event.isCanceled() && shouldRun(event)) {
            // Pre event is a valid parent as it just carries identical
            // information to its own parent
            renderGameOverlay(event);
            // Only cancel right before replacing vanilla HUD
            // Other mods get a chance to cancel the HUD altogether
            event.setCanceled(true);
        }
    }

    /**
     * @return {@code true} if Better HUD should run after {@code event}.
     */
    private static boolean shouldRun(RenderGameOverlayEvent.Pre event) {
        return BetterHud.getProxy().isModEnabled()
            && !event.isCanceled() // See comment in preOverlayEarly
            && event.getType() == ElementType.ALL; // Only run once each frame
    }

    /**
     * Replacement drop-in for {@link GuiIngameForge#renderGameOverlay(float)}
     * Starting after the {@code Pre} event.
     */
    private static void renderGameOverlay(RenderGameOverlayEvent event) {
        // TODO not here
        BetterHud.MANAGER.reset(event.getResolution());

        for (OverlayElement element : OverlayElements.get().getRegistered(SortField.PRIORITY)) {
            if (shouldRender(element, event)) {
                Minecraft.getMinecraft().mcProfiler.startSection(element.getRegistryName().toString());
                element.render(event);
                Minecraft.getMinecraft().mcProfiler.endSection();
            }
        }
    }

    /**
     * @return {@code true} if all conditions for rendering {@code hudElement}
     * are currently satisfied.
     */
    public static boolean shouldRender(OverlayElement hudElement, RenderGameOverlayEvent context) {
        return hudElement.getServerDependency().containsVersion(BetterHud.getServerVersion())
            && hudElement.isEnabled()
            && hudElement.shouldRender(context);
    }

    /**
     * Prepares OpenGL state and posts an appropriate event to mimic
     * {@link GuiIngameForge#renderGameOverlay(float)} just before rendering
     * {@code elementType}.
     *
     * @return {@code true} if the event was canceled.
     */
    public static boolean mimicPre(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        // TODO load OpenGL state

        return MinecraftForge.EVENT_BUS.post(
            new RenderGameOverlayEvent.Pre(parentEvent, elementType));
    }

    /**
     * Prepares OpenGL state and posts an appropriate event to mimic
     * {@link GuiIngameForge#renderGameOverlay(float)} just after rendering
     * {@code elementType}.
     */
    public static void mimicPost(RenderGameOverlayEvent parentEvent, ElementType elementType) {
        // TODO load OpenGL state

        MinecraftForge.EVENT_BUS.post(
            new RenderGameOverlayEvent.Pre(parentEvent, elementType));
    }
}
