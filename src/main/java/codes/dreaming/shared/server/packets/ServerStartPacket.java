package codes.dreaming.shared.server.packets;

import java.io.Serial;

public class ServerStartPacket extends ServerPacket {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Integer wordLength;

    public ServerStartPacket(Integer wordLength) {
        this.wordLength = wordLength;
    }

    public Integer getWordLength() {
        return wordLength;
    }
}
