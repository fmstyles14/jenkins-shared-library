#!/usr/bin/env groovy

def call(String namespace, String deploymentTemplate, String nodePort) {
    writeFile file: 'inventory.ini', text: '''
[k8s]
kubexmaster.ithpharma.local ansible_user=root ansible_password=BlueDragon1339954 ansible_ssh_common_args='-o StrictHostKeyChecking=no'
'''

    def uatNodePort = '30007'

    sh """
      ansible-playbook -i inventory.ini ansible/deploy-k8s-uat-prod.yml \
        -e k8s_namespace=${namespace} \
        -e full_image=${env.ECR_REGISTRY}/${env.ECR_REPO_NAME}:${env.IMAGE_TAG} \
          -e nodeport=${nodePort} \
        -e deployment_template=${deploymentTemplate}
    """
}
