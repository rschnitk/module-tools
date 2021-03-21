package com.ceyoniq.nscale.tooling;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import sun.misc.Unsafe;

public class ModuleOptionsImpl implements ModuleOptions {

    private Method methodAddOpens;

    public ModuleOptionsImpl() {
        try {
            // this method is called by command line: java --add-opens
            methodAddOpens = Module.class.getDeclaredMethod( "implAddOpensToAllUnnamed", String.class );
            
            try {
                // For Java 9-15, try to use Unsafe to setAccessible() - no warnings on command line
                Field override = AccessibleObject.class.getDeclaredField( "override" );

                // Use unsafe
                Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
                f.setAccessible( true );
                Unsafe unsafe = ( Unsafe ) f.get( null );
                
                // set accessible = true
                unsafe.putBoolean( methodAddOpens, unsafe.objectFieldOffset( override ), true );
                
            } catch ( NoSuchFieldException ex ) {
                // Java 16: minimum JVM option is:   "--add-opens" "java.base/java.lang=ALL-UNNAMED"
                methodAddOpens.setAccessible( true );
            }
            
        } catch ( ReflectiveOperationException | SecurityException ex ) {
        }
    }

    @Override
    public void addOpens( String module, String pn ) {

        Optional< Module > mod = ModuleLayer.boot().findModule( module );
        if ( mod.isPresent() ) {
            try {
                methodAddOpens.invoke( mod.get(), pn );
            } catch ( ReflectiveOperationException | IllegalArgumentException e ) {
            }
        }
    }
}