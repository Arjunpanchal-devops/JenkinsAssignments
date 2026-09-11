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
                        expression { !params.SKIP_STABILITY }
                    }
                    steps {
                        dir('stability') {
                            echo 'Running Code Stability Tests'
                            checkout scm
                            sh 'mvn clean test'
                        }
                    }
                }

                stage('Code Quality Analysis') {
                    when {
                        expression { !params.SKIP_QUALITY }
                    }
                    steps {
                        dir('quality') {
                            echo 'Running Code Quality Analysis'
                            checkout scm
                            sh 'mvn clean verify'
                        }
                    }
                }

                stage('Code Coverage Analysis') {
                    when {
                        expression { !params.SKIP_COVERAGE }
                    }
                    steps {
                        dir('coverage') {
                            echo 'Running Code Coverage Analysis'
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

                junit '*/target/surefire-reports/*.xml'

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
        }

        failure {
            echo 'FAILURE: Build or artifact publication failed.'
        }

        aborted {
            echo 'ABORTED: Build was aborted or publication was denied.'
        }
    }
}
