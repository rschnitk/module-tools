module-tools
============

wer Java Version >= 9 benutzt, kennt die unschönen Meldungen:

> WARNING: An illegal reflective access operation has occurred    
> WARNING: Illegal reflective access by com.caucho.hessian.io.JavaDeserializer (al-cmdclient-7.15.1100.jar) to field java.lang.StackTraceElement.classLoaderName    
> WARNING: Please consider reporting this to the maintainers of com.caucho.hessian.io.JavaDeserializer    
> WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations    
> WARNING: All illegal access operations will be denied in a future release    

Das kann mit einer Java/JVM Option umgangen werden:
```bash
    java  --add-opens=java.base/java.lang=ALL-UNNAMED -jar  Application.jar
```

Bei kleinen Tools ist das aber doof, da der Benutzer das immer mitgeben muss.
Falls man Java 8 erwischt hat, startet die JVM nicht mit dieser Option.

Allerdings kann man das auch programmatisch lösen, dafür gibt es die kleine Library „module-tools“ bzw. die Klasse ModuleOptions. Hier muss man nur das Modul und das Package übergeben. Falls Java 8 aktiv ist, wird eine Dummy Klasse referenziert, es gibt keinen Fehler.

```java
public static void main ( String[] args ) throws Exception {

    Locale.setDefault( Locale.US );
        
    ModuleOptions mopt = ModuleOptions.create();
    mopt.addOpens( "java.base", "java.lang" );
    mopt.addOpens( "java.base", "java.io" );
        
    ...  
}
```

Mit Gradle/Maven muss nur die Library referenziert werden ( auf http://artifactory1/artifactory/libs-release-local/ )

```java
dependencies {

    // add-options
    implementation  "com.ceyoniq.nscale.tooling:module-tools:1.0"

}
```
