pipeline {
  agent { label 'macos' }
  environment {
    TEST_JENKINS_URL="https://cb.codes101.com/jpi/"
    TEST_LOGIN=credentials('test-jenkins-admin-user')
  }
  stages {
    stage('Run the test for 1280x720') {
      steps {
        sh './mvnw clean test'
      }
    }
    stage('Run the test for 1920x1080') {
      environment {
        VIEWPORT_WIDTH="1920"
        VIEWPORT_HEIGHT="1080"
      }
      steps {
        sh './mvnw clean test'
      }
    }
    stage('Run the test for 1290x2796') {
      environment {
        VIEWPORT_WIDTH="1290"
        VIEWPORT_HEIGHT="2796"
      }
      steps {
        sh './mvnw clean test'
      }
    }
  }
  post {
    success {
      archiveArtifacts artifacts: 'videos/*.webm'
    }
  }
}