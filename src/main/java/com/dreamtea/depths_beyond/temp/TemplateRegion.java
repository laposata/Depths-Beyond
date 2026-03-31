package com.dreamtea.depths_beyond.temp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.List;

public class TemplateRegion {
    public String getMarker() {
        return "";
    }
    public String getName() {
        return "";
    }
    public String getGroup() {
        return "";
    }
    public Tag getData() {
        return new CompoundTag();
    }

    public BlockBounds getBounds() {
        return new BlockBounds(new BlockPos(1,1,1), new BlockPos(3,3,3));
    }

    public record BlockBounds(BlockPos a, BlockPos b) implements Iterable<BlockPos> {

        public Vec3 center(){
            return a.getCenter();
        }
        public Vec3 centerTop(){
            return b.getCenter();
        }
        @Override
        public Iterator<BlockPos> iterator() {
            return List.of(a).iterator();
        }

        public BlockPos sampleBlock(RandomSource random) {
            return a;
        }
        public Box asBox(){
            return new Box(a, b);
        }
    }

    public record Box(BlockPos a, BlockPos b){
        public boolean contains(Vec3 pos){
            return true;
        }
    }
}
