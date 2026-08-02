package jak.groupsorter.group;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record GroupDefinition(String displayName, List<Identifier> items) {
    public static final Codec<GroupDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("display_name").forGetter(GroupDefinition::displayName),
        Identifier.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(GroupDefinition::items)
    ).apply(instance, GroupDefinition::new));
}
