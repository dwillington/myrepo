job('maven-sample') {
    scm 
    {
        git
        {
            remote
            {
                 branch('develop')
                 url('https://github.com/dwillington/myrepo.git')
                 credentials('git-dwillington-repo')
            }
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
            rootPOM('maven-projects/sample/pom.xml')
        }
    }
}