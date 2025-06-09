#!/usr/bin/env groovy

def call(String selectedNamespace, String mappedNodePort, String deploymentTemplate) {
    writeFile file: 'inventory.ini', text: '''
[k8s]
10.100.2.5 ansible_user=root ansible_password=BeforeDeskDeath59 ansible_ssh_common_args='-o StrictHostKeyChecking=no'
'''

    sh """
      ansible-playbook -i inventory.ini ansible/deploy-k8s-dev.yml \
        -e k8s_namespace=${selectedNamespace} \
        -e full_image=${env.ECR_REGISTRY}/${env.ECR_REPO_NAME}:${env.IMAGE_TAG} \
        -e nodeport=${mappedNodePort} \
        -e deployment_template=${deploymentTemplate}
    """
}

