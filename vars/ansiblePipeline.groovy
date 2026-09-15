def call(Map config = [:]) {

    try {

        stage('Clone') {
            echo 'Cloning project repository'
            checkout scm
        }

        if (config.KEEP_APPROVAL_STAGE.toBoolean()) {
            stage('User Approval') {
                input(
                    message: "Approve Ansible deployment to ${config.ENVIRONMENT}?",
                    ok: 'Approve'
                )
            }
        }

        stage('Playbook Execution') {
            echo "Executing Ansible playbook for ${config.ENVIRONMENT}"

            sh """
                ansible-playbook \
                -i localhost, \
                -c local \
                -e environment=${config.ENVIRONMENT} \
                -e code_base_path=${config.CODE_BASE_PATH} \
                Assignment-6/ansible/site.yml
            """
        }

        stage('Notification') {
            echo "Sending notification to ${config.SLACK_CHANNEL_NAME}"

            slackSend(
                channel: config.SLACK_CHANNEL_NAME,
                color: 'good',
                message: config.ACTION_MESSAGE
            )
        }

    } catch (err) {

        echo "Pipeline failed: ${err}"

        slackSend(
            channel: config.SLACK_CHANNEL_NAME,
            color: 'danger',
            message: "FAILURE: ${config.ACTION_MESSAGE}"
        )

        throw err
    }
}
