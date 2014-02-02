package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.beans.*;

public class StatusBeanBeanInfo extends SimpleBeanInfo {


  // Bean descriptor //GEN-FIRST:BeanDescriptor
  private static BeanDescriptor beanDescriptor = new BeanDescriptor  ( StatusBean.class , null );

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
  private static final int PROPERTY_taStatus = 63;
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
      properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", StatusBean.class, "getRegisteredKeyStrokes", null );
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", StatusBean.class, "isValid", null );
      properties[PROPERTY_y] = new PropertyDescriptor ( "y", StatusBean.class, "getY", null );
      properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", StatusBean.class, "getInsets", null );
      properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", StatusBean.class, "isFocusCycleRoot", "setFocusCycleRoot" );
      properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", StatusBean.class, "isMaximumSizeSet", null );
      properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", StatusBean.class, "isPreferredSizeSet", null );
      properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", StatusBean.class, "getUIClassID", null );
      properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", StatusBean.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
      properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor ( "propertyChangeListeners", StatusBean.class, "getPropertyChangeListeners", null );
      properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", StatusBean.class, "getAlignmentY", "setAlignmentY" );
      properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", StatusBean.class, "isDoubleBuffered", "setDoubleBuffered" );
      properties[PROPERTY_font] = new PropertyDescriptor ( "font", StatusBean.class, "getFont", "setFont" );
      properties[PROPERTY_focusListeners] = new PropertyDescriptor ( "focusListeners", StatusBean.class, "getFocusListeners", null );
      properties[PROPERTY_width] = new PropertyDescriptor ( "width", StatusBean.class, "getWidth", null );
      properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor ( "mouseMotionListeners", StatusBean.class, "getMouseMotionListeners", null );
      properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", StatusBean.class, "getForeground", "setForeground" );
      properties[PROPERTY_componentListeners] = new PropertyDescriptor ( "componentListeners", StatusBean.class, "getComponentListeners", null );
      properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", StatusBean.class, "getMaximumSize", "setMaximumSize" );
      properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", StatusBean.class, "isEnabled", "setEnabled" );
      properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", StatusBean.class, "getInputVerifier", "setInputVerifier" );
      properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", StatusBean.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
      properties[PROPERTY_containerListeners] = new PropertyDescriptor ( "containerListeners", StatusBean.class, "getContainerListeners", null );
      properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", StatusBean.class, "isFocusTraversable", null );
      properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", StatusBean.class, "getToolTipText", "setToolTipText" );
      properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", StatusBean.class, "getInputMethodRequests", null );
      properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", StatusBean.class, "getMinimumSize", "setMinimumSize" );
      properties[PROPERTY_ancestorListeners] = new PropertyDescriptor ( "ancestorListeners", StatusBean.class, "getAncestorListeners", null );
      properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", StatusBean.class, "getGraphicsConfiguration", null );
      properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", StatusBean.class, "getParent", null );
      properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor ( "focusTraversalPolicySet", StatusBean.class, "isFocusTraversalPolicySet", null );
      properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor ( "mouseWheelListeners", StatusBean.class, "getMouseWheelListeners", null );
      properties[PROPERTY_height] = new PropertyDescriptor ( "height", StatusBean.class, "getHeight", null );
      properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", StatusBean.class, "isOpaque", "setOpaque" );
      properties[PROPERTY_keyListeners] = new PropertyDescriptor ( "keyListeners", StatusBean.class, "getKeyListeners", null );
      properties[PROPERTY_foregroundSet] = new PropertyDescriptor ( "foregroundSet", StatusBean.class, "isForegroundSet", null );
      properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", StatusBean.class, "getAccessibleContext", null );
      properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor ( "focusTraversalPolicy", StatusBean.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy" );
      properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor ( "hierarchyBoundsListeners", StatusBean.class, "getHierarchyBoundsListeners", null );
      properties[PROPERTY_UI] = new PropertyDescriptor ( "UI", StatusBean.class, "getUI", "setUI" );
      properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", StatusBean.class, "isPaintingTile", null );
      properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor ( "vetoableChangeListeners", StatusBean.class, "getVetoableChangeListeners", null );
      properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor ( "hierarchyListeners", StatusBean.class, "getHierarchyListeners", null );
      properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor ( "focusTraversalKeysEnabled", StatusBean.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled" );
      properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", StatusBean.class, "getColorModel", null );
      properties[PROPERTY_x] = new PropertyDescriptor ( "x", StatusBean.class, "getX", null );
      properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", StatusBean.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
      properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", StatusBean.class, "getVisibleRect", null );
      properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", StatusBean.class, "isVisible", "setVisible" );
      properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", StatusBean.class, "getRootPane", null );
      properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", StatusBean.class, "getTreeLock", null );
      properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor ( "focusCycleRootAncestor", StatusBean.class, "getFocusCycleRootAncestor", null );
      properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", StatusBean.class, "getPeer", null );
      properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", StatusBean.class, "getDropTarget", "setDropTarget" );
      properties[PROPERTY_transferHandler] = new PropertyDescriptor ( "transferHandler", StatusBean.class, "getTransferHandler", "setTransferHandler" );
      properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", StatusBean.class, "getLocale", "setLocale" );
      properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor ( "ignoreRepaint", StatusBean.class, "getIgnoreRepaint", "setIgnoreRepaint" );
      properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", StatusBean.class, "getCursor", "setCursor" );
      properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", StatusBean.class, "getAlignmentX", "setAlignmentX" );
      properties[PROPERTY_backgroundSet] = new PropertyDescriptor ( "backgroundSet", StatusBean.class, "isBackgroundSet", null );
      properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", StatusBean.class, "isOptimizedDrawingEnabled", null );
      properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", StatusBean.class, "getActionMap", "setActionMap" );
      properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", StatusBean.class, "isShowing", null );
      properties[PROPERTY_taStatus] = new PropertyDescriptor ( "taStatus", StatusBean.class, "getTaStatus", null );
      properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", StatusBean.class, "getToolkit", null );
      properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", StatusBean.class, "getNextFocusableComponent", "setNextFocusableComponent" );
      properties[PROPERTY_focusOwner] = new PropertyDescriptor ( "focusOwner", StatusBean.class, "isFocusOwner", null );
      properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", StatusBean.class, "getAutoscrolls", "setAutoscrolls" );
      properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", StatusBean.class, "getBounds", "setBounds" );
      properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor ( "inputMethodListeners", StatusBean.class, "getInputMethodListeners", null );
      properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", StatusBean.class, "isMinimumSizeSet", null );
      properties[PROPERTY_focusable] = new PropertyDescriptor ( "focusable", StatusBean.class, "isFocusable", "setFocusable" );
      properties[PROPERTY_background] = new PropertyDescriptor ( "background", StatusBean.class, "getBackground", "setBackground" );
      properties[PROPERTY_cursorSet] = new PropertyDescriptor ( "cursorSet", StatusBean.class, "isCursorSet", null );
      properties[PROPERTY_border] = new PropertyDescriptor ( "border", StatusBean.class, "getBorder", "setBorder" );
      properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", StatusBean.class, "getLayout", "setLayout" );
      properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", StatusBean.class, "getPreferredSize", "setPreferredSize" );
      properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", StatusBean.class, "getTopLevelAncestor", null );
      properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", StatusBean.class, "isDisplayable", null );
      properties[PROPERTY_mouseListeners] = new PropertyDescriptor ( "mouseListeners", StatusBean.class, "getMouseListeners", null );
      properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", StatusBean.class, "isValidateRoot", null );
      properties[PROPERTY_components] = new PropertyDescriptor ( "components", StatusBean.class, "getComponents", null );
      properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", StatusBean.class, "isManagingFocus", null );
      properties[PROPERTY_fontSet] = new PropertyDescriptor ( "fontSet", StatusBean.class, "isFontSet", null );
      properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", StatusBean.class, "getComponentOrientation", "setComponentOrientation" );
      properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", StatusBean.class, "getComponentCount", null );
      properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", StatusBean.class, "isLightweight", null );
      properties[PROPERTY_name] = new PropertyDescriptor ( "name", StatusBean.class, "getName", "setName" );
      properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", StatusBean.class, "getGraphics", null );
      properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", StatusBean.class, "getInputContext", null );
      properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", StatusBean.class, "getLocationOnScreen", null );
      properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", StatusBean.class, null, null, "getComponent", null );
      properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor ( "focusTraversalKeys", StatusBean.class, null, null, "getFocusTraversalKeys", "setFocusTraversalKeys" );
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
      eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"inputMethodTextChanged", "caretPositionChanged"}, "addInputMethodListener", "removeInputMethodListener" );
      eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseMoved", "mouseDragged"}, "addMouseMotionListener", "removeMouseMotionListener" );
      eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" );
      eventSets[EVENT_containerListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener" );
      eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorMoved", "ancestorRemoved", "ancestorAdded"}, "addAncestorListener", "removeAncestorListener" );
      eventSets[EVENT_focusListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusLost", "focusGained"}, "addFocusListener", "removeFocusListener" );
      eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
      eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
      eventSets[EVENT_keyListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyReleased", "keyPressed", "keyTyped"}, "addKeyListener", "removeKeyListener" );
      eventSets[EVENT_componentListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentResized", "componentMoved", "componentHidden", "componentShown"}, "addComponentListener", "removeComponentListener" );
      eventSets[EVENT_mouseListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mouseEntered", "mouseExited", "mouseClicked", "mouseReleased", "mousePressed"}, "addMouseListener", "removeMouseListener" );
      eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" );
      eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[] {"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

}//GEN-LAST:Events

  // Method identifiers //GEN-FIRST:Methods
  private static final int METHOD_printlnStatus0 = 0;
  private static final int METHOD_printStatus1 = 1;
  private static final int METHOD_printlnStatus2 = 2;
  private static final int METHOD_printStatus3 = 3;
  private static final int METHOD_clearStatus4 = 4;
  private static final int METHOD_updateUI5 = 5;
  private static final int METHOD_isLightweightComponent6 = 6;
  private static final int METHOD_firePropertyChange7 = 7;
  private static final int METHOD_addPropertyChangeListener8 = 8;
  private static final int METHOD_grabFocus9 = 9;
  private static final int METHOD_disable10 = 10;
  private static final int METHOD_getToolTipText11 = 11;
  private static final int METHOD_firePropertyChange12 = 12;
  private static final int METHOD_registerKeyboardAction13 = 13;
  private static final int METHOD_registerKeyboardAction14 = 14;
  private static final int METHOD_paintImmediately15 = 15;
  private static final int METHOD_printAll16 = 16;
  private static final int METHOD_revalidate17 = 17;
  private static final int METHOD_createToolTip18 = 18;
  private static final int METHOD_getInputMap19 = 19;
  private static final int METHOD_getDefaultLocale20 = 20;
  private static final int METHOD_paintImmediately21 = 21;
  private static final int METHOD_getInsets22 = 22;
  private static final int METHOD_getConditionForKeyStroke23 = 23;
  private static final int METHOD_firePropertyChange24 = 24;
  private static final int METHOD_getListeners25 = 25;
  private static final int METHOD_getInputMap26 = 26;
  private static final int METHOD_getClientProperty27 = 27;
  private static final int METHOD_firePropertyChange28 = 28;
  private static final int METHOD_firePropertyChange29 = 29;
  private static final int METHOD_contains30 = 30;
  private static final int METHOD_repaint31 = 31;
  private static final int METHOD_repaint32 = 32;
  private static final int METHOD_firePropertyChange33 = 33;
  private static final int METHOD_firePropertyChange34 = 34;
  private static final int METHOD_removePropertyChangeListener35 = 35;
  private static final int METHOD_firePropertyChange36 = 36;
  private static final int METHOD_getSize37 = 37;
  private static final int METHOD_getActionForKeyStroke38 = 38;
  private static final int METHOD_removeNotify39 = 39;
  private static final int METHOD_unregisterKeyboardAction40 = 40;
  private static final int METHOD_reshape41 = 41;
  private static final int METHOD_addNotify42 = 42;
  private static final int METHOD_print43 = 43;
  private static final int METHOD_resetKeyboardActions44 = 44;
  private static final int METHOD_requestDefaultFocus45 = 45;
  private static final int METHOD_setInputMap46 = 46;
  private static final int METHOD_getPropertyChangeListeners47 = 47;
  private static final int METHOD_paint48 = 48;
  private static final int METHOD_getBounds49 = 49;
  private static final int METHOD_scrollRectToVisible50 = 50;
  private static final int METHOD_putClientProperty51 = 51;
  private static final int METHOD_update52 = 52;
  private static final int METHOD_computeVisibleRect53 = 53;
  private static final int METHOD_getToolTipLocation54 = 54;
  private static final int METHOD_setDefaultLocale55 = 55;
  private static final int METHOD_requestFocus56 = 56;
  private static final int METHOD_getLocation57 = 57;
  private static final int METHOD_requestFocusInWindow58 = 58;
  private static final int METHOD_requestFocus59 = 59;
  private static final int METHOD_enable60 = 60;
  private static final int METHOD_removeAll61 = 61;
  private static final int METHOD_insets62 = 62;
  private static final int METHOD_add63 = 63;
  private static final int METHOD_add64 = 64;
  private static final int METHOD_remove65 = 65;
  private static final int METHOD_getComponentAt66 = 66;
  private static final int METHOD_applyComponentOrientation67 = 67;
  private static final int METHOD_invalidate68 = 68;
  private static final int METHOD_transferFocusDownCycle69 = 69;
  private static final int METHOD_transferFocusBackward70 = 70;
  private static final int METHOD_minimumSize71 = 71;
  private static final int METHOD_findComponentAt72 = 72;
  private static final int METHOD_isFocusCycleRoot73 = 73;
  private static final int METHOD_add74 = 74;
  private static final int METHOD_add75 = 75;
  private static final int METHOD_list76 = 76;
  private static final int METHOD_isAncestorOf77 = 77;
  private static final int METHOD_paintComponents78 = 78;
  private static final int METHOD_getComponentAt79 = 79;
  private static final int METHOD_add80 = 80;
  private static final int METHOD_areFocusTraversalKeysSet81 = 81;
  private static final int METHOD_locate82 = 82;
  private static final int METHOD_deliverEvent83 = 83;
  private static final int METHOD_printComponents84 = 84;
  private static final int METHOD_layout85 = 85;
  private static final int METHOD_remove86 = 86;
  private static final int METHOD_preferredSize87 = 87;
  private static final int METHOD_findComponentAt88 = 88;
  private static final int METHOD_validate89 = 89;
  private static final int METHOD_list90 = 90;
  private static final int METHOD_doLayout91 = 91;
  private static final int METHOD_countComponents92 = 92;
  private static final int METHOD_inside93 = 93;
  private static final int METHOD_add94 = 94;
  private static final int METHOD_handleEvent95 = 95;
  private static final int METHOD_createImage96 = 96;
  private static final int METHOD_dispatchEvent97 = 97;
  private static final int METHOD_mouseMove98 = 98;
  private static final int METHOD_getLocation99 = 99;
  private static final int METHOD_transferFocusUpCycle100 = 100;
  private static final int METHOD_list101 = 101;
  private static final int METHOD_action102 = 102;
  private static final int METHOD_setSize103 = 103;
  private static final int METHOD_paintAll104 = 104;
  private static final int METHOD_size105 = 105;
  private static final int METHOD_postEvent106 = 106;
  private static final int METHOD_mouseEnter107 = 107;
  private static final int METHOD_hasFocus108 = 108;
  private static final int METHOD_move109 = 109;
  private static final int METHOD_location110 = 110;
  private static final int METHOD_mouseExit111 = 111;
  private static final int METHOD_transferFocus112 = 112;
  private static final int METHOD_nextFocus113 = 113;
  private static final int METHOD_getFontMetrics114 = 114;
  private static final int METHOD_remove115 = 115;
  private static final int METHOD_getSize116 = 116;
  private static final int METHOD_repaint117 = 117;
  private static final int METHOD_mouseUp118 = 118;
  private static final int METHOD_keyDown119 = 119;
  private static final int METHOD_list120 = 120;
  private static final int METHOD_lostFocus121 = 121;
  private static final int METHOD_setLocation122 = 122;
  private static final int METHOD_mouseDown123 = 123;
  private static final int METHOD_resize124 = 124;
  private static final int METHOD_imageUpdate125 = 125;
  private static final int METHOD_keyUp126 = 126;
  private static final int METHOD_repaint127 = 127;
  private static final int METHOD_repaint128 = 128;
  private static final int METHOD_show129 = 129;
  private static final int METHOD_list130 = 130;
  private static final int METHOD_checkImage131 = 131;
  private static final int METHOD_checkImage132 = 132;
  private static final int METHOD_toString133 = 133;
  private static final int METHOD_show134 = 134;
  private static final int METHOD_prepareImage135 = 135;
  private static final int METHOD_prepareImage136 = 136;
  private static final int METHOD_hide137 = 137;
  private static final int METHOD_createImage138 = 138;
  private static final int METHOD_resize139 = 139;
  private static final int METHOD_createVolatileImage140 = 140;
  private static final int METHOD_setBounds141 = 141;
  private static final int METHOD_bounds142 = 142;
  private static final int METHOD_contains143 = 143;
  private static final int METHOD_enable144 = 144;
  private static final int METHOD_mouseDrag145 = 145;
  private static final int METHOD_setSize146 = 146;
  private static final int METHOD_setLocation147 = 147;
  private static final int METHOD_enableInputMethods148 = 148;
  private static final int METHOD_createVolatileImage149 = 149;
  private static final int METHOD_gotFocus150 = 150;

  // Method array 
  private static MethodDescriptor[] methods = new MethodDescriptor[151];

  static {
    try {
      methods[METHOD_printlnStatus0] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printlnStatus", new Class[] {String.class}));
      methods[METHOD_printlnStatus0].setDisplayName ( "" );
      methods[METHOD_printStatus1] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printStatus", new Class[] {String.class}));
      methods[METHOD_printStatus1].setDisplayName ( "" );
      methods[METHOD_printlnStatus2] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printlnStatus", new Class[] {String.class, Boolean.TYPE}));
      methods[METHOD_printlnStatus2].setDisplayName ( "" );
      methods[METHOD_printStatus3] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printStatus", new Class[] {String.class, Boolean.TYPE}));
      methods[METHOD_printStatus3].setDisplayName ( "" );
      methods[METHOD_clearStatus4] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("clearStatus", new Class[] {}));
      methods[METHOD_clearStatus4].setDisplayName ( "" );
      methods[METHOD_updateUI5] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("updateUI", new Class[] {}));
      methods[METHOD_updateUI5].setDisplayName ( "" );
      methods[METHOD_isLightweightComponent6] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("isLightweightComponent", new Class[] {java.awt.Component.class}));
      methods[METHOD_isLightweightComponent6].setDisplayName ( "" );
      methods[METHOD_firePropertyChange7] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Boolean.TYPE, Boolean.TYPE}));
      methods[METHOD_firePropertyChange7].setDisplayName ( "" );
      methods[METHOD_addPropertyChangeListener8] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("addPropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
      methods[METHOD_addPropertyChangeListener8].setDisplayName ( "" );
      methods[METHOD_grabFocus9] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("grabFocus", new Class[] {}));
      methods[METHOD_grabFocus9].setDisplayName ( "" );
      methods[METHOD_disable10] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("disable", new Class[] {}));
      methods[METHOD_disable10].setDisplayName ( "" );
      methods[METHOD_getToolTipText11] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getToolTipText", new Class[] {java.awt.event.MouseEvent.class}));
      methods[METHOD_getToolTipText11].setDisplayName ( "" );
      methods[METHOD_firePropertyChange12] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Byte.TYPE, Byte.TYPE}));
      methods[METHOD_firePropertyChange12].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction13] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, javax.swing.KeyStroke.class, Integer.TYPE}));
      methods[METHOD_registerKeyboardAction13].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction14] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("registerKeyboardAction", new Class[] {java.awt.event.ActionListener.class, java.lang.String.class, javax.swing.KeyStroke.class, Integer.TYPE}));
      methods[METHOD_registerKeyboardAction14].setDisplayName ( "" );
      methods[METHOD_paintImmediately15] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("paintImmediately", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_paintImmediately15].setDisplayName ( "" );
      methods[METHOD_printAll16] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printAll", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_printAll16].setDisplayName ( "" );
      methods[METHOD_revalidate17] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("revalidate", new Class[] {}));
      methods[METHOD_revalidate17].setDisplayName ( "" );
      methods[METHOD_createToolTip18] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("createToolTip", new Class[] {}));
      methods[METHOD_createToolTip18].setDisplayName ( "" );
      methods[METHOD_getInputMap19] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getInputMap", new Class[] {Integer.TYPE}));
      methods[METHOD_getInputMap19].setDisplayName ( "" );
      methods[METHOD_getDefaultLocale20] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getDefaultLocale", new Class[] {}));
      methods[METHOD_getDefaultLocale20].setDisplayName ( "" );
      methods[METHOD_paintImmediately21] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_paintImmediately21].setDisplayName ( "" );
      methods[METHOD_getInsets22] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getInsets", new Class[] {java.awt.Insets.class}));
      methods[METHOD_getInsets22].setDisplayName ( "" );
      methods[METHOD_getConditionForKeyStroke23] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getConditionForKeyStroke", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_getConditionForKeyStroke23].setDisplayName ( "" );
      methods[METHOD_firePropertyChange24] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_firePropertyChange24].setDisplayName ( "" );
      methods[METHOD_getListeners25] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getListeners", new Class[] {java.lang.Class.class}));
      methods[METHOD_getListeners25].setDisplayName ( "" );
      methods[METHOD_getInputMap26] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getInputMap", new Class[] {}));
      methods[METHOD_getInputMap26].setDisplayName ( "" );
      methods[METHOD_getClientProperty27] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getClientProperty", new Class[] {java.lang.Object.class}));
      methods[METHOD_getClientProperty27].setDisplayName ( "" );
      methods[METHOD_firePropertyChange28] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Short.TYPE, Short.TYPE}));
      methods[METHOD_firePropertyChange28].setDisplayName ( "" );
      methods[METHOD_firePropertyChange29] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Long.TYPE, Long.TYPE}));
      methods[METHOD_firePropertyChange29].setDisplayName ( "" );
      methods[METHOD_contains30] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_contains30].setDisplayName ( "" );
      methods[METHOD_repaint31] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("repaint", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_repaint31].setDisplayName ( "" );
      methods[METHOD_repaint32] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint32].setDisplayName ( "" );
      methods[METHOD_firePropertyChange33] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Double.TYPE, Double.TYPE}));
      methods[METHOD_firePropertyChange33].setDisplayName ( "" );
      methods[METHOD_firePropertyChange34] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Character.TYPE, Character.TYPE}));
      methods[METHOD_firePropertyChange34].setDisplayName ( "" );
      methods[METHOD_removePropertyChangeListener35] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("removePropertyChangeListener", new Class[] {java.lang.String.class, java.beans.PropertyChangeListener.class}));
      methods[METHOD_removePropertyChangeListener35].setDisplayName ( "" );
      methods[METHOD_firePropertyChange36] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("firePropertyChange", new Class[] {java.lang.String.class, Float.TYPE, Float.TYPE}));
      methods[METHOD_firePropertyChange36].setDisplayName ( "" );
      methods[METHOD_getSize37] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getSize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_getSize37].setDisplayName ( "" );
      methods[METHOD_getActionForKeyStroke38] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getActionForKeyStroke", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_getActionForKeyStroke38].setDisplayName ( "" );
      methods[METHOD_removeNotify39] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("removeNotify", new Class[] {}));
      methods[METHOD_removeNotify39].setDisplayName ( "" );
      methods[METHOD_unregisterKeyboardAction40] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("unregisterKeyboardAction", new Class[] {javax.swing.KeyStroke.class}));
      methods[METHOD_unregisterKeyboardAction40].setDisplayName ( "" );
      methods[METHOD_reshape41] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_reshape41].setDisplayName ( "" );
      methods[METHOD_addNotify42] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("addNotify", new Class[] {}));
      methods[METHOD_addNotify42].setDisplayName ( "" );
      methods[METHOD_print43] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("print", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_print43].setDisplayName ( "" );
      methods[METHOD_resetKeyboardActions44] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("resetKeyboardActions", new Class[] {}));
      methods[METHOD_resetKeyboardActions44].setDisplayName ( "" );
      methods[METHOD_requestDefaultFocus45] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("requestDefaultFocus", new Class[] {}));
      methods[METHOD_requestDefaultFocus45].setDisplayName ( "" );
      methods[METHOD_setInputMap46] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setInputMap", new Class[] {Integer.TYPE, javax.swing.InputMap.class}));
      methods[METHOD_setInputMap46].setDisplayName ( "" );
      methods[METHOD_getPropertyChangeListeners47] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getPropertyChangeListeners", new Class[] {java.lang.String.class}));
      methods[METHOD_getPropertyChangeListeners47].setDisplayName ( "" );
      methods[METHOD_paint48] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("paint", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paint48].setDisplayName ( "" );
      methods[METHOD_getBounds49] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getBounds", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_getBounds49].setDisplayName ( "" );
      methods[METHOD_scrollRectToVisible50] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("scrollRectToVisible", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_scrollRectToVisible50].setDisplayName ( "" );
      methods[METHOD_putClientProperty51] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("putClientProperty", new Class[] {java.lang.Object.class, java.lang.Object.class}));
      methods[METHOD_putClientProperty51].setDisplayName ( "" );
      methods[METHOD_update52] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("update", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_update52].setDisplayName ( "" );
      methods[METHOD_computeVisibleRect53] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("computeVisibleRect", new Class[] {java.awt.Rectangle.class}));
      methods[METHOD_computeVisibleRect53].setDisplayName ( "" );
      methods[METHOD_getToolTipLocation54] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getToolTipLocation", new Class[] {java.awt.event.MouseEvent.class}));
      methods[METHOD_getToolTipLocation54].setDisplayName ( "" );
      methods[METHOD_setDefaultLocale55] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setDefaultLocale", new Class[] {java.util.Locale.class}));
      methods[METHOD_setDefaultLocale55].setDisplayName ( "" );
      methods[METHOD_requestFocus56] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("requestFocus", new Class[] {Boolean.TYPE}));
      methods[METHOD_requestFocus56].setDisplayName ( "" );
      methods[METHOD_getLocation57] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getLocation", new Class[] {java.awt.Point.class}));
      methods[METHOD_getLocation57].setDisplayName ( "" );
      methods[METHOD_requestFocusInWindow58] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("requestFocusInWindow", new Class[] {}));
      methods[METHOD_requestFocusInWindow58].setDisplayName ( "" );
      methods[METHOD_requestFocus59] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("requestFocus", new Class[] {}));
      methods[METHOD_requestFocus59].setDisplayName ( "" );
      methods[METHOD_enable60] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("enable", new Class[] {}));
      methods[METHOD_enable60].setDisplayName ( "" );
      methods[METHOD_removeAll61] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("removeAll", new Class[] {}));
      methods[METHOD_removeAll61].setDisplayName ( "" );
      methods[METHOD_insets62] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("insets", new Class[] {}));
      methods[METHOD_insets62].setDisplayName ( "" );
      methods[METHOD_add63] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.awt.Component.class, Integer.TYPE}));
      methods[METHOD_add63].setDisplayName ( "" );
      methods[METHOD_add64] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class, Integer.TYPE}));
      methods[METHOD_add64].setDisplayName ( "" );
      methods[METHOD_remove65] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("remove", new Class[] {Integer.TYPE}));
      methods[METHOD_remove65].setDisplayName ( "" );
      methods[METHOD_getComponentAt66] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_getComponentAt66].setDisplayName ( "" );
      methods[METHOD_applyComponentOrientation67] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("applyComponentOrientation", new Class[] {java.awt.ComponentOrientation.class}));
      methods[METHOD_applyComponentOrientation67].setDisplayName ( "" );
      methods[METHOD_invalidate68] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("invalidate", new Class[] {}));
      methods[METHOD_invalidate68].setDisplayName ( "" );
      methods[METHOD_transferFocusDownCycle69] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("transferFocusDownCycle", new Class[] {}));
      methods[METHOD_transferFocusDownCycle69].setDisplayName ( "" );
      methods[METHOD_transferFocusBackward70] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("transferFocusBackward", new Class[] {}));
      methods[METHOD_transferFocusBackward70].setDisplayName ( "" );
      methods[METHOD_minimumSize71] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("minimumSize", new Class[] {}));
      methods[METHOD_minimumSize71].setDisplayName ( "" );
      methods[METHOD_findComponentAt72] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("findComponentAt", new Class[] {java.awt.Point.class}));
      methods[METHOD_findComponentAt72].setDisplayName ( "" );
      methods[METHOD_isFocusCycleRoot73] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("isFocusCycleRoot", new Class[] {java.awt.Container.class}));
      methods[METHOD_isFocusCycleRoot73].setDisplayName ( "" );
      methods[METHOD_add74] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.awt.Component.class, java.lang.Object.class}));
      methods[METHOD_add74].setDisplayName ( "" );
      methods[METHOD_add75] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.awt.Component.class}));
      methods[METHOD_add75].setDisplayName ( "" );
      methods[METHOD_list76] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("list", new Class[] {java.io.PrintStream.class, Integer.TYPE}));
      methods[METHOD_list76].setDisplayName ( "" );
      methods[METHOD_isAncestorOf77] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("isAncestorOf", new Class[] {java.awt.Component.class}));
      methods[METHOD_isAncestorOf77].setDisplayName ( "" );
      methods[METHOD_paintComponents78] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("paintComponents", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paintComponents78].setDisplayName ( "" );
      methods[METHOD_getComponentAt79] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getComponentAt", new Class[] {java.awt.Point.class}));
      methods[METHOD_getComponentAt79].setDisplayName ( "" );
      methods[METHOD_add80] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.lang.String.class, java.awt.Component.class}));
      methods[METHOD_add80].setDisplayName ( "" );
      methods[METHOD_areFocusTraversalKeysSet81] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("areFocusTraversalKeysSet", new Class[] {Integer.TYPE}));
      methods[METHOD_areFocusTraversalKeysSet81].setDisplayName ( "" );
      methods[METHOD_locate82] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_locate82].setDisplayName ( "" );
      methods[METHOD_deliverEvent83] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("deliverEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_deliverEvent83].setDisplayName ( "" );
      methods[METHOD_printComponents84] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("printComponents", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_printComponents84].setDisplayName ( "" );
      methods[METHOD_layout85] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("layout", new Class[] {}));
      methods[METHOD_layout85].setDisplayName ( "" );
      methods[METHOD_remove86] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("remove", new Class[] {java.awt.Component.class}));
      methods[METHOD_remove86].setDisplayName ( "" );
      methods[METHOD_preferredSize87] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("preferredSize", new Class[] {}));
      methods[METHOD_preferredSize87].setDisplayName ( "" );
      methods[METHOD_findComponentAt88] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_findComponentAt88].setDisplayName ( "" );
      methods[METHOD_validate89] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("validate", new Class[] {}));
      methods[METHOD_validate89].setDisplayName ( "" );
      methods[METHOD_list90] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("list", new Class[] {java.io.PrintWriter.class, Integer.TYPE}));
      methods[METHOD_list90].setDisplayName ( "" );
      methods[METHOD_doLayout91] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("doLayout", new Class[] {}));
      methods[METHOD_doLayout91].setDisplayName ( "" );
      methods[METHOD_countComponents92] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("countComponents", new Class[] {}));
      methods[METHOD_countComponents92].setDisplayName ( "" );
      methods[METHOD_inside93] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_inside93].setDisplayName ( "" );
      methods[METHOD_add94] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("add", new Class[] {java.awt.PopupMenu.class}));
      methods[METHOD_add94].setDisplayName ( "" );
      methods[METHOD_handleEvent95] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("handleEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_handleEvent95].setDisplayName ( "" );
      methods[METHOD_createImage96] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("createImage", new Class[] {java.awt.image.ImageProducer.class}));
      methods[METHOD_createImage96].setDisplayName ( "" );
      methods[METHOD_dispatchEvent97] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("dispatchEvent", new Class[] {java.awt.AWTEvent.class}));
      methods[METHOD_dispatchEvent97].setDisplayName ( "" );
      methods[METHOD_mouseMove98] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseMove", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseMove98].setDisplayName ( "" );
      methods[METHOD_getLocation99] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getLocation", new Class[] {}));
      methods[METHOD_getLocation99].setDisplayName ( "" );
      methods[METHOD_transferFocusUpCycle100] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("transferFocusUpCycle", new Class[] {}));
      methods[METHOD_transferFocusUpCycle100].setDisplayName ( "" );
      methods[METHOD_list101] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("list", new Class[] {}));
      methods[METHOD_list101].setDisplayName ( "" );
      methods[METHOD_action102] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("action", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_action102].setDisplayName ( "" );
      methods[METHOD_setSize103] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setSize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_setSize103].setDisplayName ( "" );
      methods[METHOD_paintAll104] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("paintAll", new Class[] {java.awt.Graphics.class}));
      methods[METHOD_paintAll104].setDisplayName ( "" );
      methods[METHOD_size105] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("size", new Class[] {}));
      methods[METHOD_size105].setDisplayName ( "" );
      methods[METHOD_postEvent106] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("postEvent", new Class[] {java.awt.Event.class}));
      methods[METHOD_postEvent106].setDisplayName ( "" );
      methods[METHOD_mouseEnter107] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseEnter", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseEnter107].setDisplayName ( "" );
      methods[METHOD_hasFocus108] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("hasFocus", new Class[] {}));
      methods[METHOD_hasFocus108].setDisplayName ( "" );
      methods[METHOD_move109] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_move109].setDisplayName ( "" );
      methods[METHOD_location110] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("location", new Class[] {}));
      methods[METHOD_location110].setDisplayName ( "" );
      methods[METHOD_mouseExit111] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseExit", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseExit111].setDisplayName ( "" );
      methods[METHOD_transferFocus112] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("transferFocus", new Class[] {}));
      methods[METHOD_transferFocus112].setDisplayName ( "" );
      methods[METHOD_nextFocus113] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("nextFocus", new Class[] {}));
      methods[METHOD_nextFocus113].setDisplayName ( "" );
      methods[METHOD_getFontMetrics114] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getFontMetrics", new Class[] {java.awt.Font.class}));
      methods[METHOD_getFontMetrics114].setDisplayName ( "" );
      methods[METHOD_remove115] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("remove", new Class[] {java.awt.MenuComponent.class}));
      methods[METHOD_remove115].setDisplayName ( "" );
      methods[METHOD_getSize116] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("getSize", new Class[] {}));
      methods[METHOD_getSize116].setDisplayName ( "" );
      methods[METHOD_repaint117] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("repaint", new Class[] {Long.TYPE}));
      methods[METHOD_repaint117].setDisplayName ( "" );
      methods[METHOD_mouseUp118] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseUp", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseUp118].setDisplayName ( "" );
      methods[METHOD_keyDown119] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("keyDown", new Class[] {java.awt.Event.class, Integer.TYPE}));
      methods[METHOD_keyDown119].setDisplayName ( "" );
      methods[METHOD_list120] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("list", new Class[] {java.io.PrintStream.class}));
      methods[METHOD_list120].setDisplayName ( "" );
      methods[METHOD_lostFocus121] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("lostFocus", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_lostFocus121].setDisplayName ( "" );
      methods[METHOD_setLocation122] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setLocation", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setLocation122].setDisplayName ( "" );
      methods[METHOD_mouseDown123] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseDown", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDown123].setDisplayName ( "" );
      methods[METHOD_resize124] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_resize124].setDisplayName ( "" );
      methods[METHOD_imageUpdate125] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("imageUpdate", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_imageUpdate125].setDisplayName ( "" );
      methods[METHOD_keyUp126] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("keyUp", new Class[] {java.awt.Event.class, Integer.TYPE}));
      methods[METHOD_keyUp126].setDisplayName ( "" );
      methods[METHOD_repaint127] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("repaint", new Class[] {}));
      methods[METHOD_repaint127].setDisplayName ( "" );
      methods[METHOD_repaint128] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("repaint", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint128].setDisplayName ( "" );
      methods[METHOD_show129] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("show", new Class[] {}));
      methods[METHOD_show129].setDisplayName ( "" );
      methods[METHOD_list130] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("list", new Class[] {java.io.PrintWriter.class}));
      methods[METHOD_list130].setDisplayName ( "" );
      methods[METHOD_checkImage131] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("checkImage", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, java.awt.image.ImageObserver.class}));
      methods[METHOD_checkImage131].setDisplayName ( "" );
      methods[METHOD_checkImage132] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("checkImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class}));
      methods[METHOD_checkImage132].setDisplayName ( "" );
      methods[METHOD_toString133] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("toString", new Class[] {}));
      methods[METHOD_toString133].setDisplayName ( "" );
      methods[METHOD_show134] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("show", new Class[] {Boolean.TYPE}));
      methods[METHOD_show134].setDisplayName ( "" );
      methods[METHOD_prepareImage135] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, java.awt.image.ImageObserver.class}));
      methods[METHOD_prepareImage135].setDisplayName ( "" );
      methods[METHOD_prepareImage136] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("prepareImage", new Class[] {java.awt.Image.class, Integer.TYPE, Integer.TYPE, java.awt.image.ImageObserver.class}));
      methods[METHOD_prepareImage136].setDisplayName ( "" );
      methods[METHOD_hide137] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("hide", new Class[] {}));
      methods[METHOD_hide137].setDisplayName ( "" );
      methods[METHOD_createImage138] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("createImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createImage138].setDisplayName ( "" );
      methods[METHOD_resize139] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("resize", new Class[] {java.awt.Dimension.class}));
      methods[METHOD_resize139].setDisplayName ( "" );
      methods[METHOD_createVolatileImage140] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE, java.awt.ImageCapabilities.class}));
      methods[METHOD_createVolatileImage140].setDisplayName ( "" );
      methods[METHOD_setBounds141] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setBounds141].setDisplayName ( "" );
      methods[METHOD_bounds142] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("bounds", new Class[] {}));
      methods[METHOD_bounds142].setDisplayName ( "" );
      methods[METHOD_contains143] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("contains", new Class[] {java.awt.Point.class}));
      methods[METHOD_contains143].setDisplayName ( "" );
      methods[METHOD_enable144] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("enable", new Class[] {Boolean.TYPE}));
      methods[METHOD_enable144].setDisplayName ( "" );
      methods[METHOD_mouseDrag145] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("mouseDrag", new Class[] {java.awt.Event.class, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDrag145].setDisplayName ( "" );
      methods[METHOD_setSize146] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setSize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setSize146].setDisplayName ( "" );
      methods[METHOD_setLocation147] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("setLocation", new Class[] {java.awt.Point.class}));
      methods[METHOD_setLocation147].setDisplayName ( "" );
      methods[METHOD_enableInputMethods148] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("enableInputMethods", new Class[] {Boolean.TYPE}));
      methods[METHOD_enableInputMethods148].setDisplayName ( "" );
      methods[METHOD_createVolatileImage149] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("createVolatileImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createVolatileImage149].setDisplayName ( "" );
      methods[METHOD_gotFocus150] = new MethodDescriptor ( jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.StatusBean.class.getMethod("gotFocus", new Class[] {java.awt.Event.class, java.lang.Object.class}));
      methods[METHOD_gotFocus150].setDisplayName ( "" );
    }
    catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
}//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = "/jp/ac/kumamoto_u/kuh/fc/jsato/swing_beans/StatusBean.gif";//GEN-BEGIN:Icons
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

