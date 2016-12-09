package com.speanegames.fairybattles.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureManagerImpl implements TextureManager {

    private TextureAtlas atlas;

    public TextureManagerImpl(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    @Override
    public TextureRegion getTexture(String textureName) {
        return atlas.findRegion(textureName);
    }
}
