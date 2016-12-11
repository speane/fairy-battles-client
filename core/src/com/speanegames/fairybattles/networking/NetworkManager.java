package com.speanegames.fairybattles.networking;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.NetworkConfig;
import com.speanegames.fairybattles.networking.transfers.lobby.JoinTeamRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.JoinTeamResponse;
import com.speanegames.fairybattles.networking.transfers.lobby.connect.ConnectToLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.connect.ConnectToLobbyResponse;
import com.speanegames.fairybattles.networking.transfers.lobby.create.CreateLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.create.CreateLobbyResponse;
import com.speanegames.fairybattles.networking.transfers.lobby.dissolve.DissolveLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.leave.LeaveLobbyRequest;
import com.speanegames.fairybattles.networking.transfers.lobby.dissolve.LobbyDissolved;
import com.speanegames.fairybattles.networking.transfers.lobby.leave.LeaveLobbyResponse;
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
            Gdx.app.log("SIGN IN REQUEST", "login: " + login + " password: " + password);

            SignInRequest request = new SignInRequest();
            request.login = login;
            request.password = password;
            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void connectToLobbyRequest(String lobbyId) {
        if (!game.isWaitingResponse()) {
            Gdx.app.log("CONNECT TO LOBBY REQUEST", "lobbyID: " + lobbyId);

            ConnectToLobbyRequest request = new ConnectToLobbyRequest();

            request.lobbyId = lobbyId;
            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void createLobbyRequest() {
        if (!game.isWaitingResponse()) {
            Gdx.app.log("CREATE LOBBY REQUEST", "");

            CreateLobbyRequest request = new CreateLobbyRequest();

            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void joinTeamRequest(String lobbyId, String team) {
        if (!game.isWaitingResponse()) {
            Gdx.app.log("JOIN TEAM REQUEST", "lobbyID: " + lobbyId + " team: " + team);

            JoinTeamRequest request = new JoinTeamRequest();

            request.lobbyId = lobbyId;
            request.team = team;

            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void dissolveLobbyRequest() {
        if (!game.isWaitingResponse()) {
            Gdx.app.log("DISSOLVE LOBBY REQUEST", "");

            DissolveLobbyRequest request = new DissolveLobbyRequest();

            game.setWaitingResponse(true);
            client.sendTCP(request);
        }
    }

    public void leaveLobbyRequest() {
        if (!game.isWaitingResponse()) {
            Gdx.app.log("LEAVE LOBBY REQUEST", "");

            LeaveLobbyRequest request = new LeaveLobbyRequest();

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
        kryo.register(JoinTeamRequest.class);
        kryo.register(JoinTeamResponse.class);
        kryo.register(DissolveLobbyRequest.class);
        kryo.register(LobbyDissolved.class);
        kryo.register(LeaveLobbyRequest.class);
        kryo.register(LeaveLobbyResponse.class);
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
                } else if (object instanceof JoinTeamResponse) {
                    handleJoinTeamResponse((JoinTeamResponse) object);
                } else if (object instanceof LobbyDissolved) {
                    handleLobbyDissolved((LobbyDissolved) object);
                } else if (object instanceof LeaveLobbyResponse) {
                    handleLeaveLobbyResponse((LeaveLobbyResponse) object);
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
            Gdx.app.log("SIGN IN RESPONSE", "SUCCESS");

            game.showMainMenuScreen();
        } else {
            Gdx.app.log("SIGN IN RESPONSE", "ERROR message: " + response.errorMessage);

            game.setSignInMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleConnectToLobbyResponse(ConnectToLobbyResponse response) {
        if (response.success) {
            Gdx.app.log("SIGN IN RESPONSE", "SUCCESS " + "lobbyID: " + response.lobbyId);

            game.showConnectedLobbyScreen(response.lobbyId);
        } else {
            Gdx.app.log("SIGN IN RESPONSE", "ERROR " + "message: " + response.errorMessage);

            game.setConnectToLobbyMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleCreateLobbyResponse(CreateLobbyResponse response) {
        if (response.success) {
            Gdx.app.log("CREATE LOBBY RESPONSE", "SUCCESS" + " lobbyID: " + response.lobbyId);

            game.showLobbyOwnerScreen(response.lobbyId);
        } else {
            Gdx.app.log("CREATE LOBBY RESPONSE", "ERROR" + " message: " + response.errorMessage);

            game.setCreateLobbyMessage(response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleJoinTeamResponse(JoinTeamResponse response) {
        if (response.success) {
            Gdx.app.log("JOIN TEAM RESPONSE", "SUCCESS " + "team: " + response.team + " placeID: " + response.placeId);

            game.joinTeam(response.team, response.placeId);
        } else {
            Gdx.app.log("JOIN TEAM RESPONSE", "ERROR " + "message: " + response.errorMessage);
        }

        game.setWaitingResponse(false);
    }

    private void handleLobbyDissolved(LobbyDissolved lobbyDissolved) {
        Gdx.app.log("LOBBY DISSOLVED", "");
        game.setWaitingResponse(false);
        game.lobbyDissolved();
    }

    private void handleLeaveLobbyResponse(LeaveLobbyResponse response) {
        Gdx.app.log("LEAVE LOBBY RESPONSE", "");
        game.setWaitingResponse(false);
        game.leaveLobby();
    }
}
