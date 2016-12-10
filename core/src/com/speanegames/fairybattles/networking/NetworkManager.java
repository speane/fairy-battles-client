package com.speanegames.fairybattles.networking;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.speanegames.fairybattles.config.NetworkConfig;
import com.speanegames.fairybattles.networking.transfers.ConnectToRoom;

import java.io.IOException;

public class NetworkManager {

    private Client client;

    public void start() {
        client = new Client();
        registerClasses();
        client.start();
        try {
            client.connect(
                    NetworkConfig.WAIT_TIMEOUT,
                    NetworkConfig.SERVER_HOST,
                    NetworkConfig.PLAY_PORT);
        } catch (IOException e) {
            // TODO log exception
        }
        initListener();
    }

    public void connectToRoom() {
        ConnectToRoom connectToRoom = new ConnectToRoom();
        connectToRoom.name = "speane";
        client.sendTCP(connectToRoom);
    }

    private void registerClasses() {
        Kryo kryo = client.getKryo();

        kryo.register(ConnectToRoom.class);
    }

    private void initListener() {
        Listener listener = new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
            }
        };

        client.addListener(new Listener.QueuedListener(listener) {
            @Override
            protected void queue(Runnable runnable) {
                Gdx.app.postRunnable(runnable);
            }
        });
    }
}
