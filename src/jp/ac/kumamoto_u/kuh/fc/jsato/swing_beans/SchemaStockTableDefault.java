/*
 * SchemStockTable.java
 *
 * Created on 2002/06/20, 8:03
 */

package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import jp.ac.kumamoto_u.kuh.fc.jsato.*;
import netscape.ldap.*;

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.net.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.sun.image.codec.jpeg.*;

import java.io.*;
import java.text.*;
import javax.media.jai.*;
import java.awt.image.*;
import java.awt.image.renderable.*;

public class SchemaStockTableDefault extends SchemaStockTable {
    public SchemaStockTableDefault() {
        super();
    }
    
    public void addButtons() {
        // do nothing
    }
    
    public String getSchemaDirectory() {
        return null;
    }
}