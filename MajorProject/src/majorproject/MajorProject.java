/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import com.esri.arcgis.beans.TOC.TOCBean;
import com.esri.arcgis.beans.globe.GlobeBean;
import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.beans.toolbar.ToolbarBean;
import com.esri.arcgis.controls.ControlsGlobeFixedZoomInCommand;
import com.esri.arcgis.controls.ControlsGlobeFixedZoomOutCommand;
import com.esri.arcgis.controls.ControlsGlobeFlyTool;
import com.esri.arcgis.controls.ControlsGlobeFullExtentCommand;
import com.esri.arcgis.controls.ControlsGlobeGlobeToolbar;
import com.esri.arcgis.controls.ControlsGlobeNavigateTool;
import com.esri.arcgis.controls.ControlsGlobeNavigationModeCommand;
import com.esri.arcgis.controls.ControlsGlobeOpenDocCommand;
import com.esri.arcgis.controls.ControlsGlobeZoomInOutTool;
import com.esri.arcgis.system.EngineInitializer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import static majorproject.UseGlobeBean.frame;
import static majorproject.UseGlobeBean.globeBean;
import static majorproject.UseGlobeBean.tocBean;
import static majorproject.UseGlobeBean.toolbarBean;


/**
 *
 * @author suraj
 */
public class MajorProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        // TODO code application logic here
//        UserInterface ui = UserInterface.getUI();
//        ui.initialize();
//        ui.initComponents();
//        ui.finalize();
        VisualFrame.createVisualFrame();
        
        
        
//        JEditorPane jEditorPane = new JEditorPane();
//        
//        // make it read-only
//        jEditorPane.setEditable(false);
//        
//        // create a scrollpane; modify its attributes as desired
//        JScrollPane scrollPane = new JScrollPane(jEditorPane);
//        
//        // add an html editor kit
//        HTMLEditorKit kit = new HTMLEditorKit();
//        jEditorPane.setEditorKit(kit);
//        
//        // add some styles to the html
//        StyleSheet styleSheet = kit.getStyleSheet();
//        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
//        styleSheet.addRule("h1 {color: blue;}");
//        styleSheet.addRule("h2 {color: #ff0000;}");
//        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
//
//        // create some simple html as a string
//        String htmlString = "<html>\n"
//                          + "<body>\n"
//                          + "<h1>Welcome!</h1>\n"
//                          + "<h2>This is an H2 header</h2>\n"
//                          + "<p>This is some sample text</p>\n"
//                          + "<p><a href=\"http://devdaily.com/blog/\">devdaily blog</a></p>\n"
//                          + "</body>\n";
//        
//        // create a document, set it on the jeditorpane, then add the html
//        Document doc = kit.createDefaultDocument();
//        jEditorPane.setDocument(doc);
//        jEditorPane.setText(htmlString);
//
//        // now add it all to a frame
//        JFrame j = new JFrame("HtmlEditorKit Test");
//        j.getContentPane().add(scrollPane, BorderLayout.CENTER);
//
//        // make it easy to close the application
//        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        
//        // display the frame
//        j.setSize(new Dimension(300,200));
//        
//        // pack it, if you prefer
//        //j.pack();
//        
//        // center the jframe, then make it visible
//        j.setLocationRelativeTo(null);
//        j.setVisible(true);
        

        
        
        
        
    }

}
