package jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans;

import java.beans.*;

public class OverlayImagePanelBeanBeanInfo extends SimpleBeanInfo {

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
  private static final int PROPERTY_maximumSizeSet = 24;
  private static final int PROPERTY_alignmentY = 25;
  private static final int PROPERTY_alignmentX = 26;
  private static final int PROPERTY_locationOnScreen = 27;
  private static final int PROPERTY_cursor = 28;
  private static final int PROPERTY_registeredKeyStrokes = 29;
  private static final int PROPERTY_preferredSizeSet = 30;
  private static final int PROPERTY_visibleRect = 31;
  private static final int PROPERTY_bounds = 32;
  private static final int PROPERTY_inputContext = 33;
  private static final int PROPERTY_focusTraversable = 34;
  private static final int PROPERTY_font = 35;
  private static final int PROPERTY_inputVerifier = 36;
  private static final int PROPERTY_lightweight = 37;
  private static final int PROPERTY_paintingTile = 38;
  private static final int PROPERTY_maximumSize = 39;
  private static final int PROPERTY_overlay = 40;
  private static final int PROPERTY_layout = 41;
  private static final int PROPERTY_treeLock = 42;
  private static final int PROPERTY_verifyInputWhenFocusTarget = 43;
  private static final int PROPERTY_foreground = 44;
  private static final int PROPERTY_preferredSize = 45;
  private static final int PROPERTY_debugGraphicsOptions = 46;
  private static final int PROPERTY_doubleBuffered = 47;
  private static final int PROPERTY_showing = 48;
  private static final int PROPERTY_nextFocusableComponent = 49;
  private static final int PROPERTY_parent = 50;
  private static final int PROPERTY_peer = 51;
  private static final int PROPERTY_srcImg = 52;
  private static final int PROPERTY_componentCount = 53;
  private static final int PROPERTY_graphicsConfiguration = 54;
  private static final int PROPERTY_height = 55;
  private static final int PROPERTY_valid = 56;
  private static final int PROPERTY_focusCycleRoot = 57;
  private static final int PROPERTY_width = 58;
  private static final int PROPERTY_toolTipText = 59;
  private static final int PROPERTY_background = 60;
  private static final int PROPERTY_validateRoot = 61;
  private static final int PROPERTY_topLevelAncestor = 62;
  private static final int PROPERTY_dropTarget = 63;
  private static final int PROPERTY_rootPane = 64;
  private static final int PROPERTY_name = 65;
  private static final int PROPERTY_component = 66;

  // Property array 
  private static PropertyDescriptor[] properties = new PropertyDescriptor[67];

  static {
    try {
      properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor ( "optimizedDrawingEnabled", OverlayImagePanelBean.class, "isOptimizedDrawingEnabled", null );
      properties[PROPERTY_colorModel] = new PropertyDescriptor ( "colorModel", OverlayImagePanelBean.class, "getColorModel", null );
      properties[PROPERTY_minimumSize] = new PropertyDescriptor ( "minimumSize", OverlayImagePanelBean.class, "getMinimumSize", "setMinimumSize" );
      properties[PROPERTY_visible] = new PropertyDescriptor ( "visible", OverlayImagePanelBean.class, "isVisible", "setVisible" );
      properties[PROPERTY_toolkit] = new PropertyDescriptor ( "toolkit", OverlayImagePanelBean.class, "getToolkit", null );
      properties[PROPERTY_displayable] = new PropertyDescriptor ( "displayable", OverlayImagePanelBean.class, "isDisplayable", null );
      properties[PROPERTY_opaque] = new PropertyDescriptor ( "opaque", OverlayImagePanelBean.class, "isOpaque", "setOpaque" );
      properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor ( "requestFocusEnabled", OverlayImagePanelBean.class, "isRequestFocusEnabled", "setRequestFocusEnabled" );
      properties[PROPERTY_enabled] = new PropertyDescriptor ( "enabled", OverlayImagePanelBean.class, "isEnabled", "setEnabled" );
      properties[PROPERTY_autoscrolls] = new PropertyDescriptor ( "autoscrolls", OverlayImagePanelBean.class, "getAutoscrolls", "setAutoscrolls" );
      properties[PROPERTY_y] = new PropertyDescriptor ( "y", OverlayImagePanelBean.class, "getY", null );
      properties[PROPERTY_x] = new PropertyDescriptor ( "x", OverlayImagePanelBean.class, "getX", null );
      properties[PROPERTY_accessibleContext] = new PropertyDescriptor ( "accessibleContext", OverlayImagePanelBean.class, "getAccessibleContext", null );
      properties[PROPERTY_componentOrientation] = new PropertyDescriptor ( "componentOrientation", OverlayImagePanelBean.class, "getComponentOrientation", "setComponentOrientation" );
      properties[PROPERTY_components] = new PropertyDescriptor ( "components", OverlayImagePanelBean.class, "getComponents", null );
      properties[PROPERTY_managingFocus] = new PropertyDescriptor ( "managingFocus", OverlayImagePanelBean.class, "isManagingFocus", null );
      properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor ( "inputMethodRequests", OverlayImagePanelBean.class, "getInputMethodRequests", null );
      properties[PROPERTY_border] = new PropertyDescriptor ( "border", OverlayImagePanelBean.class, "getBorder", "setBorder" );
      properties[PROPERTY_locale] = new PropertyDescriptor ( "locale", OverlayImagePanelBean.class, "getLocale", "setLocale" );
      properties[PROPERTY_insets] = new PropertyDescriptor ( "insets", OverlayImagePanelBean.class, "getInsets", null );
      properties[PROPERTY_UIClassID] = new PropertyDescriptor ( "UIClassID", OverlayImagePanelBean.class, "getUIClassID", null );
      properties[PROPERTY_graphics] = new PropertyDescriptor ( "graphics", OverlayImagePanelBean.class, "getGraphics", null );
      properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor ( "minimumSizeSet", OverlayImagePanelBean.class, "isMinimumSizeSet", null );
      properties[PROPERTY_actionMap] = new PropertyDescriptor ( "actionMap", OverlayImagePanelBean.class, "getActionMap", "setActionMap" );
      properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor ( "maximumSizeSet", OverlayImagePanelBean.class, "isMaximumSizeSet", null );
      properties[PROPERTY_alignmentY] = new PropertyDescriptor ( "alignmentY", OverlayImagePanelBean.class, "getAlignmentY", "setAlignmentY" );
      properties[PROPERTY_alignmentX] = new PropertyDescriptor ( "alignmentX", OverlayImagePanelBean.class, "getAlignmentX", "setAlignmentX" );
      properties[PROPERTY_locationOnScreen] = new PropertyDescriptor ( "locationOnScreen", OverlayImagePanelBean.class, "getLocationOnScreen", null );
      properties[PROPERTY_cursor] = new PropertyDescriptor ( "cursor", OverlayImagePanelBean.class, "getCursor", "setCursor" );
      properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor ( "registeredKeyStrokes", OverlayImagePanelBean.class, "getRegisteredKeyStrokes", null );
      properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor ( "preferredSizeSet", OverlayImagePanelBean.class, "isPreferredSizeSet", null );
      properties[PROPERTY_visibleRect] = new PropertyDescriptor ( "visibleRect", OverlayImagePanelBean.class, "getVisibleRect", null );
      properties[PROPERTY_bounds] = new PropertyDescriptor ( "bounds", OverlayImagePanelBean.class, "getBounds", "setBounds" );
      properties[PROPERTY_inputContext] = new PropertyDescriptor ( "inputContext", OverlayImagePanelBean.class, "getInputContext", null );
      properties[PROPERTY_focusTraversable] = new PropertyDescriptor ( "focusTraversable", OverlayImagePanelBean.class, "isFocusTraversable", null );
      properties[PROPERTY_font] = new PropertyDescriptor ( "font", OverlayImagePanelBean.class, "getFont", "setFont" );
      properties[PROPERTY_inputVerifier] = new PropertyDescriptor ( "inputVerifier", OverlayImagePanelBean.class, "getInputVerifier", "setInputVerifier" );
      properties[PROPERTY_lightweight] = new PropertyDescriptor ( "lightweight", OverlayImagePanelBean.class, "isLightweight", null );
      properties[PROPERTY_paintingTile] = new PropertyDescriptor ( "paintingTile", OverlayImagePanelBean.class, "isPaintingTile", null );
      properties[PROPERTY_maximumSize] = new PropertyDescriptor ( "maximumSize", OverlayImagePanelBean.class, "getMaximumSize", "setMaximumSize" );
      properties[PROPERTY_overlay] = new PropertyDescriptor ( "overlay", OverlayImagePanelBean.class, "getOverlay", "setOverlay" );
      properties[PROPERTY_layout] = new PropertyDescriptor ( "layout", OverlayImagePanelBean.class, "getLayout", "setLayout" );
      properties[PROPERTY_treeLock] = new PropertyDescriptor ( "treeLock", OverlayImagePanelBean.class, "getTreeLock", null );
      properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor ( "verifyInputWhenFocusTarget", OverlayImagePanelBean.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget" );
      properties[PROPERTY_foreground] = new PropertyDescriptor ( "foreground", OverlayImagePanelBean.class, "getForeground", "setForeground" );
      properties[PROPERTY_preferredSize] = new PropertyDescriptor ( "preferredSize", OverlayImagePanelBean.class, "getPreferredSize", "setPreferredSize" );
      properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor ( "debugGraphicsOptions", OverlayImagePanelBean.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions" );
      properties[PROPERTY_doubleBuffered] = new PropertyDescriptor ( "doubleBuffered", OverlayImagePanelBean.class, "isDoubleBuffered", "setDoubleBuffered" );
      properties[PROPERTY_showing] = new PropertyDescriptor ( "showing", OverlayImagePanelBean.class, "isShowing", null );
      properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor ( "nextFocusableComponent", OverlayImagePanelBean.class, "getNextFocusableComponent", "setNextFocusableComponent" );
      properties[PROPERTY_parent] = new PropertyDescriptor ( "parent", OverlayImagePanelBean.class, "getParent", null );
      properties[PROPERTY_peer] = new PropertyDescriptor ( "peer", OverlayImagePanelBean.class, "getPeer", null );
      properties[PROPERTY_srcImg] = new PropertyDescriptor ( "srcImg", OverlayImagePanelBean.class, "getSrcImg", "setSrcImg" );
      properties[PROPERTY_componentCount] = new PropertyDescriptor ( "componentCount", OverlayImagePanelBean.class, "getComponentCount", null );
      properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor ( "graphicsConfiguration", OverlayImagePanelBean.class, "getGraphicsConfiguration", null );
      properties[PROPERTY_height] = new PropertyDescriptor ( "height", OverlayImagePanelBean.class, "getHeight", null );
      properties[PROPERTY_valid] = new PropertyDescriptor ( "valid", OverlayImagePanelBean.class, "isValid", null );
      properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor ( "focusCycleRoot", OverlayImagePanelBean.class, "isFocusCycleRoot", null );
      properties[PROPERTY_width] = new PropertyDescriptor ( "width", OverlayImagePanelBean.class, "getWidth", null );
      properties[PROPERTY_toolTipText] = new PropertyDescriptor ( "toolTipText", OverlayImagePanelBean.class, "getToolTipText", "setToolTipText" );
      properties[PROPERTY_background] = new PropertyDescriptor ( "background", OverlayImagePanelBean.class, "getBackground", "setBackground" );
      properties[PROPERTY_validateRoot] = new PropertyDescriptor ( "validateRoot", OverlayImagePanelBean.class, "isValidateRoot", null );
      properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor ( "topLevelAncestor", OverlayImagePanelBean.class, "getTopLevelAncestor", null );
      properties[PROPERTY_dropTarget] = new PropertyDescriptor ( "dropTarget", OverlayImagePanelBean.class, "getDropTarget", "setDropTarget" );
      properties[PROPERTY_rootPane] = new PropertyDescriptor ( "rootPane", OverlayImagePanelBean.class, "getRootPane", null );
      properties[PROPERTY_name] = new PropertyDescriptor ( "name", OverlayImagePanelBean.class, "getName", "setName" );
      properties[PROPERTY_component] = new IndexedPropertyDescriptor ( "component", OverlayImagePanelBean.class, null, null, "getComponent", null );
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
      eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[] {"mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener" );
      eventSets[EVENT_ancestorListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[] {"ancestorMoved", "ancestorAdded"}, "addAncestorListener", "removeAncestorListener" );
      eventSets[EVENT_inputMethodListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[] {"inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener" );
      eventSets[EVENT_componentListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "componentListener", java.awt.event.ComponentListener.class, new String[] {"componentResized", "componentShown"}, "addComponentListener", "removeComponentListener" );
      eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[] {"ancestorMoved"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener" );
      eventSets[EVENT_mouseListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "mouseListener", java.awt.event.MouseListener.class, new String[] {"mousePressed", "mouseClicked", "mouseEntered"}, "addMouseListener", "removeMouseListener" );
      eventSets[EVENT_focusListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "focusListener", java.awt.event.FocusListener.class, new String[] {"focusGained"}, "addFocusListener", "removeFocusListener" );
      eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
      eventSets[EVENT_keyListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "keyListener", java.awt.event.KeyListener.class, new String[] {"keyTyped", "keyReleased"}, "addKeyListener", "removeKeyListener" );
      eventSets[EVENT_hierarchyListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[] {"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener" );
      eventSets[EVENT_containerListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "containerListener", java.awt.event.ContainerListener.class, new String[] {"componentAdded"}, "addContainerListener", "removeContainerListener" );
      eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( OverlayImagePanelBean.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
    }
    catch( IntrospectionException e) {}//GEN-HEADEREND:Events

    // Here you can add code for customizing the event sets array.

}//GEN-LAST:Events

  // Method identifiers //GEN-FIRST:Methods
  private static final int METHOD_createOverlay0 = 0;
  private static final int METHOD_removeOverlay1 = 1;
  private static final int METHOD_repaintNow2 = 2;
  private static final int METHOD_update3 = 3;
  private static final int METHOD_paint4 = 4;
  private static final int METHOD_drawJavaImage5 = 5;
  private static final int METHOD_addJavaImage6 = 6;
  private static final int METHOD_createImage7 = 7;
  private static final int METHOD_updateUI8 = 8;
  private static final int METHOD_hasFocus9 = 9;
  private static final int METHOD_unregisterKeyboardAction10 = 10;
  private static final int METHOD_revalidate11 = 11;
  private static final int METHOD_getListeners12 = 12;
  private static final int METHOD_getInputMap13 = 13;
  private static final int METHOD_removeNotify14 = 14;
  private static final int METHOD_getToolTipLocation15 = 15;
  private static final int METHOD_getBounds16 = 16;
  private static final int METHOD_firePropertyChange17 = 17;
  private static final int METHOD_getActionForKeyStroke18 = 18;
  private static final int METHOD_firePropertyChange19 = 19;
  private static final int METHOD_repaint20 = 20;
  private static final int METHOD_getInputMap21 = 21;
  private static final int METHOD_firePropertyChange22 = 22;
  private static final int METHOD_firePropertyChange23 = 23;
  private static final int METHOD_repaint24 = 24;
  private static final int METHOD_addPropertyChangeListener25 = 25;
  private static final int METHOD_computeVisibleRect26 = 26;
  private static final int METHOD_hide27 = 27;
  private static final int METHOD_reshape28 = 28;
  private static final int METHOD_putClientProperty29 = 29;
  private static final int METHOD_getLocation30 = 30;
  private static final int METHOD_setInputMap31 = 31;
  private static final int METHOD_paintImmediately32 = 32;
  private static final int METHOD_disable33 = 33;
  private static final int METHOD_resetKeyboardActions34 = 34;
  private static final int METHOD_enable35 = 35;
  private static final int METHOD_grabFocus36 = 36;
  private static final int METHOD_getToolTipText37 = 37;
  private static final int METHOD_getClientProperty38 = 38;
  private static final int METHOD_getSize39 = 39;
  private static final int METHOD_firePropertyChange40 = 40;
  private static final int METHOD_removePropertyChangeListener41 = 41;
  private static final int METHOD_requestDefaultFocus42 = 42;
  private static final int METHOD_addNotify43 = 43;
  private static final int METHOD_firePropertyChange44 = 44;
  private static final int METHOD_requestFocus45 = 45;
  private static final int METHOD_getConditionForKeyStroke46 = 46;
  private static final int METHOD_firePropertyChange47 = 47;
  private static final int METHOD_registerKeyboardAction48 = 48;
  private static final int METHOD_registerKeyboardAction49 = 49;
  private static final int METHOD_isLightweightComponent50 = 50;
  private static final int METHOD_createToolTip51 = 51;
  private static final int METHOD_print52 = 52;
  private static final int METHOD_paintImmediately53 = 53;
  private static final int METHOD_getInsets54 = 54;
  private static final int METHOD_printAll55 = 55;
  private static final int METHOD_contains56 = 56;
  private static final int METHOD_firePropertyChange57 = 57;
  private static final int METHOD_scrollRectToVisible58 = 58;
  private static final int METHOD_getComponentAt59 = 59;
  private static final int METHOD_add60 = 60;
  private static final int METHOD_preferredSize61 = 61;
  private static final int METHOD_locate62 = 62;
  private static final int METHOD_list63 = 63;
  private static final int METHOD_add64 = 64;
  private static final int METHOD_add65 = 65;
  private static final int METHOD_invalidate66 = 66;
  private static final int METHOD_printComponents67 = 67;
  private static final int METHOD_doLayout68 = 68;
  private static final int METHOD_layout69 = 69;
  private static final int METHOD_list70 = 70;
  private static final int METHOD_add71 = 71;
  private static final int METHOD_remove72 = 72;
  private static final int METHOD_isAncestorOf73 = 73;
  private static final int METHOD_findComponentAt74 = 74;
  private static final int METHOD_findComponentAt75 = 75;
  private static final int METHOD_insets76 = 76;
  private static final int METHOD_getComponentAt77 = 77;
  private static final int METHOD_paintComponents78 = 78;
  private static final int METHOD_countComponents79 = 79;
  private static final int METHOD_minimumSize80 = 80;
  private static final int METHOD_deliverEvent81 = 81;
  private static final int METHOD_removeAll82 = 82;
  private static final int METHOD_remove83 = 83;
  private static final int METHOD_add84 = 84;
  private static final int METHOD_validate85 = 85;
  private static final int METHOD_gotFocus86 = 86;
  private static final int METHOD_toString87 = 87;
  private static final int METHOD_list88 = 88;
  private static final int METHOD_enableInputMethods89 = 89;
  private static final int METHOD_mouseEnter90 = 90;
  private static final int METHOD_getSize91 = 91;
  private static final int METHOD_add92 = 92;
  private static final int METHOD_contains93 = 93;
  private static final int METHOD_transferFocus94 = 94;
  private static final int METHOD_action95 = 95;
  private static final int METHOD_setSize96 = 96;
  private static final int METHOD_show97 = 97;
  private static final int METHOD_mouseDown98 = 98;
  private static final int METHOD_imageUpdate99 = 99;
  private static final int METHOD_repaint100 = 100;
  private static final int METHOD_getFontMetrics101 = 101;
  private static final int METHOD_lostFocus102 = 102;
  private static final int METHOD_postEvent103 = 103;
  private static final int METHOD_show104 = 104;
  private static final int METHOD_handleEvent105 = 105;
  private static final int METHOD_list106 = 106;
  private static final int METHOD_setBounds107 = 107;
  private static final int METHOD_mouseDrag108 = 108;
  private static final int METHOD_enable109 = 109;
  private static final int METHOD_createImage110 = 110;
  private static final int METHOD_keyUp111 = 111;
  private static final int METHOD_setLocation112 = 112;
  private static final int METHOD_repaint113 = 113;
  private static final int METHOD_repaint114 = 114;
  private static final int METHOD_keyDown115 = 115;
  private static final int METHOD_nextFocus116 = 116;
  private static final int METHOD_bounds117 = 117;
  private static final int METHOD_move118 = 118;
  private static final int METHOD_prepareImage119 = 119;
  private static final int METHOD_prepareImage120 = 120;
  private static final int METHOD_resize121 = 121;
  private static final int METHOD_getLocation122 = 122;
  private static final int METHOD_remove123 = 123;
  private static final int METHOD_setSize124 = 124;
  private static final int METHOD_list125 = 125;
  private static final int METHOD_location126 = 126;
  private static final int METHOD_paintAll127 = 127;
  private static final int METHOD_dispatchEvent128 = 128;
  private static final int METHOD_checkImage129 = 129;
  private static final int METHOD_checkImage130 = 130;
  private static final int METHOD_mouseExit131 = 131;
  private static final int METHOD_mouseMove132 = 132;
  private static final int METHOD_setLocation133 = 133;
  private static final int METHOD_mouseUp134 = 134;
  private static final int METHOD_size135 = 135;
  private static final int METHOD_inside136 = 136;
  private static final int METHOD_resize137 = 137;

  // Method array 
  private static MethodDescriptor[] methods = new MethodDescriptor[138];

  static {
    try {
      methods[METHOD_createOverlay0] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("createOverlay", new Class[] {}));
      methods[METHOD_createOverlay0].setDisplayName ( "" );
      methods[METHOD_removeOverlay1] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("removeOverlay", new Class[] {}));
      methods[METHOD_removeOverlay1].setDisplayName ( "" );
      methods[METHOD_repaintNow2] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaintNow", new Class[] {}));
      methods[METHOD_repaintNow2].setDisplayName ( "" );
      methods[METHOD_update3] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("update", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_update3].setDisplayName ( "" );
      methods[METHOD_paint4] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("paint", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paint4].setDisplayName ( "" );
      methods[METHOD_drawJavaImage5] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("drawJavaImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_drawJavaImage5].setDisplayName ( "" );
      methods[METHOD_addJavaImage6] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("addJavaImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_addJavaImage6].setDisplayName ( "" );
      methods[METHOD_createImage7] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("createImage", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_createImage7].setDisplayName ( "" );
      methods[METHOD_updateUI8] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("updateUI", new Class[] {}));
      methods[METHOD_updateUI8].setDisplayName ( "" );
      methods[METHOD_hasFocus9] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("hasFocus", new Class[] {}));
      methods[METHOD_hasFocus9].setDisplayName ( "" );
      methods[METHOD_unregisterKeyboardAction10] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("unregisterKeyboardAction", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_unregisterKeyboardAction10].setDisplayName ( "" );
      methods[METHOD_revalidate11] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("revalidate", new Class[] {}));
      methods[METHOD_revalidate11].setDisplayName ( "" );
      methods[METHOD_getListeners12] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getListeners", new Class[] {Class.forName("java.lang.Class")}));
      methods[METHOD_getListeners12].setDisplayName ( "" );
      methods[METHOD_getInputMap13] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getInputMap", new Class[] {}));
      methods[METHOD_getInputMap13].setDisplayName ( "" );
      methods[METHOD_removeNotify14] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("removeNotify", new Class[] {}));
      methods[METHOD_removeNotify14].setDisplayName ( "" );
      methods[METHOD_getToolTipLocation15] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getToolTipLocation", new Class[] {Class.forName("java.awt.event.MouseEvent")}));
      methods[METHOD_getToolTipLocation15].setDisplayName ( "" );
      methods[METHOD_getBounds16] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getBounds", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_getBounds16].setDisplayName ( "" );
      methods[METHOD_firePropertyChange17] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Double.TYPE, Double.TYPE}));
      methods[METHOD_firePropertyChange17].setDisplayName ( "" );
      methods[METHOD_getActionForKeyStroke18] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getActionForKeyStroke", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_getActionForKeyStroke18].setDisplayName ( "" );
      methods[METHOD_firePropertyChange19] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Character.TYPE, Character.TYPE}));
      methods[METHOD_firePropertyChange19].setDisplayName ( "" );
      methods[METHOD_repaint20] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaint", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_repaint20].setDisplayName ( "" );
      methods[METHOD_getInputMap21] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getInputMap", new Class[] {Integer.TYPE}));
      methods[METHOD_getInputMap21].setDisplayName ( "" );
      methods[METHOD_firePropertyChange22] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Float.TYPE, Float.TYPE}));
      methods[METHOD_firePropertyChange22].setDisplayName ( "" );
      methods[METHOD_firePropertyChange23] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_firePropertyChange23].setDisplayName ( "" );
      methods[METHOD_repaint24] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaint", new Class[] {Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint24].setDisplayName ( "" );
      methods[METHOD_addPropertyChangeListener25] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("addPropertyChangeListener", new Class[] {Class.forName("java.lang.String"), Class.forName("java.beans.PropertyChangeListener")}));
      methods[METHOD_addPropertyChangeListener25].setDisplayName ( "" );
      methods[METHOD_computeVisibleRect26] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("computeVisibleRect", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_computeVisibleRect26].setDisplayName ( "" );
      methods[METHOD_hide27] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("hide", new Class[] {}));
      methods[METHOD_hide27].setDisplayName ( "" );
      methods[METHOD_reshape28] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("reshape", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_reshape28].setDisplayName ( "" );
      methods[METHOD_putClientProperty29] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("putClientProperty", new Class[] {Class.forName("java.lang.Object"), Class.forName("java.lang.Object")}));
      methods[METHOD_putClientProperty29].setDisplayName ( "" );
      methods[METHOD_getLocation30] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getLocation", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_getLocation30].setDisplayName ( "" );
      methods[METHOD_setInputMap31] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setInputMap", new Class[] {Integer.TYPE, Class.forName("javax.swing.InputMap")}));
      methods[METHOD_setInputMap31].setDisplayName ( "" );
      methods[METHOD_paintImmediately32] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("paintImmediately", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_paintImmediately32].setDisplayName ( "" );
      methods[METHOD_disable33] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("disable", new Class[] {}));
      methods[METHOD_disable33].setDisplayName ( "" );
      methods[METHOD_resetKeyboardActions34] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("resetKeyboardActions", new Class[] {}));
      methods[METHOD_resetKeyboardActions34].setDisplayName ( "" );
      methods[METHOD_enable35] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("enable", new Class[] {}));
      methods[METHOD_enable35].setDisplayName ( "" );
      methods[METHOD_grabFocus36] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("grabFocus", new Class[] {}));
      methods[METHOD_grabFocus36].setDisplayName ( "" );
      methods[METHOD_getToolTipText37] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getToolTipText", new Class[] {Class.forName("java.awt.event.MouseEvent")}));
      methods[METHOD_getToolTipText37].setDisplayName ( "" );
      methods[METHOD_getClientProperty38] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getClientProperty", new Class[] {Class.forName("java.lang.Object")}));
      methods[METHOD_getClientProperty38].setDisplayName ( "" );
      methods[METHOD_getSize39] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getSize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_getSize39].setDisplayName ( "" );
      methods[METHOD_firePropertyChange40] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Long.TYPE, Long.TYPE}));
      methods[METHOD_firePropertyChange40].setDisplayName ( "" );
      methods[METHOD_removePropertyChangeListener41] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("removePropertyChangeListener", new Class[] {Class.forName("java.lang.String"), Class.forName("java.beans.PropertyChangeListener")}));
      methods[METHOD_removePropertyChangeListener41].setDisplayName ( "" );
      methods[METHOD_requestDefaultFocus42] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("requestDefaultFocus", new Class[] {}));
      methods[METHOD_requestDefaultFocus42].setDisplayName ( "" );
      methods[METHOD_addNotify43] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("addNotify", new Class[] {}));
      methods[METHOD_addNotify43].setDisplayName ( "" );
      methods[METHOD_firePropertyChange44] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Byte.TYPE, Byte.TYPE}));
      methods[METHOD_firePropertyChange44].setDisplayName ( "" );
      methods[METHOD_requestFocus45] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("requestFocus", new Class[] {}));
      methods[METHOD_requestFocus45].setDisplayName ( "" );
      methods[METHOD_getConditionForKeyStroke46] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getConditionForKeyStroke", new Class[] {Class.forName("javax.swing.KeyStroke")}));
      methods[METHOD_getConditionForKeyStroke46].setDisplayName ( "" );
      methods[METHOD_firePropertyChange47] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Short.TYPE, Short.TYPE}));
      methods[METHOD_firePropertyChange47].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction48] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("registerKeyboardAction", new Class[] {Class.forName("java.awt.event.ActionListener"), Class.forName("javax.swing.KeyStroke"), Integer.TYPE}));
      methods[METHOD_registerKeyboardAction48].setDisplayName ( "" );
      methods[METHOD_registerKeyboardAction49] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("registerKeyboardAction", new Class[] {Class.forName("java.awt.event.ActionListener"), Class.forName("java.lang.String"), Class.forName("javax.swing.KeyStroke"), Integer.TYPE}));
      methods[METHOD_registerKeyboardAction49].setDisplayName ( "" );
      methods[METHOD_isLightweightComponent50] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("isLightweightComponent", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_isLightweightComponent50].setDisplayName ( "" );
      methods[METHOD_createToolTip51] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("createToolTip", new Class[] {}));
      methods[METHOD_createToolTip51].setDisplayName ( "" );
      methods[METHOD_print52] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("print", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_print52].setDisplayName ( "" );
      methods[METHOD_paintImmediately53] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("paintImmediately", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_paintImmediately53].setDisplayName ( "" );
      methods[METHOD_getInsets54] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getInsets", new Class[] {Class.forName("java.awt.Insets")}));
      methods[METHOD_getInsets54].setDisplayName ( "" );
      methods[METHOD_printAll55] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("printAll", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_printAll55].setDisplayName ( "" );
      methods[METHOD_contains56] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("contains", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_contains56].setDisplayName ( "" );
      methods[METHOD_firePropertyChange57] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("firePropertyChange", new Class[] {Class.forName("java.lang.String"), Boolean.TYPE, Boolean.TYPE}));
      methods[METHOD_firePropertyChange57].setDisplayName ( "" );
      methods[METHOD_scrollRectToVisible58] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("scrollRectToVisible", new Class[] {Class.forName("java.awt.Rectangle")}));
      methods[METHOD_scrollRectToVisible58].setDisplayName ( "" );
      methods[METHOD_getComponentAt59] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getComponentAt", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_getComponentAt59].setDisplayName ( "" );
      methods[METHOD_add60] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_add60].setDisplayName ( "" );
      methods[METHOD_preferredSize61] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("preferredSize", new Class[] {}));
      methods[METHOD_preferredSize61].setDisplayName ( "" );
      methods[METHOD_locate62] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("locate", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_locate62].setDisplayName ( "" );
      methods[METHOD_list63] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("list", new Class[] {Class.forName("java.io.PrintWriter"), Integer.TYPE}));
      methods[METHOD_list63].setDisplayName ( "" );
      methods[METHOD_add64] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Class.forName("java.lang.Object"), Integer.TYPE}));
      methods[METHOD_add64].setDisplayName ( "" );
      methods[METHOD_add65] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Integer.TYPE}));
      methods[METHOD_add65].setDisplayName ( "" );
      methods[METHOD_invalidate66] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("invalidate", new Class[] {}));
      methods[METHOD_invalidate66].setDisplayName ( "" );
      methods[METHOD_printComponents67] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("printComponents", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_printComponents67].setDisplayName ( "" );
      methods[METHOD_doLayout68] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("doLayout", new Class[] {}));
      methods[METHOD_doLayout68].setDisplayName ( "" );
      methods[METHOD_layout69] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("layout", new Class[] {}));
      methods[METHOD_layout69].setDisplayName ( "" );
      methods[METHOD_list70] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("list", new Class[] {Class.forName("java.io.PrintStream"), Integer.TYPE}));
      methods[METHOD_list70].setDisplayName ( "" );
      methods[METHOD_add71] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.lang.String"), Class.forName("java.awt.Component")}));
      methods[METHOD_add71].setDisplayName ( "" );
      methods[METHOD_remove72] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("remove", new Class[] {Integer.TYPE}));
      methods[METHOD_remove72].setDisplayName ( "" );
      methods[METHOD_isAncestorOf73] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("isAncestorOf", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_isAncestorOf73].setDisplayName ( "" );
      methods[METHOD_findComponentAt74] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("findComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_findComponentAt74].setDisplayName ( "" );
      methods[METHOD_findComponentAt75] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("findComponentAt", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_findComponentAt75].setDisplayName ( "" );
      methods[METHOD_insets76] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("insets", new Class[] {}));
      methods[METHOD_insets76].setDisplayName ( "" );
      methods[METHOD_getComponentAt77] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getComponentAt", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_getComponentAt77].setDisplayName ( "" );
      methods[METHOD_paintComponents78] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("paintComponents", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paintComponents78].setDisplayName ( "" );
      methods[METHOD_countComponents79] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("countComponents", new Class[] {}));
      methods[METHOD_countComponents79].setDisplayName ( "" );
      methods[METHOD_minimumSize80] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("minimumSize", new Class[] {}));
      methods[METHOD_minimumSize80].setDisplayName ( "" );
      methods[METHOD_deliverEvent81] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("deliverEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_deliverEvent81].setDisplayName ( "" );
      methods[METHOD_removeAll82] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("removeAll", new Class[] {}));
      methods[METHOD_removeAll82].setDisplayName ( "" );
      methods[METHOD_remove83] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("remove", new Class[] {Class.forName("java.awt.Component")}));
      methods[METHOD_remove83].setDisplayName ( "" );
      methods[METHOD_add84] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.awt.Component"), Class.forName("java.lang.Object")}));
      methods[METHOD_add84].setDisplayName ( "" );
      methods[METHOD_validate85] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("validate", new Class[] {}));
      methods[METHOD_validate85].setDisplayName ( "" );
      methods[METHOD_gotFocus86] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("gotFocus", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_gotFocus86].setDisplayName ( "" );
      methods[METHOD_toString87] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("toString", new Class[] {}));
      methods[METHOD_toString87].setDisplayName ( "" );
      methods[METHOD_list88] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("list", new Class[] {Class.forName("java.io.PrintStream")}));
      methods[METHOD_list88].setDisplayName ( "" );
      methods[METHOD_enableInputMethods89] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("enableInputMethods", new Class[] {Boolean.TYPE}));
      methods[METHOD_enableInputMethods89].setDisplayName ( "" );
      methods[METHOD_mouseEnter90] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseEnter", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseEnter90].setDisplayName ( "" );
      methods[METHOD_getSize91] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getSize", new Class[] {}));
      methods[METHOD_getSize91].setDisplayName ( "" );
      methods[METHOD_add92] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("add", new Class[] {Class.forName("java.awt.PopupMenu")}));
      methods[METHOD_add92].setDisplayName ( "" );
      methods[METHOD_contains93] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("contains", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_contains93].setDisplayName ( "" );
      methods[METHOD_transferFocus94] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("transferFocus", new Class[] {}));
      methods[METHOD_transferFocus94].setDisplayName ( "" );
      methods[METHOD_action95] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("action", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_action95].setDisplayName ( "" );
      methods[METHOD_setSize96] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setSize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setSize96].setDisplayName ( "" );
      methods[METHOD_show97] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("show", new Class[] {}));
      methods[METHOD_show97].setDisplayName ( "" );
      methods[METHOD_mouseDown98] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseDown", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDown98].setDisplayName ( "" );
      methods[METHOD_imageUpdate99] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("imageUpdate", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_imageUpdate99].setDisplayName ( "" );
      methods[METHOD_repaint100] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaint", new Class[] {Long.TYPE}));
      methods[METHOD_repaint100].setDisplayName ( "" );
      methods[METHOD_getFontMetrics101] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getFontMetrics", new Class[] {Class.forName("java.awt.Font")}));
      methods[METHOD_getFontMetrics101].setDisplayName ( "" );
      methods[METHOD_lostFocus102] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("lostFocus", new Class[] {Class.forName("java.awt.Event"), Class.forName("java.lang.Object")}));
      methods[METHOD_lostFocus102].setDisplayName ( "" );
      methods[METHOD_postEvent103] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("postEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_postEvent103].setDisplayName ( "" );
      methods[METHOD_show104] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("show", new Class[] {Boolean.TYPE}));
      methods[METHOD_show104].setDisplayName ( "" );
      methods[METHOD_handleEvent105] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("handleEvent", new Class[] {Class.forName("java.awt.Event")}));
      methods[METHOD_handleEvent105].setDisplayName ( "" );
      methods[METHOD_list106] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("list", new Class[] {Class.forName("java.io.PrintWriter")}));
      methods[METHOD_list106].setDisplayName ( "" );
      methods[METHOD_setBounds107] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setBounds", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setBounds107].setDisplayName ( "" );
      methods[METHOD_mouseDrag108] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseDrag", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseDrag108].setDisplayName ( "" );
      methods[METHOD_enable109] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("enable", new Class[] {Boolean.TYPE}));
      methods[METHOD_enable109].setDisplayName ( "" );
      methods[METHOD_createImage110] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("createImage", new Class[] {Class.forName("java.awt.image.ImageProducer")}));
      methods[METHOD_createImage110].setDisplayName ( "" );
      methods[METHOD_keyUp111] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("keyUp", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE}));
      methods[METHOD_keyUp111].setDisplayName ( "" );
      methods[METHOD_setLocation112] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setLocation", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_setLocation112].setDisplayName ( "" );
      methods[METHOD_repaint113] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaint", new Class[] {}));
      methods[METHOD_repaint113].setDisplayName ( "" );
      methods[METHOD_repaint114] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("repaint", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}));
      methods[METHOD_repaint114].setDisplayName ( "" );
      methods[METHOD_keyDown115] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("keyDown", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE}));
      methods[METHOD_keyDown115].setDisplayName ( "" );
      methods[METHOD_nextFocus116] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("nextFocus", new Class[] {}));
      methods[METHOD_nextFocus116].setDisplayName ( "" );
      methods[METHOD_bounds117] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("bounds", new Class[] {}));
      methods[METHOD_bounds117].setDisplayName ( "" );
      methods[METHOD_move118] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("move", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_move118].setDisplayName ( "" );
      methods[METHOD_prepareImage119] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("prepareImage", new Class[] {Class.forName("java.awt.Image"), Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_prepareImage119].setDisplayName ( "" );
      methods[METHOD_prepareImage120] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("prepareImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_prepareImage120].setDisplayName ( "" );
      methods[METHOD_resize121] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("resize", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_resize121].setDisplayName ( "" );
      methods[METHOD_getLocation122] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("getLocation", new Class[] {}));
      methods[METHOD_getLocation122].setDisplayName ( "" );
      methods[METHOD_remove123] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("remove", new Class[] {Class.forName("java.awt.MenuComponent")}));
      methods[METHOD_remove123].setDisplayName ( "" );
      methods[METHOD_setSize124] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setSize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_setSize124].setDisplayName ( "" );
      methods[METHOD_list125] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("list", new Class[] {}));
      methods[METHOD_list125].setDisplayName ( "" );
      methods[METHOD_location126] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("location", new Class[] {}));
      methods[METHOD_location126].setDisplayName ( "" );
      methods[METHOD_paintAll127] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("paintAll", new Class[] {Class.forName("java.awt.Graphics")}));
      methods[METHOD_paintAll127].setDisplayName ( "" );
      methods[METHOD_dispatchEvent128] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("dispatchEvent", new Class[] {Class.forName("java.awt.AWTEvent")}));
      methods[METHOD_dispatchEvent128].setDisplayName ( "" );
      methods[METHOD_checkImage129] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("checkImage", new Class[] {Class.forName("java.awt.Image"), Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_checkImage129].setDisplayName ( "" );
      methods[METHOD_checkImage130] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("checkImage", new Class[] {Class.forName("java.awt.Image"), Integer.TYPE, Integer.TYPE, Class.forName("java.awt.image.ImageObserver")}));
      methods[METHOD_checkImage130].setDisplayName ( "" );
      methods[METHOD_mouseExit131] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseExit", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseExit131].setDisplayName ( "" );
      methods[METHOD_mouseMove132] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseMove", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseMove132].setDisplayName ( "" );
      methods[METHOD_setLocation133] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("setLocation", new Class[] {Class.forName("java.awt.Point")}));
      methods[METHOD_setLocation133].setDisplayName ( "" );
      methods[METHOD_mouseUp134] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("mouseUp", new Class[] {Class.forName("java.awt.Event"), Integer.TYPE, Integer.TYPE}));
      methods[METHOD_mouseUp134].setDisplayName ( "" );
      methods[METHOD_size135] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("size", new Class[] {}));
      methods[METHOD_size135].setDisplayName ( "" );
      methods[METHOD_inside136] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("inside", new Class[] {Integer.TYPE, Integer.TYPE}));
      methods[METHOD_inside136].setDisplayName ( "" );
      methods[METHOD_resize137] = new MethodDescriptor ( Class.forName("jp.ac.kumamoto_u.kuh.fc.jsato.swing_beans.OverlayImagePanelBean").getMethod("resize", new Class[] {Class.forName("java.awt.Dimension")}));
      methods[METHOD_resize137].setDisplayName ( "" );
    }
    catch( Exception e) {}//GEN-HEADEREND:Methods

    // Here you can add code for customizing the methods array.
    
}//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null; //GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null; //GEN-END:IconsDef
  private static String iconNameC16 = "OverlayImagePanelBean.gif";//GEN-BEGIN:Icons
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

