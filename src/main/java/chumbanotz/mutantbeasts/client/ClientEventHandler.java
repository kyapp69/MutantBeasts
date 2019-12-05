package chumbanotz.mutantbeasts.client;

import chumbanotz.mutantbeasts.MutantBeasts;
import chumbanotz.mutantbeasts.client.particle.LargePortalParticle;
import chumbanotz.mutantbeasts.client.particle.SkullSpiritParticle;
import chumbanotz.mutantbeasts.client.renderer.entity.BodyPartRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.CreeperMinionEggRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.CreeperMinionRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.EndersoulFragmentRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantArrowRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantCreeperRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantEndermanRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantSkeletonRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantSnowGolemRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.MutantZombieRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.SpiderPigRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.ThrowableBlockRenderer;
import chumbanotz.mutantbeasts.client.renderer.entity.layers.CreeperMinionShoulderLayer;
import chumbanotz.mutantbeasts.entity.BodyPartEntity;
import chumbanotz.mutantbeasts.entity.CreeperMinionEggEntity;
import chumbanotz.mutantbeasts.entity.CreeperMinionEntity;
import chumbanotz.mutantbeasts.entity.EndersoulFragmentEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantCreeperEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantEndermanEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantSkeletonEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantSnowGolemEntity;
import chumbanotz.mutantbeasts.entity.mutant.MutantZombieEntity;
import chumbanotz.mutantbeasts.entity.mutant.SpiderPigEntity;
import chumbanotz.mutantbeasts.entity.projectile.ChemicalXEntity;
import chumbanotz.mutantbeasts.entity.projectile.MutantArrowEntity;
import chumbanotz.mutantbeasts.entity.projectile.ThrowableBlockEntity;
import chumbanotz.mutantbeasts.particles.MBParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = MutantBeasts.MOD_ID, value = Dist.CLIENT)
public enum ClientEventHandler {
	INSTANCE;

	public void openCreeperMinionTrackerScreen(CreeperMinionEntity creeperMinion) {
		Minecraft.getInstance().displayGuiScreen(new CreeperMinionTrackerScreen(creeperMinion));
	}

	public static void registerEntityRenderers(Minecraft client) {
		RenderingRegistry.registerEntityRenderingHandler(BodyPartEntity.class, BodyPartRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ChemicalXEntity.class, render -> new SpriteRenderer<>(render, client.getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(CreeperMinionEntity.class, CreeperMinionRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(CreeperMinionEggEntity.class, CreeperMinionEggRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EndersoulFragmentEntity.class, EndersoulFragmentRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantArrowEntity.class, MutantArrowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantCreeperEntity.class, MutantCreeperRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantEndermanEntity.class, MutantEndermanRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantSkeletonEntity.class, MutantSkeletonRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantSnowGolemEntity.class, MutantSnowGolemRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MutantZombieEntity.class, MutantZombieRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SpiderPigEntity.class, SpiderPigRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ThrowableBlockEntity.class, ThrowableBlockRenderer::new);

		for (PlayerRenderer renderer : client.getRenderManager().getSkinMap().values()) {
			renderer.addLayer(new CreeperMinionShoulderLayer<>(renderer));
		}
	}

	@EventBusSubscriber(modid = MutantBeasts.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	enum Mod {
		;
		@SubscribeEvent
		public static void onParticleFactoryRegistry(ParticleFactoryRegisterEvent event) {
			Minecraft.getInstance().particles.registerFactory(MBParticleTypes.LARGE_PORTAL, LargePortalParticle.Factory::new);
			Minecraft.getInstance().particles.registerFactory(MBParticleTypes.SKULL_SPIRIT, SkullSpiritParticle.Factory::new);
		}
	}
}