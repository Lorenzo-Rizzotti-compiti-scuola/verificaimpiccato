package codes.dreaming.shared.client.packets;

import java.io.Serial;

public class ClientLetterPacket extends ClientPacket {
    @Serial
    private static final long serialVersionUID = 1L;
    private final char letter;

    public ClientLetterPacket(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }
}
