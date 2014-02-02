package open.dolphin.client;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.CharacterIterator;
import javax.swing.text.JTextComponent;

/**
 * マックの上下キー問題 2013/06/24
 * @author kazushi
 */
public final class MacInputFixer  {
    
    private boolean typing;
    
    public MacInputFixer() {
    }
    
    public void fix(JTextComponent textComponent) {
        
        textComponent.addInputMethodListener(new InputMethodListener() {

            @Override
            public void inputMethodTextChanged(InputMethodEvent e) {
                if (e.getCommittedCharacterCount() > 0) {
                    typing = false; // 確定
                }
                else if (e.getText().first() == CharacterIterator.DONE) {
                    typing = false; // キャンセル
                }
                else {
                    typing = true; // 入力中
                }
                JTextComponent tcm = (JTextComponent)e.getSource();
                // 入力中はキャレットを非表示
                tcm.getCaret().setVisible(!typing);
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {
            }
        });
        textComponent.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }
        });
        textComponent.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
}
