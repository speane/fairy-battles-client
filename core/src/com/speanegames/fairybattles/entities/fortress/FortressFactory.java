package com.speanegames.fairybattles.entities.fortress;

import com.speanegames.fairybattles.rendering.TextureManager;

import java.util.HashMap;

public class FortressFactory {

    private TextureManager textureManager;

    private HashMap<String, FortressInfo> fortressInfoMap;

    public FortressFactory(TextureManager textureManager) {
        this.textureManager = textureManager;

        initFortressInfoMap();
    }

    public Fortress createFortress(String name, int x, int y, int rotation) {
        FortressInfo fortressInfo = fortressInfoMap.get(name);

        Fortress fortress = new Fortress();

        fortress.setPosition(x - fortressInfo.getWidth() / 2, y - fortressInfo.getHeight() / 2);
        fortress.setRotation(rotation);

        fortress.setSize(fortressInfo.getWidth(), fortressInfo.getHeight());
        fortress.setTexture(
                textureManager.getTexture(fortressInfo.getTextureName()));

        return fortress;
    }

    private void initFortressInfoMap() {
        fortressInfoMap = new HashMap<String, FortressInfo>();

        FortressInfo sunFortressInfo = new FortressInfo();

        sunFortressInfo.setTextureName("sun_fortress");
        sunFortressInfo.setWidth(320);
        sunFortressInfo.setHeight(160);

        fortressInfoMap.put("SUN", sunFortressInfo);

        FortressInfo moonFortressInfo = new FortressInfo();

        moonFortressInfo.setTextureName("moon_fortress");
        moonFortressInfo.setWidth(320);
        moonFortressInfo.setHeight(160);

        fortressInfoMap.put("MOON", moonFortressInfo);
    }
}
