package open.dolphin.letter;

import open.dolphin.client.GUIConst;
import open.dolphin.client.Letter;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LetterStateMgr {

    private Letter letterImpl;
    private LetterState emptyState = new EmptyState();
    private LetterState cleanState = new CleanState();
    private StartEditingState startEditingState = new StartEditingState();
    private DirtyState dirtyState = new DirtyState();
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

    public void processModifyKarteEvent() {
        currentState = startEditingState;
        currentState.enter();
    }

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
        return currentState == dirtyState ? true : false;
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
            boolean canEdit = letterImpl.getContext().isReadOnly() ? false : true;
            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_DELETE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);
        }
    }

    protected final class CleanState extends LetterState {

        public CleanState() {
        }

        @Override
        public void enter() {
            letterImpl.setEditables(false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            boolean canEdit = letterImpl.getContext().isReadOnly() ? false : true;
            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_DELETE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);
        }
    }

    class StartEditingState extends LetterState {

        @Override
        public void enter() {
            letterImpl.setEditables(true);
            letterImpl.setListeners();
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }

    class DirtyState extends LetterState {

        @Override
        public void enter() {
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
}
