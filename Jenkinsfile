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
                        sh 'mvn test'
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
                        sh 'mvn verify'
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
                        sh 'mvn test jacoco:report'
                    }
                }
            }
        }

        stage('Generate Report') {
            steps {
                echo 'Generating Reports'

                junit 'target/surefire-reports/*.xml'

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
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
                    artifacts: 'target/*.jar',
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
