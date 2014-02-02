/*
 * Dolphin.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2004 Digital Globe, Inc. All rights reserved.
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

import java.util.Hashtable;

import open.dolphin.plugin.IPluginContext;
import open.dolphin.plugin.InitialPluginContext;

/**
 * Dolphin
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class Dolphin  {
    
    /** Create new Dolphin */
    public Dolphin() {
    }
    
    /**
     * Starts Dolphin.
     */
    public void start() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("open.dolphin.client.DelegaterFactory");
        sb.append(":");
        sb.append("open.dolphin.plugin.helper.ResourceFactory");
        String factories = sb.toString();
        
        // InitialContextFactory ‚ÖŠÂ‹«‚ð’ñ‹Ÿ‚·‚é
        Hashtable<String,String> env = new Hashtable<String,String>(7, 0.75f);
        env.put("java.naming.factory.initial",
                "open.dolphin.plugin.PluginContextFactory");
        env.put("java.naming.factory.object", factories);
        
        try {
            IPluginContext ctx = new InitialPluginContext(env);
            
            ClientContextStub stub = new ClientContextStub(ctx);
            ClientContext.setClientContextStub(stub);
            new MainWindow().start();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Starts Dolphin App.
     */
    public static void main(String[] args) {
        
        new Dolphin().start();
    }
}