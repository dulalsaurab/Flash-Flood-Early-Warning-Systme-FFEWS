/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author suraj
 */
public class MyChartFactory {

    String type;
    PieDataset pieDataSet;
    CategoryDataset catDataSet;

    public void createChart(String type, Number[][] data) {
        this.type = type;
        switch (type) {
            case "PieChart":
                DefaultPieDataset pDataset = new DefaultPieDataset();
                
                break;
            case "LineChart":
                CategoryDataset cDataset = DatasetUtilities.createCategoryDataset("S",
                "C", data);
                break;
        }

    }
}
