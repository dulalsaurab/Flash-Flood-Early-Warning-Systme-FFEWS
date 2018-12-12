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
import java.io.IOException;

import javax.swing.JFrame;

import com.esri.arcgis.beans.TOC.TOCBean;
import com.esri.arcgis.beans.globe.GlobeBean;
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

/**
 * Sample showing GlobeBean, ToolbarBean and TOCBean
 */
public class UseGlobeBean {

    static javax.swing.JFrame frame = null;
    static GlobeBean globeBean = null;
    static ToolbarBean toolbarBean = null;
    static TOCBean tocBean = null;

    public UseGlobeBean() {
//        initializeArcGISLicenses();
        frame = new JFrame("GlobeBean Java Sample:  GlobeBean, Toolbar and Toc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        globeBean = new GlobeBean();
        toolbarBean = new ToolbarBean();
        tocBean = new TOCBean();
    }

//    public static void main(String[] args) {
//        try {
//            EngineInitializer.initializeVisualBeans();
//            UseGlobeBean globe = new UseGlobeBean();
//            globe.display();
//        } catch (IOException ex) {
//            // ignore
//        }
//    }

    

    public void display() throws java.io.IOException {
        globeBean.setSize(600, 600);
        toolbarBean.setSize(800, 25);
        tocBean.setSize(200, 600);
        frame.getContentPane().add(toolbarBean, BorderLayout.NORTH);
        frame.getContentPane().add(globeBean, BorderLayout.CENTER);
        frame.getContentPane().add(tocBean, BorderLayout.WEST);
        frame.setSize(new Dimension(800, 600));
        frame.setVisible(true);

        tocBean.setBuddyControl(globeBean);
        toolbarBean.setBuddyControl(globeBean);

        // Load the pre-built tools
        toolbarBean.addItem(new ControlsGlobeOpenDocCommand(), 0, -1, false, 0, 1); // Open
        toolbarBean.addItem(new ControlsGlobeNavigateTool(), 0, -1, false, 0, 1); // Navigate
        toolbarBean.addItem(new ControlsGlobeZoomInOutTool(), 0, -1, false, 0, 1); // ZoomInOut
        toolbarBean.addItem(new ControlsGlobeFlyTool(), 0, -1, false, 0, 1); // Fly
        toolbarBean.addItem(new ControlsGlobeFixedZoomInCommand(), 0, -1, false, 0, 1); // FixedZoomIn
        toolbarBean.addItem(new ControlsGlobeFixedZoomOutCommand(), 0, -1, false, 0, 1); // FixedZoomOut
        toolbarBean.addItem(new ControlsGlobeFullExtentCommand(), 0, -1, false, 0, 1); // FullExtent
        toolbarBean.addItem(new ControlsGlobeGlobeToolbar(), 0, -1, false, 0, 1); // ToolbarGlobe
        toolbarBean.addItem(new ControlsGlobeNavigationModeCommand(), 0, -1, false, 0, 1); // NavigationMode
    }
}
