/*
 * Trace.java
 *
 * Created on 2001/10/06, 10:19
 */
package open.dolphin.server;

/**
 */
public interface Trace {
    
      public void clear();

      // turn on and off debugging
      public void setDebug( boolean debug );

      // write out a debug message
      public void debug( String message );

      // write out an error message
      public void error( String message );
}