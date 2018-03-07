#!groovy
package com.kartheek.devops.scm

String WORKSPACE = pwd()

/**************************************************
***** Function to checkout the code
***************************************************/
def gitCheckout()
{
   try {
       
	   BRANCH = scm.getBranches()[0]
       REPOSITORY = scm.getUserRemoteConfigs()[0].getUrl()
      
       if ( "${env.BRANCH_NAME}" != "null" ) {
	      BRANCH = "${env.BRANCH_NAME}"
	   }
	   if ( "$REPOSITORY" == "null" )
       {
          error "\u001B[41m[ERROR] unable to get Git repository name....."
       }
       if ( "$BRANCH" == "null" )
       {
          error "\u001B[41m[ERROR] unable to get Git Branch name...."
       } 
       
          CREDENTIAL_ID = 'git-credentials'
          println "\u001B[32m[INFO] checking out from branch ${BRANCH}, please wait..."          
		  checkout scm
          env.GIT_BRANCH = "${BRANCH}"
          env.GIT_URL = "$REPOSITORY"
          env.GIT_COMMIT = getGitCommitHash()
          //env.GIT_AUTHOR_EMAIL = getCommitAuthorEmail()
		  
		  println "BRANCH : ${GIT_BRANCH}"
		  println "URL : ${GIT_URL}"
       
	   currentBuild.result = 'SUCCESS'
   }
   catch (error) {
       
          print "\u001B[41m[ERROR] clone for repository ${env.GIT_URL} failed, please check the logs..."
          throw error

	   currentBuild.result = 'FAILED'
   }   
}

/************************************
***** Function to get the Git Hash 
*************************************/
def getGitCommitHash()
{
   try {
     gitCommit = bat(returnStdout: true, script: 'git rev-parse HEAD').trim()
     shortCommit = gitCommit.take(8)
     return shortCommit
	 println "Commit : ${shortCommit}"
   }
   catch (Exception error)
   {
		print "\u001B[41m[ERROR] failed to get last Git commit ID....."
        throw error
     
   }
}

/**********************************************
***** Function to get the committer email id
***********************************************/
def getCommitAuthorEmail()
{
   try {
     def COMMIT = getGitCommitHash()
     bat "git log --format='%ae' $COMMIT | sort > author"
     def author = readFile('author').trim()
     return author
   }
   catch (Exception error)
   {     
		print "\u001B[41m[ERROR] failed to get the last Git commit author email ID....."
        throw error     
   }
}




