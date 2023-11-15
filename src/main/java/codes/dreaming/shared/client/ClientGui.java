package codes.dreaming.shared.client;

import codes.dreaming.shared.server.Server;
import com.googlecode.lanterna.gui2.*;

public class ClientGui {
    public static Window createClientWindow(MultiWindowTextGUI multiWindowTextGUI) {
        final BasicWindow window = new BasicWindow("Client Configuration");
        Panel contentPanel = new Panel(new GridLayout(2));

        Label portLabel = new Label("Enter Port:");
        TextBox portInput = new TextBox();

        Label ipLabel = new Label("Enter Ip:");
        TextBox ipInput = new TextBox();

        Button submitButton = new Button("Connect", () -> {
            int port = Integer.parseInt(portInput.getText());
            String host = ipInput.getText();
            Client client = new Client(multiWindowTextGUI, host, port);
            new Thread(client).start();
            window.close();
        });

        contentPanel.addComponent(portLabel);
        contentPanel.addComponent(portInput);

        contentPanel.addComponent(ipLabel);
        contentPanel.addComponent(ipInput);

        contentPanel.addComponent(submitButton);

        window.setComponent(contentPanel);
        return window;
    }
}
