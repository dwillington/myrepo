job('maven-sample') {
    scm 
	{
	  branch('develop')
      git('https://github.com/dwillington/myrepo.git')
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