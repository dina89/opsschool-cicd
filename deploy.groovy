node("linux") {
def customImage = ""
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
      withKubeConfig([credentialsId: 'user1', serverUrl: 'https://api.k8s.my-company.com']) {
      sh 'kubectl apply -f webapp-deployment.yaml'
    }
}
}
