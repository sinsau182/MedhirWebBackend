pipeline {
    agent any

    stages {
        stage('Pre-Build') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'gitea-token', url: 'https://git.home.medhir.in/Medhir/nayati.git']])
            }
        }
        stage('Build') {
            steps {
                sh "./gradlew clean build"
            }
        }
        stage("build image") {
            steps {
                script {
                    sh "podman build -t git.home.medhir.in/medhir/medhir-api:$BUILD_ID api"
                }
            }
        }
        stage("publish image") {
            steps {
                withCredentials([string(credentialsId: 'container-registry-token', variable: 'TOKEN')]) {
                    script {
                        sh "podman push --creds podman:$TOKEN git.home.medhir.in/medhir/medhir-api:$BUILD_ID"
                    }
                }
            }
        }
        stage("clean workspace") {
            steps {
                script {
                    sh "ls"
                    cleanWs()
                    sh "ls"
                    sh "podman image rm git.home.medhir.in/medhir/medhir-api:$BUILD_ID"
                }
            }
        }
    }
}
