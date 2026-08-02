package jak.groupsorter.menu;

import net.minecraft.client.gui.components.Button;

public final class GroupGridLayout {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 8;
    private static final int TOP_MARGIN = 30;

    private GroupGridLayout() {}

    public static int computeColumns(int screenWidth) {
        int maxFit = (screenWidth - PADDING) / (BUTTON_WIDTH + PADDING);
        return Math.clamp(maxFit, 2, 3);
    }

    public static void place(Button button, int index, int columns, int screenWidth) {
        int totalWidth = columns * BUTTON_WIDTH + (columns - 1) * PADDING;
        int startX = (screenWidth - totalWidth) / 2;
        int col = index % columns;
        int row = index / columns;
        int x = startX + col * (BUTTON_WIDTH + PADDING);
        int y = TOP_MARGIN + row * (BUTTON_HEIGHT + PADDING);
        button.setX(x);
        button.setY(y);
    }

    public static int buttonWidth() { return BUTTON_WIDTH; }
    public static int buttonHeight() { return BUTTON_HEIGHT; }
}
