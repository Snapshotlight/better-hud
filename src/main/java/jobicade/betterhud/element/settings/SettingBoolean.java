package jobicade.betterhud.element.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class SettingBoolean extends SettingAlignable<SettingBoolean> implements IStringSetting {
	public static final String VISIBLE = "betterHud.value.visible";

	@Override
	protected SettingBoolean getThis() {
		return this;
	}

	protected GuiActionButton toggler;
	private String unlocalizedValue = "options";

	private boolean value = false;

	public SettingBoolean(String name) {
		this(name, Direction.CENTER);
	}

	public SettingBoolean(String name, Direction alignment) {
		super(name, alignment);
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	public SettingBoolean setValuePrefix(String value) {
		this.unlocalizedValue = value;
		return this;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		toggler = new GuiActionButton("").setBounds(bounds).setCallback(b -> value = !value);
		parts.add(toggler);
		callbacks.put(toggler, this);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggler.actionPerformed();
	}

	@Override
	public IStringSetting getStringSetting() {
		return this;
	}

	@Override
	public String getStringValue() {
		return String.valueOf(value);
	}

	@Override
	public String getDefaultValue() {
		return "false";
	}

	@Override
	public void loadStringValue(String stringValue) {
		stringValue = stringValue.trim();

		if ("true".equalsIgnoreCase(stringValue)) {
			value = true;
			//return true;
		} else if ("false".equalsIgnoreCase(stringValue)) {
			value = false;
			//return true;
		} else {
			//return false;
		}
	}

	@Override
	public void loadDefaultValue() {
		value = false;
	}

	@Override
	public void updateGuiParts(Collection<Setting> settings) {
		super.updateGuiParts(settings);
		toggler.enabled = enabled();
		toggler.updateText(getUnlocalizedName(), unlocalizedValue, value);
	}
}
