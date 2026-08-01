package jak.groupsorter.networking;

import io.netty.buffer.ByteBuf;
import jak.groupsorter.JAKGroupSorter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record AssignGroupPayload(BlockPos controllerPos, BlockPos chestPos, Identifier group) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AssignGroupPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "assign_group"));

    public static StreamCodec<ByteBuf, AssignGroupPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AssignGroupPayload::controllerPos,
        BlockPos.STREAM_CODEC, AssignGroupPayload::chestPos,
        Identifier.STREAM_CODEC, AssignGroupPayload::group,
        AssignGroupPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
