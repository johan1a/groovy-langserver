def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label,
  serviceAccount: 'jenkins-master',
  containers: [
      containerTemplate(name: 'gradle', image: 'gradle', ttyEnabled: true),
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', command: 'cat', hostPath: '/var/run/docker.sock')
]) {
  node(label) {
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT

    stage('Run unit tests') {
      container('gradle') {
          sh """
            ./gradlew test
            """
      }
    }
  }
}
