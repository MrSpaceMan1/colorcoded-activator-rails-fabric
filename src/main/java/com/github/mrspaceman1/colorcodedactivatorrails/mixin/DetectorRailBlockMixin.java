package com.github.mrspaceman1.colorcodedactivatorrails.mixin;


import com.github.mrspaceman1.colorcodedactivatorrails.ColorcodedDetectorRail;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;


@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {

    @Inject(at=@At("TAIL"), method = "getInteractingMinecartOfType", cancellable = true)
    public <T extends AbstractMinecart> void  onGetInteractingMinecraftOfType(Level level, BlockPos pos, Class<T> cartType, Predicate<Entity> filter, CallbackInfoReturnable<List<T>> cir){
        List<T> foundMinecarts = cir.getReturnValue();
        var twoBlockUnderPos = pos.below(2);
        var underRailBlock = level.getBlockState(twoBlockUnderPos);
        if(!(underRailBlock.getBlock() instanceof BarrelBlock)) return;
        var barrelEntity = (BarrelBlockEntity) level.getBlockEntity(twoBlockUnderPos);
        var barrelItemStacks = new HashSet<>(((BarrelBlockEntityAccessor) barrelEntity).callGetItems());
        

        var filtered = foundMinecarts.stream().filter(x -> x
                .getPassengers()
                .stream()
                .filter(p -> p instanceof ServerPlayer)
                .anyMatch(p -> {
                    var items = new HashSet<>(((ServerPlayer) p).getInventory().items);
                    items.remove(ItemStack.EMPTY);
                    return ColorcodedDetectorRail.checkInventoryContainsItemStacks(barrelItemStacks, items);
                })
        ).toList();
        cir.setReturnValue(filtered);
    }
}
