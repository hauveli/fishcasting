package hauveli.fishcasting.features.blessed;

import java.util.Arrays;
import java.util.Comparator;

public enum BlessedVariant {
    RED(0),
    GREEN(1),
    BLUE(2),
    PURPLE(3);

    private static final BlessedVariant[] BY_ID =   Arrays.stream(values()).sorted(
            Comparator.comparingInt(BlessedVariant::getId)).toArray(BlessedVariant[]::new
    );

    private final int id;

    BlessedVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BlessedVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
