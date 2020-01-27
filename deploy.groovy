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
      checkout(
            changelog: false, 
            poll: false, 
            scm: [$class: 'GitSCM', 
                  branches: [[name: '*/master']], 
                  doGenerateSubmoduleConfigurations: false, 
                  extensions: [[$class: 'CleanBeforeCheckout']], 
                  submoduleCfg: [], 
                  userRemoteConfigs: [[credentialsId: 'Github-Dina89', 
                                       url: 'https://github.com/dina89/opsschool-cicd']]])
      kubernetesDeploy(
            configs: '**/webapp-deployment.yaml/**', 
            kubeconfigId: 'k8s_kubeconfig',
            textCredentials: [serverUrl: 'https://'])
}
}
