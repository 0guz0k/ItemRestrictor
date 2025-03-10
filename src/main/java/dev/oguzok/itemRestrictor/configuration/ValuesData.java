package dev.oguzok.itemRestrictor.configuration;

import org.bukkit.Material;

import java.util.List;

public record ValuesData(List<Material> materials,
                         List<String> enchantments,
                         List<String> potionTypes,
                         int customModelData,
                         int maxAmount,
                         String message,
                         String storageMessage) {}