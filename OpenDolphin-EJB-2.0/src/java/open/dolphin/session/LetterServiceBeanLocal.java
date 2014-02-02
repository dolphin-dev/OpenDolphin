/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.session;

import javax.ejb.Local;

/**
 *
 * @author kazushi
 */
@Local
public interface LetterServiceBeanLocal {

    public long saveOrUpdateLetter(open.dolphin.infomodel.LetterModule model);

    public java.util.List<open.dolphin.infomodel.LetterModule> getLetterList(long karteId);

    public open.dolphin.infomodel.LetterModule getLetter(long letterPk);

    public void delete(long pk);
    
}
