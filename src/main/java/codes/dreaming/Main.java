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
    private Terminal terminal;
    private Screen screen;
    private BasicWindow window;
    private MultiWindowTextGUI gui;

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    private void run() throws IOException {
        setupOutput();
        initializeGUI();
        setupContent();
    }

    private void setupOutput() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.screen.startScreen();
    }

    private void initializeGUI() {
        this.gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        this.window = new BasicWindow();
    }

    private void setupContent() {
        Panel contentPanel = createContentPanel();
        Panel mainPanel = setupMainPanel(contentPanel);
        window.setComponent(mainPanel);
        gui.addWindowAndWait(window);
    }

    private Panel createContentPanel() {
        Panel contentPanel = new Panel(new GridLayout(2));
        createButtons(contentPanel);
        return contentPanel;
    }

    private void createButtons(Panel contentPanel) {
        Button serverButton = createServerButton();
        Button clientButton = createClientButton();
        addButtonsToPanel(serverButton, clientButton, contentPanel);
    }

    private Button createServerButton() {
        return new Button("Start Server", () -> {
            Window serverWindow = ServerGui.createServerWindow();
            gui.addWindowAndWait(serverWindow);
        });
    }

    private Button createClientButton() {
        return new Button("Start Client", () -> {
            Window clientWindow = ClientGui.createClientWindow(gui);
            gui.addWindowAndWait(clientWindow);
        });
    }

    private void addButtonsToPanel(Button serverButton, Button clientButton, Panel contentPanel) {
        contentPanel.addComponent(serverButton);
        contentPanel.addComponent(clientButton);
    }

    private Panel setupMainPanel(Panel contentPanel) {
        Panel mainPanel = mainPanelWithBorderLayout();
        addComponentToMainPanel(contentPanel, mainPanel);
        return mainPanel;
    }

    private Panel mainPanelWithBorderLayout() {
        return new Panel(new BorderLayout());
    }

    private void addComponentToMainPanel(Panel contentPanel, Panel mainPanel) {
        addEmptySpaceToPanel(mainPanel, BorderLayout.Location.TOP);
        addEmptySpaceToPanel(mainPanel, BorderLayout.Location.LEFT);
        mainPanel.addComponent(contentPanel, BorderLayout.Location.CENTER);
        addEmptySpaceToPanel(mainPanel, BorderLayout.Location.RIGHT);
        addEmptySpaceToPanel(mainPanel, BorderLayout.Location.BOTTOM);
    }

    private void addEmptySpaceToPanel(Panel panel, BorderLayout.Location location) {
        panel.addComponent(new EmptySpace(), location);
    }
}
