package com.ceyoniq.nscale.tooling;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.util.Optional;

import sun.misc.Unsafe; //NOSONAR

public class ModuleOptionsImpl implements ModuleOptions {

    private static final boolean DEBUG = "1".equals( System.getenv( "MODULE_TOOLS_DEBUG" ) );
    
    private Method methodAddOpens;
    private boolean accessible;

    public ModuleOptionsImpl() {
        try {
            // this method is called by command line: java --add-opens
            methodAddOpens = Module.class.getDeclaredMethod( "implAddOpensToAllUnnamed", String.class );
            
            // Use unsafe
            Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
            f.trySetAccessible();
            Unsafe unsafe = ( Unsafe ) f.get( null );

            // set accessible = true
            unsafe.putBoolean( methodAddOpens, accessibleObjectOffset( unsafe ), true );
            
            this.accessible = true;
                
        } catch ( NoClassDefFoundError exc ) { // unsafe missing

            if ( methodAddOpens != null ) {
                try {
                    methodAddOpens.setAccessible(true);
                    this.accessible = true;
                } catch ( InaccessibleObjectException e ) {
                    // let accessible false
                }
            }

        } catch ( Exception ex ) {
            if ( DEBUG ) { 
                ex.printStackTrace(System.out);
            }
        }
    }

    @Override
    public boolean isAccessible() {
        return this.accessible;
    }
    
    @Override
    public void addOpens( String module, String pn ) {

        Optional< Module > mod = ModuleLayer.boot().findModule( module );
        if ( mod.isPresent() ) {
            try {
                methodAddOpens.invoke( mod.get(), pn );
            } catch ( ReflectiveOperationException | IllegalArgumentException e ) {
                if ( DEBUG ) { 
                    e.printStackTrace(System.out);
                }
            }
        }
    }
    
    // ------------------------------------------------------------------------------------------------------------------

    private static long accessibleObjectOffset( Unsafe unsafe ) throws IllegalArgumentException {
        
        try {
            Field override = AccessibleObject.class.getDeclaredField( "override" );
            return unsafe.objectFieldOffset( override );
        } catch ( NoSuchFieldException e ) {
            return 12; // hard coded default
        }
    }

    // ------------------------------------------------------------------------------------------------------------------

    // simple test (main)
    public static void main( String[] args ) {
        ModuleOptionsImpl mopt = new ModuleOptionsImpl();
        if ( mopt.isAccessible() ) {
            mopt.addOpens( "java.base", "java.lang" );
            mopt.addOpens( "java.base", "java.util" );
            System.out.println( "Done." ); //NOSONAR
        } else {
            System.out.println( "Missing JVM Option: \"--add-opens\" \"java.base/java.lang=ALL-UNNAMED\"" ); //NOSONAR
        }
    }
}