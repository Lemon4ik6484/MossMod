package dev.lemonnik.moss.entity.client;

import dev.lemonnik.moss.Moomoss;
import dev.lemonnik.moss.entity.custom.MoomossEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

import static net.minecraft.block.FlowerbedBlock.FLOWER_AMOUNT;

public class MoomossRenderer extends GeoEntityRenderer<MoomossEntity> {
    public MoomossRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MoomossModel());
    }

    @Override
    public Identifier getTexture(MoomossEntity animatable) {
        if (animatable.isPink()) {
            //? if <1.21 {
            /*return new Identifier(Moomoss.MOD_ID, "textures/entity/moomoss_blossoming.png");*/
            //?} else
            return Identifier.of(Moomoss.MOD_ID, "textures/entity/moomoss_blossoming.png");
        }
        //? if <1.21 {
        /*return new Identifier(Moomoss.MOD_ID, "textures/entity/moomoss");*/
        //?} else
        return Identifier.of(Moomoss.MOD_ID, "textures/entity/moomoss.png");
    }

    @Override
    public void render(MoomossEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (entity.isFlowered() && entity.getFlowerState() != null) {
            BlockState flowerState = entity.getFlowerState();
            BlockRenderManager blockRenderer = MinecraftClient.getInstance().getBlockRenderManager();

            poseStack.push();

            poseStack.translate(0.0F, 0.0F, 0.0F);

            float rotationAngle = MoomossModel.getBodyRot();
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));

            Map<String, Float> bodyPos = MoomossModel.getBodyPos();
            poseStack.translate(bodyPos.get("X")-0.23F, bodyPos.get("Y") + 0.655F, bodyPos.get("Z")-0.33F);

            if (flowerState == Blocks.PINK_PETALS.getDefaultState().with(FLOWER_AMOUNT, 4)) {
                if (entity.isSheared()) {
                    poseStack.scale(0.5F, 0.35F, 0.5F);
                } else {
                    poseStack.scale(0.5F, 1.0F, 0.5F);
                }
            } else {
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            blockRenderer.renderBlockAsEntity(flowerState, poseStack, bufferSource, packedLight, OverlayTexture.DEFAULT_UV);

            poseStack.pop();
        }
    }
}
