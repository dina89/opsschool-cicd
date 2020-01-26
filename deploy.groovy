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
      kubernetesDeploy(
            configs: '**/webapp-deployment.yaml/**', 
            kubeconfigId: 'k8s_kubeconfig')
}
}
