package codes.dreaming;

import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;

public class GuiUtils {
    public static TextBox createFieldAndAddToContentPanel(Panel contentPanel, String labelText) {
        Label label = new Label(labelText);
        TextBox textBox = new TextBox();

        contentPanel.addComponent(label);
        contentPanel.addComponent(textBox);

        return textBox;
    }
}
