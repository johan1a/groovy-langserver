def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label,
  serviceAccount: 'jenkins-master',
  containers: [
      containerTemplate(name: 'gradle', image: 'gradle', ttyEnabled: true, command: 'cat'),
],
volumes: [ ]) {
  node(label) {
    def myRepo = checkout scm
    def gitCommit = myRepo.GIT_COMMIT

    try {
        stage('Run unit tests') {
          sh """
            ./gradlew test --no-daemon
          """
        }
    } finally {
        archiveArtifacts artifacts: 'build/reports/**/*', fingerprint: true
        junit 'build/test-results/**/*.xml'
    }
  }
}
