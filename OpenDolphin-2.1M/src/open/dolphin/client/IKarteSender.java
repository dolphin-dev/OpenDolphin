package open.dolphin.client;

import open.dolphin.infomodel.DocumentModel;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public interface IKarteSender {

    public Chart getContext();

    public void setContext(Chart context);

    public void prepare(DocumentModel data);

    public void send(DocumentModel data);

}