package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

import com.mojang.realmsclient.gui.ChatFormatting;

import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;

public class MobInfo extends EntityInfo {
	private final StatBarHealth bar = new StatBarHealth();
	private SettingSlider compress;

	public MobInfo() {
		super("mobInfo");

		settings.addChild(compress = new SettingSlider("compress", 0, 200, 20) {
			@Override
			public String getDisplayValue(double value) {
				if(value == 0) {
					return I18n.format("betterHud.value.never");
				} else {
					return super.getDisplayValue(value);
				}
			}
		});
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		compress.set(40.0);
		settings.setPriority(-1);
	}

	@Override
	public Rect render(RenderMobInfoEvent context) {
		EntityLivingBase entity = context.getEntity();
		bar.setHost(entity);
		bar.setCompressThreshold(compress.getInt());

		int health = MathUtil.getHealthForDisplay(entity.getHealth());
		int maxHealth = MathUtil.getHealthForDisplay(entity.getMaxHealth());

		String text = String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, health, maxHealth);

		Point size = GlUtil.getStringSize(text);
		Point barSize = bar.getPreferredSize();

		if(barSize.getX() > size.getX()) {
			size = new Point(barSize.getX(), size.getY() + barSize.getY());
		} else {
			size = size.add(0, barSize.getY());
		}

		Rect bounds = MANAGER.position(Direction.SOUTH, new Rect(size).grow(SPACER));
		GlUtil.drawRect(bounds, Color.TRANSLUCENT);
		bounds = bounds.grow(-SPACER);

		GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
		Rect barRect = new Rect(barSize).anchor(bounds, Direction.SOUTH_WEST);

		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		bar.setBounds(barRect).render();
		return null;
	}
}
