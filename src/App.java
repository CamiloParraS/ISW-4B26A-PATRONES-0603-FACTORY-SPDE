
import javax.swing.SwingUtilities;
import ui.DocumentUI;

public class App {

    public static void main(String[] args) {
        // Always launch Swing UIs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DocumentUI ui = new DocumentUI();
            ui.setVisible(true);
        });
    }
}
