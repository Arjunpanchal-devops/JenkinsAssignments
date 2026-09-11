pipeline {

    agent any

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
                git 'https://github.com/Arjunpanchal-devops/JenkinsAssignments.git'
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
                        echo 'Code Quality Analysis'
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
                        sh 'mvn test jacoco:report'
                    }
                }
            }
        }

        stage('Generate Report') {
            steps {
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
                    message: 'Approve artifact publication?',
                    ok: 'Approve'
                )
            }
        }

        stage('Publish Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {

        success {
            echo 'Build and artifact publication successful.'

            // Slack notification will be enabled after Slack configuration
            echo 'SUCCESS: Jenkins Assignment 4 completed successfully.'
        }

        failure {
            echo 'Build or artifact publication failed.'

            // Slack notification will be enabled after Slack configuration
            echo 'FAILURE: Jenkins Assignment 4 failed.'
        }

        aborted {
            echo 'Build was aborted or publication was denied.'
        }
    }
}

