import javax.swing.*;

public class GUI extends JFrame {
    private static final String WINDOW_TITLE = "Clue";

    public GUI() {
        var frame = new JFrame();
        frame.setTitle(WINDOW_TITLE);
        frame.setSize(800, 600);

        var menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        var makeAccusationBtn = new JMenuItem("Make Accusation");
        makeAccusationBtn.setEnabled(false);

        makeAccusationBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "message"));

        menuBar.add(makeAccusationBtn);

        frame.setLayout(null);
        frame.setVisible(true);
    }
}
