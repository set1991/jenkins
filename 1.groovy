pipeline {
    agent any
    parameters {
        string(name: "TEST_STRING", defaultValue: "SONARQ syntax testing", description: "Sample string parameter")
    }

    stages {
        stage ('GIT event') {
            steps {
                echo 'pull code from git'
            }
        }        
        stage ('prebuild tests') {
            steps {
                echo 'Test type: syntax'
                echo "${params.TEST_STRING}"
            }
        }
        stage ('build') {
            steps {
                echo 'Building with Gradle'
                //sh "gradle build ./.."
            }
        }
        stage ('post build tests') {
            steps {
                echo 'Testing with Gradle'
                //sh "gradle test ./.."
            }
        }        
        stage ('push to artifactory') {
            steps {
                echo 'JFrog publish'
            }
        }
        stage ('deployment') {
            steps {
                echo 'deploy to NGINXWebServer'
            }
        }           
        stage ('post deployment tests') {
        parallel {
                    stage('Integration tests'){
            steps {
                echo 'integration testing'
            }            
            }
                    stage('Smoke testing'){
            steps {
                echo 'smoke testing'
            }            
            }
                    stage('Load testing'){
            steps {
                echo 'load tests'
        }
        }      
        }    
    }
}

post {
    always {
        echo 'One way or another, I have finished'
    }
    success {
        echo 'I succeeded!'
    }
    failure {
        echo 'I failed :('
    }
    changed {
        echo 'Things were different before...'
    }
}      
}
