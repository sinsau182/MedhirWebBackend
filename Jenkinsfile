pipeline {
    agent any

    environment {
        PODMAN_CMD = "sudo -u podman -i"
        IMAGE_NAME = "backend"
        IMAGE_TAG = "latest"
        JAR_FILE = "build/libs/backend-0.0.1-SNAPSHOT.jar"
        SSH_USER = "ankitm"
        SERVER_IP = "192.168.0.200"
        GIT_REPO = "http://192.168.0.101:3000/jadhav.manoj/Backend-Rest.git"
        GIT_BRANCH = "main"
        GIT_CREDENTIALS_ID = "jenkins"
        SSH_CREDENTIALS_ID = "ankitm-ssh-key"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Build JAR File with Gradle') {
            steps {
                script {
                    try {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew clean build'
                        echo "✅ Gradle build completed successfully."
                    } catch (Exception e) {
                        error "❌ Gradle build failed: ${e.message}"
                    }
                }
            }
        }

        stage('Transfer JAR File and Deploy on Remote Server') {
            steps {
                sshagent(credentials: ["${SSH_CREDENTIALS_ID}"]) {
                    script {
                        try {
                            // Transfer JAR file
                            sh """
                                scp -o StrictHostKeyChecking=no ${JAR_FILE} ${SSH_USER}@${SERVER_IP}:/home/${SSH_USER}/shared/
                            """

                            // Run Podman build and deploy
                            sh """
                                ssh -v -o StrictHostKeyChecking=no ${SSH_USER}@${SERVER_IP} "
                                set -xe;
                                cd /home/${SSH_USER}/shared;

                                # Debugging - Check Dockerfile existence and permissions
                                echo '🔍 Checking Dockerfile:';
                                ls -l /home/${SSH_USER}/shared;
                                pwd;

                                # Double-check case sensitivity
                                if [ ! -f Dockerfile ] && [ ! -f dockerfile ]; then
                                    echo '❌ Dockerfile not found in shared directory!';
                                    exit 1;
                                fi

                                # Ensure proper permissions
                                chmod -R 755 /home/${SSH_USER}/shared;

                                # SELinux Fix (if applicable)
                                if command -v chcon &> /dev/null; then
                                    chcon -t container_file_t /home/${SSH_USER}/shared/Dockerfile || true;
                                fi

                                # Stop and remove old container if it exists
                                if ${PODMAN_CMD} podman ps -a --format '{{.Names}}' | grep -q '${IMAGE_NAME}'; then
                                    echo '🛑 Stopping existing container...';
                                    ${PODMAN_CMD} podman stop ${IMAGE_NAME} || true;
                                    ${PODMAN_CMD} podman rm ${IMAGE_NAME} || true;
                                fi

                                # Remove old image if exists
                                if ${PODMAN_CMD} podman images -q ${IMAGE_NAME}:${IMAGE_TAG} | grep -q '.'; then
                                    echo '🗑 Removing old image...';
                                    ${PODMAN_CMD} podman rmi -f ${IMAGE_NAME}:${IMAGE_TAG} || true;
                                fi

                                # Build Podman image with explicit Dockerfile path
                                echo '🚀 Building new image...';
                                ${PODMAN_CMD} podman build -t ${IMAGE_NAME}:${IMAGE_TAG} -f /home/${SSH_USER}/shared/Dockerfile /home/${SSH_USER}/shared;

                                # Run the container
                                echo '🆕 Starting new container...';
                                ${PODMAN_CMD} podman run -d --name ${IMAGE_NAME} --network shared -p 4000:4000 ${IMAGE_NAME}:${IMAGE_TAG};
                                echo '✅ Backend container started successfully.'
                                "
                            """

                        } catch (Exception e) {
                            error "❌ Deployment failed: ${e.message}"
                        }
                    }
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sshagent(credentials: ["${SSH_CREDENTIALS_ID}"]) {
                    script {
                        try {
                            sh """
                                ssh -o StrictHostKeyChecking=no ${SSH_USER}@${SERVER_IP} "
                                set -xe;
                                ${PODMAN_CMD} podman ps -a | grep ${IMAGE_NAME};
                                ${PODMAN_CMD} podman logs ${IMAGE_NAME};
                                "
                            """
                            echo "✅ Deployment verification completed."
                        } catch (Exception e) {
                            error "❌ Deployment verification failed: ${e.message}"
                        }
                    }
                }
            }
        }
    }
}
