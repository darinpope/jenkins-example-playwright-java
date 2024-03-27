pipeline {
  agent { label 'macos' }
  environment {
    TEST_JENKINS_URL="https://cb.codes101.com/jpi/"
    TEST_USER=credentials('test-jenkins-admin-user')
  }
  stages {
    stage('Run the tests') {
      steps {
        sh './mvnw clean test'
      }
      post {
        success {
          archiveArtifacts artifacts: 'videos/*.webm'
        }
      }
    }
  }
}