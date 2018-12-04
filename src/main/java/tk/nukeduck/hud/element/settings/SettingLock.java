package tk.nukeduck.hud.element.settings;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.gui.GuiTexturedButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Point;

public class SettingLock extends SettingBoolean {
	private Bounds bounds;

	public SettingLock(String name) {
		super(name);
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		toggler = new GuiTexturedButton(new Bounds(0, 60, 20, 10)).setBounds(bounds).setCallback(b -> toggle());
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
		getGuiParts(parts, callbacks, bounds);
		return null;
	}
}
