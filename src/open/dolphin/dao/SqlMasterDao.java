/*
 * SqlMasterDao.java
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
package open.dolphin.dao;

import java.sql.*;
import java.util.*;

import open.dolphin.infomodel.DiseaseEntry;
import open.dolphin.infomodel.MedicineEntry;
import open.dolphin.infomodel.ToolMaterialEntry;
import open.dolphin.infomodel.TreatmentEntry;
import open.dolphin.util.*;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public final class SqlMasterDao extends SqlDaoBean {
    
    private int totalCount;

    /** Creates a new instance of SqlMasterDao */
    public SqlMasterDao() {
    }
    
    public int getTotalCount() {
        return totalCount;
    }
        
    public ArrayList getByName(String master, String name, boolean startsWith, String serchClassCode,
                               String sortBy, String order) { 
        
        ArrayList results = null;
        
        if (master.equals("disease")) {
            results = getDiseaseByName(name, startsWith, sortBy, order);
            
        } else if (master.equals("medicine")) {
            if (serchClassCode.equals("20")) {
                results = getMedicineByName(name, startsWith, sortBy, order);
                
            } else {
                results = getInjectionByName(name, startsWith, sortBy, order);
            }
            
        } else if (master.equals("treatment")) {
            results = getTreatmentByName(name, startsWith, serchClassCode, sortBy, order);
            
        } else if (master.equals("tool_material")) {
            results = getToolMaterialByName(name, startsWith, sortBy, order);
            
        } else {
            //assert false : master + " table is not exist.";
            System.out.println(master + " table is not exist");
        }
        
        return results;
    }    


    /**
     * •a–¼ŒŸõ
     */
    private ArrayList getDiseaseByName(String text, boolean startsWith, String sortBy, String order) {

        Connection con = null;
        ArrayList collection = null;
        Statement st = null;

        // Constracts sql
        StringBuffer buf = new StringBuffer();
        buf.append("select byomeicd, byomei, byomeikana, icd10, haisiymd from tbl_byomei where ");
        
        String word = null;
        boolean codeSearch = false;
        
        if (StringTool.isAllKana(text)) {
            word = StringTool.toKatakana(text, false);
            buf.append("byomeikana like ");
        
        } else if (StringTool.isAllDigit(text)) {
            word = text;
            codeSearch = true;
            buf.append("byomeicd like ");
        
        } else {
            word = text;
            buf.append("byomei like ");
        }
        
        if (startsWith || codeSearch) {
            buf.append(addSingleQuote(word + "%"));
        }
        else {
            buf.append(addSingleQuote("%" + word + "%"));
        }

        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by byomeicd");
        }

        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            DiseaseEntry de;
            collection = new ArrayList();

            while (rs.next()) {
                de = new DiseaseEntry();
                de.setCode(rs.getString(1));        // Code
                de.setName(rs.getString(2));        // Name
                de.setKana(rs.getString(3));         // Kana
                de.setIcdTen(rs.getString(4));      // IcdTen
                de.setDisUseDate(rs.getString(5));  // DisUseDate
                collection.add(de);
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting disease: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);

        return collection;
    }

    /**
     * ˆã–ò•iŒŸõ
     */
     private ArrayList getMedicineByName(String text, boolean startsWith, String sortBy, String order) {

        Connection con = null;
        ArrayList collection = null;
        ArrayList outUse = null;
        Statement st = null;

        // Constracts sql
        //text = StringTool.toKatakana(text, true);
        StringBuffer buf = new StringBuffer();
        if (text.equals("*")) {
            //buf.append("select srycd,name,taniname,tensikibetu,ten,yakkakjncd,haisiymd from tbl_tensu where srycd like '6%'");
        } else {
            buf.append("select srycd, name, kananame, taniname, tensikibetu, ten, yakkakjncd, yukostymd, yukoedymd from tbl_tensu where srycd like '6%' and ");
        }
        
        String word = null;
        boolean codeSearch = false;
        
        if (StringTool.isAllKana(text)) {
            word = StringTool.toKatakana(text, true);
            buf.append("kananame like ");
        
        } else if (StringTool.isAllDigit(text)) {
            word = text;
            codeSearch = true;
            buf.append("srycd like ");
        
        } else {
            word = text;
            buf.append("name like ");
        }
        
        if (startsWith || codeSearch) {
            buf.append(addSingleQuote(word + "%"));
        }
        else {
            buf.append(addSingleQuote("%" + word + "%"));
        }
        
        buf.append(" and ykzkbn != ");
        buf.append(addSingleQuote("4"));

        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            MedicineEntry me;
            collection = new ArrayList();
            outUse = new ArrayList();

            while (rs.next()) {
                me = new MedicineEntry();
                me.setCode(rs.getString(1));        // Code
                me.setName(rs.getString(2));        // Name
                me.setKana(rs.getString(3));        // Name
                me.setUnit(rs.getString(4));        // Unit
                me.setCostFlag(rs.getString(5));    // Cost flag
                me.setCost(rs.getString(6));        // Cost
                me.setJNCD(rs.getString(7));        // JNCD
                me.setStartDate(rs.getString(8));  // startDate
                me.setEndDate(rs.getString(9));    // endDate 
                
                if (me.isInUse()) {
                    collection.add(me);
                } else {
                    outUse.add(me);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting medicine: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);

        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }
        
        outUse = null;
        
        return collection;
     }
     
     
    /**
     * ’ŽË–òŒŸõ
     */
     private ArrayList getInjectionByName(String text, boolean startsWith, String sortBy, String order) {

        Connection con = null;
        ArrayList collection = null;
        ArrayList outUse = null;
        Statement st = null;

        // Constracts sql
        StringBuffer buf = new StringBuffer();
        if (text.equals("*")) {
            //buf.append("select srycd,name,taniname,tensikibetu,ten,yakkakjncd,haisiymd from tbl_tensu where srycd like '6%'");
        } else {
            buf.append("select srycd, name, kananame, taniname, tensikibetu, ten, yakkakjncd, yukostymd, yukoedymd from tbl_tensu where srycd like '6%' and ");
        }
        
        String word = null;
        boolean codeSearch = false;
        
        if (StringTool.isAllKana(text)) {
            word = StringTool.toKatakana(text, true);
            buf.append("kananame like ");
        
        } else if (StringTool.isAllDigit(text)) {
            word = text;
            codeSearch = true;
            buf.append("srycd like ");
        
        } else {
            word = text;
            buf.append("name like ");
        }
        
        if (startsWith || codeSearch) {
            buf.append(addSingleQuote(word + "%"));
        }
        else {
            buf.append(addSingleQuote("%" + word + "%"));
        }
        
        buf.append(" and ykzkbn = ");
        buf.append(addSingleQuote("4"));
        
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            MedicineEntry me;
            collection = new ArrayList();
            outUse = new ArrayList();

            while (rs.next()) {
                me = new MedicineEntry();
                me.setCode(rs.getString(1));        // Code
                me.setName(rs.getString(2));        // Name
                me.setKana(rs.getString(3));        // Name
                me.setUnit(rs.getString(4));        // Unit
                me.setCostFlag(rs.getString(5));    // Cost flag
                me.setCost(rs.getString(6));
                me.setJNCD(rs.getString(7));
                me.setStartDate(rs.getString(8));  // start Date 
                me.setEndDate(rs.getString(9));    // end Date 
                
                if (me.isInUse()) {
                    collection.add(me);
                } else {
                    outUse.add(me);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting injection: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);
        
        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }

        outUse = null;
        
        return collection;
     }     


    /**
     * f—Ãsˆ×ŒŸõ
     */
     private ArrayList getTreatmentByName(String text, boolean startsWith, String orderClassCode, String sortBy, String order) {

        Connection con = null;
        ArrayList collection = null;
        ArrayList outUse = null;
        Statement st = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd, name, kananame, tensikibetu, ten, nyugaitekkbn, routekkbn, srysyukbn, hospsrykbn, yukostymd, yukoedymd from tbl_tensu where (srycd like '1%' or srycd like '002%') and ");
        
        String word = null;
        boolean codeSearch = false;
        
        if (StringTool.isAllKana(text)) {
            word = StringTool.toKatakana(text, true);
            buf.append("kananame like ");
        
        } else if (StringTool.isAllDigit(text)) {
            word = text;
            codeSearch = true;
            buf.append("srycd like ");
        
        } else {
            word = text;
            buf.append("name like ");
        }
        
        if (startsWith || codeSearch) {
            buf.append(addSingleQuote(word + "%"));
        }
        else {
            buf.append(addSingleQuote("%" + word + "%"));
        }
        
        if (orderClassCode != null) {
            String[] cClass = new String[]{"",""};
            int index = 0;
            StringTokenizer tokenizer = new StringTokenizer(orderClassCode,"-");
            while (tokenizer.hasMoreTokens()) {
                cClass[index++] = tokenizer.nextToken();
            }
            String min = cClass[0];
            String max = cClass[1];
            
            if ( (! min.equals("")) && max.equals("") ) {
            buf.append(" and srysyukbn = ");
            buf.append(addSingleQuote(min));
        
            } else if ((! min.equals("")) && (! max.equals("")) ) {
                buf.append(" and srysyukbn >= ");
                buf.append(addSingleQuote(min));
                buf.append(" and srysyukbn <= ");
                buf.append(addSingleQuote(max));
            }
        }

        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            TreatmentEntry te;
            collection = new ArrayList();
            outUse = new ArrayList();

            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana
                //te.setUnit(rs.getString(3));          // Unit
                te.setCostFlag(rs.getString(4));        // tensikibetu
                te.setCost(rs.getString(5));            // ten
                te.setInOutFlag(rs.getString(6));       // nyugaitekkbn
                te.setOldFlag(rs.getString(7));  	// routekkbn									// OldFlag
                te.setClaimClassCode(rs.getString(8));  // srysuykbn
                te.setHospitalClinicFlag(rs.getString(9)); // hospsrykbn
                te.setStartDate(rs.getString(10));     // start
                te.setEndDate(rs.getString(11));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting treatment: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);
        
        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }

        outUse = null;
        
        return collection;
     }
     
    /**
     * f—Ãsˆ×ŒŸõ
     */
     public ArrayList getByClaimClass(String master, String claimClass, String sortBy, String order) {
         
        Connection con = null;
        ArrayList collection = null;
        ArrayList outUse = null;
        Statement st = null;

        // Constracts sql
        String[] cClass = new String[]{"",""};
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(claimClass,"-");
        while (tokenizer.hasMoreTokens()) {
            cClass[index++] = tokenizer.nextToken();
        }
        String min = cClass[0];
        String max = cClass[1];
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd,name,kananame,tensikibetu,ten,nyugaitekkbn,routekkbn,srysyukbn,hospsrykbn, yukostymd, yukoedymd from tbl_tensu where srycd like '1%' and ");
        
        if ( (! min.equals("")) && max.equals("") ) {
            buf.append("srysyukbn = ");
            buf.append(addSingleQuote(min));
        
        } else if ((! min.equals("")) && (! max.equals("")) ) {
            buf.append("srysyukbn >= ");
            buf.append(addSingleQuote(min));
            buf.append(" and srysyukbn <= ");
            buf.append(addSingleQuote(max));
        }

        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }

        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            TreatmentEntry te;
            collection = new ArrayList();
            outUse = new ArrayList();

            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana
                //te.setUnit(rs.getString(3));          // Unit
                te.setCostFlag(rs.getString(4));        // tensikibetu
                te.setCost(rs.getString(5));            // ten
                te.setInOutFlag(rs.getString(6));       // nyugaitekkbn
                te.setOldFlag(rs.getString(7));  	// routekkbn									// OldFlag
                te.setClaimClassCode(rs.getString(8));  // srysuykbn
                te.setHospitalClinicFlag(rs.getString(9)); // hospsrykbn
                te.setStartDate(rs.getString(10));     // start
                te.setEndDate(rs.getString(11));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting treatment: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);

        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }
            
        outUse = null;
        
        return collection;
     } 
     
     /**
      * ŽB‰e•”ˆÊŒŸõ
      */
     public ArrayList getRadLocation(String master, String sortBy, String order) {

        Connection con = null;
        ArrayList collection = null;
        ArrayList outUse = null;
        Statement st = null;
        
        StringBuffer buf = new StringBuffer();
        buf.append("select srycd,name,kananame,srysyukbn,yukostymd, yukoedymd from tbl_tensu where srycd like '002%'");
        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            TreatmentEntry te;
            collection = new ArrayList();
            outUse = new ArrayList();
            
            while (rs.next()) {
                te = new TreatmentEntry();
                te.setCode(rs.getString(1));            // srycd
                te.setName(rs.getString(2));            // name
                te.setKana(rs.getString(3));            // kana
                //te.setUnit(rs.getString(3));          // Unit
                //te.setCostFlag(rs.getString(3));        // tensikibetu
                //te.setCost(rs.getString(4));            // ten
                //te.setInOutFlag(rs.getString(5));       // nyugaitekkbn
                //te.setOldFlag(rs.getString(6));  	// routekkbn									// OldFlag
                te.setClaimClassCode(rs.getString(4));  // srysuykbn
                //te.setHospitalClinicFlag(rs.getString(8)); // hospsrykbn
                te.setStartDate(rs.getString(5));     // start
                te.setEndDate(rs.getString(6));     // end
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting treatment: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);
        
        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }

        outUse = null;
        
        return collection;
     }
     

    /**
     * ŠíÞŒŸõ
     */
     private ArrayList getToolMaterialByName(String text, boolean startsWith, String sortBy, String order) {
         
         Connection con = null;
         ArrayList collection = null;
         ArrayList outUse = null;
         Statement st = null;

        // Constracts sql
        //text = StringTool.toKatakana(text, true);
        StringBuffer buf = new StringBuffer();
        //buf.append("select code,name,unit,costFlag,cost,freqFlag from tool_material where kana like ");
        buf.append("select srycd, name, kananame, taniname, tensikibetu, ten ,yukostymd, yukoedymd from tbl_tensu where srycd like '7%' and ");
        
        String word = null;
        boolean codeSearch = false;
        
        if (StringTool.isAllKana(text)) {
            word = StringTool.toKatakana(text, true);
            buf.append("kananame like ");
        
        } else if (StringTool.isAllDigit(text)) {
            word = text;
            codeSearch = true;
            buf.append("srycd like ");
        
        } else {
            word = text;
            buf.append("name like ");
        }
        
        if (startsWith || codeSearch) {
            buf.append(addSingleQuote(word + "%"));
        }
        else {
            buf.append(addSingleQuote("%" + word + "%"));
        }

        String orderBy = getOrderBy(sortBy, order);
        if (orderBy != null) {
            buf.append(orderBy);
        } else {
            buf.append(" order by srycd");
        }
        String sql = buf.toString();
        printTrace(sql);

        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            // ValueObject
            ToolMaterialEntry te;
            collection = new ArrayList();
            outUse = new ArrayList();

            while (rs.next()) {
                te = new ToolMaterialEntry();
                te.setCode(rs.getString(1));        // Code
                te.setName(rs.getString(2));        // name
                te.setKana(rs.getString(3));        // kana
                te.setUnit(rs.getString(4));        // Unit
                te.setCostFlag(rs.getString(5));    // Cost flag
                te.setCost(rs.getString(6));        // Cost 
                te.setStartDate(rs.getString(7));        // start  
                te.setEndDate(rs.getString(8));        // end 
                
                if (te.isInUse()) {
                    collection.add(te);
                } else {
                    outUse.add(te);
                }
            }
            rs.close();

        } catch (SQLException e) {
            processError(con, collection, "SQLException while getting tool material: " + e.toString());
        }

        closeStatement(st);
        closeConnection(con);
        
        //Collections.sort(collection);
        int count = outUse.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                collection.add(outUse.get(i));
            }
        }
        outUse = null;
        
        return collection;
     }
     
     private String getOrderBy(String sortBy, String order) {
         
         StringBuffer buf = null;
         
         if (sortBy != null) {
             buf = new StringBuffer();
             buf.append(" order by ");
             buf.append(sortBy);
         }
         
         if (order != null) {
             buf.append(" ");
             buf.append(order);
         }
         
         return (buf != null) ? buf.toString() : null;
     }
}