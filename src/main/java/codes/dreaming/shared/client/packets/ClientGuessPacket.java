package codes.dreaming.shared.client.packets;

import java.io.Serial;

public class ClientGuessPacket extends ClientPacket {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String guess;

    public ClientGuessPacket(String guess) {
        this.guess = guess;
    }

    public String getGuess() {
        return guess;
    }
}
