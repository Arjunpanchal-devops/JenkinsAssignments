node {

    properties([
        parameters([
            booleanParam(
                name: 'SKIP_STABILITY',
                defaultValue: false,
                description: 'Skip code stability analysis'
            ),
            booleanParam(
                name: 'SKIP_QUALITY',
                defaultValue: false,
                description: 'Skip code quality analysis'
            ),
            booleanParam(
                name: 'SKIP_COVERAGE',
                defaultValue: false,
                description: 'Skip code coverage analysis'
            )
        ])
    ])

    try {

        stage('Code Checkout') {
            echo 'Checking out Java project'
            checkout scm
        }

        stage('Parallel Scans') {

            def scans = [:]

            if (!params.SKIP_STABILITY) {
                scans['Code Stability'] = {
                    echo 'Running Code Stability Analysis'
                    sh 'mvn test'
                }
            }

            if (!params.SKIP_QUALITY) {
                scans['Code Quality Analysis'] = {
                    echo 'Running Code Quality Analysis'
                    sh 'mvn verify'
                }
            }

            if (!params.SKIP_COVERAGE) {
                scans['Code Coverage Analysis'] = {
                    echo 'Running Code Coverage Analysis'
                    sh 'mvn test jacoco:report'
                }
            }

            if (scans.isEmpty()) {
                echo 'All scans have been skipped.'
            } else {
                parallel scans
            }
        }

        stage('Generate Report') {

            echo 'Generating code quality and coverage reports'

            if (!params.SKIP_COVERAGE) {

                junit 'target/surefire-reports/*.xml'

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
                ])

            } else {
                echo 'Coverage report skipped.'
            }
        }

        stage('Approval') {

            try {

                input(
                    message: 'Approve artifact publication?',
                    ok: 'Approve'
                )

                echo 'Publication APPROVED.'

            } catch (err) {

                echo 'Publication DENIED.'
                error('Artifact publication was denied.')

            }
        }

        stage('Publish Artifacts') {

            echo 'Building and publishing Java artifact'

            sh 'mvn package -DskipTests'

            archiveArtifacts(
                artifacts: 'target/*.jar',
                fingerprint: true
            )

            echo 'Artifact published successfully.'
        }

        currentBuild.result = 'SUCCESS'

    } catch (err) {

        currentBuild.result = 'FAILURE'

        echo "Pipeline failed: ${err}"

        throw err

    } finally {

        if (currentBuild.result == 'SUCCESS') {

            echo 'Sending SUCCESS notifications'

            slackSend(
                channel: '#all-jenkins-workspace',
                color: 'good',
                message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER} - Build and artifact publication successful."
            )

            emailext(
                to: 'arjunrpanchal09@gmail.com',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Jenkins Build Successful

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Status: SUCCESS

Artifact publication completed successfully.
"""
            )

        } else {

            echo 'Sending FAILURE notifications'

            slackSend(
                channel: '#all-jenkins-workspace',
                color: 'danger',
                message: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER} - Build failed or publication was denied."
            )

            emailext(
                to: 'arjunrpanchal09@gmail.com',
                subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Jenkins Build Failed

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Status: FAILURE

Build failed or artifact publication was denied.
"""
            )
        }
    }
}

