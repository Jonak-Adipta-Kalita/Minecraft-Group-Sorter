package jak.groupsorter.attachments;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;


public record LinkedOutputData(BlockPos controllerPos, Identifier group) {
    public static final MapCodec<LinkedOutputData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockPos.CODEC.fieldOf("controller_pos").forGetter(LinkedOutputData::controllerPos),
        Identifier.CODEC.fieldOf("group").forGetter(LinkedOutputData::group)
    ).apply(instance, LinkedOutputData::new));
}
