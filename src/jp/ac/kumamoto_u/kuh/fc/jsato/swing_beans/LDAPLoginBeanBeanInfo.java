package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.beans.*;

public class LDAPLoginBeanBeanInfo extends SimpleBeanInfo {

  // Property identifiers //GEN-FIRST:Properties
  private static final int PROPERTY_optimizedDrawingEnabled = 0;
  private static final int PROPERTY_colorModel = 1;
  private static final int PROPERTY_minimumSize = 2;
  private static final int PROPERTY_visible = 3;
  private static final int PROPERTY_toolkit = 4;
  private static final int PROPERTY_displayable = 5;
  private static final int PROPERTY_opaque = 6;
  private static final int PROPERTY_requestFocusEnabled = 7;
  private static final int PROPERTY_enabled = 8;
  private static final int PROPERTY_autoscrolls = 9;
  private static final int PROPERTY_y = 10;
  private static final int PROPERTY_x = 11;
  private static final int PROPERTY_accessibleContext = 12;
  private static final int PROPERTY_componentOrientation = 13;
  private static final int PROPERTY_components = 14;
  private static final int PROPERTY_managingFocus = 15;
  private static final int PROPERTY_inputMethodRequests = 16;
  private static final int PROPERTY_border = 17;
  private static final int PROPERTY_locale = 18;
  private static final int PROPERTY_insets = 19;
  private static final int PROPERTY_UIClassID = 20;
  private static final int PROPERTY_graphics = 21;
  private static final int PROPERTY_minimumSizeSet = 22;
  private static final int PROPERTY_actionMap = 23;
  private static final int PROPERTY_password = 24;
  private static final int PROPERTY_maximumSizeSet = 25;
  private static final int PROPERTY_alignmentY = 26;
  private static final int PROPERTY_alignmentX = 27;
  private static final int PROPERTY_locationOnScreen = 28;
  private static final int PROPERTY_cursor = 29;
  private static final int PROPERTY_registeredKeyStrokes = 30;
  private static final int PROPERTY_preferredSizeSet = 31;
  private static final int PROPERTY_visibleRect = 32;
  private static final int PROPERTY_bounds = 33;
  private static final int PROPERTY_inputContext = 34;
  private static final int PROPERTY_focusTraversable = 35;
  private static final int PROPERTY_font = 36;
  private static final int PROPERTY_inputVerifier = 37;
  private static final int PROPERTY_lightweight = 38;
  private static final int PROPERTY_paintingTile = 39;
  private static final int PROPERTY_maximumSize = 40;
  private static final int PROPERTY_layout = 41;
  private static final int PROPERTY_treeLock = 42;
  private static final int PROPERTY_bindDN = 43;
  private static final int PROPERTY_verifyInputWhenFocusTarget = 44;
  private static final int PROPERTY_foreground = 45;
  private static final int PROPERTY_preferredSize = 46;
  private static final int PROPERTY_debugGraphicsOptions = 47;
  private static final int PROPERTY_doubleBuffered = 48;
  private static final int PROPERTY_baseDN = 49;
  private static final int PROPERTY_showing = 50;
  private static final int PROPERTY_nextFocusableComponent = 51;
  private static final int PROPERTY_parent = 52;
  private static final int PROPERTY_port = 53;
  private static final int PROPERTY_peer = 54;
  private static final int PROPERTY_componentCount = 55;
  private static final int PROPERTY_graphicsConfiguration = 56;
  private static final int PROPERTY_host = 57;
  private static final int PROPERTY_height = 58;
  private static final int PROPERTY_valid = 59;
  private static final int PROPERTY_focusCycleRoot = 60;
  private static final int PROPERTY_width = 61;
  private static final int PROPERTY_toolTipText = 62;
  private static final int PROPERTY_background = 63;
  private static final int PROPERTY_validateRoot = 64;
  private static final int PROPERTY_topLevelAncestor = 65;
  private static final int PROPERTY_dropTarget = 66;
  private static final int PROPERTY_rootPane = 67;
  private static final int PROPERTY_name = 68;
  private static final int PROPERTY_component = 69;

  // Property array 
  private static PropertyDescriptor[] properties = new PropertyDescriptor[70];

  static {
    try {
      properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", LDAPLoginBean.class, "isOptimizedDrawingEnabled", null );
      properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", LDAPLoginBean.class, "getColorModel", null );
      properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", LDAPLoginBean.class, "getMinimumSize", "setMinimumSize" );
      properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", LDAPLoginBean.class, "isVisible", "setVisible" );
      properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", LDAPLoginBean.class, "getToolkit", null );
      properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", LDAPLoginBean.class, "isDisplayable", null );
      properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", LDAPLoginBean.class, "isOpaque", "setOpaque" );
      properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", LDAPLoginBean.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
      properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", LDAPLoginBean.class, "isEnabled", "setEnabled" );
      properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", LDAPLoginBean.class, "getAutoscrolls", "setAutoscrolls" );
      properties[PROPERTY_y] = new PropertyDescriptor ( "y", LDAPLoginBean.class, "getY", null );
      properties[PROPERTY_x] = new PropertyDescriptor ( "x", LDAPLoginBean.class, "getX", null );
      properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", LDAPLoginBean.class, "getAccessibleContext", null );
      properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", LDAPLoginBean.class, "getComponentOrientation", "setComponentOrientation" );
      properties[PROPERTY_components] = new PropertyDescriptor ( "components", LDAPLoginBean.class, "getComponents", null );
      properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", LDAPLoginBean.class, "isManagingFocus", null );
      properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", LDAPLoginBean.class, "getInputMethodRequests", null );
      properties[PROPERTY_border] = new PropertyDescriptor ( "border", LDAPLoginBean.class, "getBorder", "setBorder" );
      properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", LDAPLoginBean.class, "getLocale", "setLocale" );
      properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", LDAPLoginBean.class, "getInsets", null );
      properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", LDAPLoginBean.class, "getUIClassID", null );
      properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", LDAPLoginBean.class, "getGraphics", null );
      properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", LDAPLoginBean.class, "isMinimumSizeSet", null );
      properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", LDAPLoginBean.class, "getActionMap", "setActionMap" );
      properties[PROPERTY_password] = new PropertyDescriptor ( "password", LDAPLoginBean.class, "getPassword", "setPassword" );
      properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", LDAPLoginBean.class, "isMaximumSizeSet", null );
      properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", LDAPLoginBean.class, "getAlignmentY", "setAlignmentY" );
      properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", LDAPLoginBean.class, "getAlignmentX", "setAlignmentX" );
      properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", LDAPLoginBean.class, "getLocationOnScreen", null );
      properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", LDAPLoginBean.class, "getCursor", "setCursor" );
      properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", LDAPLoginBean.class, "getRegisteredKeyStrokes", null );
      properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", LDAPLoginBean.class, "isPreferredSizeSet", null );
      properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", LDAPLoginBean.class, "getVisibleRect", null );
      properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", LDAPLoginBean.class, "getBounds", "setBounds" );
      properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", LDAPLoginBean.class, "getInputContext", null );
      properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", LDAPLoginBean.class, "isFocusTraversable", null );
      properties[PROPERTY_font] = new PropertyDescriptor ( "font", LDAPLoginBean.class, "getFont", "setFont" );
      properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", LDAPLoginBean.class, "getInputVerifier", "setInputVerifier" );
      properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", LDAPLoginBean.class, "isLightweight", null );
      properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", LDAPLoginBean.class, "isPaintingTile", null );
      properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", LDAPLoginBean.class, "getMaximumSize", "setMaximumSize" );
      properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", LDAPLoginBean.class, "getLayout", "setLayout" );
      properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", LDAPLoginBean.class, "getTreeLock", null );
      properties[PROPERTY_bindDN] = new PropertyDescriptor ( "bindDN", LDAPLoginBean.class, "getBindDN", "setBindDN" );
      properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", LDAPLoginBean.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
      properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", LDAPLoginBean.class, "getForeground", "setForeground" );
      properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", LDAPLoginBean.class, "getPreferredSize", "setPreferredSize" );
      properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", LDAPLoginBean.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
      properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", LDAPLoginBean.class, "isDoubleBuffered", "setDoubleBuffered" );
      properties[PROPERTY_baseDN] = new PropertyDescriptor ( "baseDN", LDAPLoginBean.class, "getBaseDN", "setBaseDN" );
      properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", LDAPLoginBean.class, "isShowing", null );
      properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", LDAPLoginBean.class, "getNextFocusableComponent", "setNextFocusableComponent" );
      properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", LDAPLoginBean.class, "getParent", null );
      properties[PROPERTY_port] = new PropertyDescriptor ( "port", LDAPLoginBean.class, "getPort", "setPort" );
      properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", LDAPLoginBean.class, "getPeer", null );
      properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", LDAPLoginBean.class, "getComponentCount", null );
      properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", LDAPLoginBean.class, "getGraphicsConfiguration", null );
      properties[PROPERTY_host] = new PropertyDescriptor ( "host", LDAPLoginBean.class, "getHost", "setHost" );
      properties[PROPERTY_height] = new PropertyDescriptor ( "height", LDAPLoginBean.class, "getHeight", null );
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", LDAPLoginBean.class, "isValid", null );
      properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", LDAPLoginBean.class, "isFocusCycleRoot", null );
      properties[PROPERTY_width] = new PropertyDescriptor ( "width", LDAPLoginBean.class, "getWidth", null );
      properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", LDAPLoginBean.class, "getToolTipText", "setToolTipText" );
      properties[PROPERTY_background] = new PropertyDescriptor ( "background", LDAPLoginBean.class, "getBackground", "setBackground" );
      properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", LDAPLoginBean.class, "isValidateRoot", null );
      properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", LDAPLoginBean.class, "getTopLevelAncestor", null );
      properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", LDAPLoginBean.class, "getDropTarget", "setDropTarget" );
      properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", LDAPLoginBean.class, "getRootPane", null );
      properties[PROPERTY_name] = new PropertyDescriptor ( "name", LDAPLoginBean.class, "getName", "setName" );
      properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", LDAPLoginBean.class, null, null, "getComponent", null );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Properties

    // Here you can add code for customizing the properties array.

}//GEN-LAST:Properties

  // EventSet identifiers//GEN-FIRST:Events
  private static final int EVENT_mouseMotionListener = 0;
  private static final int EVENT_ancestorListener = 1;
  private static final int EVENT_inputMethodListener = 2;
  private static final int EVENT_componentListener = 3;
  private static final int EVENT_hierarchyBoundsListener = 4;
  private static final int EVENT_mouseListener = 5;
  private static final int EVENT_focusListener = 6;
  private static final int EVENT_propertyChangeListener = 7;
  private static final int EVENT_keyListener = 8;
  private static final int EVENT_hierarchyListener = 9;
  private static final int EVENT_containerListener = 10;
  private static final int EVENT_vetoableChangeListener = 11;

  // EventSet array
  private static EventSetDescriptor[] eventSets = new EventSetDescriptor[12];

  static {
    try {
      eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( LDAPLoginBean.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" );
      eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( LDAPLoginBean.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener" );
      eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( LDAPLoginBean.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" );
      eventSets[EVENT_componentListener] = new EventSetDescriptor ( LDAPLoginBean.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" );
      eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( LDAPLoginBean.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" );
      eventSets[EVENT_mouseListener] = new EventSetDescriptor ( LDAPLoginBean.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mousePressed", "mouseClicked", "mouseEntered"}, "addMouseListener", "removeMouseListener" );
      eventSets[EVENT_focusListener] = new EventSetDescriptor ( LDAPLoginBean.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained"}, "addFocusListener", "removeFocusListener" );
      eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( LDAPLoginBean.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
      eventSets[EVENT_keyListener] = new EventSetDescriptor ( LDAPLoginBean.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyTyped", "keyReleased"}, "addKeyListener", "removeKeyListener" );
      eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( LDAPLoginBean.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" );
      eventSets[EVENT_containerListener] = new EventSetDescriptor ( LDAPLoginBean.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded"}, "addContainerListener", "removeContainerListener" );
      eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( LDAPLoginBean.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

}//GEN-LAST:Events

  // Method identifiers //GEN-FIRST:Methods
  private static final int METHOD_updateUI0 = 0;
  private static final int METHOD_hasFocus1 = 1;
  private static final int METHOD_unregisterKeyboardAction2 = 2;
  private static final int METHOD_revalidate3 = 3;
  private static final int METHOD_getListeners4 = 4;
  private static final int METHOD_getInputMap5 = 5;
  private static final int METHOD_removeNotify6 = 6;
  private static final int METHOD_getToolTipLocation7 = 7;
  private static final int METHOD_getBounds8 = 8;
  private static final int METHOD_firePropertyChange9 = 9;
  private static final int METHOD_getActionForKeyStroke10 = 10;
  private static final int METHOD_firePropertyChange11 = 11;
  private static final int METHOD_repaint12 = 12;
  private static final int METHOD_getInputMap13 = 13;
  private static final int METHOD_firePropertyChange14 = 14;
  private static final int METHOD_firePropertyChange15 = 15;
  private static final int METHOD_repaint16 = 16;
  private static final int METHOD_addPropertyChangeListener17 = 17;
  private static final int METHOD_computeVisibleRect18 = 18;
  private static final int METHOD_hide19 = 19;
  private static final int METHOD_reshape20 = 20;
  private static final int METHOD_putClientProperty21 = 21;
  private static final int METHOD_getLocation22 = 22;
  private static final int METHOD_setInputMap23 = 23;
  private static final int METHOD_paintImmediately24 = 24;
  private static final int METHOD_disable25 = 25;
  private static final int METHOD_resetKeyboardActions26 = 26;
  private static final int METHOD_enable27 = 27;
  private static final int METHOD_grabFocus28 = 28;
  private static final int METHOD_getToolTipText29 = 29;
  private static final int METHOD_getClientProperty30 = 30;
  private static final int METHOD_getSize31 = 31;
  private static final int METHOD_firePropertyChange32 = 32;
  private static final int METHOD_removePropertyChangeListener33 = 33;
  private static final int METHOD_requestDefaultFocus34 = 34;
  private static final int METHOD_addNotify35 = 35;
  private static final int METHOD_firePropertyChange36 = 36;
  private static final int METHOD_requestFocus37 = 37;
  private static final int METHOD_getConditionForKeyStroke38 = 38;
  private static final int METHOD_firePropertyChange39 = 39;
  private static final int METHOD_registerKeyboardAction40 = 40;
  private static final int METHOD_registerKeyboardAction41 = 41;
  private static final int METHOD_isLightweightComponent42 = 42;
  private static final int METHOD_paint43 = 43;
  private static final int METHOD_createToolTip44 = 44;
  private static final int METHOD_print45 = 45;
  private static final int METHOD_update46 = 46;
  private static final int METHOD_paintImmediately47 = 47;
  private static final int METHOD_getInsets48 = 48;
  private static final int METHOD_printAll49 = 49;
  private static final int METHOD_contains50 = 50;
  private static final int METHOD_firePropertyChange51 = 51;
  private static final int METHOD_scrollRectToVisible52 = 52;
  private static final int METHOD_getComponentAt53 = 53;
  private static final int METHOD_add54 = 54;
  private static final int METHOD_preferredSize55 = 55;
  private static final int METHOD_locate56 = 56;
  private static final int METHOD_list57 = 57;
  private static final int METHOD_add58 = 58;
  private static final int METHOD_add59 = 59;
  private static final int METHOD_invalidate60 = 60;
  private static final int METHOD_printComponents61 = 61;
  private static final int METHOD_doLayout62 = 62;
  private static final int METHOD_layout63 = 63;
  private static final int METHOD_list64 = 64;
  private static final int METHOD_add65 = 65;
  private static final int METHOD_remove66 = 66;
  private static final int METHOD_isAncestorOf67 = 67;
  private static final int METHOD_findComponentAt68 = 68;
  private static final int METHOD_findComponentAt69 = 69;
  private static final int METHOD_insets70 = 70;
  private static final int METHOD_getComponentAt71 = 71;
  private static final int METHOD_paintComponents72 = 72;
  private static final int METHOD_countComponents73 = 73;
  private static final int METHOD_minimumSize74 = 74;
  private static final int METHOD_deliverEvent75 = 75;
  private static final int METHOD_removeAll76 = 76;
  private static final int METHOD_remove77 = 77;
  private static final int METHOD_add78 = 78;
  private static final int METHOD_validate79 = 79;
  private static final int METHOD_gotFocus80 = 80;
  private static final int METHOD_toString81 = 81;
  private static final int METHOD_list82 = 82;
  private static final int METHOD_enableInputMethods83 = 83;
  private static final int METHOD_mouseEnter84 = 84;
  private static final int METHOD_getSize85 = 85;
  private static final int METHOD_add86 = 86;
  private static final int METHOD_contains87 = 87;
  private static final int METHOD_transferFocus88 = 88;
  private static final int METHOD_action89 = 89;
  private static final int METHOD_setSize90 = 90;
  private static final int METHOD_show91 = 91;
  private static final int METHOD_mouseDown92 = 92;
  private static final int METHOD_imageUpdate93 = 93;
  private static final int METHOD_repaint94 = 94;
  private static final int METHOD_getFontMetrics95 = 95;
  private static final int METHOD_lostFocus96 = 96;
  private static final int METHOD_postEvent97 = 97;
  private static final int METHOD_show98 = 98;
  private static final int METHOD_handleEvent99 = 99;
  private static final int METHOD_list100 = 100;
  private static final int METHOD_setBounds101 = 101;
  private static final int METHOD_mouseDrag102 = 102;
  private static final int METHOD_enable103 = 103;
  private static final int METHOD_createImage104 = 104;
  private static final int METHOD_keyUp105 = 105;
  private static final int METHOD_createImage106 = 106;
  private static final int METHOD_setLocation107 = 107;
  private static final int METHOD_repaint108 = 108;
  private static final int METHOD_repaint109 = 109;
  private static final int METHOD_keyDown110 = 110;
  private static final int METHOD_nextFocus111 = 111;
  private static final int METHOD_bounds112 = 112;
  private static final int METHOD_move113 = 113;
  private static final int METHOD_prepareImage114 = 114;
  private static final int METHOD_prepareImage115 = 115;
  private static final int METHOD_resize116 = 116;
  private static final int METHOD_getLocation117 = 117;
  private static final int METHOD_remove118 = 118;
  private static final int METHOD_setSize119 = 119;
  private static final int METHOD_list120 = 120;
  private static final int METHOD_location121 = 121;
  private static final int METHOD_paintAll122 = 122;
  private static final int METHOD_dispatchEvent123 = 123;
  private static final int METHOD_checkImage124 = 124;
  private static final int METHOD_checkImage125 = 125;
  private static final int METHOD_mouseExit126 = 126;
  private static final int METHOD_mouseMove127 = 127;
  private static final int METHOD_setLocation128 = 128;
  private static final int METHOD_mouseUp129 = 129;
  private static final int METHOD_size130 = 130;
  private static final int METHOD_inside131 = 131;
  private static final int METHOD_resize132 = 132;

  // Method array 
  private static MethodDescriptor[] methods = new MethodDescriptor[133];

  static {
    try {
      methods[METHOD_updateUI0] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("updateUI", new Class[] {}));
      methods[METHOD_updateUI0].setDisplayName ( "" );
      methods[METHOD_hasFocus1] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("hasFocus", new Class[] {}));
      methods[METHOD_hasFocus1].setDisplayName ( "" );
      methods[METHOD_unregisterKeyboardAction2] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("unregisterKeyboardAction", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_unregisterKeyboardAction2].setDisplayName ( "" );
      methods[METHOD_revalidate3] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("revalidate", new Class[] {}));
      methods[METHOD_revalidate3].setDisplayName ( "" );
      methods[METHOD_getListeners4] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getListeners", new Class[] {Class.forName("java.lang.Class")}));
      methods[METHOD_getListeners4].setDisplayName ( "" );
      methods[METHOD_getInputMap5] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getInputMap", new Class[] {}));
      methods[METHOD_getInputMap5].setDisplayName ( "" );
      methods[METHOD_removeNotify6] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("removeNotify", new Class[] {}));
      methods[METHOD_removeNotify6].setDisplayName ( "" );
      methods[METHOD_getToolTipLocation7] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getToolTipLocation", new Class[] {Class.forName("java.awt.event.MouseEvent")}));
      methods[METHOD_getToolTipLocation7].setDisplayName ( "" );
      methods[METHOD_getBounds8] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getBounds", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_getBounds8].setDisplayName ( "" );
      methods[METHOD_firePropertyChange9] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Double.TYPE, Double.TYPE}));
      methods[METHOD_firePropertyChange9].setDisplayName ( "" );
      methods[METHOD_getActionForKeyStroke10] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getActionForKeyStroke", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_getActionForKeyStroke10].setDisplayName ( "" );
      methods[METHOD_firePropertyChange11] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Character.TYPE, Character.TYPE}));
      methods[METHOD_firePropertyChange11].setDisplayName ( "" );
      methods[METHOD_repaint12] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("repaint", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_repaint12].setDisplayName ( "" );
      methods[METHOD_getInputMap13] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getInputMap", new Class[] {Integer.TYPE}));
      methods[METHOD_getInputMap13].setDisplayName ( "" );
      methods[METHOD_firePropertyChange14] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Float.TYPE, Float.TYPE}));
      methods[METHOD_firePropertyChange14].setDisplayName ( "" );
      methods[METHOD_firePropertyChange15] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_firePropertyChange15].setDisplayName ( "" );
      methods[METHOD_repaint16] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint16].setDisplayName ( "" );
      methods[METHOD_addPropertyChangeListener17] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("addPropertyChangeListener", new Class[] {Class.forName("java.lang.String"), Class.forName("java.beans.PropertyChangeListener")}));
      methods[METHOD_addPropertyChangeListener17].setDisplayName ( "" );
      methods[METHOD_computeVisibleRect18] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("computeVisibleRect", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_computeVisibleRect18].setDisplayName ( "" );
      methods[METHOD_hide19] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("hide", new Class[] {}));
      methods[METHOD_hide19].setDisplayName ( "" );
      methods[METHOD_reshape20] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_reshape20].setDisplayName ( "" );
      methods[METHOD_putClientProperty21] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("putClientProperty", new Class[] {Class.forName("java.lang.Object"), Class.forName("java.lang.Object")}));
      methods[METHOD_putClientProperty21].setDisplayName ( "" );
      methods[METHOD_getLocation22] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getLocation", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_getLocation22].setDisplayName ( "" );
      methods[METHOD_setInputMap23] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setInputMap", new Class[] {Integer.TYPE, Class.forName("javax.swing.InputMap")}));
      methods[METHOD_setInputMap23].setDisplayName ( "" );
      methods[METHOD_paintImmediately24] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("paintImmediately", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_paintImmediately24].setDisplayName ( "" );
      methods[METHOD_disable25] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("disable", new Class[] {}));
      methods[METHOD_disable25].setDisplayName ( "" );
      methods[METHOD_resetKeyboardActions26] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("resetKeyboardActions", new Class[] {}));
      methods[METHOD_resetKeyboardActions26].setDisplayName ( "" );
      methods[METHOD_enable27] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("enable", new Class[] {}));
      methods[METHOD_enable27].setDisplayName ( "" );
      methods[METHOD_grabFocus28] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("grabFocus", new Class[] {}));
      methods[METHOD_grabFocus28].setDisplayName ( "" );
      methods[METHOD_getToolTipText29] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getToolTipText", new Class[] {Class.forName("java.awt.event.MouseEvent")}));
      methods[METHOD_getToolTipText29].setDisplayName ( "" );
      methods[METHOD_getClientProperty30] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getClientProperty", new Class[] {Class.forName("java.lang.Object")}));
      methods[METHOD_getClientProperty30].setDisplayName ( "" );
      methods[METHOD_getSize31] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getSize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_getSize31].setDisplayName ( "" );
      methods[METHOD_firePropertyChange32] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Long.TYPE, Long.TYPE}));
      methods[METHOD_firePropertyChange32].setDisplayName ( "" );
      methods[METHOD_removePropertyChangeListener33] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("removePropertyChangeListener", new Class[] {Class.forName("java.lang.String"), Class.forName("java.beans.PropertyChangeListener")}));
      methods[METHOD_removePropertyChangeListener33].setDisplayName ( "" );
      methods[METHOD_requestDefaultFocus34] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("requestDefaultFocus", new Class[] {}));
      methods[METHOD_requestDefaultFocus34].setDisplayName ( "" );
      methods[METHOD_addNotify35] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("addNotify", new Class[] {}));
      methods[METHOD_addNotify35].setDisplayName ( "" );
      methods[METHOD_firePropertyChange36] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Byte.TYPE, Byte.TYPE}));
      methods[METHOD_firePropertyChange36].setDisplayName ( "" );
      methods[METHOD_requestFocus37] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("requestFocus", new Class[] {}));
      methods[METHOD_requestFocus37].setDisplayName ( "" );
      methods[METHOD_getConditionForKeyStroke38] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getConditionForKeyStroke", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_getConditionForKeyStroke38].setDisplayName ( "" );
      methods[METHOD_firePropertyChange39] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Short.TYPE, Short.TYPE}));
      methods[METHOD_firePropertyChange39].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction40] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("registerKeyboardAction", new Class[] {Class.forName("java.awt.event.ActionListener"), Class.forName("javax.swing.KeyStroke"), Integer.TYPE}));
      methods[METHOD_registerKeyboardAction40].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction41] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("registerKeyboardAction", new Class[] {Class.forName("java.awt.event.ActionListener"), Class.forName("java.lang.String"), Class.forName("javax.swing.KeyStroke"), Integer.TYPE}));
      methods[METHOD_registerKeyboardAction41].setDisplayName ( "" );
      methods[METHOD_isLightweightComponent42] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("isLightweightComponent", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_isLightweightComponent42].setDisplayName ( "" );
      methods[METHOD_paint43] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("paint", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paint43].setDisplayName ( "" );
      methods[METHOD_createToolTip44] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("createToolTip", new Class[] {}));
      methods[METHOD_createToolTip44].setDisplayName ( "" );
      methods[METHOD_print45] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("print", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_print45].setDisplayName ( "" );
      methods[METHOD_update46] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("update", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_update46].setDisplayName ( "" );
      methods[METHOD_paintImmediately47] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_paintImmediately47].setDisplayName ( "" );
      methods[METHOD_getInsets48] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getInsets", new Class[] {Class.forName("java.awt.Insets")}));
      methods[METHOD_getInsets48].setDisplayName ( "" );
      methods[METHOD_printAll49] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("printAll", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_printAll49].setDisplayName ( "" );
      methods[METHOD_contains50] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_contains50].setDisplayName ( "" );
      methods[METHOD_firePropertyChange51] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Boolean.TYPE, Boolean.TYPE}));
      methods[METHOD_firePropertyChange51].setDisplayName ( "" );
      methods[METHOD_scrollRectToVisible52] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("scrollRectToVisible", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_scrollRectToVisible52].setDisplayName ( "" );
      methods[METHOD_getComponentAt53] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getComponentAt", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_getComponentAt53].setDisplayName ( "" );
      methods[METHOD_add54] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_add54].setDisplayName ( "" );
      methods[METHOD_preferredSize55] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("preferredSize", new Class[] {}));
      methods[METHOD_preferredSize55].setDisplayName ( "" );
      methods[METHOD_locate56] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_locate56].setDisplayName ( "" );
      methods[METHOD_list57] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("list", new Class[] {Class.forName("java.io.PrintWriter"), Integer.TYPE}));
      methods[METHOD_list57].setDisplayName ( "" );
      methods[METHOD_add58] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Class.forName("java.lang.Object"), Integer.TYPE}));
      methods[METHOD_add58].setDisplayName ( "" );
      methods[METHOD_add59] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Integer.TYPE}));
      methods[METHOD_add59].setDisplayName ( "" );
      methods[METHOD_invalidate60] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("invalidate", new Class[] {}));
      methods[METHOD_invalidate60].setDisplayName ( "" );
      methods[METHOD_printComponents61] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("printComponents", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_printComponents61].setDisplayName ( "" );
      methods[METHOD_doLayout62] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("doLayout", new Class[] {}));
      methods[METHOD_doLayout62].setDisplayName ( "" );
      methods[METHOD_layout63] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("layout", new Class[] {}));
      methods[METHOD_layout63].setDisplayName ( "" );
      methods[METHOD_list64] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("list", new Class[] {Class.forName("java.io.PrintStream"), Integer.TYPE}));
      methods[METHOD_list64].setDisplayName ( "" );
      methods[METHOD_add65] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.lang.String"), Class.forName("java.awt.Component")}));
      methods[METHOD_add65].setDisplayName ( "" );
      methods[METHOD_remove66] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("remove", new Class[] {Integer.TYPE}));
      methods[METHOD_remove66].setDisplayName ( "" );
      methods[METHOD_isAncestorOf67] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("isAncestorOf", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_isAncestorOf67].setDisplayName ( "" );
      methods[METHOD_findComponentAt68] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_findComponentAt68].setDisplayName ( "" );
      methods[METHOD_findComponentAt69] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("findComponentAt", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_findComponentAt69].setDisplayName ( "" );
      methods[METHOD_insets70] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("insets", new Class[] {}));
      methods[METHOD_insets70].setDisplayName ( "" );
      methods[METHOD_getComponentAt71] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_getComponentAt71].setDisplayName ( "" );
      methods[METHOD_paintComponents72] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("paintComponents", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paintComponents72].setDisplayName ( "" );
      methods[METHOD_countComponents73] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("countComponents", new Class[] {}));
      methods[METHOD_countComponents73].setDisplayName ( "" );
      methods[METHOD_minimumSize74] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("minimumSize", new Class[] {}));
      methods[METHOD_minimumSize74].setDisplayName ( "" );
      methods[METHOD_deliverEvent75] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("deliverEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_deliverEvent75].setDisplayName ( "" );
      methods[METHOD_removeAll76] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("removeAll", new Class[] {}));
      methods[METHOD_removeAll76].setDisplayName ( "" );
      methods[METHOD_remove77] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("remove", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_remove77].setDisplayName ( "" );
      methods[METHOD_add78] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Class.forName("java.lang.Object")}));
      methods[METHOD_add78].setDisplayName ( "" );
      methods[METHOD_validate79] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("validate", new Class[] {}));
      methods[METHOD_validate79].setDisplayName ( "" );
      methods[METHOD_gotFocus80] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("gotFocus", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_gotFocus80].setDisplayName ( "" );
      methods[METHOD_toString81] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("toString", new Class[] {}));
      methods[METHOD_toString81].setDisplayName ( "" );
      methods[METHOD_list82] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("list", new Class[] {Class.forName("java.io.PrintStream")}));
      methods[METHOD_list82].setDisplayName ( "" );
      methods[METHOD_enableInputMethods83] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("enableInputMethods", new Class[] {Boolean.TYPE}));
      methods[METHOD_enableInputMethods83].setDisplayName ( "" );
      methods[METHOD_mouseEnter84] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseEnter", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseEnter84].setDisplayName ( "" );
      methods[METHOD_getSize85] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getSize", new Class[] {}));
      methods[METHOD_getSize85].setDisplayName ( "" );
      methods[METHOD_add86] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("add", new Class[] {Class.forName("java.awt.PopupMenu")}));
      methods[METHOD_add86].setDisplayName ( "" );
      methods[METHOD_contains87] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("contains", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_contains87].setDisplayName ( "" );
      methods[METHOD_transferFocus88] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("transferFocus", new Class[] {}));
      methods[METHOD_transferFocus88].setDisplayName ( "" );
      methods[METHOD_action89] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("action", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_action89].setDisplayName ( "" );
      methods[METHOD_setSize90] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setSize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setSize90].setDisplayName ( "" );
      methods[METHOD_show91] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("show", new Class[] {}));
      methods[METHOD_show91].setDisplayName ( "" );
      methods[METHOD_mouseDown92] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseDown", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDown92].setDisplayName ( "" );
      methods[METHOD_imageUpdate93] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("imageUpdate", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_imageUpdate93].setDisplayName ( "" );
      methods[METHOD_repaint94] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("repaint", new Class[] {Long.TYPE}));
      methods[METHOD_repaint94].setDisplayName ( "" );
      methods[METHOD_getFontMetrics95] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getFontMetrics", new Class[] {Class.forName("java.awt.Font")}));
      methods[METHOD_getFontMetrics95].setDisplayName ( "" );
      methods[METHOD_lostFocus96] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("lostFocus", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_lostFocus96].setDisplayName ( "" );
      methods[METHOD_postEvent97] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("postEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_postEvent97].setDisplayName ( "" );
      methods[METHOD_show98] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("show", new Class[] {Boolean.TYPE}));
      methods[METHOD_show98].setDisplayName ( "" );
      methods[METHOD_handleEvent99] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("handleEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_handleEvent99].setDisplayName ( "" );
      methods[METHOD_list100] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("list", new Class[] {Class.forName("java.io.PrintWriter")}));
      methods[METHOD_list100].setDisplayName ( "" );
      methods[METHOD_setBounds101] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setBounds101].setDisplayName ( "" );
      methods[METHOD_mouseDrag102] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseDrag", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDrag102].setDisplayName ( "" );
      methods[METHOD_enable103] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("enable", new Class[] {Boolean.TYPE}));
      methods[METHOD_enable103].setDisplayName ( "" );
      methods[METHOD_createImage104] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("createImage", new Class[] {Class.forName("java.awt.image.ImageProducer")}));
      methods[METHOD_createImage104].setDisplayName ( "" );
      methods[METHOD_keyUp105] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("keyUp", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE}));
      methods[METHOD_keyUp105].setDisplayName ( "" );
      methods[METHOD_createImage106] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("createImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createImage106].setDisplayName ( "" );
      methods[METHOD_setLocation107] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setLocation", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setLocation107].setDisplayName ( "" );
      methods[METHOD_repaint108] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("repaint", new Class[] {}));
      methods[METHOD_repaint108].setDisplayName ( "" );
      methods[METHOD_repaint109] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("repaint", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint109].setDisplayName ( "" );
      methods[METHOD_keyDown110] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("keyDown", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE}));
      methods[METHOD_keyDown110].setDisplayName ( "" );
      methods[METHOD_nextFocus111] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("nextFocus", new Class[] {}));
      methods[METHOD_nextFocus111].setDisplayName ( "" );
      methods[METHOD_bounds112] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("bounds", new Class[] {}));
      methods[METHOD_bounds112].setDisplayName ( "" );
      methods[METHOD_move113] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_move113].setDisplayName ( "" );
      methods[METHOD_prepareImage114] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("prepareImage", new Class[] {Class.forName("java.awt.Image"), Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_prepareImage114].setDisplayName ( "" );
      methods[METHOD_prepareImage115] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("prepareImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_prepareImage115].setDisplayName ( "" );
      methods[METHOD_resize116] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_resize116].setDisplayName ( "" );
      methods[METHOD_getLocation117] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("getLocation", new Class[] {}));
      methods[METHOD_getLocation117].setDisplayName ( "" );
      methods[METHOD_remove118] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("remove", new Class[] {Class.forName("java.awt.MenuComponent")}));
      methods[METHOD_remove118].setDisplayName ( "" );
      methods[METHOD_setSize119] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setSize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_setSize119].setDisplayName ( "" );
      methods[METHOD_list120] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("list", new Class[] {}));
      methods[METHOD_list120].setDisplayName ( "" );
      methods[METHOD_location121] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("location", new Class[] {}));
      methods[METHOD_location121].setDisplayName ( "" );
      methods[METHOD_paintAll122] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("paintAll", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paintAll122].setDisplayName ( "" );
      methods[METHOD_dispatchEvent123] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("dispatchEvent", new Class[] {Class.forName("java.awt.AWTEvent")}));
      methods[METHOD_dispatchEvent123].setDisplayName ( "" );
      methods[METHOD_checkImage124] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("checkImage", new Class[] {Class.forName("java.awt.Image"), Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_checkImage124].setDisplayName ( "" );
      methods[METHOD_checkImage125] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("checkImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_checkImage125].setDisplayName ( "" );
      methods[METHOD_mouseExit126] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseExit", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseExit126].setDisplayName ( "" );
      methods[METHOD_mouseMove127] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseMove", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseMove127].setDisplayName ( "" );
      methods[METHOD_setLocation128] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("setLocation", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_setLocation128].setDisplayName ( "" );
      methods[METHOD_mouseUp129] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("mouseUp", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseUp129].setDisplayName ( "" );
      methods[METHOD_size130] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("size", new Class[] {}));
      methods[METHOD_size130].setDisplayName ( "" );
      methods[METHOD_inside131] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_inside131].setDisplayName ( "" );
      methods[METHOD_resize132] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.LDAPLoginBean").getMethod("resize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_resize132].setDisplayName ( "" );
    }
    catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
}//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = "LDAPLoginBean.gif";//GEN-BEGIN:Icons
  private static String iconNameC32 = null;
  private static String iconNameM16 = null;
  private static String iconNameM32 = null;//GEN-END:Icons

  private static int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
  private static int defaultEventIndex = -1;//GEN-END:Idx


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
        return defaultPropertyIndex;
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
                if( iconNameM32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
        }
        return null;
    }

}

