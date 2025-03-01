package net.satisfy.vinery.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.satisfy.vinery.core.registry.MobEffectRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EventHandler {
    private static final Random random = new Random();

    public static EventResult onAttack(Player player, Level level, Entity target, InteractionHand hand, @Nullable EntityHitResult result) {
        if (player.hasEffect(MobEffectRegistry.PARTY_EFFECT.get())) {
            if (target instanceof LivingEntity entity) {
                int color = random.nextInt(0xFFFFFF);

                ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
                CompoundTag fireworkNbt = new CompoundTag();
                ListTag explosions = new ListTag();
                CompoundTag explosion = new CompoundTag();

                explosion.putIntArray("Colors", new int[]{color});
                explosion.putByte("Type", (byte) 0);
                explosions.add(explosion);
                fireworkNbt.put("Explosions", explosions);
                fireworkNbt.putByte("Flight", (byte) 0);
                fireworkStack.getOrCreateTagElement("Fireworks").put("Explosions", explosions);

                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(level, fireworkStack, entity);
                fireworkRocket.setAirSupply(0);
                level.addFreshEntity(fireworkRocket);

                if (!(target instanceof Player || target instanceof Mob)) {
                    return EventResult.interruptTrue();
                }

                return EventResult.pass();
            }
        }

        return EventResult.pass();
    }


    public static void register() {
        PlayerEvent.ATTACK_ENTITY.register(EventHandler::onAttack);
    }
}
