package kpp.dev.aloM0d.client.core.module.modules

import kpp.dev.aloM0d.client.core.event.ClientTickEvent
import kpp.dev.aloM0d.client.core.module.Module
import kpp.dev.aloM0d.client.core.module.ModuleCategory
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.Items
import net.minecraft.world.level.ClipContext
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

object AutoMlgModule : Module(
    id = "auto-mlg",
    title = "Auto MLG",
    description = "Places water under you from inventory while falling.",
    category = ModuleCategory.PLAYER
) {
    private var cooldownTicks = 0

    override fun onDisable() {
        cooldownTicks = 0
    }

    override fun onTick(event: ClientTickEvent) {
        if (cooldownTicks > 0) {
            cooldownTicks--
            return
        }

        val client = event.client
        val player = client.player ?: return

        if (player.onGround()) return
        if (player.isInWater || player.isInLava) return
        if (player.isPassenger || player.isFallFlying) return
        if (player.deltaMovement.y >= FALLING_SPEED_THRESHOLD) return

        val reach = player.blockInteractionRange() + REACH_EPSILON
        if (player.fallDistance < reach - FALL_DISTANCE_EPSILON) return

        if (findWaterPlacementHit(client, reach) == null) return

        val hand = selectWaterBucket(client) ?: return

        useWaterBucketDown(client, hand)
        player.swing(hand)
        cooldownTicks = USE_COOLDOWN_TICKS
    }

    private fun findWaterPlacementHit(client: Minecraft, reach: Double): BlockHitResult? {
        val level = client.level ?: return null
        val player = client.player ?: return null
        val from = player.eyePosition
        val to = from.add(0.0, -reach, 0.0)
        val hitResult = level.clip(
            ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
            )
        )

        if (hitResult.type != HitResult.Type.BLOCK) return null
        if (hitResult.direction != Direction.UP) return null
        if (hitResult.location.distanceTo(from) > reach) return null

        val targetPos = hitResult.blockPos
        val placementPos = targetPos.relative(hitResult.direction)
        val targetState = level.getBlockState(targetPos)
        val placementState = level.getBlockState(placementPos)

        if (targetState.isAir) return null
        if (!placementState.canBeReplaced()) return null

        return BlockHitResult(hitResult.location, hitResult.direction, targetPos, hitResult.isInside)
    }

    private fun selectWaterBucket(client: Minecraft): InteractionHand? {
        val player = client.player ?: return null

        if (player.mainHandItem.item == Items.WATER_BUCKET) return InteractionHand.MAIN_HAND
        if (player.offhandItem.item == Items.WATER_BUCKET) return InteractionHand.OFF_HAND
        if (player.containerMenu != player.inventoryMenu) return null

        val inventory = player.inventory
        val waterBucketSlot = (0 until MAIN_INVENTORY_SIZE)
            .firstOrNull { slot -> inventory.getItem(slot).item == Items.WATER_BUCKET }
            ?: return null

        if (Inventory.isHotbarSlot(waterBucketSlot)) {
            inventory.selectedSlot = waterBucketSlot
            return InteractionHand.MAIN_HAND
        }

        val gameMode = client.gameMode ?: return null
        val sourceSlotId = screenSlotId(waterBucketSlot) ?: return null

        gameMode.handleContainerInput(
            player.inventoryMenu.containerId,
            sourceSlotId,
            inventory.selectedSlot,
            ContainerInput.SWAP,
            player
        )

        return InteractionHand.MAIN_HAND
    }

    private fun useWaterBucketDown(client: Minecraft, hand: InteractionHand) {
        val player = client.player ?: return
        val connection = client.connection ?: return

        if (hand == InteractionHand.MAIN_HAND) {
            connection.send(ServerboundSetCarriedItemPacket(player.inventory.selectedSlot))
        }

        connection.send(
            ServerboundUseItemPacket(
                hand,
                USE_ITEM_SEQUENCE,
                player.yRot,
                DOWNWARD_USE_PITCH
            )
        )
    }

    private fun screenSlotId(inventorySlot: Int): Int? {
        return when (inventorySlot) {
            in HOTBAR_SLOT_RANGE -> HOTBAR_SCREEN_SLOT_OFFSET + inventorySlot
            in MAIN_SLOT_RANGE -> inventorySlot
            else -> null
        }
    }

    private val HOTBAR_SLOT_RANGE = 0 until Inventory.getSelectionSize()
    private val MAIN_SLOT_RANGE = Inventory.getSelectionSize() until MAIN_INVENTORY_SIZE
    private const val MAIN_INVENTORY_SIZE = 36
    private const val HOTBAR_SCREEN_SLOT_OFFSET = 36
    private const val FALLING_SPEED_THRESHOLD = -0.08
    private const val FALL_DISTANCE_EPSILON = 0.25
    private const val REACH_EPSILON = 0.25
    private const val USE_COOLDOWN_TICKS = 10
    private const val USE_ITEM_SEQUENCE = 0
    private const val DOWNWARD_USE_PITCH = 90.0f
}
