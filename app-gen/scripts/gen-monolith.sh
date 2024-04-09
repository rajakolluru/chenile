#!/bin/bash

function usage(){
  	echo "Usage: $prog <monolith-name> [monolith-version] [dest-folder] [service-to-bundle-name] [service-to-bundle-version]" >&2
  	echo "monolith version will default to $defaultVersion" >&2
	echo "dest-folder will default to $defaultDestFolder" >&2
	echo "Service to bundle along with the monolith. If this is not provided we will default to $defaultServiceName as the name of the service"
	echo "Service to bundle version. If this is not provided we will default to $defaultServiceVersion as the version of the service"
}

function _exit {
	rm -f $json_file
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



function generateMonolith(){
	template_folder=$template_folder_base/monolith
  	generateModule $template_folder $dest_folder $json_file "monolith com org company Monolith"
  	generateCurlScript 
  	doGitInit $dest_folder/$monolith $monolithVersion
}

function generateCurlScript(){
	tmpfile=/tmp/$RANDOM.$$.${prog}
	# [[ -z $serviceTemplate ]] && return
	cur_curl_script=${dest_folder}/${monolith}/scripts/curl-scripts.sh
	service_curl_script=${template_folder_base}/${serviceTemplate}/__service__/scripts/curl-scripts.sh.mustache
	[[ ! -f ${cur_curl_script} ]] && return
	[[ ! -f ${service_curl_script} ]] && return
	$scripts_folder/node_modules/mustache/bin/mustache $json_file $service_curl_script > $tmpfile
	sed '1,/#--/d' ${tmpfile} >> ${cur_curl_script}
	rm $tmpfile
}

function constructJsonfile(){
	echo "{"
	echo "\"monolith\": \"$monolith\","
	echo "\"monolithVersion\": \"$monolithVersion\","
	echo "\"com\": \"$com\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"chenilePackage\": \"$chenilePackage\","
	echo "\"chenileVersion\": \"$chenileVersion\","
	echo "\"Monolith\": \"$Monolith\","
	echo "\"service\": \"$service\","
	echo "\"serviceTemplate\": \"$serviceTemplate\","
	echo "\"serviceVersion\": \"$serviceVersion\","
	echo "\"services\": "'['" $services "']'
	echo "}"
}

## Finds the list of services that have been exposed by the serviceTemplate
function getServiceList(){
	tmpfile=/tmp/$RANDOM.$$.${prog}
	filename=${template_folder_base}/${serviceTemplate}/.meta/service-modules.txt
	$scripts_folder/node_modules/mustache/bin/mustache $json_file $filename > $tmpfile
	services=$(sed 's/^\(.*\)$/"\1"/' $tmpfile  | tr '\n' ',') # surround each line with double-quotes
	constructJsonfile > $json_file
	rm $tmpfile
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
serviceVersion=${5:-$defaultVersion}

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating monolith ${monolith}(${monolithVersion}) with included service $service($serviceVersion) in folder $dest_folder" >&2
constructJsonfile > $json_file
getServiceList
generateMonolith
_exit 0
