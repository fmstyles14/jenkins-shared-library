#!/usr/bin/env groovy

def call(String namespace, String serviceName, String deploymentTemplate,String nodePort) {
    def allowedApprovers = ['muhammad.catubig', 'femi.oshin', 'admin.admin']
    def allowedApproversLower = allowedApprovers.collect { it.toLowerCase() }
    def buildUser = ''

    wrap([$class: 'BuildUser']) {
        buildUser = env.BUILD_USER_ID ?: env.BUILD_USER ?: 'unknown'
    }

    def approval = input(
            id: 'ProdApproval',
            message: 'Do you want to deploy to PROD?',
            parameters: [
                    booleanParam(name: 'confirmProdDeploy', defaultValue: false, description: 'Check to deploy to PROD.'),
                    string(name: 'Approver', defaultValue: '', description: 'Your Jenkins username')
            ]
    )

    def approverInput = approval.Approver?.trim()?.toLowerCase()

    if (!approval.confirmProdDeploy) {
        echo "[PROD DEPLOY] Skipped: User did not confirm deployment."
        return
    }

    if (approverInput == buildUser?.toLowerCase()) {
        error "[PROD DEPLOY] Aborted: '${approval.Approver}' triggered the build and cannot approve their own deployment. Please have a different team member approve it."
    }


    if (!allowedApproversLower.contains(approverInput)) {
        echo "[PROD DEPLOY] Skipped: ${approval.Approver} is not in the list of allowed approvers."
        return
    }


    writeFile file: 'inventory.ini', text: '''
[k8s]
10.100.2.5 ansible_user=root ansible_password=BeforeDeskDeath59 ansible_ssh_common_args='-o StrictHostKeyChecking=no'
'''

    def fullImage = "${env.ECR_REGISTRY}/${env.ECR_REPO_NAME}:${env.IMAGE_TAG}"
    def previousImageTag = ''

    try {
        // Fetch previous image used for rollback
        previousImageTag = sh(
                script: """
                ansible-playbook -i inventory.ini ansible/deploy-k8s-uat-prod.yml \
                  -e k8s_namespace=${namespace} \
                  -e service_name=${serviceName} \
                  -e nodeport=${nodePort} \
                  --tags fetch_image | tee fetch_output.log
                grep 'PREVIOUS_IMAGE=' fetch_output.log | awk -F '=' '{print \$2}'
            """,
                returnStdout: true
        ).trim()
        echo "Previous image tag: ${previousImageTag}"
    } catch (e) {
        echo "[WARNING] Could not determine previous image tag for rollback."
    }

    try {
        echo "[PROD DEPLOY] Starting deployment..."
        sh """
            ansible-playbook -i inventory.ini ansible/deploy-k8s-uat-prod.yml \
              -e k8s_namespace=${namespace} \
              -e full_image=${fullImage} \
              -e nodeport=${nodePort} \
              -e deployment_template=${deploymentTemplate}
        """
        echo "[PROD DEPLOY] Deployment completed successfully."
    } catch (e) {
        echo "[ERROR] Deployment failed. Rolling back to previous image: ${previousImageTag}"
        if (previousImageTag?.trim()) {
            sh """
                ansible-playbook -i inventory.ini ansible/deploy-k8s-uat-prod.yml \
                  -e k8s_namespace=${namespace} \
                  -e full_image=${previousImageTag} \
                  -e nodeport=${nodePort} \
                  -e deployment_template=${deploymentTemplate}
            """
            echo "[ROLLBACK] Rollback completed successfully."
        } else {
            echo "[ROLLBACK] Skipped: No previous image found."
        }
        throw e
    }
}
