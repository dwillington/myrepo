job('maven-sample') {
    scm 
    {
        git
        {
            branch('develop')
            url('https://github.com/dwillington/myrepo.git')
            credentials('git-dwillington-repo')
        }
    }
    triggers 
    {
        scm('*/15 * * * *')
    }
    steps 
    {
        maven
		{
            goals('-e clean package')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            rootPom('maven-projects/sample/pom.xml')
        }
    }
}