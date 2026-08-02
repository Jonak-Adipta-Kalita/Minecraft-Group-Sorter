package jak.groupsorter.networking;

import io.netty.buffer.ByteBuf;
import jak.groupsorter.JAKGroupSorter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record AssignChestGroupPayload(BlockPos controllerPos, BlockPos chestPos, Identifier group) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AssignChestGroupPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "assign_chest_group"));

    public static StreamCodec<ByteBuf, AssignChestGroupPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AssignChestGroupPayload::controllerPos,
        BlockPos.STREAM_CODEC, AssignChestGroupPayload::chestPos,
        Identifier.STREAM_CODEC, AssignChestGroupPayload::group,
        AssignChestGroupPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
