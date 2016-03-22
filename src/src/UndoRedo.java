
package src;

import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
/** Manager for performing undo- and redo- actions to opened documents.
 *  @version 1.2
 *  @since 1.0
 */
public class UndoRedo extends JEditorPane {
	
	static final long serialVersionUID = 42L;
	
    protected UndoManager undoManager=new UndoManager();

    UndoRedo() {
        super();
        setEditorKit(new StyledEditorKit());
        getDocument().addUndoableEditListener(undoManager);
        undoManager.refreshControls();
    }

    /** Overrides CompoundEdit with given params */
    class MyCompoundEdit extends CompoundEdit {
    	static final long serialVersionUID = 42L;
    	
        boolean isUnDone=false;
        public int getLength() {
            return edits.size();
        }
 
        public void undo() throws CannotUndoException {
            super.undo();
            isUnDone=true;
        }
        public void redo() throws CannotUndoException {
            super.redo();
            isUnDone=false;
        }
        public boolean canUndo() {
            return edits.size()>0 && !isUnDone;
        }

        public boolean canRedo() {
            return edits.size()>0 && isUnDone;
        }
    }
    
    class UndoManager extends AbstractUndoableEdit implements UndoableEditListener {
    	
    	static final long serialVersionUID = 42L;
    	
        String lastEditName = null;
        ArrayList<MyCompoundEdit> edits = new ArrayList<>();
        MyCompoundEdit current;
        int pointer = -1;
 
        public void undoableEditHappened(UndoableEditEvent e) {
            UndoableEdit edit = e.getEdit();
            if (edit instanceof AbstractDocument.DefaultDocumentEvent) {
                    boolean isNeedStart = false;
                    
                    if (current == null) {
                        isNeedStart = true;
                    } else if (lastEditName == null || !lastEditName.equals(edit.getPresentationName())) {
                        isNeedStart = true;
                    }
 
                    while (pointer<edits.size()-1) {
                        edits.remove(edits.size()-1);
                        isNeedStart = true;
                    }
                    if (isNeedStart) {
                        createCompoundEdit();
                    }
 
                    current.addEdit(edit);
                    lastEditName = edit.getPresentationName();
 
                    refreshControls();
            }
        }
 
        public void createCompoundEdit() {
            if (current==null) {
                current = new MyCompoundEdit();
            }
            else if (current.getLength()>0) {
                current = new MyCompoundEdit();
            }
 
            edits.add(current);
            pointer++;
        }
 
        public void undo() throws CannotUndoException {
            if (!canUndo()) {
                throw new CannotUndoException();
            }
 
            MyCompoundEdit u=edits.get(pointer);
            u.undo();
            pointer--;
 
            refreshControls();
        }
 
        public void redo() throws CannotUndoException {
            if (!canRedo()) {
                throw new CannotUndoException();
            }
 
            pointer++;
            MyCompoundEdit u = edits.get(pointer);
            u.redo();
 
            refreshControls();
        }
 
        public boolean canUndo() {
            return pointer >= 0;
        }

        public boolean canRedo() {
            return edits.size() > 0 && pointer < edits.size() - 1;
        }
 
        public void refreshControls() {
            NoteFrame.undoButton.setEnabled(canUndo());
            NoteFrame.redoButton.setEnabled(canRedo());
        }

    }
}