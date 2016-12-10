package com.speanegames.fairybattles.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public class RendererImpl implements Renderer {

    private Batch batch;
    private Array<Drawable> drawables;

    public RendererImpl(Batch batch) {
        this.batch = batch;
        drawables = new Array<Drawable>();
    }

    public void renderAll() {
        batch.begin();

        for (Drawable drawable : drawables) {
            draw(drawable);
        }

        batch.end();
    }

    @Override
    public void subscribe(Drawable drawable) {
        drawables.add(drawable);
    }

    public void draw(Drawable drawable) {
        batch.draw(
                drawable.getTexture(),
                drawable.getX(),
                drawable.getY(),
                drawable.getWidth() / 2,
                drawable.getHeight() / 2,
                drawable.getWidth(),
                drawable.getHeight(),
                1,
                1,
                drawable.getRotation() + 180
        );
    }
}
