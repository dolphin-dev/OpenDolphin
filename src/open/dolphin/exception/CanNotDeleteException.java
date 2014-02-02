/*
 * Copyright(C) 2007 Digital Globe, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package open.dolphin.exception;

import javax.persistence.PersistenceException;

/**
 * CanNotDeleteException
 *
 * @author Kazushi Minagawa
 */
public class CanNotDeleteException extends PersistenceException {
    
    private static final long serialVersionUID = -4477191043594458879L;
    
    private String message;
    
    public CanNotDeleteException(String message) {
        super(message);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }   
}