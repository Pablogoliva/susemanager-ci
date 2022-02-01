def run(params) {
    stage('Clone project') {
        git url: params.git_repo, branch: params.git_branch
    }
    stage('JUnit tests coverage') {
        dir("uyuni") {
            sh "./susemanager-utils/testing/automation/java-unittests-pgsql.sh -t test-coverage-report"
        }
    }
    stage("SonarQube scanner') {
        dir("uyuni") {
            sh "set +x; source /home/jenkins/.credentials; set -x; " +
                "docker run --rm -v $PWD:/usr/src " +
                "-e SONAR_HOST_URL=http://dockerhost0.mgr.suse.de:9090 " +
                "-e SONAR_LOGIN=$SONAR_LOGIN " +
                "-e sonar.projectKey=uyuni-project_uyuni " +
                "-e sonar.sources=. " +
                "-e sonar.java.binaries=java/build/classes/ " +
                "-e sonar.java.libraries='java/lib/*.jar' " +
                "-e sonar.junit.reportPaths=java/test-results/ " +
                "-e sonar.coverage.jacoco.xmlReportPaths=java/test-results/coverage/report.xml " +
                "sonarsource/sonar-scanner-cli"
        }
    }
}

return this
