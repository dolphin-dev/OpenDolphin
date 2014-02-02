/*
 * ModuleModel.java
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
package open.dolphin.infomodel;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * ModuleModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Entity
@Table(name = "d_module")
public class ModuleModel extends KarteEntryBean implements Stamp {
    
    private static final long serialVersionUID = -8781968977231876023L;;
    
    @Embedded
    private ModuleInfoBean moduleInfo;
    
    @Transient
    private IInfoModel model;
    
    @Lob
    @Column(nullable=false)
    private byte[] beanBytes;
    
    @ManyToOne
    @JoinColumn(name="doc_id", nullable=false)
    private DocumentModel document;
    
    /**
     * ModuleModelオブジェクトを生成する。
     */
    public ModuleModel() {
        setModuleInfo(new ModuleInfoBean());
    }
    
    public DocumentModel getDocument() {
        return document;
    }
    
    public void setDocument(DocumentModel document) {
        this.document = document;
    }
    
    /**
     * モジュール情報を設定する。
     * @param moduleInfo モジュール情報
     */
    public void setModuleInfo(ModuleInfoBean moduleInfo) {
        this.moduleInfo = moduleInfo;
    }
    
    /**
     * モジュール情報を返す。
     * @return モジュール情報
     */
    public ModuleInfoBean getModuleInfo() {
        return moduleInfo;
    }
    
    /**
     * モジュールの情報モデル（実体のPOJO)を設定する。
     * @param model モデル
     */
    public void setModel(IInfoModel model) {
        this.model = model;
    }
    
    /**
     * モジュールの情報モデル（実体のPOJO)を返す。
     * @return モデル
     */
    public IInfoModel getModel() {
        return model;
    }
    
    /**
     * モジュールの永続化バイト配列を返す。
     * @return モジュールの永続化バイト配列
     */
    public byte[] getBeanBytes() {
        return beanBytes;
    }
    
    /**
     * モジュールの永続化バイト配列を設定する。
     * @param beanBytes モジュールの永続化バイト配列
     */
    public void setBeanBytes(byte[] beanBytes) {
        this.beanBytes = beanBytes;
    }
    
    /**
     * ドキュメントに現れる順番で比較する。
     * @return 比較値
     */
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            ModuleInfoBean moduleInfo1 = getModuleInfo();
            ModuleInfoBean moduleInfo2 = ((ModuleModel)other).getModuleInfo();
            return moduleInfo1.compareTo(moduleInfo2);
        }
        return -1;
    }
}
