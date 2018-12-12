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
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IRasterLayer;
import com.esri.arcgis.carto.RasterLayer;
import com.esri.arcgis.controls.ControlsGlobeFixedZoomInCommand;
import com.esri.arcgis.controls.ControlsGlobeFixedZoomOutCommand;
import com.esri.arcgis.controls.ControlsGlobeFlyTool;
import com.esri.arcgis.controls.ControlsGlobeFullExtentCommand;
import com.esri.arcgis.controls.ControlsGlobeGlobeToolbar;
import com.esri.arcgis.controls.ControlsGlobeNavigateTool;
import com.esri.arcgis.controls.ControlsGlobeNavigationModeCommand;
import com.esri.arcgis.controls.ControlsGlobeOpenDocCommand;
import com.esri.arcgis.controls.ControlsGlobeZoomInOutTool;
import com.esri.arcgis.controls.ControlsMapFullExtentCommand;
import com.esri.arcgis.controls.ControlsMapPanTool;
import com.esri.arcgis.controls.ControlsMapZoomInTool;
import com.esri.arcgis.controls.ControlsMapZoomOutTool;
import com.esri.arcgis.controls.ControlsPageNewMapCommand;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.datasourcesraster.RasterDataset;
import com.esri.arcgis.datasourcesraster.RasterWorkspace;
import com.esri.arcgis.datasourcesraster.RasterWorkspaceFactory;
import com.esri.arcgis.geodatabase.FeatureClass;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IRasterDataset;
import com.esri.arcgis.geodatabase.IRasterWorkspace2;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.systemUI.esriCommandStyles;
import com.sun.jmx.snmp.BerDecoder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.rmi.activation.Activator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author suraj
 */
public class UserInterface extends JFrame implements MouseListener {

    private static UserInterface ui = new UserInterface();


    private UserInterface() {

    }

    public void initialize() throws ClassNotFoundException, SQLException {
//        ArcGIS.initializeArcGISLicenses();
        EngineInitializer.initializeVisualBeans();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        new DatabaseConnection();
//        PreparedStatement pStmt = con.prepareStatement("SELECT * from `rainfall` where ");

    }

    public void finalize() {
        ui.setVisible(true);
        ui.setSize(new Dimension(800, 600));
    }

    public static UserInterface getUI() {
        return ui;
    }

    public void initComponents() throws IOException {

        //create ArcGIS components
        BeanFactory beanFactory = new BeanFactory();
        MapBean mapBean = beanFactory.getMapBean();
        mapBean.setSize(100, 20);

        ToolbarBean toolBarBean = beanFactory.getToolbarBean();
        toolBarBean.setSize(800, 25);
        TOCBean tocBean = beanFactory.getTocBean();
        tocBean.setSize(200, 600);
        GlobeBean globeBean = beanFactory.getGlobeBean();
        globeBean.setSize(600, 600);
        //end

        //create custom components
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.Y_AXIS));
        customPanel.setPreferredSize(new Dimension(this.getSize().width, 80));

        JLabel newDataSourceBtn = new JLabel();
        newDataSourceBtn.setIcon(new ImageIcon("res/open.png"));
        newDataSourceBtn.setPreferredSize(new Dimension(30, 35));
        JLabel newDataSourceLabel = new JLabel();
        newDataSourceLabel.setPreferredSize(new Dimension(40, 35));
        newDataSourceLabel.setText("Open");
        newDataSourceLabel.disable();
        newDataSourceBtn.setToolTipText("<html>Open New Data Source</html>");

        JSeparator dataSimulateSeparate = new JSeparator();
        dataSimulateSeparate.setOrientation(JSeparator.NORTH);
        dataSimulateSeparate.setPreferredSize(new Dimension(10, 35));

        JLabel simulateBtn = new JLabel();
        simulateBtn.setIcon(new ImageIcon("res/simulate.png"));
        simulateBtn.setPreferredSize(new Dimension(30, 35));
//        simulateBtn.disable();
        JLabel simulateLabel = new JLabel();
        simulateLabel.setPreferredSize(new Dimension(60, 35));
        simulateLabel.setText("Simulate");
        simulateLabel.disable();

        JSeparator simulateVisualizeSeparate = new JSeparator();
        simulateVisualizeSeparate.setOrientation(JSeparator.NORTH);
        simulateVisualizeSeparate.setPreferredSize(new Dimension(10, 35));

        JLabel visualizeBtn = new JLabel();
        visualizeBtn.setIcon(new ImageIcon("res/visualize.png"));
        visualizeBtn.setPreferredSize(new Dimension(30, 35));
        visualizeBtn.addMouseListener(this);
        JLabel visualizeLabel = new JLabel();
        visualizeLabel.setPreferredSize(new Dimension(100, 35));
        visualizeLabel.setText("Visualize");
        visualizeLabel.disable();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setSize(this.getSize().width, 35);
        menuPanel.setBackground(Color.GREEN);

        menuPanel.add(newDataSourceBtn);
        menuPanel.add(newDataSourceLabel);
        menuPanel.add(dataSimulateSeparate);
        menuPanel.add(simulateBtn);
        menuPanel.add(simulateLabel);
        menuPanel.add(simulateVisualizeSeparate);
        menuPanel.add(visualizeBtn);
        menuPanel.add(visualizeLabel);

        customPanel.add(menuPanel);
        customPanel.add(toolBarBean);

        //end
        //add the components to the frame
//        ui.getContentPane().add(menuBar,BorderLayout.);
        ui.getContentPane().add(customPanel, BorderLayout.NORTH);
        ui.getContentPane().add(mapBean, BorderLayout.CENTER);
        ui.getContentPane().add(tocBean, BorderLayout.WEST);

        tocBean.setBuddyControl(mapBean);
        toolBarBean.setBuddyControl(mapBean);

        //end
//        toolBarBean.addItem(new ControlsGlobeOpenDocCommand(), 0, -1, false, 0, 1); // Open
//        toolBarBean.addItem(new ControlsGlobeNavigateTool(), 0, -1, false, 0, 1); // Navigate
//        toolBarBean.addItem(new ControlsGlobeZoomInOutTool(), 0, -1, false, 0, 1); // ZoomInOut
//        toolBarBean.addItem(new ControlsGlobeFlyTool(), 0, -1, false, 0, 1); // Fly
//        toolBarBean.addItem(new ControlsGlobeFixedZoomInCommand(), 0, -1, false, 0, 1); // FixedZoomIn
//        toolBarBean.addItem(new ControlsGlobeFixedZoomOutCommand(), 0, -1, false, 0, 1); // FixedZoomOut
//        toolBarBean.addItem(new ControlsGlobeFullExtentCommand(), 0, -1, false, 0, 1); // FullExtent
//        toolBarBean.addItem(new ControlsGlobeGlobeToolbar(), 0, -1, false, 0, 1); // ToolbarGlobe
//        toolBarBean.addItem(new ControlsGlobeNavigationModeCommand(), 0, -1, false, 0, 1); // NavigationMode
//        ControlsPageNewMapCommand
        toolBarBean.addItem(new ControlsMapZoomInTool(), 0, -1, true, 0,
                esriCommandStyles.esriCommandStyleIconAndText); // ZoomIn
        toolBarBean.addItem(new ControlsMapZoomOutTool(), 0, -1, false, 0,
                esriCommandStyles.esriCommandStyleIconAndText); // ZoomOut
        toolBarBean.addItem(new ControlsMapPanTool(), 0, -1, false, 0,
                esriCommandStyles.esriCommandStyleIconAndText); // Pan

        toolBarBean.addItem(new ControlsMapFullExtentCommand(), 0, -1, true, 20,
                esriCommandStyles.esriCommandStyleTextOnly);

        toolBarBean.addItem(new ControlsPageNewMapCommand(), 0, -1, true, 20,
                esriCommandStyles.esriCommandStyleTextOnly);

        
        ////////////////////////////////////////////////////////////
        RasterWorkspaceFactory rasterWorkspaceFactory = new RasterWorkspaceFactory();
        RasterWorkspace rasterWorkspace = new RasterWorkspace(rasterWorkspaceFactory.openFromFile("H:/Projects/MajorProject/data/Aster Global DEM/", 0)); //yeha directory ho hai
        RasterDataset rasterDataset = new RasterDataset(rasterWorkspace.openRasterDataset("ASTGTM2_N44W094_dem.tif")); //yo chai tyo directory bhitra ko file

        RasterLayer rasterLayer = new RasterLayer();
        rasterLayer.createFromDataset(rasterDataset);
        mapBean.addLayer(rasterLayer, 0);
        /////////////////////////////////////////////////////////////

//        ShapefileWorkspaceFactory wsf = new ShapefileWorkspaceFactory();
//        Workspace work = new Workspace(wsf.openFromFile("H:\\Projects\\MajorProject\\NPL_wat\\", 0));
//        IFeatureClass featureClass = work.openFeatureClass("NPL_water_areas_dcw");
//
//        FeatureLayer featureLayer = new FeatureLayer();
//        featureLayer.setFeatureClassByRef(featureClass);
//        mapBean.addLayer(featureLayer,1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            //        VisualFrame.createVisualFrame();
//        NewThread visualizationThread = new NewThread();
//        visualizationThread.start();
            VisualizationFrame.createVisualFrame();
        } catch (SQLException ex) {
            Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
