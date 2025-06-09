#!/usr/bin/env groovy

@Library('jenkins-shared-library')_

pipeline {
  agent any

  tools {
    maven 'Maven-3.6.3'
    jdk 'openjdk11'
  }

  parameters {
    choice(
      name: 'development_type',
      choices: ['test', 'uat', 'live'],
      description: 'Select the build type'
    )
    extendedChoice(
      name: 'DEPLOY_ENVIRONMENTS',
      type: 'PT_CHECKBOX',
      description: 'Select one or more environments to deploy to',
      multiSelectDelimiter: ',',
      quoteValue: false,
      defaultValue: 'dev',
      value: 'dev,uat,prod'
    )
    choice(
      name: 'K8S_NAMESPACE',
      choices: ['dev-1', 'dev-2', 'dev-3', 'dev-4'],
      description: 'Choose the DEV Kubernetes namespace to deploy to'
    )
  }

  environment {
    AWS_DEFAULT_REGION = 'eu-west-2'
    ECR_REGISTRY       = '652184415878.dkr.ecr.eu-west-2.amazonaws.com'
    ECR_REPO_NAME      = 'check-order-app'
    K8S_NAMESPACE      = "${params.K8S_NAMESPACE}"
    TEAMS_WEBHOOK_URL  = credentials('ithos-pipeline-webhook')

    UAT_K8S_NAMESPACE  = 'test-check-orders-app'
    PROD_K8S_NAMESPACE = 'test-check-orders-app'
    SERVICE_NAME       = 'check-orders-micro-app'
  }

  stages {

    stage('Increment Version') {
      steps {
        script {
          incrementVersion()
        }
      }
    }

    stage('Build and Test') {
      steps {
        script {
          buildJar()
        }
      }
    }

    stage('Build and Push Docker Image to ECR') {
      steps {
        script {
          buildAndPushDockerImagesToEcr(
            env.AWS_DEFAULT_REGION,
            env.ECR_REGISTRY,
            env.ECR_REPO_NAME,
            env.IMAGE_TAG
          )
        }
      }
    }

    stage('Deploy to DEV') {
      when {
        expression {
          return params.DEPLOY_ENVIRONMENTS.tokenize(',').contains('dev')
        }
      }
      steps {
        script {
          def nodePortMap = [
            'dev-1': '30007',
            'dev-2': '30042',
            'dev-3': '30043',
            'dev-4': '30044'
          ]
          def selectedNamespace = params.K8S_NAMESPACE.toLowerCase()
          def mappedNodePort = nodePortMap[selectedNamespace]
            ?: error("No nodePort mapping found for namespace: ${selectedNamespace}")
          def deploymentTemplate = 'check-order-app-deployment.yaml.j2'

          devEnvironmentDeployment(selectedNamespace, mappedNodePort, deploymentTemplate)
        }
      }
    }

    stage('Deploy to UAT') {
      when {
        expression {
          return params.DEPLOY_ENVIRONMENTS.tokenize(',').contains('uat')
        }
      }
      steps {
        script {
          def deploymentTemplate = 'check-order-app-deployment.yaml.j2'
          def nodePort = '30007'
          echo "Deploying to UAT"
          uatEnvironmentDeployment(env.UAT_K8S_NAMESPACE, deploymentTemplate)
        }
      }
    }

    stage('Parameter Validation') {
      steps {
        script {
          parameterValidation()
        }
      }
    }

    stage('Approval for LIVE') {
      when {
        beforeInput true
        expression {
          return params.development_type == 'live'
        }
      }
      steps {
        script {
          liveApproval()
        }
      }
    }

    stage('Deploy to PROD') {
      when {
        allOf {
          branch 'master'
          expression {
            return params.DEPLOY_ENVIRONMENTS.tokenize(',').contains('prod')
          }
        }
      }
      steps {
        script {
          def deploymentTemplate = 'check-order-app-deployment.yaml.j2'
          echo "Deploying to PROD"
          prodEnvironmentDeployment(
            env.PROD_K8S_NAMESPACE,
            env.SERVICE_NAME,
            deploymentTemplate
          )
        }
      }
    }

    stage('Commit Version Update') {
      when {
        expression {
          env.IMAGE_TAG?.trim()
        }
      }
      steps {
        script {
          commitVersionUpdate()
        }
      }
    }
  }
}
