package com.speanegames.fairybattles.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
        clearScreen();

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

    private void draw(Drawable drawable) {
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
                drawable.getRotation()
        );
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
    }
}
