package open.dolphin.server;

/**
 */
public class SystemTrace implements Trace {

      private boolean debug;
      
      public void clear() {
      }

      public void setDebug( boolean debug ) {
            this.debug = debug;
      }

      public void debug( String message ) {
            if( debug ) {  // only print if debug is true
                  System.out.println( "DEBUG: " + message );
            }
      }
      public void error( String message ) {
            // always print out errors
            System.out.println( "ERROR: " + message );
      }
}