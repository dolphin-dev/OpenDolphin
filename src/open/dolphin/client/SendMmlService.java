/*
 * SendMmlService.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import javax.swing.*;
import javax.swing.table.*;

import open.dolphin.infomodel.Schema;
import open.dolphin.plugin.*;
import open.dolphin.plugin.event.*;
import open.dolphin.project.*;
import open.dolphin.table.*;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * MML 送信サービス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class SendMmlService extends AbstractFramePlugin implements MmlMessageListener {
    
    // 送信リストテーブルのカラム名
    private String[] columnNames = {
        "生成時間", "患者ID", "患者氏名", "性別", "文書タイトル", "内容", "送信済"
    };
    
    // 送信リストテーブルのカラム幅
    private int[] columnWidth = {150, 100, 100, 30, 200, 80, 50};
    
    // 送信テーブルモデル
    private ArrayListTableModel tableModel;
    
    // CSGW への書き込みパス
    private String csgwPath;
    
    // MML Encoding
    private String encoding;
    
    // Work Queue
    private LinkedList queue;
    
    private JPanel ui;
    
    private Kicker kicker;
    
    /** Creates new SendMmlService */
    public SendMmlService() {
    }
    
    public String getCSGWPath() {
        return csgwPath;
    }
    
    public void setCSGWPath(String val) {
        csgwPath = val;
        File directory = new File(csgwPath);
        if (! directory.exists()) {
            directory.mkdirs();
        }
    }
    
    public void stop() {
        if (kicker != null) {
            kicker.interrupt();            
        }
        super.stop();    
    }
    
    public void initComponent() {
		ui = createUI();
		Dimension size = new Dimension(740, 300);
		centerFrame(size, ui);
    }
    
    public void start() {
           
        // CSGW 書き込みパスを設定する
        setCSGWPath(Project.getCSGWPath());
        
        encoding = Project.getMMLEncoding();
        
        // 送信キューを生成する
        queue = new LinkedList();
        kicker = new Kicker();
        kicker.start();
    }
    
    private JPanel createUI() {
        
        tableModel = new ArrayListTableModel(columnNames, 10) {
            
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            public Class getColumnClass (int col) {
                return (col == 6) ? java.lang.Boolean.class : java.lang.String.class;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scroller = new JScrollPane(table, 
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TableColumn column = null;
        int len = columnNames.length;
        for (int i = 0; i < len; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidth[i]);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroller, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        return panel;
    }
        
    public synchronized void mmlMessageEvent(MmlMessageEvent e) {        
        queue.addLast(e);      
        String id = e.getPatientId();
        String name = e.getPatientName();
        String sex = e.getPatientSex();
        String title = e.getTitle();
        String contentInfo = e.getContentInfo();
        String timeStamp = e.getConfirmDate();
        tableModel.addRow(new Object[]{timeStamp, id, name, sex, title, contentInfo, new Boolean("false")});
        e.setNumber(tableModel.getDataCount() -1);
        notify();
    }
    
    public synchronized Object getMML() throws InterruptedException {        
       while (queue.size() == 0) {
          wait();
       }
       return queue.removeFirst();
    }
    
    protected String getCSGWPathname(String fileName, String ext) {
        StringBuffer buf = new StringBuffer();
        buf.append(csgwPath);
        buf.append(File.separator);
        buf.append(fileName);
        buf.append(".");
        buf.append(ext);
        return buf.toString();
    }          
    
    protected class Kicker extends Thread {
        
        public void run() {
            
            try {
            
                String instance;
                String groupId;
                Schema[] schemas;
                int num;
                String dest;
                String temp;            
                File f;
                BufferedOutputStream writer;
                byte[] bytes;

                while (! interrupted()) {

                    try {                     
                        // MML パッケージを取得                  
                        MmlMessageEvent pkg = (MmlMessageEvent)getMML();
                        groupId = pkg.getGroupId();
                        instance = pkg.getMmlInstance();
                        num = pkg.getNumber();
                        schemas = pkg.getSchema();

                        // ファイル名を生成                    
                        dest = getCSGWPathname(groupId, "xml");
                        temp = getCSGWPathname(groupId, "xml.tmp");            
                        f = new File(temp);

                        // インスタンスを UTF8 で書き込む
                        writer = new BufferedOutputStream(new FileOutputStream(f));
                        bytes = instance.getBytes(encoding);
                        writer.write(bytes);
                        writer.flush();
                        writer.close();     

                        // 書き込み終了後にリネームする (.tmp -> .xml)
                        f.renameTo(new File(dest));

                        // 画像を送信する
                        if (schemas != null) {             
                            int count = schemas.length;
                            for (int i = 0; i < count; i++) {
                                dest = csgwPath + File.separator + schemas[i].getFileName();
                                temp = dest + ".tmp";
                                f = new File(temp);
                                writer = new BufferedOutputStream(new FileOutputStream(f));
                                writer.write(schemas[i].getJPEGByte());
                                writer.flush();
                                writer.close();

                                // Rename
                                f.renameTo(new File(dest));                                   
                            }
                        }

                        // 送信チェック
                        Object[] o = tableModel.getRowData(num);
                        if (o != null) {
                            o[6] = new Boolean("true");
                            tableModel.fireTableCellUpdated(num, 6);
                        }
                    }
                    catch (IOException e) {
                        System.out.println("Exception while sending the MML instance: " + e.toString());
                        //e.printStackTrace();
                    }
                }
            }
            catch (InterruptedException ie) {
            }
        }
    }
}