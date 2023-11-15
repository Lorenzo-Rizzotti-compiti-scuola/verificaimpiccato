package codes.dreaming.shared.server;

import com.googlecode.lanterna.gui2.*;

public class ServerGui {
    public static Window createServerWindow() {
        final BasicWindow window = new BasicWindow("Server Configuration");
        Panel contentPanel = new Panel(new GridLayout(2));

        Label portLabel = new Label("Enter Port:");
        TextBox portInput = new TextBox();

        Label playerLabel = new Label("Enter Player Count:");
        TextBox playerInput = new TextBox();

        Label worldLabel = new Label("Enter World to guess:");
        TextBox worldInput = new TextBox();

        Button submitButton = new Button("Start Server", () -> {
            int port = Integer.parseInt(portInput.getText());
            int players = Integer.parseInt(playerInput.getText());
            String world = worldInput.getText();
            Server server = new Server(port, players, world);
            new Thread(server).start();
            window.close();
        });

        contentPanel.addComponent(portLabel);
        contentPanel.addComponent(portInput);

        contentPanel.addComponent(playerLabel);
        contentPanel.addComponent(playerInput);

        contentPanel.addComponent(worldLabel);
        contentPanel.addComponent(worldInput);

        contentPanel.addComponent(submitButton);

        window.setComponent(contentPanel);
        return window;
    }
}
