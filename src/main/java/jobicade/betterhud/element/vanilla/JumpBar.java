package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class JumpBar extends OverrideElement {
	private SettingPosition position;

	public JumpBar() {
		super("jumpBar");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(position = new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(2);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().player.isRidingHorse();
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

		Rect bounds = new Rect(182, 5);
		if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
			bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		float charge = Minecraft.getMinecraft().player.getHorseJumpPower();
		int filled = (int)(charge * bounds.getWidth());

		GlUtil.drawRect(bounds, bounds.move(0, 84));

		if(filled > 0) {
			GlUtil.drawRect(bounds.withWidth(filled), new Rect(0, 89, filled, bounds.getHeight()));
		}
		return bounds;
	}

	@Override
	protected ElementType getType() {
		return ElementType.JUMPBAR;
	}
}
