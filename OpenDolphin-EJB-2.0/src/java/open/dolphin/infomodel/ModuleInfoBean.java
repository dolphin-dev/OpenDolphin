/*
 * ModuleInfo.java
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
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
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Stamp 及び Module の属性を保持するクラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Embeddable
public class ModuleInfoBean extends InfoModel implements StampInfo, Comparable, java.io.Serializable {
    
    /** Module 名: StampTree、 オーダ履歴当に表示する名前 */
    @Column(nullable=false)
    private String name;
    
    /** SOA または P の役割 */
    @Column(nullable=false)
    private String role;
    
    /** ドキュメントに出現する順番 */
    @Column(nullable=false)
    private int stampNumber;
    
    /** 情報の実体名 */
    @Column(nullable=false)
    private String entity;
    
    /** 編集可能かどうか */
    @Transient
    private boolean editable = true;
    
    /** ASP 提供か */
    @Transient
    private boolean asp;
    
    /** DB 保存されている場合、そのキー */
    @Transient
    private String stampId;
    
    /** Memo の内容説明 */
    @Transient
    private String memo;
    
    /** 折り返し表示するかどうか */
    @Transient
    private boolean turnIn;
    
    /**
     * ModuleInfoオブジェクトを生成する。
     */
    public ModuleInfoBean() {
    }
    
    /**
     * スタンプ名を返す。
     * @return スタンプ名
     */
    public String getStampName() {
        return name;
    }
    
    /**
     * スタンプ名を設定する。
     * @param name スタンプ名
     */
    public void setStampName(String name) {
        this.name = name;
    }
    
    /**
     * スタンプのロールを返す。
     * @return スタンプのロール
     */
    public String getStampRole() {
        return role;
    }
    
    /**
     * スタンプのロールを設定する。
     * @param role スタンプのロール
     */
    public void setStampRole(String role) {
        this.role = role;
    }
    
    /**
     * スタンプのエンティティ名を返す。
     * @return エンティティ名
     */
    public String getEntity() {
        return entity;
    }
    
    /**
     * スタンプのエンティティ名を設定する。
     * @param entity エンティティ名
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    /**
     * シリアライズされているかどうかを返す。
     * @return シリアライズされている時 true
     */
    public boolean isSerialized() {
        return stampId != null ? true : false;
    }
    
    /**
     * ASP提供かどうかを返す。
     * @return ASP提供の時 true
     */
    public boolean isASP() {
        return asp;
    }
    
    /**
     * ASP提供を設定する。
     * @param asp ASP提供の真偽値
     */
    public void setASP(boolean asp) {
        this.asp = asp;
    }
    
    /**
     * Databseに保存されている時の PK を変えす。
     * @return Primary Key
     */
    public String getStampId() {
        return stampId;
    }
    
    /**
     * Databseに保存される時の PK を設定する。
     * @param id Primary Key
     */
    public void setStampId(String id) {
        stampId = id;
    }
    
    /**
     * スタンプのメモを返す。
     * @return スタンプのメモ
     */
    public String getStampMemo() {
        return memo;
    }
    
    /**
     * スタンプのメモを設定する。
     * @param memo スタンプのメモ
     */
    public void setStampMemo(String memo) {
        this.memo = memo;
    }
    
    /**
     * このスタンプが編集可能かどうかを設定する。
     * @param editable 編集可能かどうかの真偽値
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    /**
     * このスタンプが編集可能かどうかを返す。
     * @return 編集可能の時 true
     */
    public boolean isEditable() {
        return editable;
    }
    
    public void setTurnIn(boolean turnIn) {
        this.turnIn = turnIn;
    }
    
    public boolean isTurnIn() {
        return turnIn;
    }
    
    /**
     * 文字列表現を返す。
     * @return スタンプ名
     */
    @Override
    public String toString() {
        // 病名でエイリアスがあればそれを返す
        if (this.entity.equals(ENTITY_DIAGNOSIS)) {
            String alias =  ModelUtils.getDiagnosisAlias(name);
            return alias != null ? alias : name;
        }
        return name;
    }
    
    /**
     * ドキュメント内の出現番号を設定する。
     * @param stampNumber　出現する番号
     */
    public void setStampNumber(int stampNumber) {
        this.stampNumber = stampNumber;
    }
    
    /**
     * ドキュメント内の出現番号を返す。
     * @return ドキュメント内の出現番号
     */
    public int getStampNumber() {
        return stampNumber;
    }
    
    /**
     * スタンプ番号で比較する。
     * @return 比較値
     */
    @Override
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            int result = getStampNumber() - ((ModuleInfoBean)other).getStampNumber();
            return result;
        }
        return -1;
    }
}
