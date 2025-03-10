package dev.oguzok.itemRestrictor.utilities.serializer;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern hexPatten = Pattern.compile("#[0-9A-Fa-f]{6}");
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();

    public static TextComponent convertHex(String input) {
        Matcher matcher = hexPatten.matcher(input);

        while (matcher.find()) {
            String hexColor = matcher.group();
            if (hexColor.startsWith("&#")) {
                input = input.replace(hexColor, "&" + hexColor);
            }
        }
        return legacyAmpersand.deserialize(input);
    }

    public static String toHex(String input) {
        return LegacyComponentSerializer.legacySection().serialize(convertHex(input));
    }
}

