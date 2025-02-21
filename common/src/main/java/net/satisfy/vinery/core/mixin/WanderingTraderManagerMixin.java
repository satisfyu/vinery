package net.satisfy.vinery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.ServerLevelData;
import net.satisfy.vinery.core.entity.TraderMuleEntity;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderManagerMixin implements CustomSpawner {
	@Shadow @Nullable protected abstract BlockPos findSpawnPositionNear(LevelReader world, BlockPos pos, int range);

	@Shadow protected abstract boolean hasEnoughSpace(BlockGetter world, BlockPos pos);

	@Shadow @Final private ServerLevelData serverLevelData;

	@Inject(method = "spawn", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"), cancellable = true)
	private void trySpawn(ServerLevel world, CallbackInfoReturnable<Boolean> cir) {
		if (world.random.nextDouble() < PlatformHelper.getTraderSpawnChance()) {
			ServerPlayer playerEntity = world.getRandomPlayer();
			if (playerEntity != null) {
				BlockPos blockPos = playerEntity.blockPosition();
				PoiManager pointOfInterestStorage = world.getPoiManager();
				Optional<BlockPos> optional = pointOfInterestStorage.find(
						type -> type.is(PoiTypes.MEETING),
						pos -> true,
						blockPos,
						48,
						PoiManager.Occupancy.ANY
				);
				BlockPos blockPos2 = optional.orElse(blockPos);
				BlockPos blockPos3 = this.findSpawnPositionNear(world, blockPos2, 48);
				if (blockPos3 != null && this.hasEnoughSpace(world, blockPos3)) {
					var biome = world.getBiome(blockPos3);
					if (biome != null && !biome.is(Biomes.THE_VOID)) {
						var wanderingWinemakerType = EntityTypeRegistry.WANDERING_WINEMAKER.get();
						if (wanderingWinemakerType != null) {
							WanderingTrader wanderingTraderEntity = wanderingWinemakerType.spawn(world, blockPos3, MobSpawnType.EVENT);
							if (wanderingTraderEntity != null) {
								if (PlatformHelper.shouldSpawnWithMules()) {
									for (int j = 0; j < 2; ++j) {
										BlockPos blockPos4 = this.findSpawnPositionNear(world, wanderingTraderEntity.blockPosition(), 4);
										if (blockPos4 != null) {
											var muleType = EntityTypeRegistry.MULE.get();
											if (muleType != null) {
												TraderMuleEntity traderMuleEntity = muleType.spawn(world, blockPos4, MobSpawnType.EVENT);
												if (traderMuleEntity != null) {
													traderMuleEntity.setLeashedTo(wanderingTraderEntity, true);
												}
											}
										}
									}
								}
								if (this.serverLevelData != null) {
									this.serverLevelData.setWanderingTraderId(wanderingTraderEntity.getUUID());
									wanderingTraderEntity.setDespawnDelay(PlatformHelper.getTraderSpawnDelay());
									wanderingTraderEntity.setWanderTarget(blockPos2);
									wanderingTraderEntity.restrictTo(blockPos2, 16);
									cir.setReturnValue(true);
								}
							}
						}
					}
				}
			}
		}
	}
}
