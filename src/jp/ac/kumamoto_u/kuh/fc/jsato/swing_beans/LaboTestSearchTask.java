/*
 * LaboTestSearchTask.java
 *
 * Created on 2001/12/10, 3:49
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import swingworker.*;
import netscape.ldap.*;
import java.util.*;

import open.dolphin.project.*;

/**
 *
 * @author  Junzo SATO
 * @version This calss is based on LongTask.java from java.sun.com example.
 */
public class LaboTestSearchTask {
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;

    private String patientId = null;
    private boolean isLocalId = false;
    private String fromDate = null;
    private String toDate = null;

    /** Creates new LaboTestSearchTask */
    public LaboTestSearchTask(
        String patientId, boolean isLocalId,
        String fromDate, String toDate ) {
        
        this.patientId = patientId;
        this.isLocalId = isLocalId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        
        //Compute length of task...
        lengthOfTask = 1000;
    }

    /**
     * Called to start the task.
     */
    void go() {
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
        worker.start();
    }

    /**
     * Called to find out how much work needs to be done.
     */
    int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called to find out how much has been done.
     */
    int getCurrent() {
        return current;
    }

    void stop() {
        current = lengthOfTask;
    }

    /**
     * Called to find out if the task has completed.
     */
    boolean done() {
        if (current >= lengthOfTask)
            return true;
        else
            return false;
    }

    String getMessage() {
        return statMessage;
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    
    Vector headerV = null;
    Vector columns = new Vector();

    Vector getHeaderV() {return headerV;}
    Vector getColumns() {return columns;}
    
    class ActualTask {
        String host = "";
        int port = 389;
        String bindDN = "";
        String passwd = "";
        
        String baseDN = "o=Dolphin";
        String laboTestDN = "cn=LaboTest," + baseDN;
        String patientDN = null;
        
        Vector row = null;
        
        ActualTask() {
            //ServerConnection sc = Karte.getServerConnection();
            //if (sc != null) {
                host = Project.getHostAddress();
                port = Project.getHostPort();
                bindDN = Project.getAuthenticationDN();
                passwd = Project.getPasswd();
            //} else {  
            if (host == null || bindDN == null || passwd == null) {    
                statMessage = "サーバー設定の取得に失敗しました。";
                try {Thread.sleep(100);} catch (Exception e){}
                stop();
                return;
            }
            
            statMessage = "サーバーへ接続中...";
            try {Thread.sleep(100);} catch (Exception e){}
            
            LDAPConnection ld = getConnection();
            if (ld == null) {
                statMessage = "サーバー接続に失敗しました。";
                try {Thread.sleep(100);} catch (Exception e){}
                stop();
                return;
            }

            if (AsyncAuthenticate(ld) == false) {
                return;
            }
            
            patientDN = searchPatient();
            if (patientDN == null) {
                statMessage = "該当するデータはありません。";
                try {Thread.sleep(100);} catch (Exception e){}
                stop();
                return;
            }
            
            headerV = new Vector();
            columns = new Vector();
            // search laboModules which have laboSampleTime between fromDate and toDate.
            searchLaboModules();
            
            // notify the successful message
            // NOTE: it is very important that this message is received by the 
            // searchTimer in LaboTestBean to check the result of this ActualTask
            statMessage = "データ取得が終了しました。";
            try {Thread.sleep(100);} catch (Exception ee){}
            stop();
        }

        // ldap connection 
        private LDAPConnection conn = null;// never use conn directly
        
        public LDAPConnection getConnection() {
            if (conn != null && conn.isConnected()) {
                return conn;
            }

            try {
                conn = new LDAPConnection();
                conn.connect( host, port, bindDN, passwd );
                return conn;
            } catch (LDAPException e) {
                System.out.println(e.toString());
                return null;
            }
        }

        public void disconnectLDAP() {
            if (conn != null && conn.isConnected()) {
                try {
                    conn.disconnect();
                    conn = null;
                } catch (LDAPException e) {
                    System.out.println(e.toString());
                }
            }
        }        
        
        // authentication
        public boolean AsyncAuthenticate(LDAPConnection ld) {          
            try {
                LDAPResponseListener r = ld.bind(
                        bindDN,//"uid=directorymanager,ou=Managers,o=Dolphin",
                        passwd,//"secret",
                        (LDAPResponseListener)null);
                // Wait until it completes
                while ( r.isResponseReceived() == false ) {
                    statMessage = "サーバーからの応答を待っています...";
                    try {Thread.sleep(100);} catch (Exception e){}
                }
                LDAPResponse response = r.getResponse();
                // Did the authentication succeed?
                int resultCode = response.getResultCode();
                if (resultCode != LDAPException.SUCCESS) {
                    // Do what the synchronous interface does - throw
                    // an exception
                    String err = LDAPException.errorCodeToString( resultCode );
                    statMessage = err;
                    throw new LDAPException( err,
                                               resultCode,
                                               response.getErrorMessage(),
                                               response.getMatchedDN()
                    );
                } else {
                    statMessage = "サーバー認証に成功しました。";
                    try {Thread.sleep(100);} catch (Exception eee){}
                }
                return true;
            } catch (LDAPException le) {
                le.printStackTrace();
                statMessage = "サーバー認証に失敗しました。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return false; 
            }
        }
        
        // search
        public String searchPatient() {
            String dn = laboTestDN;
            String facilityOrLocal = "";
            if ( isLocalId ) {
                facilityOrLocal = "local";
            } else {
                facilityOrLocal = "facility";
            }

            String pid = facilityOrLocal + "__" + patientId.replaceAll(",","");
            String filter = "(mmlPid=" + pid + ")";
            filter = filter.replaceAll("-","");
            filter = filter.replaceAll(":","");

            return searchDN(dn, filter);
        }

        public void searchLaboModules() {
            // get connection
            LDAPConnection ld = getConnection();
            if (ld == null) {
                statMessage = "サーバー接続に失敗しました。";
                try {Thread.sleep(100);} catch (Exception e){}
                stop();
                return;
            }

            String dn = patientDN;        
            String filter = "(&(objectclass=laboModule)(&(laboSampleTime>=" + 
                fromDate + "T00:00:00)(laboSampleTime<=" + toDate + "T23:59:59)))";

            String[] attrs = {
                "laboSampleTime","laboRegistTime","laboReportTime","mmlConfirmDate",
                "laboReportStatus","laboReportMemo","laboReportFreeMemo",
                "laboTestCenterName","laboSet"
            };
            /*
             String[] names = {
                "検体採取日時","検査登録日時","報告日時","確定日",
                "報告状態","報告メモ","報告フリーメモ","検査施設名","セット名"
            };
             */
            String[] names = {
                "採取","登録","報告","確定",
                "","","","",""
            };

            //printlnStatus("trying to search from the base: " + dn);
            //printlnStatus("filter: " + filter);

            String[] sortattrs = {
                "laboSampleTime","laboRegistId"
            };
            boolean[] ascend = {
                true, true
            };

            LDAPSearchResults results = null;
            try {
                statMessage = "データ検索中...";
                try {Thread.sleep(100);} catch (Exception ee){}

                results = ld.search(dn, netscape.ldap.LDAPConnection.SCOPE_ONE, filter, attrs, false);
                //===============================================================
                if (results != null) {
                    results.sort(new LDAPCompareAttrNames(sortattrs, ascend));
                }
                //===============================================================
            } catch (LDAPException e) {
                statMessage = "該当するデータはありません。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return;
            }

            // handle laboModules
            while ( results.hasMoreElements() ) {    
                LDAPEntry entry = null;
                try {
                    entry = (LDAPEntry)results.next();
                } catch (LDAPException e) {
                    continue;
                }

                // get each entry of the laboModule for this patient
                String moduleDN = entry.getDN();
                statMessage = moduleDN;
                try {Thread.sleep(100);} catch (Exception ee){}
                //printlnStatus(moduleDN);
                
                row = new Vector();////////////////////////////
                //-----------------------------------------------------
                for (int i = 0; i < attrs.length; ++i) {
                    statMessage = names[i];
                    try {Thread.sleep(100);} catch (Exception ee){}

                    LDAPAttribute attr = entry.getAttribute(attrs[i]);
                    if (attr == null) {
                        //printlnStatus(names[i] + ": is null.");
                        continue;
                    }

                    //===================================================
                    if (attrs[i].equals("laboSampleTime")) {
                    // HEADER //
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) {
                            headerV.add(names[i] + ": " + st);
                        } else {
                            headerV.add(names[i] + ": ");
                        }
                        //printlnStatus(names[i] + ": " + st);

                    } else {
                    // COLUMN //
                        // get values of attribute attrs[i] if the attribute has multiple values.
                        Enumeration enumVals = attr.getStringValues();
                        while ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            String es = (String)enumVals.nextElement();
                            String pre = "";
                            if (names[i].equals("") == false) {
                                pre = names[i] + ": ";
                            }
                            if (es != null) {
                                row.add(pre + es);
                            } else {
                                row.add(pre);
                            }
                            //printlnStatus(names[i] + ": " + es);
                        }
                    }
                    //===================================================
                }
                //-----------------------------------------------------
                // go deeper...never forget to add result to the row! :-)
                searchLaboTests(moduleDN);

                columns.add(row);////////////////////////////
            }
        }

        public void searchLaboTests(String moduleDN) {
            // get connection
            LDAPConnection ld = getConnection();
            if (ld == null) {
                //printlnMessage("*** Couldn't search DN. Connection is null.");
                statMessage = "サーバー接続に失敗しました。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return;
            }

            //==================================================
            // Asyncronous Authentication
            //==================================================

            String dn = moduleDN;
            String filter = "(objectclass=laboTest)";

            String[] attrs = {
                "laboSpecimenName","laboSpecimenMemo","laboSpecimenFreeMemo"
            };
             String[] names = {
                "検体名","検体メモ","検体フリーメモ"
            };

            //printlnStatus("trying to search from the base: " + dn);
            //printlnStatus("filter: " + filter);

            String[] sortattrs = {
                "laboSpecimenCodeId","laboSpecimenCode"
            };
            boolean[] ascend = {
                true, true
            };

            LDAPSearchResults results = null;
            try {
                statMessage = "データ検索中...";
                try {Thread.sleep(100);} catch (Exception ee){}

                results = ld.search(dn, netscape.ldap.LDAPConnection.SCOPE_ONE, filter, attrs, false);
                //===============================================================
                if (results != null) {
                    results.sort(new LDAPCompareAttrNames(sortattrs, ascend));
                }
                //===============================================================
            } catch (LDAPException e) {
                //printlnMessage("*** No laboTest was found: " + e.getLDAPResultCode());
                statMessage = "該当するデータはありません。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return;
            }

            while ( results.hasMoreElements() ) {
                LDAPEntry entry = null;
                try {
                    entry = (LDAPEntry)results.next();
                } catch (LDAPException e) {
                    continue;
                }

                // get each entry of the laboModule for this patient
                String testDN = entry.getDN();
                statMessage = testDN;
                try {Thread.sleep(100);} catch (Exception ee){}
                //-----------------------------------------------------
                for (int i = 0; i < attrs.length; ++i) {
                    statMessage = names[i];
                    try {Thread.sleep(100);} catch (Exception ee){}

                    LDAPAttribute attr = entry.getAttribute(attrs[i]);
                    if (attr == null) {
                        //attr names[i] is empty
                        continue;
                    }

                    // get values of attribute attrs[i]
                    Enumeration enumVals = attr.getStringValues();
                    while ( (enumVals != null) && enumVals.hasMoreElements() ) {
                        String se = (String)enumVals.nextElement();
                        if (se != null) {
                            row.add(/*names[i] + ": " + */se);
                        } else {
                            row.add(/*names[i] + ": " + */"");
                        }
                    }
                }
                //-----------------------------------------------------
                // go deeper... never forget to add result to the row! :-):-)
                searchLaboItems(testDN);
            }
        }

        public void searchLaboItems(String testDN) {
            // get connection
            LDAPConnection ld = getConnection();
            if (ld == null) {
                //printlnMessage("*** Couldn't search DN. Connection is null.");
                statMessage = "サーバー接続に失敗しました。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return;
            }

            String dn = testDN;
            String filter = "(objectclass=laboItem)";

            String[] attrs = {
                "laboItemName","laboValue","laboUnit","laboLow","laboUp","laboNormal","laboOut",
                "laboItemMemo","laboItemFreeMemo",
                "laboExtRefHref"
            };
            String[] names = {
                "検査項目","値","単位","下限値","上限値","基準値","異常値フラグ",
                "項目メモ","項目フリーメモ","外部参照ファイル"
            };

            //printlnStatus("trying to search from the base: " + dn);
            //printlnStatus("filter: " + filter);

            String[] sortattrs = {
                "laboItemCode","laboItemCodeId"
            };
            boolean[] ascend = {
                true, true
            };

            LDAPSearchResults results = null;
            try {
                statMessage = "データ検索中...";
                try {Thread.sleep(100);} catch (Exception ee){}

                results = ld.search(dn, netscape.ldap.LDAPConnection.SCOPE_ONE, filter, attrs, false);
                //===============================================================
                if (results != null) {
                    results.sort(new LDAPCompareAttrNames(sortattrs, ascend));
                }
                //===============================================================
            } catch (LDAPException e) {
                //printlnMessage("*** No laboItem was found: " + e.getLDAPResultCode());
                statMessage = "該当するデータはありません。";
                try {Thread.sleep(100);} catch (Exception ee){}
                stop();
                return;
            }

            while ( results.hasMoreElements() ) {
                LDAPEntry entry = null;
                try {
                    entry = (LDAPEntry)results.next();
                } catch (LDAPException e) {
                    continue;
                }

                // get each entry of the laboModule for this patient
                String itemDN = entry.getDN();
                statMessage = itemDN;
                try {Thread.sleep(100);} catch (Exception ee){}
                //printlnStatus(itemDN);
                //-----------------------------------------------------
                int len = attrs.length;
                String strItemName = "", strValue = "", strUnit = "",
                       strLow = "", strUp = "", strNormal = "", strOut = "";
                Vector memos = new Vector();
                for (int i = 0; i < len; ++i) {
                    statMessage = names[i];
                    try {Thread.sleep(100);} catch (Exception ee){}

                    LDAPAttribute attr = entry.getAttribute(attrs[i]);
                    if (attr == null) {
                        //printlnStatus(names[i] + ": is null.");
                        continue;
                    }

                    if (attrs[i].equals("laboItemName")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strItemName = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboValue")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strValue = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboUnit")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strUnit = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboLow")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strLow = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboUp")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strUp = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboNormal")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strNormal = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else if (attrs[i].equals("laboOut")) {
                        String st = (String)attr.getStringValues().nextElement();
                        if (st != null) strOut = st;
                        //printlnStatus(names[i] + ": " + st);
                    } else {
                        // get values of attribute attrs[i]
                        Enumeration enumVals = attr.getStringValues();
                        while ( (enumVals != null) && enumVals.hasMoreElements() ) {
                            String se = (String)enumVals.nextElement();
                            if (se != null) {
                                //row.add(names[i] + ": " + se);
                                memos.add(names[i] + ": " + se);
                            } else {
                                //row.add(names[i] + ": ");
                            }
                            //printlnStatus(names[i] + ": " + se);
                        }
                    }
                }

                /////////////////////////////////////////////////////////////////////////////////////////
                String ss = strItemName + ": " + strValue + " " + strUnit;
                MyTableCellData d = new MyTableCellData(
                    ss,
                    strLow, strUp, strNormal, strOut, 
                    memos
                );
                row.add(d);
                /////////////////////////////////////////////////////////////////////////////////////////
                //-----------------------------------------------------
                // find extRef here...
                //-----------------------------------------------------
            }
        }

        public String searchDN(String dn, String filter) {
            // get connection
            LDAPConnection ld = getConnection();
            if (ld == null) {
                return null;
            }

            String[] attrs = null;
            LDAPSearchResults results = null;
            try {
                results = ld.search(dn, netscape.ldap.LDAPConnection.SCOPE_ONE, filter, attrs, false);
            } catch (LDAPException e) {
                return null;
            }

            if ( results.hasMoreElements() == false ) {
                return null;
            }

            // get first entry we found
            LDAPEntry entry = null;
            try {
                entry = (LDAPEntry)results.next();
            } catch (LDAPException e) {
                return null;
            }
            return entry.getDN();
        }
    }// EOF class ActualTask
}
