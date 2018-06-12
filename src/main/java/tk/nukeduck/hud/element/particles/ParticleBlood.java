package tk.nukeduck.hud.element.particles;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.RANDOM;

import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.util.Point;

public class ParticleBlood extends Particle {
	float opacity;
	float size;
	int rotation;
	int u, v;

	public ParticleBlood(int x, int y, float opacity, int rotation, float size, int u, int v) {
		super(new Point(x, y));
		this.opacity = opacity;
		this.size = size;
		this.rotation = rotation;
		this.u = u;
		this.v = v;
	}

	public static ParticleBlood random(int width, int height) {
		int x = RANDOM.nextInt(width);
		int y = RANDOM.nextInt(height);
		float opacity = RANDOM.nextFloat() / 2;
		int rotation = RANDOM.nextInt(360);
		float size = 2f + RANDOM.nextFloat() * 4f;
		int u = RANDOM.nextInt(4);
		int v = RANDOM.nextInt(4);
		return new ParticleBlood(x, y, opacity, rotation, size, u, v);
	}

	@Override
	public void render() {
		GlStateManager.pushMatrix();

		GlStateManager.color(1, 1, 1, opacity);
		GlStateManager.translate(position.getX(), position.getY(), 0.0F);
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.scale(this.size, this.size, 1.0F);
		MC.ingameGUI.drawTexturedModalRect(0, 0, this.u * 16, this.v * 16, 16, 16);

		GlStateManager.popMatrix();
	}

	@Override
	public boolean update() {
		return (this.opacity -= 0.003) <= 0;
	}
}
