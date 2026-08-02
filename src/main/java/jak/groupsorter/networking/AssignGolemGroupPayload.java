package jak.groupsorter.networking;

import io.netty.buffer.ByteBuf;
import jak.groupsorter.JAKGroupSorter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record AssignGolemGroupPayload(BlockPos controllerPos, UUID golemId, Identifier group, boolean assign) implements CustomPacketPayload {
    public static final Type<AssignGolemGroupPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(JAKGroupSorter.MOD_ID, "assign_golem_group"));

    public static final StreamCodec<ByteBuf, AssignGolemGroupPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AssignGolemGroupPayload::controllerPos,
        UUIDUtil.STREAM_CODEC, AssignGolemGroupPayload::golemId,
        Identifier.STREAM_CODEC, AssignGolemGroupPayload::group,
        ByteBufCodecs.BOOL, AssignGolemGroupPayload::assign,
        AssignGolemGroupPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
