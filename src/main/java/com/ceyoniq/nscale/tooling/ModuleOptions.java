package com.ceyoniq.nscale.tooling;

public interface ModuleOptions {

    /**
     * Is add-opens accessible
     * @return true if module options can use setAccessible
     * @since 1.0.5
     */
    default boolean isAccessible() { return false; }
    
    /**
     * Open module for given package.
     * <p> This has the same meaning as the Java command line option '--add-opens=module/package=ALL-UNNAMED' </p> 
     * @param module name of module to open (e.g. "java.base")
     * @param pn package name (e.g. "java.lang")
     */
    default void addOpens( String module, String pn ) {}
    
    /**
     * Create ModuleOptions for Java &gt;= 9 or dummy implementation on Java 8. 
     * <p>
     * Java 9-15 implementation will use Unsafe to allow deep reflection. </br>
     * Java 16 JEP 396: <i>  Strongly Encapsulate JDK Internals by Default</i> does not allow reflection access on fields. </br>
     * Therefore you must open "java.lang" for this module or all unnamed: </br>
     * <code> java --add-opens java.base/java.lang=ALL-UNNAMED <code> .
     * </p>
     * @return the ModuleOptions implementation
     */
    static ModuleOptions create() {
        String className = ModuleOptions.class.getName() + "Impl";
        try {
            Class<?> cls = Class.forName( className );
            return ( ModuleOptions ) cls.getConstructor().newInstance();
            
        } catch ( ReflectiveOperationException e ) {
            throw new IllegalArgumentException( "Reflection error", e );

        } catch ( UnsupportedClassVersionError e ) {
            return new ModuleOptions() {};
        }
    }
}
