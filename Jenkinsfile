pipeline {
	agent any

    environment {
		IMAGE_NAME = 'celairm/backend2'
        IMAGE_TAG = "0.${BUILD_NUMBER}"
        GITHUB_REPO = 'https://github.com/celarim/jenkins_test'
        NAMESPACE = 'kgj'
    }

    stages {
		stage('Git Clone') {
			steps{
				echo "Cloneing Repository"
                git branch: 'main', url: 'https://github.com/celarim/jenkins_test'
            }
        }
        stage('Gradle Build') {
			steps{
				echo "Add Permission"
                sh 'chmod +x gradlew'

                echo "Build"
                sh './gradlew bootJar'
            }
        }
        stage('Build Docker Image') {
			steps {
				script {
					docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
                }
            }
        }
        stage('Push to Registry') {
			steps {
				script {
					withDockerRegistry([credentialsId: 'DOCKER_HUB']) {
						docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
                    }
                }
            }
        }
        stage('Get Blue or Green') {
			steps {
				script {
					if (BUILD_ID.toInteger() % 2 == 0) {
						env.BORG = "blue"
						env.NOTBORG = "green"
					} else {
						env.BORG = "green"
						env.NOTBORG = "blue"
					}
                }
            }
        }



        stage('SSH') {
			steps{
				script{
					sshPublisher(
                        publishers: [
                            sshPublisherDesc(
                                configName: 'k8s',
                                verbose: true,
                                transfers: [
                                    sshTransfer(
                                        sourceFiles: 'k8s/backend-deployment.yml',
                                        remoteDirectory: '/',
                                        execCommand: '''
                                            sed -i "s/latest/0.$BUILD_ID/g" k8s/backend-deployment.yml
                                        '''
                                    ),
                                    sshTransfer(
                                        sourceFiles: 'k8s/backend-deployment.yml',
                                        remoteDirectory: '/',
                                        execCommand: '''
                                            sed -i "s/borg/$BORG/g" k8s/backend-deployment.yml
                                        '''
                                    ),
                                    sshTransfer(
                                        execCommand: '''
                                            kubectl apply -f /home/test/k8s/backend-deployment.yml -n $NAMESPACE
                                        '''
                                    ),
                                    sshTransfer(
                                        execCommand: '''
                                            kubectl wait --for=condition=available deployment/backend-$BORG --timeout=120s
                                        '''
                                    ),
                                    sshTransfer(
                                        sourceFiles: 'k8s/backend-service.yml',
                                        remoteDirectory: '/',
                                        execCommand: '''
                                            sed -i "s/borg/$BORG/g" k8s/backend-service.yml
                                        '''
                                    ),
                                    sshTransfer(
                                        execCommand: '''
                                            kubectl patch service back-svc -n $NAMESPACE -p '{\"spec\": {\"selector\": {\"deployment\": \"$NOTBORG\"}}}'
                                        '''
                                    ),
									sshTransfer(
                                        execCommand: '''
                                            kubectl scale deployment backend-green --replicas=0 -n $NAMESPACE
                                        '''
                                    ),
                                ]
                            )
                        ]
                    )
                }
            }
        }
    }
}