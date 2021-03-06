module-tools
============

who uses Java version >= 9 knows the unsightly messages:

> WARNING: An illegal reflective access operation has occurred    
> WARNING: Illegal reflective access by com.caucho.hessian.io.JavaDeserializer (al-cmdclient-7.15.1100.jar) to field java.lang.StackTraceElement.classLoaderName    
> WARNING: Please consider reporting this to the maintainers of com.caucho.hessian.io.JavaDeserializer    
> WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations    
> WARNING: All illegal access operations will be denied in a future release    

This can be avoided with a Java/JVM option:
```bash
    java  --add-opens=java.base/java.lang=ALL-UNNAMED -jar  Application.jar
```

With small tools, however, this is stupid, since the user always has to give it.
If you have used Java 8, the JVM does not start with this option.

However, this can also be solved programmatically, for this there is the small library "module-tools" or the class ModuleOptions. Here you only have to hand over the module and the package. If Java 8 is active, a dummy class is referenced, there is no error.

```java
public static void main ( String[] args ) throws Exception {

    Locale.setDefault( Locale.US );
        
    ModuleOptions mopt = ModuleOptions.create();
    mopt.addOpens( "java.base", "java.lang" );
    mopt.addOpens( "java.base", "java.io" );
        
    ...  
}
```
