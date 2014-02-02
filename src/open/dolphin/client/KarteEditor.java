/*
 * KarteEditor2.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003-2004 Digital Globe, Inc. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.*;
import java.awt.print.PageFormat;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;

import open.dolphin.dao.SqlDaoFactory;
import open.dolphin.dao.SqlKarteSaverDao;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.AccessRight;
import open.dolphin.infomodel.DocInfo;
import open.dolphin.infomodel.ExtRef;
import open.dolphin.infomodel.ID;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.Karte;
import open.dolphin.infomodel.Module;
import open.dolphin.infomodel.ModuleInfo;
import open.dolphin.infomodel.ProgressCourse;
import open.dolphin.infomodel.Schema;
import open.dolphin.message.*;
import open.dolphin.message.MessageBuilder;
import open.dolphin.plugin.event.ClaimMessageEvent;
import open.dolphin.plugin.event.ClaimMessageListener;
import open.dolphin.plugin.event.MmlMessageEvent;
import open.dolphin.plugin.event.MmlMessageListener;
import open.dolphin.project.Project;
import open.dolphin.util.DesignFactory;
import open.dolphin.util.MMLDate;

import com.sun.image.codec.jpeg.*;

/**
 * 2号カルテクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class KarteEditor extends DefaultChartDocument {
    
    private static final int PANE_WIDTH       = 345;
    private static final int PANE_HEIGHT      = 32000;
    //private static final int PANE_HEIGHT      = 700;
    private static final int MARGIN_LEFT      = 10;
    private static final int MARGIN_TOP       = 10;
    private static final int MARGIN_RIGHT     = 10;
    private static final int MARGIN_BOTTOM    = 10;
    
    private static final String DOLPHIN_CODE_SYSTEM = "dolphin_2001-10-03";
    private static final String FUJITSU_RECICON     = "富士通";
    private static final String FUJITSU_CODE_SYSTEM = "Fujitsu SX-P V1";
    
    /** このエディタのモデル */
    private Karte model;
    
    /** このエディタを構成するコンポーネント */
    private KartePane soaPane;
    private KartePane pPane;
    private Panel2 panel2;
	private StatusPanel statusPanel;
    
    /** 編集可能かどうかのフラグ */
    private boolean editable;
        
    /** 修正時に true  */
    private boolean modify;
        
    /** Listeners to handle XML */
    private ClaimMessageListener claimListener;
    private MmlMessageListener mmlListener;
    private boolean sendMml;
    private boolean sendClaim;
    
    /** State Manager */
    private StateMgr stateMgr;
    
    
    /** Creates new KarteEditor2 */
    public KarteEditor() {
    }
    
    public Karte getModel() {
    	return model;
    }
    
    public void setModel(Karte model) {
    	this.model = model;
    }
    
	public void setNewModel(Karte m) {
		
		this.model = m;
		
		Runnable runner = new Runnable() {
			
			public void run() {
				
				String timeStamp = model.getDocInfo().getFirstConfirmDate();
				setTimestamp(timeStamp);
				
				SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						KarteRenderer renderer = new KarteRenderer(soaPane, pPane);
						renderer.render(model);
					} 
				});
			}
		};
		
		Thread t = new Thread(runner);
		t.start();
	}   
    
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Junzo SATO
    public void printPanel2(final PageFormat format) {
        String name = ((ChartPlugin)context).getPatient().getName();
        panel2.printPanel(format, 1, false, name);
    }
    
    public void printPanel2(final PageFormat format, final int copies, final boolean useDialog) {
        String name = ((ChartPlugin)context).getPatient().getName();
        panel2.printPanel(format, copies, useDialog, name);
    }
    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public KartePane getSOAPane() {
        return soaPane;
    }
    
    public KartePane getPPane() {
        return pPane;
    }
    
    public void setEditable(boolean b) {
        editable = b;
    }
    
    public void addMMLListner(MmlMessageListener l) throws TooManyListenersException {
        if (mmlListener != null) {
            throw new TooManyListenersException();
        }
        mmlListener = l;
    }
 
    public void removeMMLListener(MmlMessageListener l) {
        if (mmlListener !=null && mmlListener == l) {
            mmlListener = null;
        }
    }
    
    public void addCLAIMListner(ClaimMessageListener l) throws TooManyListenersException {
        if (claimListener != null) {
            throw new TooManyListenersException();
        }
        claimListener = l;
    }
 
    public void removeCLAIMListener(ClaimMessageListener l) {
        if (claimListener !=null && claimListener == l) {
            claimListener = null;
        }
    }    
        
    public void setModify(boolean b) {
        modify = b;
    }
    
    public void enter() {
        super.enter();
        super.controlMenu();
        stateMgr.controlMenu();
    }
        
    public void setDirty(boolean b) {
        boolean b2 = (soaPane.isDirty() || pPane.isDirty()) ? true : false;
        stateMgr.setDirty(b);
    }
    
    public boolean isDirty() {
        return stateMgr.isDirty();
    }
            
    public void start() {
        
        setLayout(new BorderLayout());

        Dimension paneDimension = new Dimension(PANE_WIDTH, PANE_HEIGHT);
        Insets insets = new Insets(MARGIN_LEFT,MARGIN_TOP,MARGIN_RIGHT,MARGIN_BOTTOM);
        ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
        
        soaPane = new KartePane(editable, mediator);
        soaPane.setMargin(insets);
        soaPane.setMaximumSize(paneDimension);
        //soaPane.setPreferredSize(paneDimension);
        soaPane.setParent(this);
        if (model != null) {
			// Schema 画像にファイル名を付けるのために必要
			String docId = model.getDocInfo().getDocId();
			soaPane.setDocId(docId);
        }
        
        pPane = new KartePane(editable, mediator);
        pPane.setMargin(insets);
        pPane.setMaximumSize(paneDimension);
        //pPane.setPreferredSize(paneDimension);
        pPane.setParent(this);
        
        soaPane.setRole("soa", pPane);
        pPane.setRole("p",soaPane);
        
        panel2 = new Panel2();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.add(soaPane);
        Component separator = Box.createRigidArea(new Dimension(1, 0));
        separator.setForeground(Color.lightGray);
        panel2.add(separator);
        panel2.add(pPane);

        JScrollPane scroller = new JScrollPane(panel2);
        scroller.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);        
        add(scroller,BorderLayout.CENTER);
        
        stateMgr = new StateMgr();
        statusPanel = ((ChartPlugin)context).getStatusPanel();
        
        // Model を表示する
        if (model != null) {
			displayModel();
        }
    }
    
    private void displayModel() {

		// Timestamp を表示する
		String timeStamp = MMLDate.getDateTime(new GregorianCalendar());
		
		// 修正の場合
		if (modify) {
			//更新: YYYY-MM-DDTHH:MM:SS (firstConfirmDate)
			StringBuffer buf = new StringBuffer();
			buf.append("更新: ");
			buf.append(MMLDate.getDateTime(new GregorianCalendar()));
			buf.append(" (");
			buf.append(model.getDocInfo().getFirstConfirmDate());
			buf.append(" )");
			timeStamp = buf.toString();			
		}
		
		// これがキー
		setTimestamp(timeStamp);
    	
    	// 内容を表示
		if (model.getModule() != null) {
			KarteRenderer renderer = new KarteRenderer(soaPane, pPane);
			renderer.render(model);
		}
    }
    
    public void setDropTargetBorder(boolean b) {
        Color c = b ? DesignFactory.getDropOkColor() : this.getBackground();
        this.setBorder(BorderFactory.createLineBorder(c, 2));
    }
        
    public void setTimestamp(String timeStamp) {
        soaPane.init();
        pPane.init();
        soaPane.setTimestamp(timeStamp);
        pPane.setTimestamp(timeStamp);
    }
    
    public boolean copyStamp() {
        return pPane.copyStamp();
    }
    
    public void pasteStamp() {
        pPane.pasteStamp();
    }
                        
    private SaveParams getSaveParams(boolean sendMML) {
       
        // SOAPane から最初の１５文字を文書タイトルとして取得する
        String text = soaPane.getTitle();
        if ( (text == null) || text.equals("") ) {
            text = "NOP";
        }

        // ダイアログを表示し、アクセス権等の保存時のパラメータを取得する
        SaveParams params = new SaveParams(sendMML);
        params.setTitle(text);
		params.setDepartment(model.getDocInfo().getClaimInfo().getDepartment());
        params.setPrintCount(0);
        
        SaveDialog sd = (SaveDialog)Project.createSaveDialog(getParentFrame(), params);
        sd.show();
        params = sd.getValue();
        sd.dispose();
        
        return params;
    } 
        
    public void save() {
        
        try {
            // 何も書かれていない時はリターンする
            if (! stateMgr.isDirty()) {
                debug("Empty karte, return");
                return;
            }

            // MML送信用のマスタIDを取得する
            // ケース１ HANIWA 方式　facilityID + patientID
            // ケース２ HIGO 方式    地域ID を使用
            ID masterID = Project.getMasterId(context.getPatient().getId());
            if (masterID == null) {
                //  地域IDが付番されていないケース
                //  2003-08-14 この場合はカルテは保存し、MML 送信はしない
                //  return;
                debug("Master ID is null");
            }

            sendMml = (Project.getSendMML() && masterID != null && mmlListener != null) ? true : false;
            sendClaim = (!modify && Project.getSendClaim() && claimListener != null) ? true : false;

            // 保存ダイアログを表示し、パラメータを得る
            SaveParams params = getSaveParams(sendMml);

            // キャンセルの場合はリターン
            if (params == null) {
                debug("Cancels to save the karte");
                return;
            }
            
            save2(params);
            
        
        } catch (DolphinException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
    
	private void save2(final SaveParams params) throws DolphinException {
		
		// DocInfo
		DocInfo docInfo = model.getDocInfo();
				
		// ConfirmDate
		String confirmDate = MMLDate.getDateTime(new GregorianCalendar());
		debug("Confirm Date: " + confirmDate);
		docInfo.setConfirmDate(confirmDate);
		if (! modify) {
			docInfo.setFirstConfirmDate(confirmDate);
			
			// Claim 用の UUID を生成する
			docInfo.getClaimInfo().setUid(Project.createUUID());
		}
		
		// Status 仮保存か確定保存か
		// 現状は確定保存のみサポート
		docInfo.setStatus(DocInfo.TT_FINAL);
		
		// title
		docInfo.setTitle(params.getTitle());
		
		// デフォルトのアクセス権を設定をする
		AccessRight ar = new AccessRight();
		ar.setPermission("all");
		ar.setLicenseeCode("creator");
		ar.setLicenseeName("記載者施設");
		ar.setLicenseeCodeType("facilityCode");
		docInfo.addAccessRight(ar);
		
		// 患者のアクセス権を設定をする
		if (params.isAllowPatientRef()) {
			ar = new AccessRight();
			ar.setPermission("read");
			ar.setLicenseeCode("patient");
			ar.setLicenseeName("被記載者(患者)");
			ar.setLicenseeCodeType("personCode");
			docInfo.addAccessRight(ar);
		}
		
		// 診療履歴のある施設のアクセス権を設定をする
		if (params.isAllowClinicRef()) {
			ar = new AccessRight();
			ar.setPermission("read");
			ar.setLicenseeCode("experience");
			ar.setLicenseeName("診療歴のある施設");
			ar.setLicenseeCodeType("facilityCode");
			docInfo.addAccessRight(ar);
		}		
		
		// DEBUG
		debug("patientId: " + model.getPatient().getId());
		debug("docId: " + model.getDocInfo().getDocId());
		debug("firstConfirmDate: " + model.getDocInfo().getFirstConfirmDate());
		debug("confirmDate: " + model.getDocInfo().getConfirmDate());
		debug("docType: " + model.getDocInfo().getDocType());
		debug("title: " + model.getDocInfo().getTitle());
		debug("purpose: " + model.getDocInfo().getPurpose());
		debug("department: " + model.getDocInfo().getClaimInfo().getDepartment());
		debug("insuranceClass: " + model.getDocInfo().getClaimInfo().getInsuranceClass());
		debug("version: " + model.getDocInfo().getVersion().getVersionNumber());
		if (model.getDocInfo().getParentId() != null) {
			debug("parentId: " + model.getDocInfo().getParentId().getId());
			debug("parentIdRelation: " + model.getDocInfo().getParentId().getRelation());
		}
		debug("creatorId: " + model.getDocInfo().getCreator().getId());
		debug("creatorName: " + model.getDocInfo().getCreator().getName());
		debug("creatorLicense: " + model.getDocInfo().getCreator().getLicense());
		debug("status: " + model.getDocInfo().getStatus());
		
		// ProgressCourseModule の ModuleInfo を保存しておく
		ModuleInfo[] progressInfo = model.getModuleInfo("progressCourse");
		if (progressInfo == null) {
			// 存在しない場合は新規に作成する
			progressInfo = new ModuleInfo[2];
			ModuleInfo mi = new ModuleInfo();
			mi.setName("progressCourse");
			mi.setEntity("progressCourse");
			mi.setRole("soaSpec");
			progressInfo[0] = mi;
			mi = new ModuleInfo();
			mi.setName("progressCourse");
			mi.setEntity("progressCourse");
			mi.setRole("pSpec");
			progressInfo[1] = mi;
		}
		
		// Clear
		model.setModule(null);
		model.setSchema(null);
    	
    	// SOAPane をダンプし model に追加する
		KartePaneDumper dumper = new KartePaneDumper();
		KarteStyledDocument doc = (KarteStyledDocument)soaPane.getDocument();
		dumper.setTopFreePos(doc.getTopFreePos());
		dumper.dump(doc);
		Module[] soa = dumper.getModule();
		if (soa != null) {
			model.addModule(soa);	
		}
				
		// ProgressCourse SOA を生成する
		ProgressCourse pc = new ProgressCourse();
		pc.setFreeText(dumper.getSpec());
		Module progressSoa = new Module();
		progressSoa.setModuleInfo(progressInfo[0]);
		progressSoa.setModel(pc);
		model.addModule(progressSoa);
		
		// Schema を追加する
		Schema[] schemas = dumper.getSchema();
		if (schemas != null) {
			//保存のため Icon を JPEG に変換する
			 for (int i = 0; i < schemas.length; i++) {
				byte[] jpegByte = getJPEGByte(schemas[i].getIcon().getImage());
				schemas[i].setJPEGByte(jpegByte);
				schemas[i].setIcon(null);
				String fileName = model.getDocInfo().getDocId() + "-" + i + ".jpg";
				schemas[i].setFileName(fileName);
				ExtRef ref = (ExtRef)schemas[i].getModel();
				ref.setHref(fileName);
			 }
		 	model.setSchema(schemas);
		}
		debug(dumper.getSpec());
    	
		// PPane をダンプし model に追加する
		dumper = new KartePaneDumper();
		doc = (KarteStyledDocument)pPane.getDocument();
		dumper.setTopFreePos(doc.getTopFreePos());
		dumper.dump((DefaultStyledDocument)pPane.getDocument());
		Module[] plan = dumper.getModule();
		
		if (plan != null) {
			model.addModule(plan);
		} else {
			sendClaim = false;
		}
		
		// ProgressCourse P を生成する
		pc = new ProgressCourse();
		pc.setFreeText(dumper.getSpec());
		Module progressP = new Module();
		progressP.setModuleInfo(progressInfo[1]);
		progressP.setModel(pc);
		model.addModule(progressP);
		
		// Setup the moduleInfo
		setupModuleInfo(model, modify);
		
		// 保存
		final SqlKarteSaverDao dao = (SqlKarteSaverDao)SqlDaoFactory.create(this, "dao.karteSaver");
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						statusPanel.start("保存しています...");
					}
					
				});
				
				dao.setKarte(model);
				dao.setPvtOid( ((ChartPlugin)context).getPatientVisit().getNumber());
				dao.setModify(modify);
				dao.doWork();
				
				// Print the DML
				writeDml(model); 
				
				SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						
						// 印刷
						int copies = params.getPrintCount();
						if (copies > 0) {
							statusPanel.setMessage("印刷しています...");
							printPanel2(ChartPlugin.pageFormat, copies, false);
						}
						
						statusPanel.stop("");
						
						// 編集不可に設定する
						soaPane.setEditableProp(false);
						pPane.setEditableProp(false);

						// 状態遷移する
						setDirty(false);
						
						//会計終了をセットする
						((ChartPlugin)context).setClaimSent(true);
					}
				});
			}
		};
		
		Thread t = new Thread(r);
		t.start();
	}
	
	private void setupModuleInfo(Karte model, boolean bModify) {
		
		String confirmDate = model.getDocInfo().getConfirmDate();
		
		Module[] module = model.getModule();
		ModuleInfo mInfo = null;
		
		for (int i = 0; i < module.length; i++) {
			
			mInfo = module[i].getModuleInfo();
			
			if (bModify) {
				// 変更の場合、parentId=oldModuleId, firstConfirmDate=oldFirst
				mInfo.setFirstConfirmDate(mInfo.getFirstConfirmDate());
				mInfo.setParentId(mInfo.getModuleId());
				mInfo.setParentIdRelation("oldEdition");
				
			} else {
				mInfo.setFirstConfirmDate(confirmDate);
				mInfo.setParentId(null);
				mInfo.setParentIdRelation(null);
			}
			
			// moduleId, confirmdate はその度ごとに新規
			mInfo.setModuleId(Project.createUUID());
			mInfo.setConfirmDate(confirmDate);
		}
	}
	
	private void writeDml(Karte model) {
		
		DmlMessageBuilder builder = new DmlMessageBuilder();
		String dml = builder.build((IInfoModel)model);
		debug(dml);
		
		if (sendClaim) {
			MessageBuilder cb = new MessageBuilder();
			cb.setTemplateFile("claim.vm");
			String claimMessage = cb.build(dml);
			debug(claimMessage);
			ClaimMessageEvent cvt = new ClaimMessageEvent(this);
			cvt.setClaimInstance(claimMessage);
			cvt.setPatientId(model.getPatient().getId());
			cvt.setPatientName(model.getPatient().getName());
			cvt.setPatientSex(model.getPatient().getGender());
			cvt.setTitle(model.getDocInfo().getTitle());
			cvt.setConfirmDate(model.getDocInfo().getConfirmDate());
			claimListener.claimMessageEvent(cvt);
		}
		
		if (sendMml) {
			MessageBuilder mb = new MessageBuilder();
			mb.setTemplateFile("mml2.3.vm");
			String mmlMessage = mb.build(dml);
			debug(mmlMessage);
			MmlMessageEvent cvt = new MmlMessageEvent(this);
			cvt.setMmlInstance(mmlMessage);
			cvt.setPatientId(model.getPatient().getId());
			cvt.setPatientName(model.getPatient().getName());
			cvt.setPatientSex(model.getPatient().getGender());
			cvt.setTitle(model.getDocInfo().getTitle());
			cvt.setConfirmDate(model.getDocInfo().getConfirmDate());
			cvt.setGroupId(model.getDocInfo().getDocId());
			cvt.setSchema(model.getSchema());
			cvt.setContentInfo(model.getDocInfo().getDocType());
			mmlListener.mmlMessageEvent(cvt);	
		}
	}
                
    /**
     * Courtesy of Junzo SATO
     */
    private byte[] getJPEGByte(Image image) {
    	
        byte[] ret = null;
        BufferedOutputStream writer = null;
        
        try {
            Dimension d = new Dimension(image.getWidth(this), image.getHeight(this));
            BufferedImage bf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bf.getGraphics();
            g.setColor(Color.white);
            g.drawImage(image, 0, 0, d.width, d.height, this);

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            writer = new BufferedOutputStream(bo);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(writer);
            encoder.encode(bf);
            writer.flush();
            writer.close();
            ret = bo.toByteArray();
            
        } catch (IOException e) {
            System.out.println("IOException while creating the JPEG image: " + e.toString());
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                }
            }
        }
        return ret;
    } 
            
    /**
     * このエディタの抽象状態クラス
     */
    protected abstract class EditorState {
        
        public EditorState() {
        }
        
        public abstract void controlMenu();
    }
    
    /**
     * No dirty 状態クラス
     */ 
    protected final class NoDirtyState extends EditorState {
        
        public NoDirtyState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
            mediator.saveKarteAction.setEnabled(false);     // 保存
            //mediator.orderAction.setEnabled(false);         // オーダのみ発行
            mediator.printAction.setEnabled(false);         // 印刷
        }
    }
    
    /**
     * Dirty 状態クラス
     */
    protected final class DirtyState extends EditorState {
        
        public DirtyState() {
        }
        
        public void controlMenu() {
            ChartMediator mediator = ((ChartPlugin)context).getChartMediator();
            mediator.saveKarteAction.setEnabled(true);      // 保存
            //mediator.orderAction.setEnabled(true);          // オーダのみ発行 Bug!
            mediator.printAction.setEnabled(true);          // 印刷
        }
    }    
        
    /**
     * 状態マネジャ
     */
    protected final class StateMgr {
        
        private EditorState noDirtyState = new NoDirtyState();
        private EditorState dirtyState = new DirtyState();
        private EditorState currentState;
        
        public StateMgr() {
            currentState = noDirtyState;
        }
        
        public boolean isDirty() {
            return currentState == dirtyState ? true : false;
        }
        
        public void setDirty(boolean b) {
            currentState = b ? dirtyState : noDirtyState;
            currentState.controlMenu();
        }
        
        public void controlMenu() {
            currentState.controlMenu();
        }
    }
}