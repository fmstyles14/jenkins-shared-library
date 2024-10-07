# jenkins-shared-library

## Demo Project:
Create a Jenkins Shared Library
Technologies used:
Jenkins, Groovy, Docker, Git, Java, Maven
Project Description:
Create a Jenkins Shared Library to extract common build
logic:
Create separate Git repository for Jenkins Shared
Library project
Create functions in the JSL to use in the Jenkins pipeline
Integrate and use the JSL in Jenkins Pipeline


## Demo Project:
Configure Webhook to trigger CI Pipeline automatically on
every change
Technologies used:
Jenkins, Githun, Git, Docker, Java, Maven
Project Description:
Install Github Plugin in Jenkins
Configure Github access token and connection to
Jenkins in GitLab project settings
Configure Jenkins to trigger trigger the CI pipeline, whenever a
change is pushed to Github

## Demo Project:
Dynamically Increment Application version in Jenkins Pipeline
Technologies used:
Jenkins, Docker, Github, Git, Java, Maven
Project Description:
Configure CI step: Increment patch version
Configure CI step: Build Java application and clean old artifacts
Configure CI step: Build Image with dynamic Docker Image Tag
Configure CI step: Push Image to private DockerHub repository
Configure CI step: Commit version update of Jenkins back to Git repository
Configure Jenkins pipeline to not trigger automatically on CI build commit to avoid commit loop
