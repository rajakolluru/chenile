#!/bin/bash

function generateInternationalServiceMonolith(){
    export serviceTemplate="monolith-with-service-international"
    service=$(captureNonNullField "Service Name")
    serviceVersion=$(captureFieldWithDefaultValue "Service Version" "$defaultVersion")
    outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    defaultMonolithName=${service}deploy
    monolith=$(captureFieldWithDefaultValue "Mini Monolith" $defaultMonolithName)
    monolithVersion=${serviceVersion}
    $scripts_folder/gen-service-monolith-international.sh $monolith $monolithVersion $outfolder $service $serviceVersion 
}

function generateService(){   
    service=$(captureNonNullField "Service Name")
    serviceVersion=$(captureFieldWithDefaultValue "Service Version" "$defaultVersion")
    outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    $scripts_folder/gen-service.sh $service $serviceVersion $outfolder
}

function generateQueryService(){   
    service=$(captureNonNullField "Service Name")
    serviceVersion=$(captureFieldWithDefaultValue "Service Version" "$defaultVersion")
    outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    $scripts_folder/gen-query-service.sh $service $serviceVersion $outfolder
}

function generateWorkflowService(){   
    service=$(captureNonNullField "Workflow Entity Name")
    serviceVersion=$(captureFieldWithDefaultValue "Workflow Entity Service Version" "$defaultVersion")
    outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    $scripts_folder/gen-workflow-service.sh $service $serviceVersion $outfolder
}

function generateMiniMonolith(){
    monolith=$(captureNonNullField "Mini Monolith Name")
    monolithVersion=$(captureFieldWithDefaultValue "Mini Monolith Version" $defaultVersion)
    [[ -z $outfolder ]] && outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    [[ -z $service ]] && service=$(captureFieldWithDefaultValue "Service Name to bundle" $defaultServiceName)
    [[ -z $serviceVersion ]] && serviceVersion=$(captureFieldWithDefaultValue "Service Version" $defaultVersion)
    $scripts_folder/gen-monolith.sh $monolith $monolithVersion $outfolder $service $serviceVersion
}

function generateLocalConfig() {
    mkdir config
    cp $config_folder/setenv.sh config
    echo "Generated a config folder under this folder with defaults in setenv.sh. You can edit setenv.sh!!!"
}

function generateNormalServiceAndMonolith(){
    export serviceTemplate="service"
    generateService
    generateMiniMonolith
}

function generateQueryServiceAndMonolith(){
    export serviceTemplate="queryservice"
    generateQueryService
    generateMiniMonolith
}

function generateWorkflowServiceAndMonolith(){
    export serviceTemplate="workflowservice"
    generateWorkflowService
    generateMiniMonolith
}

function generateInterceptor(){
    export serviceTemplate="interceptor-template"
    interceptorName=$(captureNonNullField "Interceptor Name")
    interceptorVersion=$(captureFieldWithDefaultValue "Interceptor Version" "$defaultVersion")
    outfolder=$(captureFieldWithDefaultValue "Output Folder" $defaultDestFolder)
    $scripts_folder/gen-interceptor.sh $interceptorName $interceptorVersion $outfolder
}

function setenv(){
	curprog=${1}
    prog=${1##*/}
    
    if  [[ $curprog != */* ]] 
    then
        scripts_folder="$PWD"
    else 
        scripts_folder=${curprog%/*}
    fi
    [[ $scripts_folder != /* ]] && scripts_folder=$(pwd)/${scripts_folder} ## handle relative path invocations

    source $scripts_folder/common-functions.sh
    setBaseVars
}

setenv "${0}" 
source $scripts_folder/input-capture.sh # init functions that allow us to capture input from user

usage="${prog}"
function usage {
	echo "$usage" 1>&2
	echo "$prog -? for this usage message" 1>&2
}

function _exit {
	rm -f $tmpfile1 $tmpfile2 $tmpfile3
	exit $1
}

if [[ $1 == "-?" ]]
then
    usage
    _exit 1
fi

choice=$(choices  \
    "N|Generate Normal Service & Mini Monolith"  \
    "W|Generate Workflow Service & Mini Monolith" \
    "I|Generate a Chenile interceptor stub" \
    "C|Create a local config")

case $choice in
    "N") generateNormalServiceAndMonolith ;;
    "W") generateWorkflowServiceAndMonolith;;
    "I") generateInterceptor;;
    "C") generateLocalConfig;;
esac
_exit 0
