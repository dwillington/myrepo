job("java-hello-world-maven") {
    jdk ('jdk-10.0.1')
    scm {
        git {
            branch('master')
            remote {
                url("https://github.com/dantheman213/java-hello-world-maven.git")
            }
        }
    }
    steps {
        maven {
            goals('--batch-mode -Dmaven.test.skip=true clean package')
            mavenInstallation('mvn-3.5.3')
        }
    }
}
