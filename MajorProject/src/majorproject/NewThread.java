/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

/**
 *
 * @author suraj
 */
public class NewThread extends Thread {

    boolean runFlag;
    public static Method method;
    private String tableName;

    enum DATATYPE {

        RAINFALL, TEMP, DISCHARGE
    }
    DATATYPE CURRENT;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setDataType(String datatype) {
        switch (datatype) {
            case "Rainfall":
                CURRENT = DATATYPE.RAINFALL;
                break;
            case "Temperature":
                CURRENT = DATATYPE.TEMP;
                break;
            case "Discharge":
                CURRENT = DATATYPE.DISCHARGE;
                break;
        }
    }

    private static Number[][] recycle(Double amount, Number[][] data) {
        for (int i = 0; i < 99; i++) {
            data[0][i] = data[0][i + 1];
        }
        data[0][99] = amount;
        return data;
    }

    public void run() {
        runFlag = true;
        try {
//            VisualizationFrame visFrame = new VisualizationFrame();
//            visFrame.setVisible(true);
            Connection con = DatabaseConnection.getCon();
            Statement stmt = con.createStatement();
            ResultSet rslt;
            if(CURRENT == DATATYPE.DISCHARGE){
                rslt = stmt.executeQuery("Select * from `" + tableName + "` ORDER BY `Year`,`Month`,`Day` ASC");
            }else{
                rslt = stmt.executeQuery("Select * from `" + tableName + "`");
            }
            
            Integer id;
            Integer year = 0, day = 0;
            Double amount = 0.0;
            String place = "";
            Number data[][] = new Number[1][100];
            ChartPanel chartPanel = new ChartPanel(null);

            VisualFrame.getInstance().getGraphsTab1().getjPanel4().setLayout(new java.awt.GridLayout());
            VisualFrame.getInstance().getGraphsTab1().getjPanel4().add(chartPanel);
//            VisualFrame.getInstance().getjPanel2().add(chartPanel);
//            VisualFrame.getInstance().getjPanel2().setLayout(new java.awt.BorderLayout());
//            VisualFrame.getInstance().getjPanel2().add(chartPanel,BorderLayout.CENTER);
//            jPanel1.validate();

//            VisualFrame.getInstance().getjTabbedPane2().getTabComponentAt(0).add
//            visFrame.getjTabbedPane1().add("LineChart",chartPanel);
            try {
                while (rslt.next() && runFlag) {
                    if (CURRENT == DATATYPE.RAINFALL) {
                        place = rslt.getString(2);
                        try {
                            id = Integer.parseInt(rslt.getString(1));
                            year = Integer.parseInt(rslt.getString(3));
                            day = Integer.parseInt(rslt.getString(4));
                            amount = Double.parseDouble(rslt.getString(5));

                        } catch (NumberFormatException numberException) {
                            continue;
                        }
                        if (day >= 100) {
                            data = recycle(amount, data);
                        } else {
                            data[0][day] = amount;
                        }
                    } else if (CURRENT == DATATYPE.TEMP) {
                        place = rslt.getString(2);
                        try {
                            id = Integer.parseInt(rslt.getString(1));
                            year = Integer.parseInt(rslt.getString(3));
                            day = Integer.parseInt(rslt.getString(4));
                            Double min = Double.parseDouble(rslt.getString(5));
                            Double max = Double.parseDouble(rslt.getString(6));
                            amount = (min + max) / 2;

                        } catch (NumberFormatException numberException) {
                            continue;
                        }
                        if (day >= 100) {
                            data = recycle(amount, data);
                        } else {
                            data[0][day] = amount;
                        }
                    } else if (CURRENT == DATATYPE.DISCHARGE) {
                        place = rslt.getString(4);
                        try {
                            id = Integer.parseInt(rslt.getString(1));
                            year = Integer.parseInt(rslt.getString(5));
                            day = Integer.parseInt(rslt.getString(7));
                            amount = Double.parseDouble(rslt.getString(8));

                        } catch (NumberFormatException numberException) {
                            continue;
                        }
                        if (day >= 100) {
                            data = recycle(amount, data);
                        } else {
                            data[0][day] = amount;
                        }
                    }
                    CategoryDataset dataset = DatasetUtilities.createCategoryDataset("A",
                            "D", data);
                    JFreeChart lineChart = ChartFactory.createLineChart("Line Chart", "Time", "Discharge",
                            dataset);

//            ChartPanel chartPanel = new ChartPanel(lineChart);
                    chartPanel.setChart(lineChart);
//                    VisualFrame.getInstance().getjPanel2().validate();
//                    VisualFrame.getInstance().getjPanel2().repaint();
                    VisualFrame.getInstance().getGraphsTab1().getjPanel4().validate();
//                    VisualFrame.getInstance().getGraphsTab1().getjPanel2().repaint();
//                    visFrame.getjTabbedPane1().repaint();
//                     VisualFrame.getInstance().getGraphsTab1().getjPanel2().add(visFrame);

//                    VisualFrame.getInstance().getjTabbedPane2().getTabComponentAt(0).set
//                            visFrame.waitSomeTime(0.001);
                    Thread.sleep(1000);
//            visFrame.jTabbedPane1.remove(chartPanel);

                }
            } catch (SQLException ex) {
                Logger.getLogger(VisualizationFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VisualizationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void end() {
        runFlag = false;
    }
}
