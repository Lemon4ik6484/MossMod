package dev.lemonnik.moss.entity.client;

import dev.lemonnik.moss.Moomoss;
import dev.lemonnik.moss.entity.custom.MoomossEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.HashMap;
import java.util.Map;

public class MoomossModel extends GeoModel<MoomossEntity> {
    static Map<String, Float> bodyPos = new HashMap<>();
    static float bodyRotY = 0;

    @Override
    public Identifier getModelResource(MoomossEntity animatable) {
        return new Identifier(Moomoss.MOD_ID, "geo/moomoss.geo.json");
    }

    @Override
    public Identifier getTextureResource(MoomossEntity animatable) {
        return new Identifier(Moomoss.MOD_ID, "textures/entity/moomoss.png");
    }

    @Override
    public Identifier getAnimationResource(MoomossEntity animatable) {
        return new Identifier(Moomoss.MOD_ID, "animations/moomoss.animation.json");
    }

    @Override
    public void setCustomAnimations(MoomossEntity animatable, long instanceId, AnimationState<MoomossEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        bodyPos.put("X", getAnimationProcessor().getBone("Body").getPosX());
        bodyPos.put("Y", getAnimationProcessor().getBone("Body").getPivotY());
        bodyPos.put("Z", getAnimationProcessor().getBone("Body").getPosZ());

        bodyRotY = -animatable.bodyYaw;

        CoreGeoBone head = getAnimationProcessor().getBone("Head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }

        CoreGeoBone moss = getAnimationProcessor().getBone("moss");
        if (moss != null) {
            moss.setHidden(animatable.isSheared());
        }
    }

    public static Map<String, Float> getBodyPos() {
        return bodyPos;
    }

    public static float getBodyRot() {
        return bodyRotY;
    }
}