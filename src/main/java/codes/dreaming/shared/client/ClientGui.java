package codes.dreaming.shared.client;

import com.googlecode.lanterna.gui2.*;

import static codes.dreaming.GuiUtils.createFieldAndAddToContentPanel;

public class ClientGui {
    public static Window createClientWindow(MultiWindowTextGUI multiWindowTextGUI) {
        final BasicWindow window = new BasicWindow("Client Configuration");
        Panel contentPanel = new Panel(new GridLayout(2));

        TextBox portInput = createFieldAndAddToContentPanel(contentPanel, "Enter Port:");
        TextBox ipInput = createFieldAndAddToContentPanel(contentPanel, "Enter Ip:");

        Button submitButton = new Button("Connect", () -> {
            connectClient(multiWindowTextGUI, portInput, ipInput, window);
        });

        contentPanel.addComponent(submitButton);

        window.setComponent(contentPanel);
        return window;
    }
    private static void connectClient(MultiWindowTextGUI multiWindowTextGUI, TextBox portInput, TextBox ipInput, BasicWindow window) {
        int port = Integer.parseInt(portInput.getText());
        String host = ipInput.getText();

        Client client = new Client(multiWindowTextGUI, host, port);
        new Thread(client).start();

        window.close();
    }
}
