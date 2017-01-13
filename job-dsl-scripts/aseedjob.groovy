


job("create-epic-environment") {
    parameters {
        stringParam('epic-name')
    }
    steps {
        shell('/bamboo/data/thdutil/serversetup/myrepo/gcloud-scripts/create-epic-environment.sh $epic-name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}
