package com.speanegames.fairybattles.networking;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.NetworkConfig;
import com.speanegames.fairybattles.networking.transfers.lobby.connect.ConnectToLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.connect.ConnectToLobbyResponse;
import com.speanegames.fairybattles.networking.transfers.lobby.create.CreateLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.create.CreateLobbyResponse;
import com.speanegames.fairybattles.networking.transfers.signin.SignInRequest;
import com.speanegames.fairybattles.networking.transfers.signin.SignInResponse;
import com.speanegames.fairybattles.networking.transfers.signup.SignUpRequest;
import com.speanegames.fairybattles.networking.transfers.signup.SignUpResponse;

import java.io.IOException;

public class NetworkManager {

    private Client client;

    private FairyBattlesGame game;

    public NetworkManager(FairyBattlesGame game) {
        this.game = game;
    }

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

    public void signInRequest(String login, String password) {
        if (!game.isWaitingResponse()) {
            SignInRequest request = new SignInRequest();
            request.login = login;
            request.password = password;
            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void connectToLobbyRequest(String lobbyId) {
        if (!game.isWaitingResponse()) {
            ConnectToLobbyRequest request = new ConnectToLobbyRequest();

            request.lobbyId = lobbyId;
            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void createLobbyRequest() {
        if (!game.isWaitingResponse()) {
            CreateLobbyRequest request = new CreateLobbyRequest();

            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    private void registerClasses() {
        Kryo kryo = client.getKryo();

        kryo.register(SignInRequest.class);
        kryo.register(SignInResponse.class);
        kryo.register(SignUpRequest.class);
        kryo.register(SignUpResponse.class);
        kryo.register(CreateLobbyRequest.class);
        kryo.register(CreateLobbyResponse.class);
        kryo.register(ConnectToLobbyRequest.class);
        kryo.register(ConnectToLobbyResponse.class);
    }

    private void initListener() {
        Listener listener = new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);

                if (object instanceof SignInResponse) {
                    handleSignInResponse((SignInResponse) object);
                } else if (object instanceof ConnectToLobbyResponse) {
                    handleConnectToLobbyResponse((ConnectToLobbyResponse) object);
                } else if (object instanceof CreateLobbyResponse) {
                    handleCreateLobbyResponse((CreateLobbyResponse) object);
                }
            }
        };

        client.addListener(new Listener.QueuedListener(listener) {
            @Override
            protected void queue(Runnable runnable) {
                Gdx.app.postRunnable(runnable);
            }
        });
    }

    private void handleSignInResponse(SignInResponse response) {
        if (response.success) {
            game.showMainMenuScreen();
        } else {
            game.setSignInMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleConnectToLobbyResponse(ConnectToLobbyResponse response) {
        if (response.success) {
            game.showConnectedLobbyScreen(response.lobbyId);
        } else {
            game.setConnectToLobbyMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleCreateLobbyResponse(CreateLobbyResponse response) {
        if (response.success) {
            game.showLobbyOwnerScreen(response.lobbyId);
        } else {
            game.setCreateLobbyMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }
}
