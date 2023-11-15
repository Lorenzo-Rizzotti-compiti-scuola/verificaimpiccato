package codes.dreaming.shared.server.packets;

import java.io.Serial;
import java.util.ArrayList;

public class ServerLetterPacket extends ServerPacket {
    @Serial
    private static final long serialVersionUID = 1L;

    public char letter;
    public ArrayList<Integer> index;

    public ServerLetterPacket(char letter, ArrayList<Integer> index) {
        this.letter = letter;
        this.index = index;
    }

    public char getLetter() {
        return letter;
    }

    public ArrayList<Integer> getIndex() {
        return index;
    }
}
