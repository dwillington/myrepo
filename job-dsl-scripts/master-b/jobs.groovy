import com.JobUtils

def jobs = [
    ["spring3-mvc-maven-xml-hello-world", "https://github.com/mkyong/spring3-mvc-maven-xml-hello-world"]
    ]

JobUtils.createPipelineJobs(this, jobs)