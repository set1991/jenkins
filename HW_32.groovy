pipeline {
    agent none
    parameters {
        string(name: "DOCKER_IMAGE", defaultValue: "nginx_jenkins", description: "Name for docker image")
    }

    stages {
        stage ('GIT clone') {
            agent { label 'master' }
            steps {
           // The below will clone your repo and will be checked out to master branch by default.
           git credentialsId: 'jenkins-rsa', url: 'git@github.com:set1991/jenkins.git'
           // Do a ls -lart to view all the files are cloned. It will be clonned. This is just for you to be sure about it.
           sh "ls -lart ./*" 
           // List all branches in your repo. 
           sh "git branch -a"
        
       }
        }        
        stage ('prebuild tests') {
            agent { label 'master' }
            steps {
                echo 'Test type: syntax'
                
                //linter test
            }
        }
        //building docker image from Dockerfile
        stage ('build') {
            agent { label 'master' }
            steps {
                echo 'Building docker image'
                sh "docker build -t set1991/${DOCKER_IMAGE} ./nginx-image" 
            }
        }
        stage ('post build tests') {
            agent { label 'master' }
            steps {
                echo 'Testing with Gradle'
                //sh "gradle test ./.."
            }
        }        
        stage ('push artifactory to dockerhub') {
            agent { label 'master' }
            steps {
                echo 'Push image to dockerhub'
                sh "docker push set1991/${DOCKER_IMAGE}"
                //sh "docker save -o ${DOCKER_IMAGE}.tar ${DOCKER_IMAGE}"
                //sh "cp ${DOCKER_IMAGE}.tar /var/lib/jenkins/${DOCKER_IMAGE}.tar"
            }
        }
        stage ('copy artifactory from dockerhub') {
            agent { label 'agent_226' }
            steps {
                echo 'get image from dockerhub'
                sh "docker pull set1991/${DOCKER_IMAGE}:latest"
        //        sh "scp ${DOCKER_IMAGE}.tar jenkins@192.168.122.226:/."
                //sh "cp ${DOCKER_IMAGE}.tar /var/lib/jenkins/${DOCKER_IMAGE}.tar"
            }
        }
        stage ('pre deploy test') {
            agent { label 'agent_226' }
            steps {
               script {
                    def status = sh(returnStdout: true, script: 'docker ps -a --filter name=test_${DOCKER_IMAGE} | wc -l')
                    if ("${status}" == "1") {
                        echo "container does not exist"
                    } else {
                        echo "delete container"
                        sh "docker stop test_${DOCKER_IMAGE}"
                        sh "docker rm test_${DOCKER_IMAGE}"
                    }
                } 
                
            }
        } 
        stage ('deployment') {
            agent { label 'agent_226' }
            steps {
                echo 'deploy to NGINXWebServer'
                sh "docker run -d --name test_${DOCKER_IMAGE} -p 8081:8081 set1991/${DOCKER_IMAGE}" 
            }
        }           
        stage('Smoke testing'){
            agent { label 'agent_226' }
            steps {
                script{
                    try{
                        sh 'curl -s localhost:8081'
                    }
                    catch (Exception e) {
                        echo "Curl command failed: ${e.getMessage()}"    
                        exit 1
                        
                    }
                    
                }
            }       
        }
        stage('Load testing'){
            steps {
                echo 'load tests'
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
