
import com.kartheek.devops.scm.git

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   try {
      def scm = new git()	  
      scm.gitCheckout()
	  
	  echo "COM : ${env.GIT_COMMIT}"
	  bat "set > set_Git_main"
   }
   catch (Exception error)
   {  
	  wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) { 
		echo "\u001B[41m[ERROR] ${error}"
        throw error   
	  }
   }
}


