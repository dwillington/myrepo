String branch = 'develop'

job("maven-sample-$branch") 
{
    scm 
    {
        git
        {
            remote
            {
                 branch(branch)
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
            goals('-e clean deploy')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            rootPOM('maven-projects/sample/pom.xml')
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}