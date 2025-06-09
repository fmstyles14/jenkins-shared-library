#!/user/bin/env groovy

def call(){
    echo "Building the application for.. $GIT_BRANCH"
    echo "Using Maven profile: ${params.development_type}"
           withMaven(maven: 'Maven-3.6.3', jdk: 'openjdk11') {
               if (params.development_type == 'live') {
                   sh "mvn clean verify -P '${params.development_type}'"
               } else {
                   sh "mvn clean verify -DskipTests -P '${params.development_type}'"
               }

           }
}
