package jobicade.betterhud.util.bars;

import java.util.Random;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.RandomWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class StatBarFood extends StatBarBasic<EntityPlayer> {
    private final Random random = new Random();

    @Override
    protected int getCurrent() {
        return host.getFoodStats().getFoodLevel();
    }

    @Override
    protected Rect getIcon(IconType icon, int pointsIndex) {
        boolean hasHunger = host.isPotionActive(MobEffects.HUNGER);
        int xOffset = hasHunger ? 88 : 52;

        switch(icon) {
            case BACKGROUND: return new Rect(hasHunger ? 133 : 16, 27, 9, 9);
            case HALF:       return new Rect(xOffset + 9, 27, 9, 9);
            case FULL:       return new Rect(xOffset, 27, 9, 9);
            default:         return null;
        }
    }

    @Override
    public Direction getNativeAlignment() {
        return Direction.EAST;
    }

    @Override
    protected int getIconBounce(int pointsIndex) {
        if(host.getFoodStats().getSaturationLevel() <= 0 && Minecraft.getMinecraft().ingameGUI.getUpdateCounter() % (getCurrent() * 3 + 1) == 0) {
            return new RandomWrapper(random).nextInt(-1, 2);
        } else {
            return 0;
        }
    }

    @Override
    public void render() {
        random.setSeed(Minecraft.getMinecraft().ingameGUI.getUpdateCounter());
        super.render();
    }
}
