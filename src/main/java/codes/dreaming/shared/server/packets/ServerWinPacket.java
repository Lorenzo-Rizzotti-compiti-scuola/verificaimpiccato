package codes.dreaming.shared.server.packets;

import java.io.Serial;

public class ServerWinPacket extends ServerPacket {
    @Serial
    private static final long serialVersionUID = 1L;

    public Boolean youWin;

    public ServerWinPacket(Boolean youWin) {
        this.youWin = youWin;
    }

    public Boolean getYouWin() {
        return youWin;
    }
}
