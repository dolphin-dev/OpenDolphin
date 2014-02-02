package open.dolphin.client;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * SubMenuAction
 * 
 * @author Minagawa,Kazushi
 *
 */
public class SubMenuAction extends AbstractAction {
	
	private static final long serialVersionUID = 3935180468934680814L;

	public SubMenuAction(String text) {
		super(text);
	}
	
	/**
	 * サブメニュー用のアクションで何も実行しない。
	 */
	public void actionPerformed(ActionEvent e) {
	}

}
