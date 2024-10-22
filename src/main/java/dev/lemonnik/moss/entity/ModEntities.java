package dev.lemonnik.moss.entity;

import dev.lemonnik.moss.Moomoss;
import dev.lemonnik.moss.entity.custom.MoomossEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<MoomossEntity> MOOMOSS = Registry.register(
            //? if <1.21 {
            /*Registries.ENTITY_TYPE, new Identifier(Moomoss.MOD_ID, "moomoss"),*/
            //?} else
            Registries.ENTITY_TYPE, Identifier.of(Moomoss.MOD_ID, "moomoss"),

            FabricEntityType.Builder.createMob(MoomossEntity::new, SpawnGroup.CREATURE, mob -> mob)
                    .dimensions(0.75f, 0.75f).build()
    );
}
