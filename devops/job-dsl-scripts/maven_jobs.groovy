job('maven-sample-develop') 
{
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
            goals('--batch-mode -e clean deploy')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            rootPOM('maven-projects/sample/pom.xml')
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}

job('maven-sample-master') 
{
    scm 
    {
        git
        {
            remote
            {
                 branch('master')
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
            goals('--batch-mode -e clean package')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            rootPOM('maven-projects/sample/pom.xml')
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}

def giturl = 'http://tocgnxp1pv.bns.bns:7990'

job('d2drl-develop-build') 
{
    scm 
    {
        git
        {
            remote
            {
                 branch('develop')
                 url("${giturl}/scm/d2drl/d2drl.git")
                 credentials('ciad_jenkins_user')
            }
            browser
            {
                stash('http://tocgnxp1pv.bns.bns:7990/projects/d2drl/repos/d2drl')
            }
        }
    }
    triggers 
    {
        scm('H/2 * * * *')
    }
    steps 
    {
        maven
		{
            goals('--batch-mode -e clean install -P web')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            rootPOM('DayToDay/pom.xml')
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}