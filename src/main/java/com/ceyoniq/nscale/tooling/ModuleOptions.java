package com.ceyoniq.nscale.tooling;

public interface ModuleOptions {

    /**
     * Open module for given package.
     * <p> This has the same meaning as the Java command line option '--add-opens=module/package=ALL-UNNAMED' </p> 
     * @param module name of module to open (e.g. "java.base")
     * @param pn package name (e.g. "java.lang")
     */
    public void addOpens( String module, String pn );
    
    /**
     * Create Module Options or dummy Implementation on Java8
     * @return
     */
    static ModuleOptions create() {
        String className = ModuleOptions.class.getName() + "Impl";
        try {
            Class<?> cls = Class.forName( className );
            return ( ModuleOptions ) cls.getConstructor().newInstance();
            
        } catch ( ReflectiveOperationException e ) {
            throw new RuntimeException ( e.getMessage() );

        } catch ( UnsupportedClassVersionError e ) {
            return new ModuleOptions() {
                @Override
                public void addOpens( String module, String pn ) {
                }
            };
        }
    }
}
