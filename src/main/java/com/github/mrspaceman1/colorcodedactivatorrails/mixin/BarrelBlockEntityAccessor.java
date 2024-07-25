package com.github.mrspaceman1.colorcodedactivatorrails.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BarrelBlockEntity.class)
public interface BarrelBlockEntityAccessor {
    @Invoker
    NonNullList<ItemStack> callGetItems();
}
