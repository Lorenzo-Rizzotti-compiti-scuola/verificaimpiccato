package codes.dreaming.shared.server;

import codes.dreaming.shared.server.packets.ServerStartPacket;
import codes.dreaming.shared.server.packets.ServerWinPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {
    final int port;
    final int playerCount;

    final String worldToGuess;

    private Boolean gameStarted = false;

    Set<ConnectionHandler> connections = ConcurrentHashMap.newKeySet();


    public Server(int port, int playerCount, String worldToGuess) {
        this.port = port;
        this.playerCount = playerCount;
        this.worldToGuess = worldToGuess;
    }

    public void startGame() {
        gameStarted = true;
    }

    public Boolean isGameStarted() {
        return gameStarted;
    }

    public ArrayList<Integer> checkLetter(char letter) {
        System.out.println("Checking letter " + letter);
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < worldToGuess.length(); i++) {
            if (worldToGuess.charAt(i) == letter) {
                index.add(i);
            }
        }
        System.out.println("Letter " + letter + " found at " + index);
        return index;
    }

    public Boolean checkWin(ConnectionHandler handler, String guess) {
        System.out.println("Checking win for " + guess);
        //Broadcast to all except the sender
        this.broadcast(handler, new ServerWinPacket(guess.equals(worldToGuess)));
        return guess.equals(worldToGuess);
    }

    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);

            // On new connection accept it and add it to the list of connections
            while (true) {
                Socket client = socket.accept();
                ConnectionHandler handler = new ConnectionHandler(client, this);
                connections.add(handler);
                new Thread(handler).start();
                if (connections.size() == playerCount) {
                    startGame();
                    this.broadcast(null, new ServerStartPacket(this.worldToGuess.length()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(ConnectionHandler exclude, Object packet) {
        System.out.println("Broadcasting packet " + packet.getClass().getSimpleName());
        connections.stream().filter(connectionHandler -> connectionHandler != exclude).forEach(connectionHandler -> {
            try {
                connectionHandler.output.writeObject(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
