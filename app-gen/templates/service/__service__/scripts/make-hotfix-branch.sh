#!/usr/bin/env bash

tag=$(git describe | cut -d- -f1)
read -p  "Hot fix branch will be created for tag $tag. Are you sure?" yes
if [[ $yes != y* ]]
then
    echo "Alright then. You need to specify a tag and create it manually using 'make create-hotfix tag=tagname'"
    echo "I am giving you some eligible tags"
    git tag
    exit 1
fi

git checkout -b b$tag $tag
echo "Created a hotfix branch b$tag and checked it out for you"
exit 0