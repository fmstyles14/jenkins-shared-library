#!/user/bin/env groovy

def call(){
    echo 'Sending mail to devopsteam@ithpharma.com'
    mail to: 'devopsteam@ithpharma.com',
            subject: "Live Build Approval Needed: ${currentBuild.fullDisplayName}",
            body: "Live build requires approval: ${env.BUILD_URL}"

    input(message: 'Requesting approval to deploy to LIVE', ok: 'Approve')
    echo '> Live deployment approved'


}
