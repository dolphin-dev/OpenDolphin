/*
 * DocumentState.java
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

/**
 *
 * @author  kazm
 */
public class DocumentState {

    public static final int INITIAL_EVENT   = 0;
    public static final int SELECTED_EVENT  = 1;
    public static final int DIRTY_EVENT     = 2;
    public static final int UNEDITABLE_EVENT = 3;

    private final InitialState initialState = new InitialState();

    private final DirtyState dirtyState = new DirtyState();

    //private final SelectedState selectedState = new SelectedState();

    //pivate final UneditableState uneditableState = new UneditableState();

    public DocumentState() {
    }

    public static DocumentState start() {
        DocumentState d = new DocumentState();
        return d.initialState;
    }

    public DocumentState processEvent(int event) {
        return null;
    }

    protected void enter() {
    }    

    private class InitialState extends DocumentState {

        public InitialState() {
        }

        public DocumentState processEvent(int event) {

            switch (event) {

                case SELECTED_EVENT:
                    //selectedState.enter();
                    return this;

                case DIRTY_EVENT:
                    dirtyState.enter();
                    return dirtyState;
            }
            
            return null;
        }

        protected void enter() {
            //newDiagnosisButton.setEnabled(true);
        }
    }

    private class DirtyState extends DocumentState {

        public DirtyState() {
        }

        public DocumentState processEvent(int event) {

            switch (event) {

                case SELECTED_EVENT:
                    //selectedState.enter();
                    return this;

                case UNEDITABLE_EVENT:
                    //uneditableState.enter();
                    return this;
            }
            
            return null;
        }

        protected void enter() {
            //dirty = true;
            //controlMenu();
        }
    }
}