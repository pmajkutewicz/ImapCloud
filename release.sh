#!/usr/bin/env bash
if [ $# -ne 2 ]
  then
    echo "Usage: release.sh releaseVersion nextVersion"
    exit 0
fi

git checkout master
git checkout -b release-$1 && git push -u origin release-$1
mvn release:clean
mvn -B release:prepare -Dtag=$1 -DreleaseVersion=$1 -DdevelopmentVersion=$2
git push
mvn release:perform

git commit -a -m "Release: $1"
git push && git push --tags

git push
