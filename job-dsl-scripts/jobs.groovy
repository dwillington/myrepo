job("java-hello-world-maven") {
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
        }
    }
}
