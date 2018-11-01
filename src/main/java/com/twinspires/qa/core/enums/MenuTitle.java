package com.twinspires.qa.core.enums;

public enum MenuTitle {
    HOW_DO_I_WAGER_TS("How Do I Wager on TwinSpires?"),
    PLAYERS_POOL("Players' Pool");

    private String text;

    MenuTitle(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static MenuTitle fromString(String text) {
        if (text != null) {
            for (MenuTitle b : MenuTitle.values()) {
            if (text.equalsIgnoreCase(b.text)) {
                return b;
            }
        }
    }
        return null;
    }
}

