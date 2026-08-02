package jak.groupsorter.group;

import net.minecraft.resources.Identifier;

import java.util.List;

public record Group(Identifier id, String displayName, List<Identifier> items) { }
