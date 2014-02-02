/*
 * CodeNameObject.java
 * Copyright (C) 2005 Digital Globe, Inc. All rights reserved.
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

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public class NameValuePair {
    
    private String value;
    private String name;
    
    public static int getIndex(NameValuePair test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public static int getIndex(String test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i].getValue())) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public NameValuePair() {
    }
    
    public NameValuePair(String name, String value) {
        this();
        setName(name);
        setValue(value);
    }
    
    public void setValue(String code) {
        this.value = code;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    public int hashCode() {
        return value.hashCode() + 15;
    }
    
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.equals(otherValue);
        }
        return false;
    }
    
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.compareTo(otherValue);
        }
        return -1;
    }
}
