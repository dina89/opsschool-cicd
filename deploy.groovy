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
    sh "aws eks --region us-east-1 update-kubeconfig --name opsSchool-eks-dina"
    sh "kubectl apply -f k8s/service.yml"
    sh "kubectl apply -f k8s/webapp-deployment.yml"
}
}
