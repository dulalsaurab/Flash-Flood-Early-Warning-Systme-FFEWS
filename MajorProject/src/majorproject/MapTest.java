
package majorproject;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapTest {
    public static void main(String[] args) {
        final Browser browser = BrowserFactory.create();

        final JTextField addressBar = new JTextField("http://www.teamdev.com/jxbrowser");
        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser.loadURL(addressBar.getText());
            }
        });

        JPanel addressPane = new JPanel(new BorderLayout());
        addressPane.add(new JLabel(" URL: "), BorderLayout.WEST);
        addressPane.add(addressBar, BorderLayout.CENTER);

        JFrame frame = new JFrame("JxBrowser - Hello World");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(addressPane, BorderLayout.NORTH);
        frame.add(browser.getView().getComponent(), BorderLayout.CENTER);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.loadURL(addressBar.getText());
    }
}