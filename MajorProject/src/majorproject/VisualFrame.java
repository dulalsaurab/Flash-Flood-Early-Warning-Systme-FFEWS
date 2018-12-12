/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

//import com.clockworksms.ClockWorkSmsService;
//import com.clockworksms.ClockworkException;
//import com.clockworksms.ClockworkSmsResult;
//import com.clockworksms.SMS;
import com.esri.arcgis.beans.TOC.TOCBean;
import com.esri.arcgis.beans.globe.GlobeBean;
import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.beans.toolbar.ToolbarBean;
import com.esri.arcgis.carto.RasterLayer;
import com.esri.arcgis.controls.ControlsMapFullExtentCommand;
import com.esri.arcgis.controls.ControlsMapPanTool;
import com.esri.arcgis.controls.ControlsMapZoomInTool;
import com.esri.arcgis.controls.ControlsMapZoomOutTool;
import com.esri.arcgis.controls.ControlsPageNewMapCommand;
import com.esri.arcgis.datasourcesraster.RasterDataset;
import com.esri.arcgis.datasourcesraster.RasterWorkspace;
import com.esri.arcgis.datasourcesraster.RasterWorkspaceFactory;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.systemUI.esriCommandStyles;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author suraj
 */
public class VisualFrame extends javax.swing.JFrame {

    private boolean dataLoaded = false;
    private boolean floodPredicted = false;

    ToolbarBean toolBarBean;
    MapBean mapBean;
    TOCBean tocBean;
    private static VisualFrame visFrame;

    /**
     * Creates new form VisualFrame
     */
    public VisualFrame() throws IOException, ClassNotFoundException, SQLException {
        initComponents();
//        EngineInitializer.initializeVisualBeans();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        new DatabaseConnection();
//        addEsriComponents();
        initialize();
        
    }

    private void loadGoogleMap(Double latitude,Double longitude) {
        Browser browser = BrowserFactory.create();
        VisualFrame.getInstance().getjTabbedPane2().add("Map",browser.getView().getComponent());
//        browser.loadURL("file:///E:/Projects/FinalYearProject/MajorProject/WebStuffs/map.html");
        browser.loadURL("file:///E:/Projects/FinalYearProject/MajorProject/WebStuffs/1405863500.layoutit/layoutit/src/index.html");
        
//        browser.loadURL("https://www.google.com/maps/@27.504505,84.2765065,677m/data=!3m1!1e3");
//        browser.loadURL("https://www.google.com/maps/@"+latitude.toString()+","+longitude.toString()+",677m/data=!3m1!1e3");
        
    }

    private void initialize() {
        if (!dataLoaded) {
            simulateBtn.setEnabled(false);
//            visualizeBtn.setEnabled(false);
            predictBtn.setEnabled(false);
        } else if (!floodPredicted) {
            simulateBtn.setEnabled(false);
//            visualizeBtn.setEnabled(true);
            predictBtn.setEnabled(true);
        } else {
            simulateBtn.setEnabled(true);
//            visualizeBtn.setEnabled(true);
            predictBtn.setEnabled(true);
        }
    }

    public static VisualFrame getInstance() {
        return visFrame;
    }

    public JButton getPredictBtn() {
        return predictBtn;
    }

    public JButton getSimulateBtn() {
        return simulateBtn;
    }

    public JButton getVisualizeBtn() {
        return visualizeBtn;
    }

    private void addEsriComponents() throws IOException {
//        BeanFactory beanFactory = new BeanFactory();
//        mapBean = beanFactory.getMapBean();
//        mapBean.setSize(this.mapPanel.getWidth(), this.mapPanel.getHeight());
//
//        toolBarBean = beanFactory.getToolbarBean();
//        toolBarBean.setSize(this.getWidth(), this.toolBarPanel.getHeight());
//        tocBean = beanFactory.getTocBean();
//        tocBean.setSize(200, 600);
//        GlobeBean globeBean = beanFactory.getGlobeBean();
//        globeBean.setSize(600, 600);
//
//        toolBarBean.addItem(new ControlsMapZoomInTool(), 0, -1, true, 0,
//                esriCommandStyles.esriCommandStyleIconAndText); // ZoomIn
//        toolBarBean.addItem(new ControlsMapZoomOutTool(), 0, -1, false, 0,
//                esriCommandStyles.esriCommandStyleIconAndText); // ZoomOut
//        toolBarBean.addItem(new ControlsMapPanTool(), 0, -1, false, 0,
//                esriCommandStyles.esriCommandStyleIconAndText); // Pan
//
//        toolBarBean.addItem(new ControlsMapFullExtentCommand(), 0, -1, true, 20,
//                esriCommandStyles.esriCommandStyleTextOnly);
//
//        toolBarBean.addItem(new ControlsPageNewMapCommand(), 0, -1, true, 20,
//                esriCommandStyles.esriCommandStyleTextOnly);
//
//        this.toolBarPanel.add(toolBarBean);
//        this.tocPanel.add(tocBean);
//        this.mapPanel.add(mapBean);
//
//        tocBean.setBuddyControl(mapBean);
//        toolBarBean.setBuddyControl(mapBean);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu8 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        dataSourceBtn = new javax.swing.JButton();
        visualizeBtn = new javax.swing.JButton();
        mapBtn = new javax.swing.JButton();
        simulateBtn = new javax.swing.JButton();
        predictBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        graphsTab1 = new majorproject.GraphsTab();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenu9 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();

        jMenu8.setText("jMenu8");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        dataSourceBtn.setText("Open Data");
        dataSourceBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataSourceBtnActionPerformed(evt);
            }
        });

        visualizeBtn.setText("Visualize");
        visualizeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualizeBtnActionPerformed(evt);
            }
        });

        mapBtn.setText("Open Map");
        mapBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mapBtnActionPerformed(evt);
            }
        });

        simulateBtn.setText("Simulate");

        predictBtn.setText("Predict");
        predictBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predictBtnActionPerformed(evt);
            }
        });

        jButton1.setText("Send Warning");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Generate NN Data");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTabbedPane2.addTab("tab1", graphsTab1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1286, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dataSourceBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mapBtn)
                        .addGap(11, 11, 11)
                        .addComponent(simulateBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(visualizeBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predictBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 514, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(predictBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(simulateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mapBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataSourceBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(visualizeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                .addContainerGap())
        );

        dataSourceBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        dataSourceBtn.setIcon(new ImageIcon("res/open.png"));
        visualizeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        visualizeBtn.setIcon(new ImageIcon("res/visualize.png"));
        mapBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mapBtn.setIcon(new ImageIcon("res/open.png"));
        simulateBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        simulateBtn.setIcon(new ImageIcon("res/simulate.png"));
        predictBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        predictBtn.setIcon(new ImageIcon("res/visualize.png"));

        jMenu1.setText("File");

        jMenu7.setText("Open Map");
        jMenu1.add(jMenu7);

        jMenu9.setText("Load Data");
        jMenu1.add(jMenu9);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Visualize");

        jMenuItem4.setText("Line Chart");
        jMenu3.add(jMenuItem4);

        jMenuItem5.setText("Pie Chart");
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Predict");

        jMenuItem3.setText("Start");
        jMenu4.add(jMenuItem3);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Simulate");

        jMenuItem1.setText("Run");
        jMenu5.add(jMenuItem1);

        jMenuItem2.setText("Stop");
        jMenu5.add(jMenuItem2);

        jMenuBar1.add(jMenu5);

        jMenu6.setText("Help");
        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mapBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mapBtnActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("TIF Files", "tif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {

                String totalPath = chooser.getSelectedFile().getAbsolutePath();
                String fileName = totalPath.substring(totalPath.lastIndexOf("\\") + 1, totalPath.length());
                if (!fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).equals("tif")) {
                    JOptionPane.showMessageDialog(this, "Please select a raster data (TIF file)", "Wrong Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String directoryName = totalPath.substring(0, totalPath.lastIndexOf("\\")) + "\\";
                RasterWorkspaceFactory rasterWorkspaceFactory = new RasterWorkspaceFactory();
//                RasterWorkspace rasterWorkspace = new RasterWorkspace(rasterWorkspaceFactory.openFromFile("H:/Projects/MajorProject/data/Aster Global DEM/", 0)); //yeha directory ho hai
//                RasterDataset rasterDataset = new RasterDataset(rasterWorkspace.openRasterDataset("ASTGTM2_N44W094_dem.tif")); //yo chai tyo directory bhitra ko file
                RasterWorkspace rasterWorkspace = new RasterWorkspace(rasterWorkspaceFactory.openFromFile(directoryName, 0)); //yeha directory ho hai
                RasterDataset rasterDataset = new RasterDataset(rasterWorkspace.openRasterDataset(fileName)); //yo chai tyo directory bhitra ko file

                RasterLayer rasterLayer = new RasterLayer();
                rasterLayer.createFromDataset(rasterDataset);
                mapBean.addLayer(rasterLayer, 0);
            } catch (IOException ex) {
                Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_mapBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        SendSms.sendSMS("Run Run Run", "9779813641099");
//        try {
//            // TODO add your handling code here:
//            
//            /////////////////////////Multiple Messages ///////////////////////////////////
////        List<SMS> messages = new ArrayList<SMS>();
////        messages.add(new SMS(TO, MESSAGE));
////        messages.add(new SMS(ANOTHER_TO, MESSAGE));
////
////        ClockWorkSmsService clockWorkSmsService = new ClockWorkSmsService(API_KEY);
////        List<ClockworkSmsResult> results = clockWorkSmsService.send(messages);
//            
//            
//            
//            ClockWorkSmsService clockWorkSmsService = new ClockWorkSmsService("0ab9772d2e5260a9616c48872bffa0d241fa4a55");
//            SMS sms = new SMS("977981325445", "Hello World");
//            ClockworkSmsResult result = clockWorkSmsService.send(sms);
//            
//            if (result.isSuccess()) {
//                System.out.println("Sent with ID: " + result.getId());
//            } else {
//                System.out.println("Error: " + result.getErrorMessage());
//            }
//        } catch (ClockworkException ex) {
//            Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void dataSourceBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataSourceBtnActionPerformed
        // TODO add your handling code here:
        ParameterBrowse.CreateParameterBrowse();
    }//GEN-LAST:event_dataSourceBtnActionPerformed

    private void predictBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_predictBtnActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_predictBtnActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            VisualizationFrame.createVisualFrame();
        } catch (SQLException ex) {
            Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void visualizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualizeBtnActionPerformed
        // TODO add your handling code here:
        NewThread visualizationThread = new NewThread();
        visualizationThread.start();
    }//GEN-LAST:event_visualizeBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void createVisualFrame() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VisualFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    visFrame = new VisualFrame();
                    visFrame.setVisible(true);
                    Double latitude = 27.504505;
                    Double longitude = 84.2765065;
                    visFrame.loadGoogleMap(latitude,longitude);
//                    new VisualFrame().setVisible(true);
                } catch (IOException | ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dataSourceBtn;
    private majorproject.GraphsTab graphsTab1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton mapBtn;
    private javax.swing.JButton predictBtn;
    private javax.swing.JButton simulateBtn;
    private javax.swing.JButton visualizeBtn;
    // End of variables declaration//GEN-END:variables

    public JTabbedPane getjTabbedPane2() {
        return jTabbedPane2;
    }

    public GraphsTab getGraphsTab1() {
        return graphsTab1;
    }
}

class MapThread extends Thread {

    @Override
    public void run() {
        Browser browser = BrowserFactory.create();
        VisualFrame.getInstance().getGraphsTab1().getjPanel2().add(browser.getView().getComponent(), BorderLayout.CENTER);
        
//        VisualFrame.getInstance().getGraphsTab1().getjPanel2().repaint();
        VisualFrame.getInstance().getGraphsTab1().getjPanel2().validate();
        browser.loadURL("http://maps.google.com");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(MapThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
}
