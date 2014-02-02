package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.beans.*;

public class ColorPanelBeanBeanInfo extends SimpleBeanInfo {


  // Bean descriptor //GEN-FIRST:BeanDescriptor
  private static BeanDescriptor beanDescriptor = new BeanDescriptor  ( ColorPanelBean.class , null );

  static {//GEN-HEADEREND:BeanDescriptor

    // Here you can add code for customizing the BeanDescriptor.

}//GEN-LAST:BeanDescriptor

  // Property identifiers //GEN-FIRST:Properties
  private static final int PROPERTY_registeredKeyStrokes = 0;
  private static final int PROPERTY_valid = 1;
  private static final int PROPERTY_y = 2;
  private static final int PROPERTY_insets = 3;
  private static final int PROPERTY_focusCycleRoot = 4;
  private static final int PROPERTY_maximumSizeSet = 5;
  private static final int PROPERTY_preferredSizeSet = 6;
  private static final int PROPERTY_UIClassID = 7;
  private static final int PROPERTY_verifyInputWhenFocusTarget = 8;
  private static final int PROPERTY_propertyChangeListeners = 9;
  private static final int PROPERTY_alignmentY = 10;
  private static final int PROPERTY_doubleBuffered = 11;
  private static final int PROPERTY_font = 12;
  private static final int PROPERTY_focusListeners = 13;
  private static final int PROPERTY_width = 14;
  private static final int PROPERTY_mouseMotionListeners = 15;
  private static final int PROPERTY_foreground = 16;
  private static final int PROPERTY_componentListeners = 17;
  private static final int PROPERTY_maximumSize = 18;
  private static final int PROPERTY_enabled = 19;
  private static final int PROPERTY_inputVerifier = 20;
  private static final int PROPERTY_debugGraphicsOptions = 21;
  private static final int PROPERTY_containerListeners = 22;
  private static final int PROPERTY_focusTraversable = 23;
  private static final int PROPERTY_toolTipText = 24;
  private static final int PROPERTY_inputMethodRequests = 25;
  private static final int PROPERTY_minimumSize = 26;
  private static final int PROPERTY_ancestorListeners = 27;
  private static final int PROPERTY_graphicsConfiguration = 28;
  private static final int PROPERTY_parent = 29;
  private static final int PROPERTY_focusTraversalPolicySet = 30;
  private static final int PROPERTY_mouseWheelListeners = 31;
  private static final int PROPERTY_height = 32;
  private static final int PROPERTY_opaque = 33;
  private static final int PROPERTY_keyListeners = 34;
  private static final int PROPERTY_foregroundSet = 35;
  private static final int PROPERTY_accessibleContext = 36;
  private static final int PROPERTY_focusTraversalPolicy = 37;
  private static final int PROPERTY_hierarchyBoundsListeners = 38;
  private static final int PROPERTY_UI = 39;
  private static final int PROPERTY_paintingTile = 40;
  private static final int PROPERTY_vetoableChangeListeners = 41;
  private static final int PROPERTY_hierarchyListeners = 42;
  private static final int PROPERTY_focusTraversalKeysEnabled = 43;
  private static final int PROPERTY_colorModel = 44;
  private static final int PROPERTY_x = 45;
  private static final int PROPERTY_requestFocusEnabled = 46;
  private static final int PROPERTY_visibleRect = 47;
  private static final int PROPERTY_visible = 48;
  private static final int PROPERTY_rootPane = 49;
  private static final int PROPERTY_treeLock = 50;
  private static final int PROPERTY_focusCycleRootAncestor = 51;
  private static final int PROPERTY_peer = 52;
  private static final int PROPERTY_dropTarget = 53;
  private static final int PROPERTY_transferHandler = 54;
  private static final int PROPERTY_locale = 55;
  private static final int PROPERTY_ignoreRepaint = 56;
  private static final int PROPERTY_cursor = 57;
  private static final int PROPERTY_alignmentX = 58;
  private static final int PROPERTY_backgroundSet = 59;
  private static final int PROPERTY_optimizedDrawingEnabled = 60;
  private static final int PROPERTY_actionMap = 61;
  private static final int PROPERTY_showing = 62;
  private static final int PROPERTY_panelColor = 63;
  private static final int PROPERTY_toolkit = 64;
  private static final int PROPERTY_nextFocusableComponent = 65;
  private static final int PROPERTY_focusOwner = 66;
  private static final int PROPERTY_autoscrolls = 67;
  private static final int PROPERTY_bounds = 68;
  private static final int PROPERTY_inputMethodListeners = 69;
  private static final int PROPERTY_minimumSizeSet = 70;
  private static final int PROPERTY_focusable = 71;
  private static final int PROPERTY_background = 72;
  private static final int PROPERTY_cursorSet = 73;
  private static final int PROPERTY_border = 74;
  private static final int PROPERTY_layout = 75;
  private static final int PROPERTY_preferredSize = 76;
  private static final int PROPERTY_topLevelAncestor = 77;
  private static final int PROPERTY_displayable = 78;
  private static final int PROPERTY_mouseListeners = 79;
  private static final int PROPERTY_validateRoot = 80;
  private static final int PROPERTY_components = 81;
  private static final int PROPERTY_managingFocus = 82;
  private static final int PROPERTY_fontSet = 83;
  private static final int PROPERTY_componentOrientation = 84;
  private static final int PROPERTY_componentCount = 85;
  private static final int PROPERTY_lightweight = 86;
  private static final int PROPERTY_name = 87;
  private static final int PROPERTY_graphics = 88;
  private static final int PROPERTY_inputContext = 89;
  private static final int PROPERTY_locationOnScreen = 90;
  private static final int PROPERTY_component = 91;
  private static final int PROPERTY_focusTraversalKeys = 92;

  // Property array 
  private static PropertyDescriptor[] properties = new PropertyDescriptor[93];

  static {
    try {
      properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", ColorPanelBean.class, "getRegisteredKeyStrokes", null );
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", ColorPanelBean.class, "isValid", null );
      properties[PROPERTY_y] = new PropertyDescriptor ( "y", ColorPanelBean.class, "getY", null );
      properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", ColorPanelBean.class, "getInsets", null );
      properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", ColorPanelBean.class, "isFocusCycleRoot", "setFocusCycleRoot" );
      properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", ColorPanelBean.class, "isMaximumSizeSet", null );
      properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", ColorPanelBean.class, "isPreferredSizeSet", null );
      properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", ColorPanelBean.class, "getUIClassID", null );
      properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", ColorPanelBean.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
      properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", ColorPanelBean.class, "getPropertyChangeListeners", null );
      properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", ColorPanelBean.class, "getAlignmentY", "setAlignmentY" );
      properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", ColorPanelBean.class, "isDoubleBuffered", "setDoubleBuffered" );
      properties[PROPERTY_font] = new PropertyDescriptor ( "font", ColorPanelBean.class, "getFont", "setFont" );
      properties[PROPERTY_focusListeners] = new PropertyDescriptor ( "focusListeners", ColorPanelBean.class, "getFocusListeners", null );
      properties[PROPERTY_width] = new PropertyDescriptor ( "width", ColorPanelBean.class, "getWidth", null );
      properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor ( "mouseMotionListeners", ColorPanelBean.class, "getMouseMotionListeners", null );
      properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", ColorPanelBean.class, "getForeground", "setForeground" );
      properties[PROPERTY_componentListeners] = new PropertyDescriptor ( "componentListeners", ColorPanelBean.class, "getComponentListeners", null );
      properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", ColorPanelBean.class, "getMaximumSize", "setMaximumSize" );
      properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", ColorPanelBean.class, "isEnabled", "setEnabled" );
      properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", ColorPanelBean.class, "getInputVerifier", "setInputVerifier" );
      properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", ColorPanelBean.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
      properties[PROPERTY_containerListeners] = new PropertyDescriptor ( "containerListeners", ColorPanelBean.class, "getContainerListeners", null );
      properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", ColorPanelBean.class, "isFocusTraversable", null );
      properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", ColorPanelBean.class, "getToolTipText", "setToolTipText" );
      properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", ColorPanelBean.class, "getInputMethodRequests", null );
      properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", ColorPanelBean.class, "getMinimumSize", "setMinimumSize" );
      properties[PROPERTY_ancestorListeners] = new PropertyDescriptor ( "ancestorListeners", ColorPanelBean.class, "getAncestorListeners", null );
      properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", ColorPanelBean.class, "getGraphicsConfiguration", null );
      properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", ColorPanelBean.class, "getParent", null );
      properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor ( "focusTraversalPolicySet", ColorPanelBean.class, "isFocusTraversalPolicySet", null );
      properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor ( "mouseWheelListeners", ColorPanelBean.class, "getMouseWheelListeners", null );
      properties[PROPERTY_height] = new PropertyDescriptor ( "height", ColorPanelBean.class, "getHeight", null );
      properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", ColorPanelBean.class, "isOpaque", "setOpaque" );
      properties[PROPERTY_keyListeners] = new PropertyDescriptor ( "keyListeners", ColorPanelBean.class, "getKeyListeners", null );
      properties[PROPERTY_foregroundSet] = new PropertyDescriptor ( "foregroundSet", ColorPanelBean.class, "isForegroundSet", null );
      properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", ColorPanelBean.class, "getAccessibleContext", null );
      properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor ( "focusTraversalPolicy", ColorPanelBean.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy" );
      properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor ( "hierarchyBoundsListeners", ColorPanelBean.class, "getHierarchyBoundsListeners", null );
      properties[PROPERTY_UI] = new PropertyDescriptor ( "UI", ColorPanelBean.class, "getUI", "setUI" );
      properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", ColorPanelBean.class, "isPaintingTile", null );
      properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor ( "vetoableChangeListeners", ColorPanelBean.class, "getVetoableChangeListeners", null );
      properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor ( "hierarchyListeners", ColorPanelBean.class, "getHierarchyListeners", null );
      properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor ( "focusTraversalKeysEnabled", ColorPanelBean.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled" );
      properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", ColorPanelBean.class, "getColorModel", null );
      properties[PROPERTY_x] = new PropertyDescriptor ( "x", ColorPanelBean.class, "getX", null );
      properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", ColorPanelBean.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
      properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", ColorPanelBean.class, "getVisibleRect", null );
      properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", ColorPanelBean.class, "isVisible", "setVisible" );
      properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", ColorPanelBean.class, "getRootPane", null );
      properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", ColorPanelBean.class, "getTreeLock", null );
      properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor ( "focusCycleRootAncestor", ColorPanelBean.class, "getFocusCycleRootAncestor", null );
      properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", ColorPanelBean.class, "getPeer", null );
      properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", ColorPanelBean.class, "getDropTarget", "setDropTarget" );
      properties[PROPERTY_transferHandler] = new PropertyDescriptor ( "transferHandler", ColorPanelBean.class, "getTransferHandler", "setTransferHandler" );
      properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", ColorPanelBean.class, "getLocale", "setLocale" );
      properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor ( "ignoreRepaint", ColorPanelBean.class, "getIgnoreRepaint", "setIgnoreRepaint" );
      properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", ColorPanelBean.class, "getCursor", "setCursor" );
      properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", ColorPanelBean.class, "getAlignmentX", "setAlignmentX" );
      properties[PROPERTY_backgroundSet] = new PropertyDescriptor ( "backgroundSet", ColorPanelBean.class, "isBackgroundSet", null );
      properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", ColorPanelBean.class, "isOptimizedDrawingEnabled", null );
      properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", ColorPanelBean.class, "getActionMap", "setActionMap" );
      properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", ColorPanelBean.class, "isShowing", null );
      properties[PROPERTY_panelColor] = new PropertyDescriptor ( "panelColor", ColorPanelBean.class, "getPanelColor", "setPanelColor" );
      properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", ColorPanelBean.class, "getToolkit", null );
      properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", ColorPanelBean.class, "getNextFocusableComponent", "setNextFocusableComponent" );
      properties[PROPERTY_focusOwner] = new PropertyDescriptor ( "focusOwner", ColorPanelBean.class, "isFocusOwner", null );
      properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", ColorPanelBean.class, "getAutoscrolls", "setAutoscrolls" );
      properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", ColorPanelBean.class, "getBounds", "setBounds" );
      properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor ( "inputMethodListeners", ColorPanelBean.class, "getInputMethodListeners", null );
      properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", ColorPanelBean.class, "isMinimumSizeSet", null );
      properties[PROPERTY_focusable] = new PropertyDescriptor ( "focusable", ColorPanelBean.class, "isFocusable", "setFocusable" );
      properties[PROPERTY_background] = new PropertyDescriptor ( "background", ColorPanelBean.class, "getBackground", "setBackground" );
      properties[PROPERTY_cursorSet] = new PropertyDescriptor ( "cursorSet", ColorPanelBean.class, "isCursorSet", null );
      properties[PROPERTY_border] = new PropertyDescriptor ( "border", ColorPanelBean.class, "getBorder", "setBorder" );
      properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", ColorPanelBean.class, "getLayout", "setLayout" );
      properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", ColorPanelBean.class, "getPreferredSize", "setPreferredSize" );
      properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", ColorPanelBean.class, "getTopLevelAncestor", null );
      properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", ColorPanelBean.class, "isDisplayable", null );
      properties[PROPERTY_mouseListeners] = new PropertyDescriptor ( "mouseListeners", ColorPanelBean.class, "getMouseListeners", null );
      properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", ColorPanelBean.class, "isValidateRoot", null );
      properties[PROPERTY_components] = new PropertyDescriptor ( "components", ColorPanelBean.class, "getComponents", null );
      properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", ColorPanelBean.class, "isManagingFocus", null );
      properties[PROPERTY_fontSet] = new PropertyDescriptor ( "fontSet", ColorPanelBean.class, "isFontSet", null );
      properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", ColorPanelBean.class, "getComponentOrientation", "setComponentOrientation" );
      properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", ColorPanelBean.class, "getComponentCount", null );
      properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", ColorPanelBean.class, "isLightweight", null );
      properties[PROPERTY_name] = new PropertyDescriptor ( "name", ColorPanelBean.class, "getName", "setName" );
      properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", ColorPanelBean.class, "getGraphics", null );
      properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", ColorPanelBean.class, "getInputContext", null );
      properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", ColorPanelBean.class, "getLocationOnScreen", null );
      properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", ColorPanelBean.class, null, null, "getComponent", null );
      properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor ( "focusTraversalKeys", ColorPanelBean.class, null, null, "getFocusTraversalKeys", "setFocusTraversalKeys" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

}//GEN-LAST:Properties

  // EventSet identifiers//GEN-FIRST:Events
  private static final int EVENT_inputMethodListener = 0;
  private static final int EVENT_mouseMotionListener = 1;
  private static final int EVENT_hierarchyBoundsListener = 2;
  private static final int EVENT_containerListener = 3;
  private static final int EVENT_ancestorListener = 4;
  private static final int EVENT_focusListener = 5;
  private static final int EVENT_propertyChangeListener = 6;
  private static final int EVENT_vetoableChangeListener = 7;
  private static final int EVENT_keyListener = 8;
  private static final int EVENT_componentListener = 9;
  private static final int EVENT_mouseListener = 10;
  private static final int EVENT_hierarchyListener = 11;
  private static final int EVENT_mouseWheelListener = 12;

  // EventSet array
  private static EventSetDescriptor[] eventSets = new EventSetDescriptor[13];

  static {
    try {
      eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"inputMethodTextChanged", "caretPositionChanged"}, "addInputMethodListener", "removeInputMethodListener" );
      eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseMoved", "mouseDragged"}, "addMouseMotionListener", "removeMouseMotionListener" );
      eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" );
      eventSets[EVENT_containerListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" );
      eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorMoved", "ancestorRemoved", "ancestorAdded"}, "addAncestorListener", "removeAncestorListener" );
      eventSets[EVENT_focusListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusLost", "focusGained"}, "addFocusListener", "removeFocusListener" );
      eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
      eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
      eventSets[EVENT_keyListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyReleased", "keyPressed", "keyTyped"}, "addKeyListener", "removeKeyListener" );
      eventSets[EVENT_componentListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentResized", "componentMoved", "componentHidden", "componentShown"}, "addComponentListener", "removeComponentListener" );
      eventSets[EVENT_mouseListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseEntered", "mouseExited", "mouseClicked", "mouseReleased", "mousePressed"}, "addMouseListener", "removeMouseListener" );
      eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" );
      eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

}//GEN-LAST:Events

  // Method identifiers //GEN-FIRST:Methods
  private static final int METHOD_paint0 = 0;
  private static final int METHOD_updateUI1 = 1;
  private static final int METHOD_isLightweightComponent2 = 2;
  private static final int METHOD_firePropertyChange3 = 3;
  private static final int METHOD_addPropertyChangeListener4 = 4;
  private static final int METHOD_grabFocus5 = 5;
  private static final int METHOD_disable6 = 6;
  private static final int METHOD_getToolTipText7 = 7;
  private static final int METHOD_firePropertyChange8 = 8;
  private static final int METHOD_registerKeyboardAction9 = 9;
  private static final int METHOD_registerKeyboardAction10 = 10;
  private static final int METHOD_paintImmediately11 = 11;
  private static final int METHOD_printAll12 = 12;
  private static final int METHOD_revalidate13 = 13;
  private static final int METHOD_createToolTip14 = 14;
  private static final int METHOD_getInputMap15 = 15;
  private static final int METHOD_getDefaultLocale16 = 16;
  private static final int METHOD_paintImmediately17 = 17;
  private static final int METHOD_getInsets18 = 18;
  private static final int METHOD_getConditionForKeyStroke19 = 19;
  private static final int METHOD_firePropertyChange20 = 20;
  private static final int METHOD_getListeners21 = 21;
  private static final int METHOD_getInputMap22 = 22;
  private static final int METHOD_getClientProperty23 = 23;
  private static final int METHOD_firePropertyChange24 = 24;
  private static final int METHOD_firePropertyChange25 = 25;
  private static final int METHOD_contains26 = 26;
  private static final int METHOD_repaint27 = 27;
  private static final int METHOD_repaint28 = 28;
  private static final int METHOD_firePropertyChange29 = 29;
  private static final int METHOD_firePropertyChange30 = 30;
  private static final int METHOD_removePropertyChangeListener31 = 31;
  private static final int METHOD_firePropertyChange32 = 32;
  private static final int METHOD_getSize33 = 33;
  private static final int METHOD_getActionForKeyStroke34 = 34;
  private static final int METHOD_removeNotify35 = 35;
  private static final int METHOD_unregisterKeyboardAction36 = 36;
  private static final int METHOD_reshape37 = 37;
  private static final int METHOD_addNotify38 = 38;
  private static final int METHOD_print39 = 39;
  private static final int METHOD_resetKeyboardActions40 = 40;
  private static final int METHOD_requestDefaultFocus41 = 41;
  private static final int METHOD_setInputMap42 = 42;
  private static final int METHOD_getPropertyChangeListeners43 = 43;
  private static final int METHOD_getBounds44 = 44;
  private static final int METHOD_scrollRectToVisible45 = 45;
  private static final int METHOD_putClientProperty46 = 46;
  private static final int METHOD_update47 = 47;
  private static final int METHOD_computeVisibleRect48 = 48;
  private static final int METHOD_getToolTipLocation49 = 49;
  private static final int METHOD_setDefaultLocale50 = 50;
  private static final int METHOD_requestFocus51 = 51;
  private static final int METHOD_getLocation52 = 52;
  private static final int METHOD_requestFocusInWindow53 = 53;
  private static final int METHOD_requestFocus54 = 54;
  private static final int METHOD_enable55 = 55;
  private static final int METHOD_removeAll56 = 56;
  private static final int METHOD_insets57 = 57;
  private static final int METHOD_add58 = 58;
  private static final int METHOD_add59 = 59;
  private static final int METHOD_remove60 = 60;
  private static final int METHOD_getComponentAt61 = 61;
  private static final int METHOD_applyComponentOrientation62 = 62;
  private static final int METHOD_invalidate63 = 63;
  private static final int METHOD_transferFocusDownCycle64 = 64;
  private static final int METHOD_transferFocusBackward65 = 65;
  private static final int METHOD_minimumSize66 = 66;
  private static final int METHOD_findComponentAt67 = 67;
  private static final int METHOD_isFocusCycleRoot68 = 68;
  private static final int METHOD_add69 = 69;
  private static final int METHOD_add70 = 70;
  private static final int METHOD_list71 = 71;
  private static final int METHOD_isAncestorOf72 = 72;
  private static final int METHOD_paintComponents73 = 73;
  private static final int METHOD_getComponentAt74 = 74;
  private static final int METHOD_add75 = 75;
  private static final int METHOD_areFocusTraversalKeysSet76 = 76;
  private static final int METHOD_locate77 = 77;
  private static final int METHOD_deliverEvent78 = 78;
  private static final int METHOD_printComponents79 = 79;
  private static final int METHOD_layout80 = 80;
  private static final int METHOD_remove81 = 81;
  private static final int METHOD_preferredSize82 = 82;
  private static final int METHOD_findComponentAt83 = 83;
  private static final int METHOD_validate84 = 84;
  private static final int METHOD_list85 = 85;
  private static final int METHOD_doLayout86 = 86;
  private static final int METHOD_countComponents87 = 87;
  private static final int METHOD_inside88 = 88;
  private static final int METHOD_add89 = 89;
  private static final int METHOD_handleEvent90 = 90;
  private static final int METHOD_createImage91 = 91;
  private static final int METHOD_dispatchEvent92 = 92;
  private static final int METHOD_mouseMove93 = 93;
  private static final int METHOD_getLocation94 = 94;
  private static final int METHOD_transferFocusUpCycle95 = 95;
  private static final int METHOD_list96 = 96;
  private static final int METHOD_action97 = 97;
  private static final int METHOD_setSize98 = 98;
  private static final int METHOD_paintAll99 = 99;
  private static final int METHOD_size100 = 100;
  private static final int METHOD_postEvent101 = 101;
  private static final int METHOD_mouseEnter102 = 102;
  private static final int METHOD_hasFocus103 = 103;
  private static final int METHOD_move104 = 104;
  private static final int METHOD_location105 = 105;
  private static final int METHOD_mouseExit106 = 106;
  private static final int METHOD_transferFocus107 = 107;
  private static final int METHOD_nextFocus108 = 108;
  private static final int METHOD_getFontMetrics109 = 109;
  private static final int METHOD_remove110 = 110;
  private static final int METHOD_getSize111 = 111;
  private static final int METHOD_repaint112 = 112;
  private static final int METHOD_mouseUp113 = 113;
  private static final int METHOD_keyDown114 = 114;
  private static final int METHOD_list115 = 115;
  private static final int METHOD_lostFocus116 = 116;
  private static final int METHOD_setLocation117 = 117;
  private static final int METHOD_mouseDown118 = 118;
  private static final int METHOD_resize119 = 119;
  private static final int METHOD_imageUpdate120 = 120;
  private static final int METHOD_keyUp121 = 121;
  private static final int METHOD_repaint122 = 122;
  private static final int METHOD_repaint123 = 123;
  private static final int METHOD_show124 = 124;
  private static final int METHOD_list125 = 125;
  private static final int METHOD_checkImage126 = 126;
  private static final int METHOD_checkImage127 = 127;
  private static final int METHOD_toString128 = 128;
  private static final int METHOD_show129 = 129;
  private static final int METHOD_prepareImage130 = 130;
  private static final int METHOD_prepareImage131 = 131;
  private static final int METHOD_hide132 = 132;
  private static final int METHOD_createImage133 = 133;
  private static final int METHOD_resize134 = 134;
  private static final int METHOD_createVolatileImage135 = 135;
  private static final int METHOD_setBounds136 = 136;
  private static final int METHOD_bounds137 = 137;
  private static final int METHOD_contains138 = 138;
  private static final int METHOD_enable139 = 139;
  private static final int METHOD_mouseDrag140 = 140;
  private static final int METHOD_setSize141 = 141;
  private static final int METHOD_setLocation142 = 142;
  private static final int METHOD_enableInputMethods143 = 143;
  private static final int METHOD_createVolatileImage144 = 144;
  private static final int METHOD_gotFocus145 = 145;

  // Method array 
  private static MethodDescriptor[] methods = new MethodDescriptor[146];

  static {
    try {
      methods[METHOD_paint0] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("paint", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paint0].setDisplayName ( "" );
      methods[METHOD_updateUI1] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("updateUI", new Class[] {}));
      methods[METHOD_updateUI1].setDisplayName ( "" );
      methods[METHOD_isLightweightComponent2] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class}));
      methods[METHOD_isLightweightComponent2].setDisplayName ( "" );
      methods[METHOD_firePropertyChange3] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE}));
      methods[METHOD_firePropertyChange3].setDisplayName ( "" );
      methods[METHOD_addPropertyChangeListener4] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
      methods[METHOD_addPropertyChangeListener4].setDisplayName ( "" );
      methods[METHOD_grabFocus5] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("grabFocus", new Class[] {}));
      methods[METHOD_grabFocus5].setDisplayName ( "" );
      methods[METHOD_disable6] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("disable", new Class[] {}));
      methods[METHOD_disable6].setDisplayName ( "" );
      methods[METHOD_getToolTipText7] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class}));
      methods[METHOD_getToolTipText7].setDisplayName ( "" );
      methods[METHOD_firePropertyChange8] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Byte.TYPE, Byte.TYPE}));
      methods[METHOD_firePropertyChange8].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction9] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, javax.swing.KeyStroke.class, Integer.TYPE}));
      methods[METHOD_registerKeyboardAction9].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction10] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE}));
      methods[METHOD_registerKeyboardAction10].setDisplayName ( "" );
      methods[METHOD_paintImmediately11] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("paintImmediately", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_paintImmediately11].setDisplayName ( "" );
      methods[METHOD_printAll12] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("printAll", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_printAll12].setDisplayName ( "" );
      methods[METHOD_revalidate13] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("revalidate", new Class[] {}));
      methods[METHOD_revalidate13].setDisplayName ( "" );
      methods[METHOD_createToolTip14] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("createToolTip", new Class[] {}));
      methods[METHOD_createToolTip14].setDisplayName ( "" );
      methods[METHOD_getInputMap15] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getInputMap", new Class[] {Integer.TYPE}));
      methods[METHOD_getInputMap15].setDisplayName ( "" );
      methods[METHOD_getDefaultLocale16] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getDefaultLocale", new Class[] {}));
      methods[METHOD_getDefaultLocale16].setDisplayName ( "" );
      methods[METHOD_paintImmediately17] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_paintImmediately17].setDisplayName ( "" );
      methods[METHOD_getInsets18] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getInsets", new Class[] {java.awt.Insets.class}));
      methods[METHOD_getInsets18].setDisplayName ( "" );
      methods[METHOD_getConditionForKeyStroke19] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_getConditionForKeyStroke19].setDisplayName ( "" );
      methods[METHOD_firePropertyChange20] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_firePropertyChange20].setDisplayName ( "" );
      methods[METHOD_getListeners21] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getListeners", new Class[] {java.lang.Class.class}));
      methods[METHOD_getListeners21].setDisplayName ( "" );
      methods[METHOD_getInputMap22] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getInputMap", new Class[] {}));
      methods[METHOD_getInputMap22].setDisplayName ( "" );
      methods[METHOD_getClientProperty23] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class}));
      methods[METHOD_getClientProperty23].setDisplayName ( "" );
      methods[METHOD_firePropertyChange24] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Short.TYPE, Short.TYPE}));
      methods[METHOD_firePropertyChange24].setDisplayName ( "" );
      methods[METHOD_firePropertyChange25] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Long.TYPE, Long.TYPE}));
      methods[METHOD_firePropertyChange25].setDisplayName ( "" );
      methods[METHOD_contains26] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_contains26].setDisplayName ( "" );
      methods[METHOD_repaint27] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("repaint", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_repaint27].setDisplayName ( "" );
      methods[METHOD_repaint28] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint28].setDisplayName ( "" );
      methods[METHOD_firePropertyChange29] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Double.TYPE, Double.TYPE}));
      methods[METHOD_firePropertyChange29].setDisplayName ( "" );
      methods[METHOD_firePropertyChange30] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Character.TYPE, Character.TYPE}));
      methods[METHOD_firePropertyChange30].setDisplayName ( "" );
      methods[METHOD_removePropertyChangeListener31] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
      methods[METHOD_removePropertyChangeListener31].setDisplayName ( "" );
      methods[METHOD_firePropertyChange32] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Float.TYPE, Float.TYPE}));
      methods[METHOD_firePropertyChange32].setDisplayName ( "" );
      methods[METHOD_getSize33] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getSize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_getSize33].setDisplayName ( "" );
      methods[METHOD_getActionForKeyStroke34] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_getActionForKeyStroke34].setDisplayName ( "" );
      methods[METHOD_removeNotify35] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("removeNotify", new Class[] {}));
      methods[METHOD_removeNotify35].setDisplayName ( "" );
      methods[METHOD_unregisterKeyboardAction36] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_unregisterKeyboardAction36].setDisplayName ( "" );
      methods[METHOD_reshape37] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_reshape37].setDisplayName ( "" );
      methods[METHOD_addNotify38] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("addNotify", new Class[] {}));
      methods[METHOD_addNotify38].setDisplayName ( "" );
      methods[METHOD_print39] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("print", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_print39].setDisplayName ( "" );
      methods[METHOD_resetKeyboardActions40] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("resetKeyboardActions", new Class[] {}));
      methods[METHOD_resetKeyboardActions40].setDisplayName ( "" );
      methods[METHOD_requestDefaultFocus41] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("requestDefaultFocus", new Class[] {}));
      methods[METHOD_requestDefaultFocus41].setDisplayName ( "" );
      methods[METHOD_setInputMap42] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setInputMap", new Class[] {Integer.TYPE, javax.swing.InputMap.class}));
      methods[METHOD_setInputMap42].setDisplayName ( "" );
      methods[METHOD_getPropertyChangeListeners43] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class}));
      methods[METHOD_getPropertyChangeListeners43].setDisplayName ( "" );
      methods[METHOD_getBounds44] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_getBounds44].setDisplayName ( "" );
      methods[METHOD_scrollRectToVisible45] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_scrollRectToVisible45].setDisplayName ( "" );
      methods[METHOD_putClientProperty46] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class}));
      methods[METHOD_putClientProperty46].setDisplayName ( "" );
      methods[METHOD_update47] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("update", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_update47].setDisplayName ( "" );
      methods[METHOD_computeVisibleRect48] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_computeVisibleRect48].setDisplayName ( "" );
      methods[METHOD_getToolTipLocation49] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class}));
      methods[METHOD_getToolTipLocation49].setDisplayName ( "" );
      methods[METHOD_setDefaultLocale50] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class}));
      methods[METHOD_setDefaultLocale50].setDisplayName ( "" );
      methods[METHOD_requestFocus51] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("requestFocus", new Class[] {Boolean.TYPE}));
      methods[METHOD_requestFocus51].setDisplayName ( "" );
      methods[METHOD_getLocation52] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getLocation", new Class[] {java.awt.Point.class}));
      methods[METHOD_getLocation52].setDisplayName ( "" );
      methods[METHOD_requestFocusInWindow53] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("requestFocusInWindow", new Class[] {}));
      methods[METHOD_requestFocusInWindow53].setDisplayName ( "" );
      methods[METHOD_requestFocus54] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("requestFocus", new Class[] {}));
      methods[METHOD_requestFocus54].setDisplayName ( "" );
      methods[METHOD_enable55] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("enable", new Class[] {}));
      methods[METHOD_enable55].setDisplayName ( "" );
      methods[METHOD_removeAll56] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("removeAll", new Class[] {}));
      methods[METHOD_removeAll56].setDisplayName ( "" );
      methods[METHOD_insets57] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("insets", new Class[] {}));
      methods[METHOD_insets57].setDisplayName ( "" );
      methods[METHOD_add58] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.awt.Component.class, Integer.TYPE}));
      methods[METHOD_add58].setDisplayName ( "" );
      methods[METHOD_add59] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class, Integer.TYPE}));
      methods[METHOD_add59].setDisplayName ( "" );
      methods[METHOD_remove60] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("remove", new Class[] {Integer.TYPE}));
      methods[METHOD_remove60].setDisplayName ( "" );
      methods[METHOD_getComponentAt61] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_getComponentAt61].setDisplayName ( "" );
      methods[METHOD_applyComponentOrientation62] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class}));
      methods[METHOD_applyComponentOrientation62].setDisplayName ( "" );
      methods[METHOD_invalidate63] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("invalidate", new Class[] {}));
      methods[METHOD_invalidate63].setDisplayName ( "" );
      methods[METHOD_transferFocusDownCycle64] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("transferFocusDownCycle", new Class[] {}));
      methods[METHOD_transferFocusDownCycle64].setDisplayName ( "" );
      methods[METHOD_transferFocusBackward65] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("transferFocusBackward", new Class[] {}));
      methods[METHOD_transferFocusBackward65].setDisplayName ( "" );
      methods[METHOD_minimumSize66] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("minimumSize", new Class[] {}));
      methods[METHOD_minimumSize66].setDisplayName ( "" );
      methods[METHOD_findComponentAt67] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("findComponentAt", new Class[] {java.awt.Point.class}));
      methods[METHOD_findComponentAt67].setDisplayName ( "" );
      methods[METHOD_isFocusCycleRoot68] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class}));
      methods[METHOD_isFocusCycleRoot68].setDisplayName ( "" );
      methods[METHOD_add69] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class}));
      methods[METHOD_add69].setDisplayName ( "" );
      methods[METHOD_add70] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.awt.Component.class}));
      methods[METHOD_add70].setDisplayName ( "" );
      methods[METHOD_list71] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE}));
      methods[METHOD_list71].setDisplayName ( "" );
      methods[METHOD_isAncestorOf72] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class}));
      methods[METHOD_isAncestorOf72].setDisplayName ( "" );
      methods[METHOD_paintComponents73] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paintComponents73].setDisplayName ( "" );
      methods[METHOD_getComponentAt74] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getComponentAt", new Class[] {java.awt.Point.class}));
      methods[METHOD_getComponentAt74].setDisplayName ( "" );
      methods[METHOD_add75] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.lang.String.class, java.awt.Component.class}));
      methods[METHOD_add75].setDisplayName ( "" );
      methods[METHOD_areFocusTraversalKeysSet76] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE}));
      methods[METHOD_areFocusTraversalKeysSet76].setDisplayName ( "" );
      methods[METHOD_locate77] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_locate77].setDisplayName ( "" );
      methods[METHOD_deliverEvent78] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_deliverEvent78].setDisplayName ( "" );
      methods[METHOD_printComponents79] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_printComponents79].setDisplayName ( "" );
      methods[METHOD_layout80] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("layout", new Class[] {}));
      methods[METHOD_layout80].setDisplayName ( "" );
      methods[METHOD_remove81] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("remove", new Class[] {java.awt.Component.class}));
      methods[METHOD_remove81].setDisplayName ( "" );
      methods[METHOD_preferredSize82] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("preferredSize", new Class[] {}));
      methods[METHOD_preferredSize82].setDisplayName ( "" );
      methods[METHOD_findComponentAt83] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_findComponentAt83].setDisplayName ( "" );
      methods[METHOD_validate84] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("validate", new Class[] {}));
      methods[METHOD_validate84].setDisplayName ( "" );
      methods[METHOD_list85] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("list", new Class[] {java.io.PrintWriter.class, Integer.TYPE}));
      methods[METHOD_list85].setDisplayName ( "" );
      methods[METHOD_doLayout86] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("doLayout", new Class[] {}));
      methods[METHOD_doLayout86].setDisplayName ( "" );
      methods[METHOD_countComponents87] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("countComponents", new Class[] {}));
      methods[METHOD_countComponents87].setDisplayName ( "" );
      methods[METHOD_inside88] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_inside88].setDisplayName ( "" );
      methods[METHOD_add89] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("add", new Class[] {java.awt.PopupMenu.class}));
      methods[METHOD_add89].setDisplayName ( "" );
      methods[METHOD_handleEvent90] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("handleEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_handleEvent90].setDisplayName ( "" );
      methods[METHOD_createImage91] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class}));
      methods[METHOD_createImage91].setDisplayName ( "" );
      methods[METHOD_dispatchEvent92] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class}));
      methods[METHOD_dispatchEvent92].setDisplayName ( "" );
      methods[METHOD_mouseMove93] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseMove93].setDisplayName ( "" );
      methods[METHOD_getLocation94] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getLocation", new Class[] {}));
      methods[METHOD_getLocation94].setDisplayName ( "" );
      methods[METHOD_transferFocusUpCycle95] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("transferFocusUpCycle", new Class[] {}));
      methods[METHOD_transferFocusUpCycle95].setDisplayName ( "" );
      methods[METHOD_list96] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("list", new Class[] {}));
      methods[METHOD_list96].setDisplayName ( "" );
      methods[METHOD_action97] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_action97].setDisplayName ( "" );
      methods[METHOD_setSize98] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setSize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_setSize98].setDisplayName ( "" );
      methods[METHOD_paintAll99] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paintAll99].setDisplayName ( "" );
      methods[METHOD_size100] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("size", new Class[] {}));
      methods[METHOD_size100].setDisplayName ( "" );
      methods[METHOD_postEvent101] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("postEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_postEvent101].setDisplayName ( "" );
      methods[METHOD_mouseEnter102] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseEnter102].setDisplayName ( "" );
      methods[METHOD_hasFocus103] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("hasFocus", new Class[] {}));
      methods[METHOD_hasFocus103].setDisplayName ( "" );
      methods[METHOD_move104] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_move104].setDisplayName ( "" );
      methods[METHOD_location105] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("location", new Class[] {}));
      methods[METHOD_location105].setDisplayName ( "" );
      methods[METHOD_mouseExit106] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseExit106].setDisplayName ( "" );
      methods[METHOD_transferFocus107] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("transferFocus", new Class[] {}));
      methods[METHOD_transferFocus107].setDisplayName ( "" );
      methods[METHOD_nextFocus108] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("nextFocus", new Class[] {}));
      methods[METHOD_nextFocus108].setDisplayName ( "" );
      methods[METHOD_getFontMetrics109] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class}));
      methods[METHOD_getFontMetrics109].setDisplayName ( "" );
      methods[METHOD_remove110] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("remove", new Class[] {java.awt.MenuComponent.class}));
      methods[METHOD_remove110].setDisplayName ( "" );
      methods[METHOD_getSize111] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("getSize", new Class[] {}));
      methods[METHOD_getSize111].setDisplayName ( "" );
      methods[METHOD_repaint112] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("repaint", new Class[] {Long.TYPE}));
      methods[METHOD_repaint112].setDisplayName ( "" );
      methods[METHOD_mouseUp113] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseUp113].setDisplayName ( "" );
      methods[METHOD_keyDown114] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE}));
      methods[METHOD_keyDown114].setDisplayName ( "" );
      methods[METHOD_list115] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("list", new Class[] {java.io.PrintStream.class}));
      methods[METHOD_list115].setDisplayName ( "" );
      methods[METHOD_lostFocus116] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_lostFocus116].setDisplayName ( "" );
      methods[METHOD_setLocation117] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setLocation", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setLocation117].setDisplayName ( "" );
      methods[METHOD_mouseDown118] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDown118].setDisplayName ( "" );
      methods[METHOD_resize119] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_resize119].setDisplayName ( "" );
      methods[METHOD_imageUpdate120] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_imageUpdate120].setDisplayName ( "" );
      methods[METHOD_keyUp121] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE}));
      methods[METHOD_keyUp121].setDisplayName ( "" );
      methods[METHOD_repaint122] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("repaint", new Class[] {}));
      methods[METHOD_repaint122].setDisplayName ( "" );
      methods[METHOD_repaint123] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("repaint", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint123].setDisplayName ( "" );
      methods[METHOD_show124] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("show", new Class[] {}));
      methods[METHOD_show124].setDisplayName ( "" );
      methods[METHOD_list125] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("list", new Class[] {java.io.PrintWriter.class}));
      methods[METHOD_list125].setDisplayName ( "" );
      methods[METHOD_checkImage126] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("checkImage", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, java.awt.image.ImageObserver.class}));
      methods[METHOD_checkImage126].setDisplayName ( "" );
      methods[METHOD_checkImage127] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class}));
      methods[METHOD_checkImage127].setDisplayName ( "" );
      methods[METHOD_toString128] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("toString", new Class[] {}));
      methods[METHOD_toString128].setDisplayName ( "" );
      methods[METHOD_show129] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("show", new Class[] {Boolean.TYPE}));
      methods[METHOD_show129].setDisplayName ( "" );
      methods[METHOD_prepareImage130] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class}));
      methods[METHOD_prepareImage130].setDisplayName ( "" );
      methods[METHOD_prepareImage131] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, java.awt.image.ImageObserver.class}));
      methods[METHOD_prepareImage131].setDisplayName ( "" );
      methods[METHOD_hide132] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("hide", new Class[] {}));
      methods[METHOD_hide132].setDisplayName ( "" );
      methods[METHOD_createImage133] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("createImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createImage133].setDisplayName ( "" );
      methods[METHOD_resize134] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("resize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_resize134].setDisplayName ( "" );
      methods[METHOD_createVolatileImage135] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE, java.awt.ImageCapabilities.class}));
      methods[METHOD_createVolatileImage135].setDisplayName ( "" );
      methods[METHOD_setBounds136] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setBounds136].setDisplayName ( "" );
      methods[METHOD_bounds137] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("bounds", new Class[] {}));
      methods[METHOD_bounds137].setDisplayName ( "" );
      methods[METHOD_contains138] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("contains", new Class[] {java.awt.Point.class}));
      methods[METHOD_contains138].setDisplayName ( "" );
      methods[METHOD_enable139] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("enable", new Class[] {Boolean.TYPE}));
      methods[METHOD_enable139].setDisplayName ( "" );
      methods[METHOD_mouseDrag140] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDrag140].setDisplayName ( "" );
      methods[METHOD_setSize141] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setSize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setSize141].setDisplayName ( "" );
      methods[METHOD_setLocation142] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("setLocation", new Class[] {java.awt.Point.class}));
      methods[METHOD_setLocation142].setDisplayName ( "" );
      methods[METHOD_enableInputMethods143] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE}));
      methods[METHOD_enableInputMethods143].setDisplayName ( "" );
      methods[METHOD_createVolatileImage144] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createVolatileImage144].setDisplayName ( "" );
      methods[METHOD_gotFocus145] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.ColorPanelBean.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_gotFocus145].setDisplayName ( "" );
    }
    catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
}//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = "/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/ColorPanelBean.gif";//GEN-BEGIN:Icons
  private static String iconNameC32 = null;
  private static String iconNameM16 = null;
  private static String iconNameM32 = null;//GEN-END:Icons

  private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
  private static final int defaultEventIndex = -1;//GEN-END:Idx


    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
	return beanDescriptor;
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties;
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * 
     * @return  An array of EventSetDescriptors describing the kinds of 
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return eventSets;
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * 
     * @return  An array of MethodDescriptors describing the methods 
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return methods;
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are 
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean. 
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconMono32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
	default: return null;
        }
    }

}

