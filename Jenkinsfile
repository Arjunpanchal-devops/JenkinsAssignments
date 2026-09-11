pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    parameters {
        booleanParam(
            name: 'SKIP_STABILITY',
            defaultValue: false,
            description: 'Skip code stability tests'
        )

        booleanParam(
            name: 'SKIP_QUALITY',
            defaultValue: false,
            description: 'Skip code quality analysis'
        )

        booleanParam(
            name: 'SKIP_COVERAGE',
            defaultValue: false,
            description: 'Skip code coverage analysis'
        )
    }

    tools {
        jdk 'JDK25'
        maven 'Maven3.9.12'
    }

    stages {

        stage('Code Checkout') {
            steps {
                echo 'Checking out Assignment 4 branch'
                checkout scm
            }
        }

        stage('Parallel Scans') {
            parallel {

                stage('Code Stability') {
                    when {
                        expression {
                            !params.SKIP_STABILITY
                        }
                    }

                    steps {
                        echo 'Running Code Stability Tests'

                        dir('stability') {
                            checkout scm
                            sh 'mvn clean test'
                        }
                    }
                }

                stage('Code Quality Analysis') {
                    when {
                        expression {
                            !params.SKIP_QUALITY
                        }
                    }

                    steps {
                        echo 'Running Code Quality Analysis'

                        dir('quality') {
                            checkout scm
                            sh 'mvn clean verify'
                        }
                    }
                }

                stage('Code Coverage Analysis') {
                    when {
                        expression {
                            !params.SKIP_COVERAGE
                        }
                    }

                    steps {
                        echo 'Running Code Coverage Analysis'

                        dir('coverage') {
                            checkout scm
                            sh 'mvn clean package jacoco:report'
                        }
                    }
                }
            }
        }

        stage('Generate Report') {
            steps {
                echo 'Generating Reports'

                junit 'coverage/target/surefire-reports/*.xml'

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'coverage/target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
                ])
            }
        }

        stage('Approval') {
            steps {
                input(
                    message: 'Do you approve publishing the artifact?',
                    ok: 'Approve'
                )
            }
        }

        stage('Publish Artifacts') {
            steps {
                echo 'Publishing Java Artifact'

                archiveArtifacts(
                    artifacts: 'coverage/target/*.jar',
                    fingerprint: true
                )
            }
        }
    }

    post {

        success {
            echo 'SUCCESS: Build and artifact publication completed successfully.'

            slackSend(
                channel: '#all-jenkins-workspace',
                color: 'good',
                message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER} completed successfully. Artifact published."
            )

            emailext(
                to: 'arjunrpanchal09@gmail.com',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>Jenkins Build Successful</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> SUCCESS</p>
                    <p>All required stages completed successfully.</p>
                    <p>Java artifact was published successfully.</p>
                """
            )
        }

        failure {
            echo 'FAILURE: Build or artifact publication failed.'

            slackSend(
                channel: '#all-jenkins-workspace',
                color: 'danger',
                message: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER} failed. Check Jenkins console."
            )

            emailext(
                to: 'arjunrpanchal09@gmail.com',
                subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>Jenkins Build Failed</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> FAILURE</p>
                    <p>Build or artifact publication failed.</p>
                    <p>Please check the Jenkins console for details.</p>
                """
            )
        }

        aborted {
            echo 'ABORTED: Build was aborted or publication was denied.'

            slackSend(
                channel: '#all-jenkins-workspace',
                color: 'warning',
                message: "ABORTED: ${env.JOB_NAME} #${env.BUILD_NUMBER} was aborted or publication was denied."
            )

            emailext(
                to: 'arjunrpanchal09@gmail.com',
                subject: "ABORTED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>Jenkins Build Aborted</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                    <p><b>Status:</b> ABORTED</p>
                    <p>The build was aborted or publication was denied.</p>
                """
            )
        }
    }
}
