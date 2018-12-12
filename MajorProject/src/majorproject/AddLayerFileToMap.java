/* Copyright 2012 ESRI
 * 
 * All rights reserved under the copyright laws of the United States
 * and applicable international laws, treaties, and conventions.
 * 
 * You may freely redistribute and use this sample code, with or
 * without modification, provided you include the original copyright
 * notice and use restrictions.
 * 
 * See the use restrictions at <your ArcGIS install location>/DeveloperKit10.1/userestrictions.txt.
 * 
 */
package majorproject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.esri.arcgis.beans.TOC.TOCBean;
import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.beans.toolbar.ToolbarBean;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.esriViewDrawPhase;
import com.esri.arcgis.controls.ControlsMapFullExtentCommand;
import com.esri.arcgis.controls.ControlsMapPanTool;
import com.esri.arcgis.controls.ControlsMapZoomInTool;
import com.esri.arcgis.controls.ControlsMapZoomOutTool;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;
import com.esri.arcgis.systemUI.esriCommandStyles;

/**
 * This sample shows how to programmatically add and delete layers from a
 * MapBean
 */
public class AddLayerFileToMap extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    static JPanel mainPanel = null;
    static JPanel rightPanel = null;
    static JButton addLayerButton = null;
    static JButton removeLayerButton = null;
    static MapBean mapBean = null;
    static TOCBean tocBean = null;
    static ToolbarBean toolbarBean = null;
    static String devKitHome = null;
    static AoInitialize aoInit;

    public AddLayerFileToMap() throws Exception {
        super("Add FeatureLayer");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    aoInit.shutdown();
                } catch (Exception ex) {
                    // exit anyway
                }
                System.exit(0);
            }
        });
        buildFrame();
        setSize(650, 500);
        setVisible(true);
        try {
            initControl();
        } catch (Exception e1) {
            System.out.println("Couldn't initialize sample application.");
            throw e1;
        }
    }

    public void buildFrame() {
        rightPanel = new JPanel();
        mainPanel = new JPanel();

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        addLayerButton = new JButton("Add Layer");
        addLayerButton.addActionListener(this);
        removeLayerButton = new JButton("Remove Layer");
        removeLayerButton.addActionListener(this);
        rightPanel.add(addLayerButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(removeLayerButton);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mapBean = new MapBean();
        tocBean = new TOCBean();
        toolbarBean = new ToolbarBean();
        tocBean.setSize(new Dimension(200, 100));
        toolbarBean.setSize(490, 20);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(tocBean, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(toolbarBean, BorderLayout.NORTH);
        mainPanel.add(mapBean, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Initialize control
     *
     * @throws Exception if couldn't add command or tool to toolbar bean
     */
    public void initControl() throws Exception {
        try {
            // Set the Buddy
            toolbarBean.setBuddyControl(mapBean);
            tocBean.setBuddyControl(mapBean);
            // Add tool bar items..
            toolbarBean.addItem(new ControlsMapZoomInTool(), 0, -1, true, 0,
                    esriCommandStyles.esriCommandStyleIconAndText); // ZoomIn
            toolbarBean.addItem(new ControlsMapZoomOutTool(), 0, -1, false, 0,
                    esriCommandStyles.esriCommandStyleIconAndText); // ZoomOut
            toolbarBean.addItem(new ControlsMapPanTool(), 0, -1, false, 0,
                    esriCommandStyles.esriCommandStyleIconAndText); // Pan

            toolbarBean.addItem(new ControlsMapFullExtentCommand(), 0, -1, true, 20,
                    esriCommandStyles.esriCommandStyleTextOnly);
        } catch (Exception e) {
            System.out.println("Couldn't add commands or tools to toolbar bean.");
            throw e;
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent event)
     * @param event
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == addLayerButton) {
            if (!loadFile()) {
                return;
            }
        }
        if (event.getSource() == removeLayerButton) {
            // Check if control has any layer
            try {
                if (mapBean.getLayerCount() > 0) {
                    RemoveLayerDialog dialog = new RemoveLayerDialog();
                    dialog.setVisible(true);
                }
            } catch (Exception e) {
                System.out.println("Could not determine layer count.  No layer removed.");
                System.out.println(e.getMessage());
                System.out.println("Continuing ...");
            }
        }
    }

    /**
     * Method loadFile loads the specified mxd file
     */
    public boolean loadFile() {
        boolean loaded = false;
        JFileChooser chooser = new JFileChooser(devKitHome + "java" + File.separator + "samples" + File.separator + "data"
                + File.separator + "usa");
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().endsWith(".lyr"));
            }

            public String getDescription() {
                return ".lyr";
            }
        });
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String fileChosen = chooser.getSelectedFile().getCanonicalPath();
                System.out.print("Loading " + fileChosen + " ... ");
                mapBean.addLayerFromFile(fileChosen, 0);
            } catch (Exception e) {
                String fileChosen;
                try {
                    fileChosen = chooser.getSelectedFile().getCanonicalPath();
                    System.out.println("Could not add layer from file: " + fileChosen);
                    System.out.println(e.getMessage());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return false;
                }

            }
            System.out.println("done");
            loaded = true;
        }
        return loaded;
    }

    /**
     * This class is used to create a dialog to remove a layer It displays a
     * drop down with list of all the layers that are currently being added to
     * mapcontrol and a removeLayer button
     */
    class RemoveLayerDialog extends JDialog implements ActionListener {

        private static final long serialVersionUID = 1L;

        JComboBox layerCombo = new JComboBox();
        JButton removeButton = new JButton("RemoveLayer");
        JButton cancel = new JButton("Cancel");
        JPanel mainPanel2 = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        public RemoveLayerDialog() throws Exception {
            super(AddLayerFileToMap.this, "Remove Layer");
            setSize(300, 150);
            this.removeButton.addActionListener(this);

            updateLayerDropDown(this.layerCombo);
            this.buttonPanel.add(this.removeButton);
            this.buttonPanel.add(this.cancel);
            this.mainPanel2.add(this.layerCombo, BorderLayout.NORTH);
            this.mainPanel2.add(this.buttonPanel, BorderLayout.SOUTH);
            this.mainPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            getContentPane().add(this.mainPanel2, BorderLayout.CENTER);
        }

        public void updateLayerDropDown(JComboBox layerCombo2) throws Exception {
            // Get layer count
            int layerCount = 0;
            layerCount = mapBean.getLayerCount();
            // Add the map's layer names to a list
            for (int i = 0; i < layerCount; i++) {
                ILayer layer = mapBean.getLayer(i);
                String name = layer.getName();
                layerCombo2.addItem(name);
            }
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent event)
         * @param event
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == this.removeButton) {
                int layerIndex = this.layerCombo.getSelectedIndex();
                try {
                    mapBean.deleteLayer(layerIndex);
                    mapBean.refresh(esriViewDrawPhase.esriViewBackground, null, null);
                } catch (Exception e) {
                    System.out.println("Could not remove layer.");
                    System.out.println(e.getMessage());
                    System.out.println("Continuing ...");
                }
                // dispose the dialog
                dispose();
            }
            if (event.getSource() == this.cancel) {
                // Dispose the password dialog
                dispose();
            }
        }
    }

    public static void main(String s[]) {
        try {
            EngineInitializer.initializeVisualBeans();

            aoInit = new AoInitialize();
            initializeArcGISLicenses(aoInit);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //Get DEVKITHOME Home
            devKitHome = System.getenv("AGSDEVKITJAVA");
            if (devKitHome == null) {
                System.out.println("Unable to obtain path to ArcGIS Developer Kit home for Java. Exiting application.");
                System.exit(-1);
            }

            new AddLayerFileToMap();
        } catch (Exception e) {
            System.out.println("Error in sample application: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Exiting ...");
        }
    }

    static void initializeArcGISLicenses(AoInitialize aoInit) {
        try {
            if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngine)
                    == esriLicenseStatus.esriLicenseAvailable) {
                aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
            } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeBasic)
                    == esriLicenseStatus.esriLicenseAvailable) {
                aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeBasic);
            } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeStandard)
                    == esriLicenseStatus.esriLicenseAvailable) {
                aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeStandard);
            } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeAdvanced)
                    == esriLicenseStatus.esriLicenseAvailable) {
                aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeAdvanced);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
