ps aux | grep corda.jar | awk '{ print $2 }' | xargs sudo kill
