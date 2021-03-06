/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author suraj
 */
public class ParameterBrowse extends javax.swing.JFrame {

    JTable table1;
    boolean isDataAvailable;
    private Vector<Double[]> totalData = new Vector<Double[]>();
    private int numParamters = 3;

    /**
     * Creates new form ParameterBrowse
     */
    public ParameterBrowse() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        parameterCombo = new javax.swing.JComboBox();
        browseBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        doneLoading = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        parameterCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rainfall", "Temperature", "Discharge" }));

        browseBtn.setText("Browse..");
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        doneLoading.setText("Done Loading");
        doneLoading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneLoadingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parameterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(browseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doneLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(doneLoading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parameterCombo)
                    .addComponent(browseBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(228, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    private void createPredictionData() throws IOException {
        File file = new File("predictionData/prediction.txt");
        FileWriter writer = new FileWriter(file);
        int dataLength = totalData.get(0).length;
        String row = "";
        for (int i = 0; i < dataLength; i++) {
            for(int j=0;j<totalData.size();j++){
                row += totalData.get(j)[i].toString()+"\t";
            }
            row+= "\r\n";
//            String row = totalData.get(i)[].toString() + "\t" + temperature[i].toString() + "\t" + discharge[i].toString() + "\r\n";
            writer.write(row);
        }

    }

    private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseBtnActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String totalPath = chooser.getSelectedFile().getAbsolutePath();
                String choosen = parameterCombo.getSelectedItem().toString();
                File file = new File(totalPath);
                FileReader reader = new FileReader(file);
                char[] totalContent = new char[(int) file.length()];
                reader.read(totalContent);
                BufferedReader bufReader = new BufferedReader(reader);
                String line = "";
                int numLines = countLines(totalPath);
                Double[] rainfall = null;
                Double[] temperature = null;
                Double[] discharge = null;
                if (choosen.equals("Rainfall")) {
                    table1.setValueAt(totalPath, 0, 1);
                    rainfall = new Double[numLines];
                    int count = 0;
                    while ((line = bufReader.readLine()) != null && line.length() != 0) {
                        String[] lineSplitValues = line.split(" ");
                        rainfall[count] = Double.parseDouble(lineSplitValues[1]);
                        count++;
                    }
                    totalData.add(rainfall);
                } else if (choosen.equals("Temperature")) {
                    table1.setValueAt(totalPath, 1, 1);
                    temperature = new Double[numLines];
                    int count = 0;
                    while ((line = bufReader.readLine()) != null && line.length() != 0) {
                        String[] lineSplitValues = line.split(" ");
                        temperature[count] = Double.parseDouble(lineSplitValues[1]);
                        count++;
                    }
                    totalData.add(temperature);
                } else if (choosen.equals("Discharge")) {
                    table1.setValueAt(totalPath, 2, 1);
                    discharge = new Double[numLines];
                    int count = 0;
                    while ((line = bufReader.readLine()) != null && line.length() != 0) {
                        String[] lineSplitValues = line.split(" ");
                        discharge[count] = Double.parseDouble(lineSplitValues[1]);
                        count++;
                    }
                    totalData.add(discharge);
                }

            } catch (IOException ex) {
                Logger.getLogger(VisualFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_browseBtnActionPerformed

    private void doneLoadingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneLoadingActionPerformed
        // TODO add your handling code here:
        if (totalData.size() < numParamters) {
            JOptionPane.showMessageDialog(this, "Please provide all paramteres", "Incomplete data set", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try {
                isDataAvailable = true;
                int size = totalData.get(0).length;
                for (int i = 0; i < totalData.size(); i++) {
                    if (totalData.get(i).length != size) {
                        JOptionPane.showMessageDialog(this, "Please select equal length data for all parameters", "Incorrect length data", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                createPredictionData();
                VisualFrame.getInstance().getVisualizeBtn().setEnabled(true);
                VisualFrame.getInstance().getPredictBtn().setEnabled(true);
            } catch (IOException ex) {
                Logger.getLogger(ParameterBrowse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_doneLoadingActionPerformed

    /**
     */
    public static void CreateParameterBrowse() {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ParameterBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ParameterBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ParameterBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ParameterBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ParameterBrowse paramBrowse = new ParameterBrowse();
                paramBrowse.setVisible(true);
                DefaultTableModel model1;
                CustomizedTable customizedTable;
                JScrollPane pane1;

                model1 = new DefaultTableModel();
                paramBrowse.table1 = new JTable(model1);
                paramBrowse.table1.setDefaultRenderer(JLabel.class, new TableCellRenderer());
                pane1 = new JScrollPane(paramBrowse.table1);
                pane1.setSize(paramBrowse.getWidth() - 60, 202);
                pane1.setLocation(20, 80);

                customizedTable = new CustomizedTable();
                customizedTable.createTable(paramBrowse.table1, model1);
                customizedTable.addColumn("Parameter", model1);
                customizedTable.addColumn("File", model1);
                customizedTable.setLightHeader(paramBrowse.table1);

                Object[] rainFallRow = new Object[]{"Rainfall", null};
                Object[] temperatureRow = new Object[]{"Tempeature", null};
                Object[] dischargeRow = new Object[]{"Discharge", null};

                customizedTable.addRows(rainFallRow, model1);
                customizedTable.addRows(temperatureRow, model1);
                customizedTable.addRows(dischargeRow, model1);
                customizedTable.setLightBody(paramBrowse.table1);

                customizedTable.resizeColumn(paramBrowse.table1, 0, 40);
                customizedTable.resizeColumn(paramBrowse.table1, 1, 60);

                paramBrowse.table1.setRowHeight(59);

                paramBrowse.add(pane1);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton doneLoading;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox parameterCombo;
    // End of variables declaration//GEN-END:variables
}
