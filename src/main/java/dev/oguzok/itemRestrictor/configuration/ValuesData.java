package dev.oguzok.itemRestrictor.configuration;

import java.util.List;

public record ValuesData(List<String> materials,
                         List<String> enchantments,
                         List<String> potionTypes,
                         int customModelData,
                         int maxAmount,
                         String message,
                         String storageMessage) {}