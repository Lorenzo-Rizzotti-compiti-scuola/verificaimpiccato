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
    private static final boolean GAME_NOT_STARTED = false;

    private final int port;
    private final int playerCount;

    private final String wordToGuess;

    private boolean gameStarted;

    private final Set<ConnectionHandler> connections = ConcurrentHashMap.newKeySet();


    public Server(int port, int playerCount, String wordToGuess) {
        this.port = port;
        this.playerCount = playerCount;
        this.wordToGuess = wordToGuess;
        this.gameStarted = GAME_NOT_STARTED;
    }

    public void startGame() {
        gameStarted = true;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public ArrayList<Integer> checkLetter(char letter) {
        ArrayList<Integer> indices = findLetterInWord(letter);
        return indices;
    }

    public boolean checkWin(ConnectionHandler handler, String guess) {
        boolean isWin = guess.equals(wordToGuess);
        if(isWin){
            broadcastWinningInfo(handler);
        }
        return isWin;
    }

    public void run() {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                Socket client = socket.accept();
                handleNewConnection(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeConnection(ConnectionHandler handler) {
        connections.remove(handler);
    }

    public void broadcast(ConnectionHandler exclude, Object packet) {
        connections.stream().
                filter(connectionHandler -> connectionHandler != exclude).
                forEach(connectionHandler -> connectionHandler.sendPacket(packet));
    }

    private ArrayList<Integer> findLetterInWord(char letter) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == letter) {
                index.add(i);
            }
        }
        return index;
    }

    private void broadcastWinningInfo(ConnectionHandler handler) {
        this.broadcast(handler, new ServerWinPacket(false));
    }

    private void handleNewConnection(Socket client) throws IOException {
        ConnectionHandler handler = new ConnectionHandler(client, this);
        connections.add(handler);
        new Thread(handler).start();
        if (connections.size() == playerCount) {
            startGame();
            this.broadcast(null, new ServerStartPacket(wordToGuess.length()));
        }
    }
}
