pipeline {

    agent any

	environment {
	    JAVA_HOME = '/opt/abts/java8/'
	}

    stages {

        stage('Build') {
            steps {
			    chmod +x gradlew ; ./gradlew --no-daemon clean build
            }
        }
    }
}
