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
stage("Pull deployment.yml") {
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
}
stage("deploy webapp") {
       withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', 
                         accessKeyVariable: 'AWS_ACCESS_KEY_ID', 
                         credentialsId: 'AWS-creds', 
                         secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
            kubernetesDeploy(
                  configs: '**/webapp-deployment.yaml/**', 
                  kubeconfigId: 'k8s_kubeconfig')
            }
      }
}
