package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name.
 *
 * <p>Settings use a variant of the "Curiously Recurring Template Pattern"
 * (CRTP), which is originally a C++ patttern. In this case a similar idea is
 * used to support a fluent interface that doesn't lose specificity after each
 * method. Without this, chaining {@code .setA(5).setB(6)} fails if {@code setA}
 * is a method of the superclass and {@code setB} is a method of the subclass,
 * and the final return value has to be casted unless no superclass methods are
 * called.
 *
 * <p>One side effect of the pattern is that all the fluent interface methods
 * return the first concrete class in the heirarchy (which specifies {@code T}).
 * Further subclasses should be avoided (e.g. mark final) or fall back on
 * casting.
 */
public abstract class Setting {
	private Setting parent = null;
	protected final List<Setting> children = new ArrayList<>();
	public final String name;

	private Category category = Category.MISC;

	private String unlocalizedName;

	/** The config property associated with this setting */
	private Property property;

	/** Set to {@code true} to hide the setting from the GUI
	 * @see #getGuiParts(List, Map, Point) */
	private boolean hidden = false;

	private BooleanSupplier enableOn = () -> true;

	public Setting(String name) {
		this.name = name;
		if(name != null) this.unlocalizedName = "betterHud.setting." + name;
	}

	public String getName() {
		return name;
	}

	public Iterable<Setting> getChildren() {
		return children;
	}

	public Category getCategory() {
		return category;
	}

	// Upcasting fluent interface without covariance

	public Setting setCategory(Category category) {
		this.category = category;
		return this;
	}

	public Setting setEnableOn(BooleanSupplier enableOn) {
		this.enableOn = enableOn;
		return this;
	}

	public Setting setHidden() {
		this.hidden = true;
		return this;
	}

	public final void addChild(Setting setting) {
		children.add(setting);
		setting.parent = this;
	}

	public final void addChildren(Iterable<Setting> settings) {
		for (Setting setting : settings) {
			addChild(setting);
		}
	}

	public final void addChildren(Setting... settings) {
		for (Setting setting : settings) {
			addChild(setting);
		}
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public Setting setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** @return {@code true} if this element and its ancestors are enabled */
	public final boolean enabled() {
		return (parent == null || parent.enabled()) && enableOn.getAsBoolean();
	}

	/** Binds properties to elements for loading and saving */
	protected final void bindConfig(HudConfig config, String category, String path) {
		if(hasValue()) {
			property = getProperty(config, category, path);
		}

		for(Setting child : children) {
			String childPath;
			if (path.isEmpty()) {
				childPath = child.name;
			} else {
				childPath = path + "." + child.name;
			}

			child.bindConfig(config, category, childPath);
		}
	}

	/** Loads this setting and all its children */
	public final void loadConfig() {
		if(property != null) {
			loadStringValue(property.getString());
		}

		for(Setting child : children) {
			child.loadConfig();
		}
	}

	/** Saves this setting and all its children to {@code config} */
	public final void saveConfig() {
		if(property != null) {
			String save = getStringValue();

			if(property.getString() != save) {
				property.setValue(save);
			}
		}

		for(Setting child : children) {
			child.saveConfig();
		}
	}

	protected Property getProperty(HudConfig config, String category, String path) {
		ConfigCategory configCategory = config.getCategory(category);
		Property property = null;
		String save = getStringValue();

		if(configCategory.containsKey(path)) {
			property = configCategory.get(path);
		}

		if(property == null || property.getType() != getPropertyType()) {
			property = new Property(path, getStringValue(), getPropertyType());
			property.setValue(save);

			configCategory.put(path, property);
		}

		property.setDefaultValue(save);
		return property;
	}

	protected Type getPropertyType() {
		return Type.STRING;
	}

	/** Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
	 * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
	 *
	 * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, Point)})
	 * populates {@code parts} and {@code callbacks} with those of the element's children
	 *
	 * @param origin The top center point for GUI parts being added
	 * @return The new origin directly below this setting's parts */
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
		return getGuiParts(parts, callbacks, origin, children);
	}

	/** Populates {@code parts} and {@code callbacks} by calling
	 * {@link #getGuiParts(List, Map, Point)} on the given {@code settings},
	 * and maintaining {@code y} between them
	 *
	 * @param origin The top center point for GUI parts being added
	 * @return The bottom center point of all {@code settings}
	 * @see #getGuiParts(List, Map, Point) */
	public static Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin, List<Setting> settings) {
		if(!settings.isEmpty()) {
			for(Setting setting : settings) {
				if(!setting.hidden) {
					Point bottom = setting.getGuiParts(parts, callbacks, origin);

					if(bottom != null) {
						origin = bottom;
					}
				}
			}
		}
		return origin;
	}

	/** Renders extra parts of this GUI */
	public void draw() {
		for(Setting setting : children) {
			setting.draw();
		}
	}

	// TODO why here?
	/** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
	 * @param button The GuiButton that was pressed. */
	public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);

	/** Updates the GUI elements based on the state of other settings.
	 * This is called when any button tied to a setting callback is pressed */
	public void updateGuiParts(Collection<Setting> settings) {
		for(Setting setting : children) {
			setting.updateGuiParts(settings);
		}
	}

    public abstract boolean hasValue();
    public abstract String getStringValue();
    public abstract String getDefaultValue();

	/**
	 * If {@code stringValue} is valid, sets the value accordingly.
     *
	 * @param stringValue A string as generated by {@link #getStringValue()}.
     * @return {@code true} if the value was updated.
	 */
    public abstract void loadStringValue(String stringValue);
    public abstract void loadDefaultValue();

	public enum Category {
		MISC("misc"), POSITION("position");

		private final String unlocalizedName;

		Category(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}

		public String getUnlocalizedName() {
			return "betterHud.group." + unlocalizedName;
		}
	}
}
