package open.dolphin.letter;

import open.dolphin.client.GUIConst;
import open.dolphin.client.Letter;
import open.dolphin.project.Project;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterStateMgr {

    private final Letter letterImpl;
    private final LetterState emptyState = new EmptyState();
    private final LetterState cleanState = new CleanState();
    private final DirtyState dirtyState = new DirtyState();
    private LetterState currentState;

    public LetterStateMgr(Letter letterImpl) {
        this.letterImpl = letterImpl;
        currentState = emptyState;
    }

    public void processEmptyEvent() {
        currentState = emptyState;
        this.enter();
    }

    public void processCleanEvent() {
        currentState = cleanState;
        this.enter();
    }

//    public void processModifyKarteEvent() {
//        currentState = startEditingState;
//        currentState.enter();
//    }

    public void processSavedEvent() {
        currentState = cleanState;
        currentState.enter();
    }

    public void processDirtyEvent() {
        boolean newDirty = letterImpl.letterIsDirty();
        currentState = newDirty ? dirtyState : emptyState;
        currentState.enter();
    }

    public boolean isDirtyState() {
        return currentState == dirtyState;
    }

    public void enter() {
        currentState.enter();
    }

    protected abstract class LetterState {

        public LetterState() {
        }

        public abstract void enter();
    }

    protected final class EmptyState extends LetterState {

        public EmptyState() {
        }

        @Override
        public void enter() {
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, false);       
        }
    }

    protected final class CleanState extends LetterState {

        public CleanState() {
        }

        @Override
        public void enter() {
            letterImpl.setEditables(false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);             
            boolean canEdit = !letterImpl.getContext().isReadOnly();           
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, canEdit);   // 修正
//s.oh^ 2014/08/19 ID権限
            //letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);             // Print
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, !Project.isOtherCare());
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINTER_SETUP, !Project.isOtherCare());
//s.oh$
        }
    }

//    class StartEditingState extends LetterState {
//
//        @Override
//        public void enter() {
//            letterImpl.setEditables(true);
//            letterImpl.setListeners();
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
//        }
//    }

    class DirtyState extends LetterState {

        @Override
        public void enter() {
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, true);
//s.oh^ 2014/08/19 ID権限
            //letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, !Project.isOtherCare());
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINTER_SETUP, !Project.isOtherCare());
//s.oh$
            //letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
}
