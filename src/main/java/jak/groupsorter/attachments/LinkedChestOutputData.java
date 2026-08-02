package jak.groupsorter.attachments;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public record LinkedChestOutputData(BlockPos controllerPos, Identifier group) {
    public static final MapCodec<LinkedChestOutputData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockPos.CODEC.fieldOf("controller_pos").forGetter(LinkedChestOutputData::controllerPos),
        Identifier.CODEC.fieldOf("group").forGetter(LinkedChestOutputData::group)
    ).apply(instance, LinkedChestOutputData::new));
}
