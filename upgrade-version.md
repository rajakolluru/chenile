# How to upgrade chenile version
These instructions are for Chenile maintainers to upgrade the version of chenile to a new version.
Let us say new version is a.b.c. Do the following:

0. Make sure that everything builds good with "make build" 
1. Edit chenile-version.txt and pom.xml and replace existing  versions with a.b.c
2. git add .; git commit -m "Bump up to a.b.c" ; git push origin main
3. make tag tag=a.b.c # this wil create the tag
4. make build # All the builds in local maven repo will have the latest version now.
5. passphrase="<secret phrase>" make deploy  # this will deploy to Maven Central
6. make push-tags # pushes the newly created tag into origin
7. make list-local-tags # ensures that the new tag is there locally
8. make list-origin-tags # ensures that the new tag is there in origin.
9. Edit chenile-version.txt and pom.xml to reflect the next snapshot
10. make build once again
