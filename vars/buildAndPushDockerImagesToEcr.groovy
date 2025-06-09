#!/user/bin/env groovy

def call(String awsRegion,String ecrRegistry,String ecrRepoName, String imageTag ){
    echo 'Logging into AWS ECR...'
    sh """
            aws ecr get-login-password --region $awsRegion | \
            docker login --username AWS --password-stdin $ecrRegistry
          """
    sh "docker build -t $ecrRepoName:$imageTag ."
    sh """
            docker tag $ecrRepoName:$imageTag $ecrRegistry/$ecrRepoName:$imageTag
            docker push $ecrRegistry/$ecrRepoName:$imageTag
          """

}
