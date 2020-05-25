package com.ceyoniq.nscale.tooling;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import sun.misc.Unsafe;

public class ModuleOptionsImpl implements ModuleOptions {

    private Unsafe unsafe;
    private Method methodAddOpens;

    public ModuleOptionsImpl() {
        try {
            // get unsafe
            Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
            f.setAccessible( true );
            unsafe = ( Unsafe ) f.get( null );
            
            // like command line: java --add-opens
            methodAddOpens = Module.class.getDeclaredMethod( "implAddOpensToAllUnnamed", String.class );
            
            // set accessible = true
            Field override = AccessibleObject.class.getDeclaredField( "override" );
            unsafe.putBoolean( methodAddOpens, unsafe.objectFieldOffset( override ), true );
            
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