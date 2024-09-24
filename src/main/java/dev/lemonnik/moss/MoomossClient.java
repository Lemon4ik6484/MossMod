package dev.lemonnik.moss;

import dev.lemonnik.moss.entity.ModEntities;
import dev.lemonnik.moss.entity.client.MoomossRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MoomossClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.MOOMOSS, MoomossRenderer::new);
    }
}
