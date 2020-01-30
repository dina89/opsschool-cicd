node("linux") {
environment{
    credentialsId = "Github-Dina89"
}
def customImage = ""
stage('Git') { // Get code from GitLab repository
    git branch: 'master',
      url: 'https://github.com/dina89/opsschool-cicd.git'
}
stage("create dockerfile") {
sh """
tee Dockerfile <<-'EOF'
FROM ubuntu:latest
RUN touch file-01.txt
EOF
"""
}
stage("build docker") {
customImage =
docker.build("training/webapp")
}
stage("verify dockers") {
sh "docker images"
}
stage("deploy webapp") {
       withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', 
                         accessKeyVariable: 'AWS_ACCESS_KEY_ID', 
                         credentialsId: 'AWS-creds', 
                         secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
            kubernetesDeploy(
                  configs: 'k8s/*', 
                  kubeconfigId: 'k8s_kubeconfig')
            }
      }
}
