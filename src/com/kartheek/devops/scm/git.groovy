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
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
          CREDENTIAL_ID = 'git-credentials'
          println "\u001B[32m[INFO] checking out from branch ${BRANCH}, please wait..."          
		  checkout scm
          env.GIT_BRANCH = "${BRANCH}"
          env.GIT_URL = "$REPOSITORY"
          env.GIT_COMMIT = getGitCommitHash()
          env.GIT_AUTHOR_EMAIL = getCommitAuthorEmail()

	   }
		  
       
	   currentBuild.result = 'SUCCESS'
   }
   catch (error) {
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
          print "\u001B[41m[ERROR] clone for repository ${env.GIT_URL} failed, please check the logs..."
          throw error
	   }
	   currentBuild.result = 'FAILED'
   }   
}

/************************************
***** Function to get the Git Hash 
*************************************/
def getGitCommitHash()
{
   try {
	 gitCommit = bat "git rev-parse HEAD > commit"
	 shortCommit = readFile('commit').trim().take(8)
	 println "Commit : ${shortCommit}"
     return shortCommit	 
   }
   catch (Exception error)
   {
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {      
		print "\u001B[41m[ERROR] failed to get last Git commit ID....."
        throw error
	   }     
   }
}

/**********************************************
***** Function to get the committer email id
***********************************************/
def getCommitAuthorEmail()
{
   try {
     def COMMIT = getGitCommitHash()
     bat "git --no-pager show -s --format='%%ae' $COMMIT | sort > author"
     def author = readFile('author').trim()
     return author
   }
   catch (Exception error)
   {     
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) { 
		print "\u001B[41m[ERROR] failed to get the last Git commit author email ID....."
        throw error 
	   }		
   }
}




