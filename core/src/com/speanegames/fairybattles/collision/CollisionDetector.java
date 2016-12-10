package com.speanegames.fairybattles.collision;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.speanegames.fairybattles.entities.moving.GameEntity;

public class CollisionDetector {

    public boolean collidesWithLayer(TiledMapTileLayer layer, Rectangle collisionModel) {
        float x = collisionModel.getX();
        float y = collisionModel.getY();
        float width = collisionModel.getWidth();
        float height = collisionModel.getHeight();
        int tileWidth = (int) layer.getTileWidth();
        int tileHeight = (int) layer.getTileHeight();

        int left = MathUtils.floor(x / tileWidth);
        int right = MathUtils.floor((x + width) / tileWidth);
        int bottom = MathUtils.floor(y / tileHeight);
        int top = MathUtils.floor((y + height) / tileHeight);

        for (int i = left; i <= right; i++) {
            for (int j = bottom; j <= top; j++) {
                if (layer.getCell(i, j) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCollision(GameEntity first, GameEntity second) {
        float left1 = first.getCollisionModel().getX();
        float right1 = left1 + first.getCollisionModel().getWidth();
        float bottom1 = first.getCollisionModel().getY();
        float top1 = bottom1 + first.getCollisionModel().getHeight();

        float left2 = second.getCollisionModel().getX();
        float right2 = left2 + second.getCollisionModel().getWidth();
        float bottom2 = second.getCollisionModel().getY();
        float top2 = bottom2 + second.getCollisionModel().getHeight();

        return (left1 < right2 &&
                left2 < right1 &&
                bottom1 < top2 &&
                bottom2 < top1);
    }
}
