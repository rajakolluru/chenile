#!/usr/bin/env bash

opt=$1
IFS=. read major minor patch <<<"$(git describe | cut -d- -f1)"

case ${opt} in
major) major=$(( major + 1 ));minor=1;patch=1;;
minor) minor=$(( minor + 1 ));patch=1;;
patch) patch=$(( patch + 1 ));;
*) echo "Usage: $0 'major|minor|patch'"; exit 1;;
esac

ver="${major}.${minor}.${patch}"

# first check that there are no pending commits.
items_to_commit=$(git status --porcelain=1 | wc -l)
if (( items_to_commit != 0 ))
then
    echo "There are pending commit items. Please commit them before tagging."
    exit 2
fi

git tag -a -m ${ver} ${ver}
read -p "Successfully created a tag $ver. Want me to build the deployable?" yes
if [[ ${yes} == y* ]]
then
    echo "Building the executable for you."
    mvn clean install
fi

exit 0

