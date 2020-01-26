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
      withKubeConfig(kubernetesDeploy configs: '**/webapp-deployment.yaml/**', 
                     kubeConfig: [path: ''], 
                     kubeconfigId: 'k8s_kubeconfig', 
                     secretName: '', 
                     ssh: [sshCredentialsId: '*', sshServer: ''], 
                     textCredentials: [certificateAuthorityData: '', 
                     clientCertificateData: '', 
                     clientKeyData: '', 
                     serverUrl: 'https://']) {
      sh 'kubectl apply -f webapp-deployment.yaml'
    }
}
}
