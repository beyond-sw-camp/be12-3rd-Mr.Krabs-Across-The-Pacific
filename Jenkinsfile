pipeline {
	agent any

    environment {
        GITHUB_REPO = 'https://github.com/celarim/jenkins_test'
        NAMESPACE = 'kgj'
        GIT_DEPLOYMENT_YAML = 'https://raw.githubusercontent.com/beyond-sw-camp/be12-3rd-Mr.Krabs-Across-The-Pacific/refs/heads/feat/cicd/jenkins/k8s/backend-deployment.yml'
        GIT_SERVICE_YAML = 'https://raw.githubusercontent.com/beyond-sw-camp/be12-3rd-Mr.Krabs-Across-The-Pacific/refs/heads/feat/cicd/jenkins/k8s/backend-service.yml'        CIRCLECI_PROJECT_SLUG = ''
        CIRCLECI_TOKEN =
        CIRCLECI_DEFINITION_ID =
    }

    stages {
		stage('Trigger CircleCI Pipeline') {
            steps {
                script {
                    echo "Starting CircleCI Pipeline..."

                    // CircleCI 파이프라인 실행 및 ID 가져오기
                    def pipeline_id = sh(script: """
                        curl -X POST 'https://circleci.com/api/v2/project/$CIRCLECI_PROJECT_SLUG/pipeline/run' \
                        --header 'Circle-Token: $CIRCLECI_TOKEN' \
                        --header 'Content-Type: application/json' \
                        --data '{
                            "definition_id": "$CIRCLECI_DEFINITION_ID",
                            "config": {
                                "branch": "circleci-project-setup"
                            },
                            "checkout": {
                                "branch": "circleci-project-setup"
                            },
                            "parameters": {
                                "tag": "$BUILD_ID"
                            }
                        }' | jq -r '.id'
                    """, returnStdout: true).trim()

                    echo "Triggered CircleCI Pipeline ID: ${pipeline_id}"
                    env.PIPELINE_ID = pipeline_id
                }
            }
        }
        stage('Wait for CircleCI Completion') {
            steps {
                script {
                    echo "Waiting for CircleCI Pipeline to Complete..."

                    def status = "running"
                    while (status == "running" || status == "pending") {
                        sleep(10) // 10초 대기 후 다시 확인

                        status = sh(script: """
                            curl --silent --location 'https://circleci.com/api/v2/pipeline/${env.PIPELINE_ID}/workflow' \
                            --header 'Circle-Token: $CIRCLECI_TOKEN' | jq -r '.items[0].status'
                        """, returnStdout: true).trim()

                        echo "CircleCI Pipeline Status: ${status}"
                    }

                    // 실패 또는 취소된 경우 빌드 중단
                    if (status != "success") {
                        error "CircleCI Pipeline Failed or Canceled! Status: ${status}"
                    }

                    echo "CircleCI Pipeline Completed Successfully!"
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
                                        execCommand: """
                                            curl -sL $GIT_DEPLOYMENT_YAML | \
                                            sed "s/borg/${env.BORG}/g" | \
                                            sed "s/latest/$BUILD_ID/g" | \
                                            kubectl apply -n $NAMESPACE -f -
                                        """
                                    ),
                                    sshTransfer(
                                        execCommand: """
                                            kubectl rollout status deployment/backend-deployment-${env.BORG} -n ${NAMESPACE}
                                            kubectl wait --for=condition=available deployment/backend-${env.BORG} --timeout=120s -n ${NAMESPACE}
                                        """
                                    ),
                                    sshTransfer(
                                        execCommand: """
                                            curl -sL $GIT_SERVICE_YAML | \
                                            sed "s/borg/${env.BORG}/g" | \
                                            kubectl apply -n $NAMESPACE -f -
                                        """
                                    ),
									sshTransfer(
                                        execCommand: """
                                            kubectl scale deployment backend-${env.NOTBORG} --replicas=0 -n $NAMESPACE
                                        """
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