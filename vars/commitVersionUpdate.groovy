#!/user/bin/env groovy

def call(){

    withCredentials([usernamePassword(credentialsId: 'ca7dfe50-a486-48de-bb7f-377787c0f48f', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
        def gitlabRepoUrl = sh(script: 'git config --get remote.origin.url', returnStdout: true).trim()
        def pushUrl = gitlabRepoUrl.replace("http://", "http://${env.GIT_USERNAME}:${GIT_PASSWORD}@")
        sh 'git config --global user.email "jenkins@example.com"'
        sh 'git config --global user.name "jenkins"'
        sh "git remote set-url origin ${pushUrl}"
        sh 'git add .'
        sh 'git commit -m "ci: version bump" || echo "Nothing to commit"'
        def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
        sh "git push origin HEAD:${env.BRANCH_NAME}"
    }

}