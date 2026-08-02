package jak.groupsorter.attachments;

import jak.groupsorter.JAKGroupSorter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JAKGroupSorter.MOD_ID);

    public static final Supplier<AttachmentType<LinkedChestOutputData>> OUTPUT_CHEST_LINK =
        ATTACHMENT_TYPES.register("output_chest_link", () ->
            AttachmentType.builder(() -> (LinkedChestOutputData) null)
                .serialize(LinkedChestOutputData.CODEC)
                .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
