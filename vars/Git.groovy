
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
   }
   catch (Exception error)
   {
      wrap([$class: 'AnsiColorBuildWrapper']) {
         echo "\u001B[41m[ERROR] ${error}"
         throw error
      }
   }

}


