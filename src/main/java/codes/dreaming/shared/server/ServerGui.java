package codes.dreaming.shared.server;

import com.googlecode.lanterna.gui2.*;

import static codes.dreaming.GuiUtils.createFieldAndAddToContentPanel;

public class ServerGui {
    public static Window createServerWindow() {
        final BasicWindow window = new BasicWindow("Server Configuration");
        Panel contentPanel = new Panel(new GridLayout(2));

        TextBox portInput = createFieldAndAddToContentPanel(contentPanel, "Enter Port:");
        TextBox playerInput = createFieldAndAddToContentPanel(contentPanel, "Enter Player Count:");
        TextBox worldInput = createFieldAndAddToContentPanel(contentPanel, "Enter World to guess:");

        Button submitButton = new Button("Start Server", () -> {
            startServer(portInput, playerInput, worldInput, window);
        });

        contentPanel.addComponent(submitButton);

        window.setComponent(contentPanel);
        return window;
    }

    private static void startServer(TextBox portInput, TextBox playerInput, TextBox worldInput, BasicWindow window) {
        int port = Integer.parseInt(portInput.getText());
        int players = Integer.parseInt(playerInput.getText());
        String world = worldInput.getText();

        Server server = new Server(port, players, world);
        new Thread(server).start();

        window.close();
    }
}
