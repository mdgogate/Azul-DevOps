
@Library('bpd-pipeline-library')_
import org.bpd.AndroidHelper
def androidUtils = new AndroidHelper(this)

pipeline {
    
    agent { label 'BuildServer' };

    stages{
        stage('Prepare'){
            steps{
              script {
                  // Set Demo Mode
                  androidUtils.replaceTextInFile( textToReplace: "isDemoMode = false", newText: "isDemoMode = true", targetFile: "build.gradle")
                  // Enable nexus repos in build.gradle
                  androidUtils.replaceTextInFile( textToReplace: "useNexusRepo = false", newText: "useNexusRepo = true", targetFile: "build.gradle")   
                  // Enable Sonar Plugin repos in build.gradle
                  androidUtils.replaceTextInFile( textToReplace: "cicdEnabled = false", newText: "cicdEnabled = true", targetFile: "build.gradle")   
				  // Set Nexus as Active Gradle Wrapper Download Path
                  androidUtils.replaceTextInFile( textToReplace: "#distributionUrl=http", newText: "distributionUrl=http", targetFile: "gradle/wrapper/gradle-wrapper.properties") 
                  // Disable external gradle wrapper download path
                  androidUtils.replaceTextInFile( textToReplace: "distributionUrl=https", newText: "#distributionUrl=https", targetFile: "gradle/wrapper/gradle-wrapper.properties")                
              }
			
            }
        }
        stage('Build'){
            steps{
              script {
              	 androidUtils.build(gradleArgs: "assembleRelease --parallel -x lintVitalRelease  " )
                //androidUtils.signAPK(apksToSign: "**/**/*.apk",keyAlias: "", keyStoreId : "" )
                step([$class: 'SignApksBuilder', apksToSign:  "**/**/*.apk", archiveSignedApks: false, skipZipalign: true, keyAlias: 'banco popular', keyStoreId: 'bpd-app-ampresarial-keystore'])
               
              }
                
            }
        }
        stage('Run Sonar'){
            steps{
                script {
                    echo "skip sonar"
                   // androidUtils.runSonarAnalysis(projectKey: "BPD_App_Empresarial")
                }
                
            }
        }
		
		stage('Distribute'){
		    //when {
            //    branch pattern: "development|(release[\\S]+)", comparator: "REGEXP"
            //}
            steps{
                script {
				
					def cause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
					
                    androidUtils.appCenterDistribute(
					requestedBy: cause.userName,
					branchName: env.BRANCH_NAME,
					buildNumber: env.BUILD_NUMBER ,
					appEnv: "Development",
					distributionGroup : "Testers",
					artifactPath: "app/build/outputs/apk/release/app-release.apk",
					appCenterApplication: "Banco-Popular-Dominicano-App-Testing/App-Empresarial-Android"
					)
                }
                
            }
        }
    }
} 