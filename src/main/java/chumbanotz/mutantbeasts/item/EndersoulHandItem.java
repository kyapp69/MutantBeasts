package chumbanotz.mutantbeasts.item;

import com.google.common.collect.Multimap;

import chumbanotz.mutantbeasts.client.MBItemStackTileEntityRenderer;
import chumbanotz.mutantbeasts.entity.mutant.MutantEndermanEntity;
import chumbanotz.mutantbeasts.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndersoulHandItem extends Item {
	public EndersoulHandItem(Properties properties) {
		super(properties.maxDamage(240).setTEISR(() -> MBItemStackTileEntityRenderer::new));
	}

	@Override
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return !player.isCreative();
	}

	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damageItem(1, attacker, e -> e.sendBreakAnimation(EquipmentSlotType.MAINHAND));
		return true;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.isPlacerSneaking()) {
			return ActionResultType.FAIL;
		} else {
			if (!context.getWorld().isRemote) {
				if (!context.getWorld().canMineBlockBody(context.getPlayer(), context.getPos())) {
					return ActionResultType.FAIL;
				}

				if (!context.getPlayer().canPlayerEdit(context.getPos(), context.getFace(), context.getItem())) {
					return ActionResultType.FAIL;
				}

				if (context.getWorld().getBlockState(context.getPos()).hasTileEntity()) {
					return ActionResultType.FAIL;
				}

//				context.getWorld().setBlockState(context.getPos(), Blocks.AIR.getDefaultState());
//				context.getWorld().addEntity(new ThrowableBlockEntity(context.getWorld(), context.getPlayer(), context.getWorld().getBlockState(context.getPos()), Block.getStateId(context.getWorld().getBlockState(context.getPos()))));
			}

			return ActionResultType.SUCCESS;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!playerIn.isSneaking()) {
			return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
		} else {
			RayTraceResult result = getMOPFromPlayer(worldIn, playerIn, 128.0F);
			if (result.getType() == RayTraceResult.Type.MISS) {
				return new ActionResult<ItemStack>(ActionResultType.FAIL, stack);
			} else {
				if (result.getType() == RayTraceResult.Type.BLOCK) {
					BlockPos pos = ((BlockRayTraceResult)result).getPos();
					Direction direction = ((BlockRayTraceResult)result).getFace();
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					x += direction.getXOffset();
					y += direction.getYOffset();
					z += direction.getZOffset();
					BlockPos checkPos = new BlockPos(x, y - 1, z);
					if (!worldIn.isAirBlock(checkPos) || !worldIn.getBlockState(checkPos).getMaterial().isSolid()) {
						Block block1 = worldIn.getBlockState(pos.up()).getBlock();
						Block block2 = worldIn.getBlockState(pos.up(2)).getBlock();
						Block block3 = worldIn.getBlockState(pos.up(3)).getBlock();
						if (block1 == Blocks.AIR) {
							x = pos.getX();
							y = pos.getY() + 1;
							z = pos.getZ();
						} else if (block2 == Blocks.AIR) {
							x = pos.getX();
							y = pos.getY() + 2;
							z = pos.getZ();
						} else if (block3 == Blocks.AIR) {
							x = pos.getX();
							y = pos.getY() + 3;
							z = pos.getZ();
						}
					}

					worldIn.playSound(null, playerIn.posX, playerIn.posY + (double)playerIn.getHeight() / 2.0D, playerIn.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, playerIn.getSoundCategory(), 1.0F, 1.0F);
					playerIn.setPositionAndUpdate((double)x + 0.5D, (double)y, (double)z + 0.5D);
					playerIn.fallDistance = 0.0F;
					EntityUtil.spawnEnderParticlesOnServer(playerIn, 64, 0.8F);

					if (!worldIn.isRemote) {
						MutantEndermanEntity.teleportAttack(playerIn);
					}

					worldIn.playSound(null, playerIn.posX, playerIn.posY + (double)playerIn.getHeight() / 2.0D, playerIn.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, playerIn.getSoundCategory(), 1.0F, 1.0F);
					playerIn.getCooldownTracker().setCooldown(this, 40);
					playerIn.swingArm(handIn);
					playerIn.addStat(Stats.ITEM_USED.get(this));
					stack.damageItem(4, playerIn, e -> e.sendBreakAnimation(handIn));
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
				}
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EquipmentSlotType.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 5.0D, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	public static RayTraceResult getMOPFromPlayer(World world, PlayerEntity player, float maxDist) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62D - player.getYOffset();
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = (double)maxDist;
		Vec3d vec31 = vec3.add(f7 * d3, f6 * d3, f8 * d3);
		return world.rayTraceBlocks(new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
	}
}