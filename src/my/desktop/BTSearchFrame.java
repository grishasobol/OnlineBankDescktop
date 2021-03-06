/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.desktop;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dmitry
 */
public class BTSearchFrame extends javax.swing.JFrame {
    private final static String SEPARATOR = ":";
    
    /**
     * Creates new form BTSearchFrame
     */
    private DefaultTableModel model;
    private String btAddress;
    private final HTTPSClient httpsClient;
    
    public BTSearchFrame(HTTPSClient httpsClient) {
        initComponents();
        
        DesktopUI.btClient = new BTClient();
        this.httpsClient = httpsClient;
        
        if(!DesktopUI.btDeviceList.isEmpty()){
            model = (DefaultTableModel) jTable1.getModel();
            model.getDataVector().removeAllElements();
            model.fireTableDataChanged();
            
            for(Entry<String,String> deviceInfo : DesktopUI.btDeviceList){
                model.addRow(new String[]{deviceInfo.getKey(), deviceInfo.getValue()});
            }
        }
    }

    public String getBtAddress() {
        return btAddress;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setText("BT Devices");
        jLabel1.setToolTipText("");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Address"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("CANCEL");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        startButton.setLabel("SEARCH");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(okButton)
                            .addComponent(cancelButton)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startButton)))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jScrollPane1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        boolean connectTo = DesktopUI.btClient.connectTo(btAddress); // connect
        if (!connectTo) { System.exit(1); }
        System.out.println(" My address: " + DesktopUI.btClient.localAddress());

        String pubkey = "getPubKey()";
        String pubkey_msg = "pubkey" + SEPARATOR + pubkey;
        boolean sendS = DesktopUI.btClient.sendS(pubkey_msg);
        if (!sendS) { System.exit(1); }        
        
        Thread t;
        t = new Thread(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(null, "You must see the same code: " + DesktopUI.btClient.localAddress());
            }
        });
        t.start();

        String receiveS = DesktopUI.btClient.receiveS();
        if (receiveS == null) { System.exit(1); }
        String[] receive = receiveS.split(SEPARATOR);
        switch (receive[0].toLowerCase()){
            case "bad_request":
                JOptionPane.showMessageDialog(null, "The initialization was cancelled by manager");
                DesktopUI.btClient.disconnect(); 
                break;
            case "pubkey":
                System.out.println(" Device public key: " + receive[1]);
                DesktopUI.pubkey = receive[1];
                DesktopUI.btClient.disconnect(); 
        
                DesktopUI.btCurrentAddress = btAddress;             

                this.setVisible(false);
                this.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Wrong output returned by manager");
                break;
        }        

        t.interrupt();
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        model = (DefaultTableModel) jTable1.getModel();
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        
        if(!DesktopUI.btDeviceList.isEmpty()){
            for(Entry<String,String> deviceInfo : DesktopUI.btDeviceList){
                model.addRow(new String[]{deviceInfo.getKey(), deviceInfo.getValue()});
            }
        }
        DesktopUI.btClient.searchDevices(false); // sync search
        ArrayList<BTClient.DeviceInfo> deviceInfoArrayList = DesktopUI.btClient.deviceList(); // list
        for (BTClient.DeviceInfo deviceInfo : deviceInfoArrayList) {
            Entry<String, String> entry = new AbstractMap.SimpleEntry<>(deviceInfo.name, deviceInfo.address);
            if(!DesktopUI.btDeviceList.contains(entry)) {
                model.addRow(new String[]{deviceInfo.name, deviceInfo.address});
                System.out.println(deviceInfo.name + " (" + deviceInfo.address + ")");
            }
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if(row != -1){
            int selectedRow = jTable1.getSelectedRow();
            Entry<String, String> entry = new AbstractMap.SimpleEntry<>((String) jTable1.getValueAt(selectedRow, 0),
                    (String) jTable1.getValueAt(selectedRow, 1));
            DesktopUI.btDeviceList.add(entry);
            btAddress = entry.getValue();
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
