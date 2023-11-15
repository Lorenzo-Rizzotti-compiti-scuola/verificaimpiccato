package codes.dreaming;

import codes.dreaming.shared.client.ClientGui;
import codes.dreaming.shared.server.ServerGui;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        final BasicWindow window = new BasicWindow();


        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        Panel contentPanel = new Panel(new GridLayout(2));
        Button serverButton = new Button("Start Server", () -> {
            Window serverWindow = ServerGui.createServerWindow();
            gui.addWindowAndWait(serverWindow);
        });

        Button clientButton = new Button("Start Client", () -> {
            Window clientWindow = ClientGui.createClientWindow(gui);
            gui.addWindowAndWait(clientWindow);
        });

        // Add the buttons to the content panel
        contentPanel.addComponent(serverButton);
        contentPanel.addComponent(clientButton);

        // Create an empty space around the content panel to center it
        Panel mainPanel = new Panel(new BorderLayout());
        mainPanel.addComponent(new EmptySpace(), BorderLayout.Location.TOP);
        mainPanel.addComponent(new EmptySpace(), BorderLayout.Location.LEFT);
        mainPanel.addComponent(contentPanel, BorderLayout.Location.CENTER);
        mainPanel.addComponent(new EmptySpace(), BorderLayout.Location.RIGHT);
        mainPanel.addComponent(new EmptySpace(), BorderLayout.Location.BOTTOM);

        window.setComponent(mainPanel);

        gui.addWindowAndWait(window);
    }
}
