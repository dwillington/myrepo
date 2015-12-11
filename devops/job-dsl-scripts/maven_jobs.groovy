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

job('scrl-develop-build') 
{
    scm
    {
        git
        {
            remote
            {
                 branch('develop')
                 url("${giturl}/scm/scrl/scrl.git")
                 credentials('ciad_jenkins_user')
            }
            browser
            {
                stash("${giturl}/projects/scrl/repos/scrl")
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
            rootPOM('pom.xml')
            goals('--batch-mode -e clean install -P web')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}

job('scrl-develop-sonar') 
{
    scm
    {
        git
        {
            remote
            {
                 branch('develop')
                 url("${giturl}/scm/scrl/scrl.git")
                 credentials('ciad_jenkins_user')
            }
            browser
            {
                stash("${giturl}/projects/scrl/repos/scrl")
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
            rootPOM('pom.xml')
            goals('--batch-mode -e sonar:sonar')
            property('sonar.host.url', 'http://172.25.6.235:9000')
            property('sonar.jdbc.url', 'jdbc:oracle:thin:@172.25.6.241:1521:sonar')
            // property('sonar.jdbc.username', 'sonar')
            property('sonar.jdbc.password', 'password')
            property('sonar.javascript.lcov.reportPath', 'SmallCompaniesOnboardC-ui/target/reports/coverage/report-lcov/lcov.info')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('apache-maven-3.3.9')
        }
    }
}