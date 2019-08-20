package com.example.lasse.cannongame;

public class WallElement extends GameElement {

    private int durability;

    public WallElement(CannonView view, int color, int x, int y, int width, int length, int durability) {
        super(view, color, CannonView.TARGET_SOUND_ID, x, y, width, length, 0);
        this.durability = durability;
    }

    public int getDurability() {
        return durability;
    }

    public void decreaseDurability() {
        this.durability = this.durability - 1;
    }
}
