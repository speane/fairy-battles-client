package com.speanegames.fairybattles.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface Drawable {

    TextureRegion getTexture();
    float getX();
    float getY();
    float getWidth();
    float getHeight();
    float getRotation();
}
