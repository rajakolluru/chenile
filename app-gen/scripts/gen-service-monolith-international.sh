#!/bin/bash

function usage(){
  	echo "Usage: $prog <monolith-name> [monolith-version] [dest-folder] [service-to-bundle-name] [service-to-bundle-version]" >&2
  	echo "monolith version will default to $defaultVersion" >&2
	echo "dest-folder will default to $defaultDestFolder" >&2
	echo "Service to bundle along with the monolith. If this is not provided we will default to $defaultServiceName as the name of the service"
	echo "Service to bundle version. If this is not provided we will default to $defaultServiceVersion as the version of the service"
}

function _exit {
	# rm -f $json_file
  	exit $1
}

## Program initialization helper that sets all the environment variables required
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



function generateAll(){
	template_folder=$template_folder_base/monolith-with-service-international
  	generateModule $template_folder $dest_folder $json_file "monolith org company Monolith service Service"
  	doGitInit $dest_folder/$service $serviceVersion
}

function constructJsonfile(){
	echo "{"
	echo "\"monolith\": \"$monolith\","
	echo "\"monolithVersion\": \"$monolithVersion\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"auroraPackage\": \"$auroraPackage\","
	echo "\"auroraVersion\": \"$auroraVersion\","
	echo "\"Monolith\": \"$Monolith\","
	echo "\"service\": \"$service\","
	echo "\"Service\": \"$Service\","
	echo "\"serviceTemplate\": \"$serviceTemplate\","
	echo "\"serviceVersion\": \"$serviceVersion\","
	echo "\"services\": "'['" $services "']'
	echo "}"
}

setenv "${0}" 
if (( $# < 1 ))
then
	usage
	_exit 1
fi


json_file=/tmp/$prog.$$
monolith=${1}
Monolith=$(camelCase $monolith)
monolithVersion=${2:-$defaultVersion}
dest_folder=${3:-$defaultDestFolder}
service=${4:-defaultServiceName}
Service=$(camelCase $service)
serviceVersion=${5:-$defaultVersion}
services="\"${service}-service-base\", \"${service}-service-can\", \"${service}-service-us\", \"${service}-service-mx\""

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating monolith ${monolith}(${monolithVersion}) with included service $service($serviceVersion) in folder $dest_folder" >&2
constructJsonfile > $json_file
# getServiceList
generateAll
_exit 0
