#!/user/bin/env groovy

def call(){
    echo 'Incrementing app version...'
    sh 'mvn build-helper:parse-version versions:set \
                -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
                 versions:commit'

    sleep 1
    def version = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()
    env.IMAGE_TAG = "${version}-${env.BUILD_NUMBER}"
    echo "Using IMAGE_TAG=${env.IMAGE_TAG}"


}
